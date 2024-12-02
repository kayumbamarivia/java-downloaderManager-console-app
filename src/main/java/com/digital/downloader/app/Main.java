package com.digital.downloader.app;

import com.digital.downloader.db.WebsiteRepository;
import com.digital.downloader.db.WebsiteService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean continueDownloading = true;

        while (continueDownloading) {
            try {
                System.out.println("WELCOME TO OUR CONSOLE DOWNLOADER APP \n ");
                WebsiteRepository.createTablesIfNotExist();
                continueDownloading = downloadWebsite(scanner);
            } catch (SQLException | IOException e) {
                System.out.println("An error occurred: " + e.getMessage());
                System.out.print("Do you want to retry? (yes/no): ");
                String retryResponse = scanner.nextLine().toLowerCase();
                if ("no".equals(retryResponse)) {
                    continueDownloading = false;
                    System.out.println("Exiting the program...");
                }
            }
        }

        scanner.close();
    }

    private static boolean downloadWebsite(Scanner scanner) throws SQLException, IOException {
        System.out.print("Enter website URL: ");
        String websiteUrl = scanner.nextLine();
        WebsiteService websiteService = new WebsiteService();
        websiteService.downloadWebsite(websiteUrl);
        System.out.print("Do you want to restart the download process? (yes/no): ");
        String userResponse = scanner.nextLine().toLowerCase();
        return !userResponse.equals("no");
    }
}
