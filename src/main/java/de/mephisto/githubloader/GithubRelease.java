package de.mephisto.githubloader;

import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GithubRelease {
  private String name;
  private String releasesUrl;
  private String url;
  private List<ReleaseArtifact> artifacts = new ArrayList<>();
  private String tag;

  public String getReleasesUrl() {
    return releasesUrl;
  }

  public void setReleasesUrl(String releasesUrl) {
    this.releasesUrl = releasesUrl;
  }

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

  @Nullable
  public ReleaseArtifact getLatestArtifact() {
    if (!this.artifacts.isEmpty()) {
      return this.artifacts.get(0);
    }
    return null;
  }

  public void setArtifacts(List<ReleaseArtifact> artifacts) {
    this.artifacts = artifacts;
  }
}
