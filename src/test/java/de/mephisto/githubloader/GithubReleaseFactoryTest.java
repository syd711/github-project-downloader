package de.mephisto.githubloader;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GithubReleaseFactoryTest {
  private final static Logger LOG = LoggerFactory.getLogger(GithubReleaseFactoryTest.class);

  @Test
  public void testMameDownload() throws Exception {
    GithubRelease githubRelease = GithubReleaseFactory.loadRelease("https://github.com/vpinball/pinmame/releases", Arrays.asList("win-", "VPinMAME"), Arrays.asList("linux", "sc-", "osx"));
    assertNotNull(githubRelease);

    ReleaseArtifact artifact = githubRelease.getArtifacts().get(0);
    assertTrue(artifact.install(new File("./test/")));
  }


  @Test
  public void testVpx() throws IOException {
    GithubRelease githubRelease = GithubReleaseFactory.loadRelease("https://github.com/vpinball/vpinball/releases", Collections.emptyList(), Arrays.asList("Debug"));
    assertNotNull(githubRelease);
  }

  @Test
  public void testBackglass() throws IOException {
    GithubRelease githubRelease = GithubReleaseFactory.loadRelease("https://github.com/vpinball/b2s-backglass/releases", Collections.emptyList(), Arrays.asList("Source"));
    assertNotNull(githubRelease);
  }
}
