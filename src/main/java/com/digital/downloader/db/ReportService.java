package com.digital.downloader.db;

import java.sql.*;

public class ReportService {

    public static void generateCompletionReport(int websiteId) throws SQLException {
        String reportSQL = "SELECT website_name, download_start_time, download_end_time, total_time_taken, total_size FROM website WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(WebsiteRepository.DB_URL, WebsiteRepository.DB_USERNAME, WebsiteRepository.DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(reportSQL)) {

            statement.setInt(1, websiteId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String websiteName = resultSet.getString("website_name");
                    Timestamp startTime = resultSet.getTimestamp("download_start_time");
                    Timestamp endTime = resultSet.getTimestamp("download_end_time");
                    long totalTime = resultSet.getLong("total_time_taken");
                    long totalSize = resultSet.getLong("total_size");  // Fetch total_size

                    System.out.println("====================================");
                    System.out.println("Website Download Report for: " + websiteName);
                    System.out.println("====================================");
                    System.out.println("Start Time: " + formatTimestamp(startTime));
                    System.out.println("End Time: " + formatTimestamp(endTime));
                    System.out.println("Total Time Taken: " + totalTime + " milliseconds");
                    System.out.println("Total Size Downloaded: " + totalSize + " bytes");
                    System.out.println("====================================");
                } else {
                    System.out.println("No data found for website ID: " + websiteId);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error generating report: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private static String formatTimestamp(Timestamp timestamp) {
        if (timestamp != null) {
            return timestamp.toString();
        }
        return "N/A";
    }
}
