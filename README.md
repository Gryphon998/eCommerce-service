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
  <img src="https://github.com/Gryphon998/eCommerce-service/assets/41406456/153cd4b7-ef67-4aec-a2dc-58ae9bf05cb7">
</p>

* The system architecture involves Nginx as the initial receiver of user requests. 
* Nginx then forwards these requests to Tomcat, which hosts the Spring MVC application.
* Within the Spring MVC application, the Spring Framework handles dependency management and executes business logic.
* MyBatis is responsible for handling database operations and connects to MySQL for data storage and retrieval.
* The web application relies on an FTP server to store static content, which can be served to end users by Nginx.

## Interfaces
<p align="center">
  <img src="https://github.com/Gryphon998/eCommerce-service/assets/41406456/36a63df9-3418-45a0-a6ae-833709559fd1">
</p>

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

### Shopping Cart
Leverage a set of meticulously designed and reusable core methods to effectively tackle the challenge of precision loss in floating-point calculations encountered during commercial operations. By implementing these methods, the module ensures accurate and reliable results, eliminating the risk of significant data discrepancies caused by inherent limitations of floating-point arithmetic.

The utilization of these highly reusable core methods not only streamlines development efforts but also guarantees consistent and precise calculations across various business processes. By encapsulating complex floating-point operations within these methods, the system promotes code reuse, modularity, and maintainability, leading to more efficient and robust commercial operations.

### Address
Developed a comprehensive set of features to enable create, read, update, and delete operations (CRUD) within the system. These functionalities were seamlessly integrated with the powerful object binding capabilities provided by Spring MVC's data binding mechanism. This integration facilitated smooth and efficient data exchange between the frontend and backend components, enhancing the overall user experience.

Furthermore, special attention was given to handling automatically generated primary keys in the context of MyBatis. By implementing effective strategies and techniques, the system ensures the proper generation and management of primary keys, thereby reinforcing data integrity and preventing potential security vulnerabilities related to horizontal privilege escalation.

Through meticulous implementation and rigorous testing, these enhancements solidify the system's resilience against horizontal privilege escalation vulnerabilities. By addressing these potential risks, the system establishes a secure foundation, ensuring that unauthorized access and data manipulation are prevented, and safeguarding the integrity and confidentiality of the system's data.

### Order
Successfully implemented a comprehensive set of features that enable users to fill in order information, generate orders, and establish seamless connections between the product module, shopping cart module, and user module. 

Throughout the implementation, a strong emphasis was placed on ensuring the system's security by proactively mitigating common vulnerabilities, including the risks associated with horizontal privilege escalation and vertical privilege escalation, which are commonly found in business logic. By addressing these security concerns, the system maintains the integrity of user data, safeguards against unauthorized access, and upholds the confidentiality of sensitive information.

## Developmenp Environment
| Name  | Version |
| ------------- | ------------- |
| Ubuntu | 22.04 LTS |
| Intellij IDEA | 2023.1.3 |
| JDK  | 1.8  |
| VMware Workstation | 17 |
| Restlet | N/A |
