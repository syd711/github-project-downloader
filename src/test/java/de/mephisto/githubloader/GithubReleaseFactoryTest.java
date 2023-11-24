package de.mephisto.githubloader;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class GithubReleaseFactoryTest {

  @Test
  public void testMameDownload() throws Exception {
    GithubRelease githubRelease = GithubReleaseFactory.loadRelease("https://github.com/vpinball/pinmame/releases", Arrays.asList("win-", "VPinMAME"), Arrays.asList("linux", "sc-", "osx"));
    assertNotNull(githubRelease);

    ReleaseArtifact artifact = githubRelease.getArtifacts().get(0);
    ReleaseArtifactActionLog install = artifact.install(new File("./test/"), false, Collections.emptyList());
    assertNotNull(install);
    assertFalse(install.getLogs().isEmpty());
    assertNull(install.getStatus());
  }

  @Test
  public void testMameDownloadSimulated() throws Exception {
    GithubRelease githubRelease = GithubReleaseFactory.loadRelease("https://github.com/vpinball/pinmame/releases", Arrays.asList("win-", "VPinMAME"), Arrays.asList("linux", "sc-", "osx"));
    assertNotNull(githubRelease);

    ReleaseArtifact artifact = githubRelease.getArtifacts().get(0);
    ReleaseArtifactActionLog install = artifact.simulateInstall(new File("./test/"), false, Collections.emptyList());
    assertNotNull(install);
    assertFalse(install.getLogs().isEmpty());
    assertNull(install.getStatus());
  }

  @Test
  public void testMameDiff() throws Exception {
    GithubRelease githubRelease = GithubReleaseFactory.loadRelease("https://github.com/vpinball/pinmame/releases", Arrays.asList("win-", "VPinMAME"), Arrays.asList("linux", "sc-", "osx"));
    assertNotNull(githubRelease);

    ReleaseArtifact artifact = githubRelease.getArtifacts().get(0);
    ReleaseArtifactActionLog install = artifact.diff(new File("./test/", artifact.getName()), new File("./test/"), false, Collections.emptyList(), "*.dll");
    assertNotNull(install);
    assertFalse(install.getLogs().isEmpty());
    assertFalse(install.getDiffEntries().isEmpty());
    assertNull(install.getStatus());
  }


  @Test
  public void testVpx() throws Exception {
    GithubRelease githubRelease = GithubReleaseFactory.loadRelease("https://github.com/vpinball/vpinball/releases", Collections.emptyList(), Arrays.asList("Debug"));
    assertNotNull(githubRelease);

    ReleaseArtifact artifact = githubRelease.getArtifacts().get(0);
    ReleaseArtifactActionLog install = artifact.install(new File("./test/"), false, Collections.emptyList());
    assertNotNull(install);
    assertNull(install.getStatus());
    assertFalse(install.getLogs().isEmpty());
  }


  @Test
  public void testVpxDiff() throws Exception {
    GithubRelease githubRelease = GithubReleaseFactory.loadRelease("https://github.com/vpinball/vpinball/releases", Collections.emptyList(), Arrays.asList("Debug"));
    assertNotNull(githubRelease);

    ReleaseArtifact artifact = githubRelease.getArtifacts().get(0);
    ReleaseArtifactActionLog install = artifact.diff(new File("./test/", artifact.getName()), new File("./test/"), false, Collections.emptyList());
    assertNotNull(install);
    assertNull(install.getStatus());
    assertFalse(install.getDiffEntries().isEmpty());

  }

  @Test
  public void testBackglass() throws Exception {
    GithubRelease githubRelease = GithubReleaseFactory.loadRelease("https://github.com/vpinball/b2s-backglass/releases", Collections.emptyList(), Arrays.asList("Source"));
    assertNotNull(githubRelease);

    ReleaseArtifact artifact = githubRelease.getArtifacts().get(0);
    ReleaseArtifactActionLog install = artifact.install(new File("./test/"), false, Collections.emptyList());
    assertNotNull(install);
    assertNull(install.getStatus());
    assertFalse(install.getLogs().isEmpty());
  }

  @Test
  public void testSerum() throws Exception {
    GithubRelease githubRelease = GithubReleaseFactory.loadRelease("https://github.com/zesinger/libserum/releases", Collections.emptyList(), Arrays.asList("Source", "tvos", "macOS", "linux", "arm", "android"));
    assertNotNull(githubRelease);

    ReleaseArtifact artifact = githubRelease.getArtifacts().get(0);
    ReleaseArtifactActionLog install = artifact.install(new File("./test/"), true, Collections.emptyList());
    assertNotNull(install);
    assertNull(install.getStatus());
    assertFalse(install.getLogs().isEmpty());
  }

  @Test
  public void testFlexDMD() throws Exception {
    GithubRelease githubRelease = GithubReleaseFactory.loadRelease("https://github.com/vbousquet/flexdmd/releases", Collections.emptyList(), Arrays.asList("Source"));
    assertNotNull(githubRelease);

    ReleaseArtifact artifact = githubRelease.getArtifacts().get(0);
    ReleaseArtifactActionLog install = artifact.install(new File("./test/"), true, Collections.emptyList());
    assertNotNull(install);
    assertNull(install.getStatus());
    assertFalse(install.getLogs().isEmpty());
  }

  @Test
  public void testFreezy() throws Exception {
    GithubRelease githubRelease = GithubReleaseFactory.loadRelease("https://github.com/freezy/dmd-extensions/releases", Collections.emptyList(), Arrays.asList("Source", ".msi"));
    assertNotNull(githubRelease);

    ReleaseArtifact artifact = githubRelease.getArtifacts().get(0);
    ReleaseArtifactActionLog install = artifact.install(new File("./test/"), false, Arrays.asList("DmdDevice.log.config", "DmdDevice.ini", "dmdext.log.config"));
    assertNotNull(install);
    assertNull(install.getStatus());
    assertFalse(install.getLogs().isEmpty());
  }
}
