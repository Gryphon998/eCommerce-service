# E-Commerce Service

## Introduction
This project is a separated back-end e-commerce website developed using the SSM framework, with MySQL as the database. It includes seven modules: user management, orders, categories, products, shopping cart, addresses, and online payment.

## Frameworks
| Name  | Version |
| ------------- | ------------- |
| Spring Framework | 4.0.0 |
| Spring MVC | 4.0.0 |
| Mybatis | 3.4.1 |
| nginx | 1.20.2 |
| MySQL  | 5.1.6 |
| Tomcat | 7.0.64 |
| logback | 1.1.2 |
| guava | 20.0 |
| Junit | 4.12 |
| Joda | 2.3 |

## Architecture
In summary as depicted in the diagram:
<p align="center">
  <img src="https://github.com/Gryphon998/eCommerce-service/assets/41406456/9e74abd1-066d-497b-997a-3dbeaf1c88ae">
</p>

* The system architecture involves Nginx as the initial receiver of user requests. 
* Nginx then forwards these requests to Tomcat, which hosts the Spring MVC application.
* Within the Spring MVC application, the Spring Framework handles dependency management and executes business logic.
* MyBatis is responsible for handling database operations and connects to MySQL for data storage and retrieval.
* The web application relies on an FTP server to store static content, which can be served to end users by Nginx.

## Modules
### User Management

### Orders

### Categories

### Products

### Shopping Cart

### Addresses

### Payment

## Interfaces

## Test

## Developmenp Environment
| Name  | Version |
| ------------- | ------------- |
| Ubuntu | 22.04 LTS |
| Intellij IDEA | 2023.1.3 |
| JDK  | 1.8  |
| VMware Workstation | 17 |
| Restlet | N/A |
