# Alternative Banking System - JavaFX & Tomcat Project

  The Alternative Banking System is a Java-based project using JavaFX for the front-end and Tomcat for the server. It facilitates connections between lenders and borrowers, creating a platform for peer-to-peer lending.

  ## Table of Contents
  - [Introduction](#introduction)
  - [Features](#features)
  - [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
  - [Usage](#usage)
  - [Architecture](#architecture)
  - [Technologies Used](#technologies-used)
  - [Documents](#documents)

## Introduction
The Alternative Banking System leverages JavaFX for the user interface and Tomcat as the server to connect loan providers with loan seekers. This platform enables users to register, create loan requests, view available loans, and manage their lending or borrowing activities.

## Features
- User registration and authentication
- Creating, viewing, and managing loan requests
- Browsing available loans based on various parameters
- Secure transaction handling
- User-friendly JavaFX interface

## Getting Started
  ### Prerequisites
    Before you begin, ensure you have the following prerequisites:
    - Java Development Kit (JDK)
    - Apache Tomcat Server

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/Yuval-Vino/Alternative-Banking
2. Build the project:
This step may involve compiling JavaFX code and creating the necessary artifacts for your project.
3. Deploy to Tomcat:
    *   Copy the compiled project files to the Tomcat webapps directory.
4. Start Tomcat server.
5. Access the application:
    *   Run the exceutable jar for each App.

### Usage
1. **Client App:**
    * Register as a user or log in if you already have an account.
    * Browse available loans or create a new loan request.
    * Lenders can view loan requests and choose to fund a request.
    * Borrowers can track their loan status and make repayments.
    * Users can manage their profiles and view transaction history.
2. **Admin App:**
    * Log in using admin credentials.
    * Manage user accounts and activities.
    * Monitor loan requests and transactions.
### Architecture
The project follows a client-server architecture with the following components:
* Client-side: The JavaFX-based front-end interface for users. It communicates with the server via HTTP requests or other communication mechanisms you've implemented.
* Admin-side: The JavaFX-based front-end interface for administrators.
* Server-side: Tomcat server handling incoming requests and interactions between clients and administrators.
* Tomcat: Apache Tomcat server hosts the application, managing requests and serving resources.

### Technologies Used
* Java
* JavaFX
* Apache Tomcat (Backend)
### Documents
* [DFD](https://github.com/Yuval-Vino/Alternative-Banking/blob/main/Documents/Alternative-Banking%20DFD.odg)
* [ERD!](https://github.com/Yuval-Vino/Alternative-Banking/blob/ce3b2aa5dd335a98ca20da2cd6849e090234057e/Alternative-Banking%20%20ERD.png)
