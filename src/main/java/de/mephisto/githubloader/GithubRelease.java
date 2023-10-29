package de.mephisto.githubloader;

import java.util.ArrayList;
import java.util.List;

public class GithubRelease {
  private String name;
  private String url;
  private List<ReleaseArtifact> artifacts = new ArrayList<>();
  private String tag;

  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public List<ReleaseArtifact> getArtifacts() {
    return artifacts;
  }

  public void setArtifacts(List<ReleaseArtifact> artifacts) {
    this.artifacts = artifacts;
  }
}
