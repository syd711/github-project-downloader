package de.mephisto.githubloader;

import edu.umd.cs.findbugs.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class ReleaseArtifact {
  private final static Logger LOG = LoggerFactory.getLogger(ReleaseArtifact.class);
  private final GithubRelease githubRelease;

  private String name;
  private String url;

  public ReleaseArtifact(GithubRelease githubRelease) {
    this.githubRelease = githubRelease;
  }

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

  @Override
  public String toString() {
    return name;
  }

  public ReleaseArtifactActionLog diff(@NonNull File sourceArchive, @NonNull File targetFolder, boolean skipRootFolder, @NonNull List<String> excludedFiles, @NonNull String... names) {
    ReleaseArtifactActionLog installLog = new ReleaseArtifactActionLog(false, true);
    try {
      if (!sourceArchive.exists()) {
        sourceArchive = new Downloader(this.getUrl(), installLog).download(sourceArchive);
      }

      if (!sourceArchive.exists()) {
        installLog.setStatus("Archive download failed for " + this + " failed, cancelling diff.");
        throw new UnsupportedOperationException("Archive download failed for " + this + " failed, cancelling diff.");
      }

      ArchiveHandler handler = new ArchiveHandler(sourceArchive, installLog, skipRootFolder, excludedFiles);
      handler.diff(targetFolder);

      if (!installLog.hasDiffFor(names)) {
        installLog.setSummary("********************* Summary *********************\n" +
          "The version tag \"" + githubRelease.getTag() + "\" of artifact \"" + this.name + "\" matches with the current installation.\n" +
          "The following files have been checked for the version comparison: " + String.join(", ", names));
      }
      else {
        installLog.setSummary("********************* Summary *********************\n" +
          "The artifact \"" + this.name + "\" does not match with the current installation, your installation may be outdated.\n" +
          "The following files have been checked for the version comparison: " + String.join(", ", names));
      }

      LOG.info(installLog.toLogString());
      return installLog;
    } catch (Exception e) {
      LOG.error("Failed to run diff: " + e.getMessage(), e);
      installLog.setStatus(e.getMessage());
    }
    return installLog;
  }

  public ReleaseArtifactActionLog simulateInstall(@NonNull File targetFolder, boolean skipRootFolder, @NonNull List<String> excludedFiles) {
    return install(targetFolder, true, skipRootFolder, excludedFiles);
  }

  public ReleaseArtifactActionLog install(@NonNull File targetFolder, boolean skipRootFolder, @NonNull List<String> excludedFiles) {
    return install(targetFolder, false, skipRootFolder, excludedFiles);
  }

  private ReleaseArtifactActionLog install(@NonNull File targetFolder, boolean simulate, boolean skipRootFolder, List<String> excludedFiles) {
    ReleaseArtifactActionLog installLog = new ReleaseArtifactActionLog(simulate, false);
    try {
      File archive = new Downloader(this.getUrl(), installLog).download(null);
      if (!archive.exists()) {
        installLog.setStatus("Archive download failed for " + this + " failed, cancelling installation.");
        throw new UnsupportedOperationException("Archive download failed for " + this + " failed, cancelling installation.");
      }

      ArchiveHandler handler = new ArchiveHandler(archive, installLog, skipRootFolder, excludedFiles);
      if (simulate) {
        handler.simulate(targetFolder);
      }
      else {
        handler.unzip(targetFolder);
      }

      if (!archive.delete()) {
        installLog.setStatus("Failed to delete download artifact \"" + archive.getAbsolutePath() + "\"");
        throw new UnsupportedOperationException("Failed to delete download artifact \"" + archive.getAbsolutePath() + "\"");
      }
      else {
        installLog.log("Deleted downloaded artifact \"" + archive.getAbsolutePath() + "\"");
      }

      LOG.info(installLog.toLogString());

      return installLog;
    } catch (Exception e) {
      LOG.error("Failed to run install (simulated= " + simulate + "): " + e.getMessage(), e);
      installLog.setStatus(e.getMessage());
    }
    return installLog;
  }
}
