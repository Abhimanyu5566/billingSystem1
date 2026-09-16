🧾 Billing System

A Billing Management System built with Java 21 and Spring Boot for managing billing, invoices, authentication, and PDF generation.

✨ Features

🔐 JWT Authentication

🧾 Billing & Invoice Management

📄 PDF Invoice Generation

📱 QR Code Support

🗄️ MySQL Database

🌐 REST APIs

🛠️ Tech Stack
Technology	Usage
Java 21	Programming Language
Spring Boot	Backend Framework
Spring Security	Authentication & Security
JWT	Token Authentication
Spring Data JPA	Database Operations
MySQL	Database
OpenPDF	PDF Generation
ZXing	QR Code Generation
MapStruct	DTO Mapping
Gradle	Build Tool



📂 Project Structure
billingSystem1/
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   └── test/
├── build.gradle
├── settings.gradle
├── gradlew
├── gradlew.bat
└── README.md

🚀 Getting Started
Clone the Repository
git clone https://github.com/Abhimanyu5566/billingSystem1.git
cd billingSystem1

Configure Database

Configure your MySQL credentials in:

src/main/resources/application.properties


Example:

spring.datasource.url=jdbc:mysql://localhost:3306/billing_system
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD

Run the Application

Windows

gradlew.bat bootRun


Linux / macOS

./gradlew bootRun

🧪 Testing

Run the tests with:

./gradlew test

🔐 Authentication

This project uses Spring Security and JWT for authentication.

Authorization: Bearer <your-jwt-token>

📄 PDF & QR Code

OpenPDF is used for generating invoice PDFs.

ZXing is used for QR code functionality.

🔗 Repository

Billing System - GitHub

👨‍💻 Author

Abhimanyu

GitHub Profile

📌 Project Status

Active Development

⭐ If you find this project useful, consider giving it a star!

[^1]: Built with Java 21 and Spring Boot.
