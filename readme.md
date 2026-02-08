
# Inventra – Intelligent Inventory Management System

**Inventra** is a full-stack inventory management system designed to manage products, monitor stock levels, and provide secure user authentication. It is built with **Spring Boot** and **MySQL** for the backend, and **HTML, CSS, and JavaScript** for the frontend, offering a modular and extensible architecture suitable for academic projects, portfolios, and real-world applications.

---

## Table of Contents

1. [Overview](#overview)
2. [Features](#features)
3. [Tech Stack](#tech-stack)
4. [Frontend Pages](#frontend-pages)
5. [Backend Structure](#backend-structure)
6. [Prerequisites](#prerequisites)
7. [Setup and Installation](#setup-and-installation)
   * [Backend Setup](#backend-setup)
   * [Frontend Setup](#frontend-setup)
8. [Running the Application](#running-the-application)
9. [Security](#security)
10. [Usage](#usage)
11. [Contributing](#contributing)
12. [License](#license)
13. [Project Owners](#project-owners)

---

## Overview

Inventra provides a structured platform to:

* Authenticate users securely
* Manage products and inventory data
* Monitor low-stock items
* View dashboards and reports
* Support password recovery workflows

The backend follows a layered architecture and exposes RESTful APIs to separate frontend presentation from business logic.

---

## Features

### Authentication & Security

* User registration and login
* JWT-based authentication
* Secure password handling
* Forgot password and reset password functionality
* Email support for authentication workflows

### Inventory Management

* Add, view, update, and delete products
* Track low-stock items
* Persistent inventory storage using MySQL

### User Interface

* Dashboard for overview of inventory
* Product management pages
* Low-stock monitoring
* Reports and analytics views
* Password recovery pages

---

## Tech Stack

| Layer     | Technology                 |
| --------- | -------------------------- |
| Backend   | Java 17, Spring Boot 3.5.9 |
| Security  | Spring Security, JWT       |
| Database  | MySQL                      |
| API       | RESTful APIs               |
| Build     | Maven                      |
| Frontend  | HTML, CSS, JavaScript      |
| Utilities | Lombok, Spring Mail        |

---

## Frontend Pages

The frontend consists of HTML, CSS, and JavaScript files:

* `index.html`
* `login.html`
* `register.html`
* `dashboard.html`
* `products.html`
* `add-product.html`
* `low-stock.html`
* `reports.html`
* `forgotpassword.html`
* `reset-password.html`

---

## Backend Structure

The backend follows a modular Spring Boot architecture:

* **config** – Application and framework configuration
* **controller** – REST API endpoints
* **dto** – Data Transfer Objects
* **entity** – JPA entities mapped to database tables
* **inventory** – Inventory-specific modules
* **repository** – Database access using Spring Data JPA
* **security** – JWT and authentication logic
* **service** – Business logic layer
* **InventraAuthApplication.java** – Application entry point

---

## Prerequisites

Before running the project, install:

* Java JDK 17 or above
* MySQL Server
* Maven
* IDE such as IntelliJ IDEA, Eclipse, or VS Code
* Modern web browser

---

## Setup and Installation

### Backend Setup

1. Clone the repository:

```bash
git clone https://github.com/polepallikeerthi/Inventra-Intelligent-Inventory-Management-System.git
cd Inventra-Intelligent-Inventory-Management-System/backend
```

2. Configure database in `application.properties`:

```
spring.datasource.url=jdbc:mysql://localhost:3306/inventra_db
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD
```

3. Build and run the backend:

```bash
mvn clean install
mvn spring-boot:run
```

---

### Frontend Setup

1. Navigate to the frontend files
2. Open `index.html` in a browser or serve via a local HTTP server
3. Ensure the backend is running to allow API communication

---

## Running the Application

* Backend runs on the configured port (default: 8080)
* Frontend interacts with backend REST APIs
* You can also test endpoints using Postman or any REST client

---

## Security

* Authentication is handled using **JWT tokens**
* Protected endpoints require valid authentication
* Spring Security manages access control
* Password recovery functionality supported via email

---

## Usage

1. Register a new user account
2. Log in using credentials
3. Access the dashboard
4. Add, view, update, or delete products
5. Monitor low-stock items
6. View inventory reports
7. Use forgot password / reset password workflow as needed

---

## Contributing

Contributions are welcome:

1. Fork the repository
2. Create a feature branch (`feature/your-feature`)
3. Commit your changes
4. Push to your fork
5. Open a pull request
Please maintain clean code and documentation.

---

## License

This project is licensed under the **MIT License**.

You are free to use, modify, and distribute this software for personal, academic, or commercial purposes, provided proper credit is given to the project owners.
See the `LICENSE` file in the repository for full license details.

---

## Project Contributors

* Keerthi Polepalli
* Prashant K Pathak
* Nagamani Mannempalli
* Jerusha
* Vaishnavi
