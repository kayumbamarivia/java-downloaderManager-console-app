package com.digital.downloader.db;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;

public class WebsiteService {

    private WebsiteRepository websiteRepository = new WebsiteRepository();
    private FileService fileService = new FileService();

    public void downloadWebsite(String websiteUrl) throws IOException, SQLException {
        String domain = extractDomainFromUrl(websiteUrl);
        File websiteFolder = new File("downloads/" + domain);
        if (!websiteFolder.exists()) {
            websiteFolder.mkdirs();
        }
        int websiteId = websiteRepository.insertWebsite(websiteUrl);
        System.out.println("Starting download of " + websiteUrl + "...");
        long startTime = System.currentTimeMillis();
        String homepageFilePath = websiteFolder.getAbsolutePath() + "/index.html";
        fileService.downloadFile(websiteUrl, homepageFilePath);
        System.out.println("Extracting links...");
        Document document = Jsoup.parse(new File(homepageFilePath), "UTF-8");
        Elements links = document.select("a[href]");

        long totalSize = 0;
        int totalTime = 0;
        System.out.println("Found external links:");
        for (Element link : links) {
            String linkHref = link.attr("abs:href");
            if (!linkHref.isEmpty()) {
                System.out.println(" - " + linkHref);
                String sanitizedLinkFilePath = websiteFolder + "/" + fileService.sanitizeFileName(linkHref);
                long linkDownloadTime = fileService.downloadFile(linkHref, sanitizedLinkFilePath);
                long fileSize = new File(sanitizedLinkFilePath).length();
                totalSize += fileSize;
                totalTime += linkDownloadTime;
                System.out.println("Downloading: " + new File(sanitizedLinkFilePath).getName() + " (" + fileSize / 1024 + " KB) in " + linkDownloadTime + "ms");
                websiteRepository.insertLink(linkHref, websiteId);
            }
        }
        long endTime = System.currentTimeMillis();
        long totalDownloadTime = endTime - startTime;
        websiteRepository.updateWebsiteDownloadInfo(websiteId, new Timestamp(endTime), totalDownloadTime, totalSize);
        ReportService.generateCompletionReport(websiteId);
        System.out.println("Download completed successfully.");
        System.out.println("Report:");
        System.out.println("- Website: " + domain);
        System.out.println("- Total time: " + totalDownloadTime + "ms");
        System.out.println("- Total size: " + totalSize / 1024 + " KB");
    }

    private String extractDomainFromUrl(String url) {
        try {
            URL website = new URL(url);
            String domain = website.getHost();
            return domain.startsWith("www.") ? domain : "www." + domain;
        } catch (Exception e) {
            return "unknown";
        }
    }
}
