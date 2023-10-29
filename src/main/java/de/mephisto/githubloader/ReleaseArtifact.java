package de.mephisto.githubloader;

import edu.umd.cs.findbugs.annotations.NonNull;

import java.io.File;

public class ReleaseArtifact {
  private String name;
  private String url;

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean install(@NonNull File targetFolder) throws Exception {
    File archive = DownloadUtil.download(this.getUrl(), targetFolder);
    ZipUtil.unzip(archive, targetFolder);
    return archive.delete();
  }
}
