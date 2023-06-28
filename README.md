# Ecommerce Service

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
### User
Implemented robust methods to mitigate security vulnerabilities, including horizontal and vertical privilege escalation. These measures incorporate techniques such as MD5 plaintext encryption with salt values to protect sensitive data. 

Additionally, the utilization of Guava caching enhances system performance while fortifying security. I have also adhered to design principles that prioritize the creation of highly reusable service response objects, ensuring consistency and minimizing the risk of introducing vulnerabilities. Moreover, the effective usage of MyBatis plugins strengthens our system's security by optimizing database interactions and preventing potential loopholes.

### Management
Developed a sophisticated recursive classification system capable of seamlessly supporting an infinite hierarchical tree structure for efficient categorization purposes. This innovative design ensures that our system can effortlessly handle complex and extensive classification hierarchies.

To further enhance the system's functionality, we meticulously rewrote the hashCode() and equals() methods. This meticulous process enables the deduplication of classification objects, preventing any redundant or duplicate data from cluttering the system. By implementing these methods with utmost care and precision, we have optimized the system's performance and streamlined the classification process.

### Merchandise
Completed the backend development of product creation and rich-text handling, which includes entering product information and editing/saving rich-text content. Additionally, implemented frontend functionalities for product search, listing, and detailed view, enabling users to conveniently browse and find product information.

Regarding integration with the FTP server, developed a Spring MVC file upload feature that allows users to upload files to the FTP server and perform read and processing operations on the uploaded files.

Furthermore, utilized file streams to read properties files, facilitating the reading and retrieval of configuration parameters from the files.

Lastly, implemented the conversion relationships between abstract POJOs (Persistent Objects), BOs (Business Objects), and VOs (View Objects) to enhance system scalability and flexibility while reducing code duplication and data transmission redundancy.

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
