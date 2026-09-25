# Database-Management-Java-SQL
A collection of Java database management exercises covering JDBC connectivity, SQL DDL/DML operations, the DAO design pattern, and relational data modeling, developed as part of a Database Management with Java and SQL course.

## Overview

This repository contains three projects that demonstrate connecting Java applications to a MySQL database using JDBC, covering the full range from schema creation and modification (DDL) to safe, parameterized data persistence (DML) and structured data retrieval.

## Note on Code Attribution

These projects were completed as structured coursework exercises, built from instructor-provided templates and lab instructions. My contribution consists of implementing the required JDBC connectivity, SQL logic, and Java classes, testing the resulting behavior, and documenting the code. This repository is shared as a record of applied learning in database management with Java, not as original tooling or production software.

## Technologies

- Java
- JDBC (Java Database Connectivity)
- MySQL
- SQL (DDL, DML, JOINs)
- Design Patterns: DAO (Data Access Object)
- JavaFx

## Contents

### coffee-order-database/
A coffee order management application that:
- Connects a Java application to a MySQL database using JDBC (`MysqlDataSource`), via a dedicated `ConnectionHelper` class.
- Retrieves and maps relational data to Java objects, executing SQL `SELECT` queries and processing `ResultSet` data into a structured `Customer` model.
- Displays customer order data through an interactive console menu built with Java's `Scanner` class.

### clicknbuy-user-registration/
A user registration system that:
- Implements the DAO (Data Access Object) design pattern to separate database logic from application logic, using a dedicated `UserDAO` class.
- Writes parameterized SQL `INSERT` queries using `PreparedStatement` to prevent SQL injection and safely persist new user records.
- Models user data (username, email, contact info, reward points) mapped to a relational database table.

### bookstore-database-manager/
A database schema management tool that:
- Programmatically creates and modifies a MySQL database schema using DDL statements (`CREATE DATABASE`, `CREATE TABLE`, `ALTER TABLE`) executed through JDBC.
- Modifies table structure to support evolving requirements — adding, modifying, and dropping columns — while maintaining data integrity.
- Practices relational data retrieval using SQL join operations (`LEFT JOIN`) on the Chinook sample database.
- Applies structured exception handling (`SQLException`) for robust database connection management and error reporting.
