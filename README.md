# Website Downloader Console Application

## Overview
The Website Downloader is a Java-based console application designed to download an entire website, including its homepage and all external resources (such as images, stylesheets, and scripts). The application allows users to input a website URL, download its homepage, extract all external links, and download corresponding resources. The project also tracks download progress and generates a detailed download report.

This tool helps to create a local copy of a website for offline browsing, while storing metadata related to the download operation in a relational database.

## Features
- **Download a website homepage**: Given a URL, the application downloads the homepage of the website.
- **Extract external links**: Extracts all external links from the homepage and stores them in a database.
- **Download external resources**: Downloads resources like images, scripts, and stylesheets linked from the homepage.
- **Track progress**: Displays the progress of each resource download in the console.
- **Generate download reports**: After the download, the system generates a report showing the overall progress and statistics of the website download.
- **Database integration**: Stores metadata like the website name, download start and end times, and download progress in a relational database.

## System Requirements
- **Java 8 or higher**: The application is built using Java 8.
- **MySQL Database**: Used for storing website download metadata and external links.
- **Maven**: For managing project dependencies and building the project.

## Project Structure
The project consists of the following components:
- **WebsiteRepository**: Handles interactions with the database, including creating tables and inserting records for websites, links, and download progress.
- **DownloadService**: Manages the download process for the homepage and linked resources.
- **ProgressReporter**: Tracks and reports the download progress of each resource.
- **Database Setup**: Uses MySQL to store data about downloaded websites, links, and progress.

### Database Tables
1. **website**: Stores metadata for the website being downloaded.
    - Columns: `id`, `website_name`, `download_start_date_time`, `download_end_date_time`, `total_elapsed_time`, `total_downloaded_kilobytes`

2. **link**: Stores the external links extracted from the website's homepage.
    - Columns: `id`, `link_name`, `website_id`, `total_elapsed_time`, `total_downloaded_kilobytes`

3. **download_report**: Tracks the download status for each link.
    - Columns: `id`, `website_id`, `link_id`, `file_size`, `download_time`, `download_date`

4. **resource_download_progress**: Stores download progress for each resource.
    - Columns: `id`, `link_id`, `downloaded_size`, `total_size`, `progress_percentage`, `timestamp`

## Setup Instructions

### Step 1: Clone the Repository
First, clone the repository to your local machine:
```bash
git clone https://github.com/kayumbamarivia/website-downloader.git
```
### Step 2: Install maven packages
Second, load maven on your intellij editor or use below command:
```bash
mvn clean package
```
### Step 3: Configure your database
Third, go to db package, locate WebsiteRepository.java class and change the db accordingly:
```bash
DB_URL=?, DB_NAME, USERNAME=?, PASSWORD
```
### Step 4: Run the application
Fourth, locate Main.java class inside app package and run the application or go inside that package and run this command:
```bash
1. javac Main.java
2.java Main
By the help of the testfile.txt containing some website urls you can use them.
and then do what is asked😂😂!
```
### Step 5: Customization and featuring
Last but not least, you can customize according to your preferences:
Bye 🙋‍♂️🙋‍♂️!!