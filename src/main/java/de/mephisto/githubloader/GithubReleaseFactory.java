package de.mephisto.githubloader;

import edu.umd.cs.findbugs.annotations.NonNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GithubReleaseFactory {
  private final static Logger LOG = LoggerFactory.getLogger(GithubReleaseFactory.class);

  public static GithubRelease loadRelease(@NonNull String url, @NonNull List<String> allowList, @NonNull List<String> ignoreList) throws IOException {
    GithubRelease githubRelease = readFirstRelease(url);
    if (githubRelease != null) {
      loadArtifacts(url, allowList, ignoreList, githubRelease);
      return githubRelease;
    }
    return null;
  }

  private static void loadArtifacts(String baseUrl, List<String> patternList, List<String> ignoreList, GithubRelease githubRelease) throws IOException {
    try {
      if (!baseUrl.endsWith("/")) {
        baseUrl += "/";
      }
      String url = baseUrl + "expanded_assets/" + githubRelease.getTag();

      LOG.info("Loading release artifactors for " + url);
      Document doc = Jsoup
        .connect(url)
        .userAgent("Mozilla")
        .get();

      doc.select("a").stream().forEach(e -> {
        String artifactUrl = "https://github.com" + e.attr("href");
        if (artifactUrl.contains("releases/download")) {

          ReleaseArtifact artifact = new ReleaseArtifact();
          artifact.setName(e.text());
          artifact.setUrl(artifactUrl);

          if (patternList.isEmpty() && ignoreList.isEmpty()) {
            githubRelease.getArtifacts().add(artifact);
            LOG.info("Added release artifact: " + e.text() + " (" + artifactUrl + ")");
          }
          else {
            for (String s : patternList) {
              if (!e.text().contains(s)) {
                return;
              }
            }

            for (String s : ignoreList) {
              if (e.text().contains(s)) {
                return;
              }
            }
          }

          githubRelease.getArtifacts().add(artifact);
          LOG.info("Added release artifact: " + e.text() + " (" + artifactUrl + ")");
        }
      });

      if (githubRelease.getArtifacts().isEmpty()) {
        LOG.info("No release artifacts found for " + githubRelease.getUrl());
      }
    } catch (Exception e) {
      LOG.error("Failed to load release page: " + e.getMessage(), e);
      throw e;
    }
  }

  private static GithubRelease readFirstRelease(String url) throws IOException {
    try {
      Document doc = Jsoup
        .connect(url)
        .userAgent("Mozilla")
        .followRedirects(true)
        .get();

      Map<String, String> tag2Url = new LinkedHashMap<>();
      doc.select("a.Link--primary").stream().forEach(e -> {
        tag2Url.put(e.text(), "https://github.com" + e.attr("href"));
      });

      Set<Map.Entry<String, String>> entries = tag2Url.entrySet();
      for (Map.Entry<String, String> entry : entries) {
        LOG.info("Resolved release entry: " + entry + " (" + entry.getValue() + ")");
      }

      if (!entries.isEmpty()) {
        Map.Entry<String, String> next = entries.iterator().next();

        String[] split = next.getValue().split("/");

        GithubRelease release = new GithubRelease();
        release.setName(next.getKey());
        release.setUrl(next.getValue());
        release.setTag(split[split.length-1]);
        return release;
      }
      else {
        LOG.info("No releases found for " + url);
      }
    } catch (IOException e) {
      LOG.error("Failed to load release page: " + e.getMessage(), e);
      throw e;
    }
    return null;
  }
}
