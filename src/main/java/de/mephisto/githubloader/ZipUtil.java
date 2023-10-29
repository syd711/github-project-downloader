package de.mephisto.githubloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtil {
  private final static Logger LOG = LoggerFactory.getLogger(ZipUtil.class);

  public static boolean unzip(File archiveFile, File targetFolder) {
    try {
      byte[] buffer = new byte[1024];
      FileInputStream fileInputStream = new FileInputStream(archiveFile);
      ZipInputStream zis = new ZipInputStream(fileInputStream);
      ZipEntry zipEntry = zis.getNextEntry();

      while (zipEntry != null) {
        if (zipEntry.isDirectory()) {

        }
        else {
          String name = zipEntry.getName();
          // fix for Windows-created archives
          if (name.contains("/")) {
            name = name.substring(name.lastIndexOf("/") + 1);
          }

          File target = new File(targetFolder, name);
          if(target.isDirectory()) {
            target.mkdirs();
          }
          else {
            if (target.exists()) {
              target.delete();
            }
            FileOutputStream fos = new FileOutputStream(target);
            int len;
            while ((len = zis.read(buffer)) > 0) {
              fos.write(buffer, 0, len);
            }
            fos.close();
            LOG.info("Written " + target.getAbsolutePath());
          }
        }

        zis.closeEntry();
        zipEntry = zis.getNextEntry();
      }

      fileInputStream.close();
      zis.closeEntry();
      zis.close();

      return true;
    } catch (Exception e) {
      LOG.error("Unzipping of " + archiveFile.getAbsolutePath() + " failed: " + e.getMessage(), e);
    }
    return false;
  }
}
