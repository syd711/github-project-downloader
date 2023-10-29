package de.mephisto.githubloader;

import edu.umd.cs.findbugs.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadUtil {
  private final static Logger LOG = LoggerFactory.getLogger(GithubReleaseFactory.class);

  @NonNull
  public static File download(String downloadUrl, File targetFolder) throws Exception {
    if (targetFolder.isFile()) {
      throw new UnsupportedOperationException("targetFolder folder must be a folder, not a file");
    }

    String[] split = downloadUrl.split("/");
    String name = split[split.length - 1];
    File targetFile = new File(targetFolder, name);
    if (targetFile.exists() && !targetFile.delete()) {
      throw new UnsupportedOperationException("Failed to delete " + targetFile.getAbsolutePath());
    }

    LOG.info("Downloading " + downloadUrl + " to " + targetFile.getAbsolutePath());
    URL url = new URL(downloadUrl);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setReadTimeout(5000);
    connection.setDoOutput(true);
    BufferedInputStream in = new BufferedInputStream(url.openStream());

    File tmp = new File(targetFolder, targetFile.getName() + ".bak");
    if (tmp.exists()) {
      tmp.delete();
    }

    FileOutputStream fileOutputStream = null;
    try {
      fileOutputStream = new FileOutputStream(tmp);
      byte dataBuffer[] = new byte[1024];
      int bytesRead;
      while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
        fileOutputStream.write(dataBuffer, 0, bytesRead);
      }
      in.close();
      fileOutputStream.close();
    } finally {
      if (fileOutputStream != null) {
        fileOutputStream.close();
      }
    }

    if (!tmp.renameTo(targetFile)) {
      LOG.error("Failed to rename download temp file to " + targetFile.getAbsolutePath());
    }

    LOG.info("Downloaded file " + targetFile.getAbsolutePath());
    return targetFile;
  }
}
