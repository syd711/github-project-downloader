package de.mephisto.githubloader;

import edu.umd.cs.findbugs.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ArchiveHandler {
  private final static Logger LOG = LoggerFactory.getLogger(ArchiveHandler.class);

  @NonNull
  private final File archiveFile;

  @NonNull
  private final ReleaseArtifactActionLog installLog;

  private final boolean skipRootFolder;

  @NonNull
  private final List<String> excludedFiles;

  private boolean diff = false;
  private boolean simulate = false;

  public ArchiveHandler(@NonNull File archiveFile, @NonNull ReleaseArtifactActionLog installLog, boolean skipRootFolder, @NonNull List<String> excludedFiles) {
    this.archiveFile = archiveFile;
    this.installLog = installLog;
    this.skipRootFolder = skipRootFolder;
    this.excludedFiles = excludedFiles;
  }

  public void diff(@NonNull File destinationDir) {
    this.diff = true;
    this.simulate = true;
    run(destinationDir);
  }

  public void simulate(@NonNull File destinationDir) {
    this.diff = false;
    this.simulate = true;
    run(destinationDir);
  }

  public void unzip(@NonNull File destinationDir) {
    this.diff = false;
    this.simulate = false;
    run(destinationDir);
  }

  private void run(@NonNull File destinationDir) {
    try {
      if (simulate) {
        installLog.log("Extracting \"" + archiveFile.getName() + "\" to \"" + destinationDir.getAbsolutePath() + "\"");
      }
      else {
        installLog.log("Simulating extraction of \"" + archiveFile.getName() + "\" to \"" + destinationDir.getAbsolutePath() + "\"");
      }

      byte[] buffer = new byte[1024];
      FileInputStream fileInputStream = new FileInputStream(archiveFile);
      ZipInputStream zis = new ZipInputStream(fileInputStream);
      ZipEntry zipEntry = zis.getNextEntry();

      while (zipEntry != null) {
        String name = zipEntry.getName();
        if (skipRootFolder) {
          name = name.substring(name.indexOf("/"));
        }

        File newFile = new File(destinationDir, name);

        if (zipEntry.isDirectory()) {
          checkDirectory(newFile);
        }
        else {
          // fix for Windows-created archives
          File parent = newFile.getParentFile();
          if (!simulate && !parent.isDirectory() && !parent.mkdirs()) {
            installLog.setStatus("Failed to create directory " + parent.getAbsolutePath());
            throw new IOException("Failed to create directory " + parent.getAbsolutePath());
          }

          checkFile(zipEntry, zis, newFile);

          unzipFile(newFile, zis, buffer);
        }
        zis.closeEntry();
        zipEntry = zis.getNextEntry();
      }
      fileInputStream.close();
      zis.closeEntry();
      zis.close();
    } catch (Exception e) {
      installLog.setStatus("Unzipping of " + archiveFile.getAbsolutePath() + " failed: " + e.getMessage());
      throw new UnsupportedOperationException("Unzipping of " + archiveFile.getAbsolutePath() + " failed: " + e.getMessage(), e);
    }
  }

  private void checkDirectory(File newFile) throws IOException {
    if (!simulate && !newFile.isDirectory() && !newFile.mkdirs()) {
      installLog.setStatus("Failed to create directory " + newFile.getAbsolutePath());
      throw new IOException("Failed to create directory " + newFile.getAbsolutePath());
    }

    if (diff) {
      if (!newFile.exists()) {
        installLog.addDiffEntry(newFile.getAbsolutePath(), DiffState.TARGET_FOLDER_NOT_EXIST, -1, -1);
      }
    }
  }

  private void checkFile(ZipEntry entry, ZipInputStream zis, File newFile) throws IOException {
    if (isExcluded(entry.getName())) {
      installLog.log("Skipped excluded file " + entry.getName());
    }

    if (newFile.exists()) {
      if (simulate && !newFile.canWrite()) {
        installLog.setStatus("Failed to delete file: " + newFile.getAbsolutePath());
        throw new UnsupportedOperationException("Failed to delete file: " + newFile.getAbsolutePath());
      }
      if (!simulate && !newFile.delete()) {
        installLog.setStatus("Failed to delete file: " + newFile.getAbsolutePath());
        throw new UnsupportedOperationException("Failed to delete file: " + newFile.getAbsolutePath());
      }

      if (simulate) {
        installLog.log("Simulating overwrite of " + newFile.getAbsolutePath());
        LOG.debug("Simulating overwriting of " + newFile.getAbsolutePath());
      }
      else {
        installLog.log("Overwriting " + newFile.getAbsolutePath());
        LOG.debug("Overwriting " + newFile.getAbsolutePath());
      }
    }
    else {
      installLog.log("Writing " + newFile.getAbsolutePath());
      LOG.debug("Writing " + newFile.getAbsolutePath());
    }


    if (diff) {
      if (newFile.exists()) {
        long zipSize = entry.getSize();
        long fileSize = newFile.length();

        if (zipSize == -1) {
          byte[] buffer = new byte[1024];
          File tempFile = File.createTempFile(newFile.getName(), ".tmp");
          tempFile.deleteOnExit();
          doUnzip(tempFile, zis, buffer);
          zipSize = tempFile.length();
          tempFile.delete();
        }

        if (zipSize != fileSize) {
          installLog.addDiffEntry(newFile.getAbsolutePath(), DiffState.SIZE_DIFF, fileSize, zipSize);
        }
        else {
          installLog.addDiffEntry(newFile.getAbsolutePath(), DiffState.FILE_MATCH, fileSize, zipSize);
        }
      }
      else {
        installLog.addDiffEntry(newFile.getAbsolutePath(), DiffState.TARGET_FILE_NOT_EXIST, -1, -1);
      }
    }
  }

  private boolean isExcluded(String name) {
    return this.excludedFiles.contains(name);
  }

  private void unzipFile(File newFile, ZipInputStream zis, byte[] buffer) throws IOException {
    if (!simulate) {
      doUnzip(newFile, zis, buffer);
    }
  }

  private void doUnzip(File newFile, ZipInputStream zis, byte[] buffer) throws IOException {
    FileOutputStream fos = new FileOutputStream(newFile);
    int len;
    while ((len = zis.read(buffer)) > 0) {
      fos.write(buffer, 0, len);
    }
    fos.close();
  }
}
