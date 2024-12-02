package com.digital.downloader.db;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.sql.Timestamp;

public class FileService {
    public static String sanitizeFileName(String url) {
        String baseUrl = url.split("\\?")[0];
        baseUrl = baseUrl.split("#")[0];
        String sanitizedFileName = baseUrl.replaceAll("[<>:\"/\\|?*]", "_");
        sanitizedFileName = sanitizedFileName.replaceAll("[&=?]", "_");
        try {
            Paths.get(sanitizedFileName);
        } catch (InvalidPathException e) {
            sanitizedFileName = sanitizedFileName.replaceAll("[^a-zA-Z0-9.-]", "_");
        }

        return sanitizedFileName;
    }

    public static long downloadFile(String fileUrl, String saveFilePath) {
        long downloadTime = 0;
        long totalSize = 0;
        try {
            String sanitizedFilePath = sanitizeFileName(fileUrl);
            URL url = new URL(fileUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setInstanceFollowRedirects(true);
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_MOVED_PERM || responseCode == HttpURLConnection.HTTP_MOVED_TEMP || responseCode == 308) {
                String redirectedUrl = connection.getHeaderField("Location");
                System.out.println("Redirected to: " + redirectedUrl);
                return downloadFile(redirectedUrl, saveFilePath);
            }
            if (responseCode != HttpURLConnection.HTTP_OK) {
                System.out.println("Error: Unable to access the domain or the page. HTTP Response Code: " + responseCode);
                return 0;
            }

            try (InputStream inputStream = connection.getInputStream()) {
                totalSize = connection.getContentLength();
                Path path = Paths.get(saveFilePath);
                Files.createDirectories(path.getParent());

                long startTime = System.currentTimeMillis();
                byte[] buffer = new byte[4096];
                int bytesRead = -1;
                long totalBytesRead = 0;

                try (OutputStream outputStream = new FileOutputStream(saveFilePath)) {
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        totalBytesRead += bytesRead;
                        outputStream.write(buffer, 0, bytesRead);
                        if (totalSize > 0) {
                            int progress = (int) (totalBytesRead * 100 / totalSize);
                            progress = Math.max(0, Math.min(progress, 100));
                            printProgressBar(progress);
                        } else {
                            System.out.print("\rDownloading...");
                        }
                    }
                }

                long endTime = System.currentTimeMillis();
                downloadTime = endTime - startTime;
                System.out.println("\nDownloaded: " + saveFilePath + " (" + new File(saveFilePath).length() / 1024 + " KB) in " + downloadTime + "ms");
                updateDownloadInfo(saveFilePath, downloadTime, totalSize);

            }
        } catch (IOException e) {
            System.out.println("Error: Unable to download the file. Check if the domain exists and if the URL is correct.");
        }
        return downloadTime;
    }

    private static void printProgressBar(int progress) {
        int width = 50;
        int completed = (progress * width) / 100;
        StringBuilder progressBar = new StringBuilder();
        progressBar.append("[");
        for (int i = 0; i < completed; i++) {
            progressBar.append("=");
        }
        for (int i = completed; i < width; i++) {
            progressBar.append(" ");
        }

        progressBar.append("] ");
        progressBar.append(progress).append("%");
        System.out.print("\r" + progressBar.toString());
    }
    private static void updateDownloadInfo(String filePath, long downloadTime, long totalSize) {
        System.out.println("Update download info: " + filePath);
        System.out.println("Download time: " + downloadTime + " ms");
        System.out.println("Total size: " + totalSize / 1024 + " KB");
    }
}
