# Rice Sales Management System (RSMS)

RSMS is an application crafted to address the challenges faced by rice dealers. This innovative software empowers store owners to efficiently manage their business remotely, eliminating the need for physical presence. By embracing digital transformation, RSMS replaces traditional bookkeeping with a streamlined, modern solution, ensuring optimal control and efficiency in store operations.

---

## Features

### **Product Management**
- Seamlessly manage product records, including adding, updating, or removing rice entries.
- Store comprehensive details such as:
    - Rice Name
    - Rice Type
    - Rice Variety
    - Origin
    - Price
    - Description, and more.

### **Warehouse Management**
- Efficiently manage warehouses with the ability to:
    - Add, update, or remove warehouse records.
    - Organize and oversee specific zones within the warehouse for better inventory control.

### **Invoice Management**
- Streamline sales and accounting processes:
    - Create and manage customer orders with ease.
    - Generate debit notes for transparent financial tracking.
    - View and analyze revenue reports to monitor store performance.

### **Customer Management**
- Efficiently manage customer data and interactions. *(More details can be added here based on functionality)*

### **User Management**
- Ensure robust access control and role assignments:
    - Admins manage store owners.
    - Store owners manage their staff.

---

# Project Dependencies and Technologies

This project leverages several modern technologies and frameworks to build a robust and efficient application. Below is a detailed breakdown of the technologies used, based on the `pom.xml` configuration.

## Core Frameworks
- **Spring Boot**: A powerful framework for building Java-based applications.
    - `spring-boot-starter-data-jpa`: Manages database operations using JPA (Java Persistence API).
    - `spring-boot-starter-security`: Implements security features like authentication and authorization.
    - `spring-boot-starter-thymeleaf`: Integrates the Thymeleaf templating engine for dynamic web content.
    - `spring-boot-starter-validation`: Provides validation support for input data.
    - `spring-boot-starter-web`: Enables the development of RESTful APIs and web applications.
    - `spring-boot-devtools`: Offers live reload and enhanced development experience.

## Security Enhancements
- **Spring Security and Thymeleaf Integration**:
    - `thymeleaf-extras-springsecurity6`: Integrates Spring Security with Thymeleaf templates for secure rendering.
    - `spring-security-test`: Facilitates testing of Spring Security configurations.

## Database
- **Microsoft SQL Server**:
    - `mssql-jdbc`: A JDBC driver for connecting the application to a Microsoft SQL Server database.

## Development Utilities
- **Lombok**: Simplifies code by auto-generating common methods such as getters, setters, and constructors.
    - Configured via `maven-compiler-plugin` for annotation processing.
- **Spring Boot Maven Plugin**: Automates the build and packaging process for Spring Boot applications.
- **Maven Compiler Plugin**: Handles annotation processing, specifically for Lombok support.

## Testing
- **Spring Boot Testing Tools**:
    - `spring-boot-starter-test`: A comprehensive library for testing Spring Boot applications.

## Java Version
- **Java 21**: The application is built and optimized for Java 21.

## Build Plugins
- **Maven Compiler Plugin**:
    - Configured for annotation processing with Lombok.
- **Spring Boot Maven Plugin**:
    - Configured to exclude `lombok` during runtime builds.

This configuration ensures the project is equipped with modern tools for developing, testing, and maintaining a secure, database-driven web application.

---

# Installation and Setup

Follow the steps below to install and set up the project on your local machine.

---

## **1. Prerequisites**
Ensure the following tools are installed on your system:
- **Java Development Kit (JDK)**: Version 21 or later
    - [Download JDK](https://www.oracle.com/java/technologies/javase-downloads.html)
- **Apache Maven**: Version 3.6.0 or later
    - [Download Maven](https://maven.apache.org/download.cgi)
- **Database**: Microsoft SQL Server or your preferred database
    - [SQL Server Download](https://www.microsoft.com/en-us/sql-server/sql-server-downloads)
- **Git**: For cloning the repository
    - [Download Git](https://git-scm.com/)
- **IDE**: Recommended: IntelliJ IDEA, Eclipse, or NetBeans

---

## **2. Clone the Repository**
Use the following command to clone the project repository:
``` bash
git clone https://github.com/namnguyen1409/sp25-swp391-group2-se1889vj
```
Open project with IDE

---

## **3. Project Configuration**
1. Create a file named `application.properties` in the `src/main/resources` directory.
2. Add the following configuration to the file
    ```properties
    spring.application.name=your-application-name
    
    # Server configuration
    server.port=your-server-port
    
    # Database configuration
    spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=your_database_name
    spring.datasource.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerDriver
    spring.datasource.username=your_username
    spring.datasource.password=your_password
    
    spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect
    spring.jpa.hibernate.ddl-auto=update
    # Email configuration
    spring.mail.host=smtp.gmail.com
    spring.mail.port=465
    spring.mail.protocol=smtps
    spring.mail.username=your-gmail-address
    spring.mail.password=your-gmail-password
    spring.mail.properties.mail.smtp.auth=true
    spring.mail.properties.mail.smtp.starttls.enable=true
   
    # Recaptcha configuration
    google.recaptcha.key.site=your-site-key
    google.recaptcha.key.secret=your-secret-key
    google.recaptcha.url=https://www.google.com/recaptcha/api/siteverify
    ```
3. Replace your_* with your actual details.

---

## **4. Run the Project**
- Run the project with your IDE
- The application should now be running on:
```http request
http://localhost:your-server-port
```
#
