package com.digital.downloader.db;

import java.sql.*;

public class WebsiteRepository {

    protected static final String DB_URL = "jdbc:mysql://localhost:3306/web_downloader";
    protected static final String DB_USERNAME = "root";
    protected static final String DB_PASSWORD = "kayumba@";

    public static void createTablesIfNotExist() throws SQLException {
        String createWebsiteTableSQL = "CREATE TABLE IF NOT EXISTS website ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "website_name VARCHAR(255), "
                + "download_start_time TIMESTAMP, "
                + "download_end_time TIMESTAMP, "
                + "total_time_taken BIGINT, "
                + "total_size BIGINT"
                + ")";

        String createLinkTableSQL = "CREATE TABLE IF NOT EXISTS link ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "link_name VARCHAR(255), "
                + "website_id INT, "
                + "FOREIGN KEY (website_id) REFERENCES website(id)"
                + ")";

        String createDownloadReportTableSQL = "CREATE TABLE IF NOT EXISTS download_report ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "website_id INT, "
                + "link_id INT, "
                + "file_size INT, "
                + "download_time INT, "
                + "download_date TIMESTAMP, "
                + "FOREIGN KEY (website_id) REFERENCES website(id), "
                + "FOREIGN KEY (link_id) REFERENCES link(id)"
                + ")";

        String createResourceDownloadProgressTableSQL = "CREATE TABLE IF NOT EXISTS resource_download_progress ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "link_id INT, "
                + "downloaded_size INT, "
                + "total_size INT, "
                + "progress_percentage INT, "
                + "timestamp TIMESTAMP, "
                + "FOREIGN KEY (link_id) REFERENCES link(id)"
                + ")";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(createWebsiteTableSQL);
            statement.executeUpdate(createLinkTableSQL);
            statement.executeUpdate(createDownloadReportTableSQL);
            statement.executeUpdate(createResourceDownloadProgressTableSQL);
        }
    }

    public static int insertWebsite(String websiteName) throws SQLException {
        String insertWebsiteSQL = "INSERT INTO website (website_name, download_start_time) VALUES (?, ?)";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(insertWebsiteSQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, websiteName);
            statement.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        }
        return -1;
    }

    public static void insertLink(String linkHref, int websiteId) throws SQLException {
        String insertLinkSQL = "INSERT INTO link (link_name, website_id) VALUES (?, ?)";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(insertLinkSQL)) {
            statement.setString(1, linkHref);
            statement.setInt(2, websiteId);
            statement.executeUpdate();
        }
    }

    public static void updateWebsiteDownloadInfo(int websiteId, Timestamp downloadEndTime, long totalTimeTaken, long totalSize) throws SQLException {
        String updateWebsiteSQL = "UPDATE website SET download_end_time = ?, total_time_taken = ?, total_size = ? WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(updateWebsiteSQL)) {
            statement.setTimestamp(1, downloadEndTime);
            statement.setLong(2, totalTimeTaken);
            statement.setLong(3, totalSize);
            statement.setInt(4, websiteId);
            statement.executeUpdate();
        }
    }

    public static void insertDownloadReport(int websiteId, int linkId, long fileSize, long downloadTime) throws SQLException {
        String insertReportSQL = "INSERT INTO download_report (website_id, link_id, file_size, download_time, download_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(insertReportSQL)) {
            statement.setInt(1, websiteId);
            statement.setInt(2, linkId);
            statement.setLong(3, fileSize);
            statement.setLong(4, downloadTime);
            statement.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            statement.executeUpdate();
        }
    }

    public static void insertResourceDownloadProgress(int linkId, int downloadedSize, int totalSize) throws SQLException {
        String insertProgressSQL = "INSERT INTO resource_download_progress (link_id, downloaded_size, total_size, progress_percentage, timestamp) VALUES (?, ?, ?, ?, ?)";
        int progressPercentage = (int) ((double) downloadedSize / totalSize * 100);
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(insertProgressSQL)) {
            statement.setInt(1, linkId);
            statement.setInt(2, downloadedSize);
            statement.setInt(3, totalSize);
            statement.setInt(4, progressPercentage);
            statement.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            statement.executeUpdate();
        }
    }
}
