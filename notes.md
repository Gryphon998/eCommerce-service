# Linux Environment Setup
## Tomcat config
Use `URIEncoding="UTF-8"` to unify the encoding.

## Vsftpd
Set `-anonymous_enable=YES` to turn on anonymous request.\
Configure firewall whitelist to bypass HTTP port `8080` and FTP ports `20`, `21`.

## Nginx
Connect local ip and custimized domain name `www.ecommerce.com` and `image.ecommerce.com`.\
Config reverse proxy:
* Monitor port 80 and domain name `www.ecommerce.com`, forward to Tomcat port 8080.
* Monitor port 80 and domain name `image.ecommerce.com`, forward to local source folders which is supported by Vsftpd.

Reverse proxy -
反向代理（Reverse Proxy）是一种网络服务器的配置，用于将客户端请求转发到内部服务器，然后将服务器的响应返回给客户端。它与常见的正向代理（Forward Proxy）相反，正向代理是为客户端提供代理服务，帮助客户端获取网络资源，而反向代理则为服务器提供代理服务，帮助服务器处理客户端请求。

## MySQL
Turn on the permission for `select`, `delete` and `create`.

# Database Table
All tables have `create_time` and `update_time` to support time-based analysis and the effiency of debugging.

## User Table
Entities - id, username, password, email, phone and etc.
* Use `id` as primary key with `AUTO_INCREMENT`. 
* To ensure there are no duplicate user names, `username` must be an unique key. In case of running in a distributed system, it can be done by setting it as `unique key` and `BTREE` format.
* In case of database leakage, it uses `MD5` to store `password`.

### BTREE
在 MySQL 数据库中，BTREE 是一种用于索引的数据结构，它被广泛用于优化数据库查询的性能。BTREE 索引是一种平衡树结构，通常是二叉树，用于加速数据库表的数据检索操作。
BTREE 索引在 MySQL 中的使用非常常见，它可以加速数据的查找操作，从而提高查询性能。当你在表的一个或多个列上创建了 BTREE 索引后，数据库系统会根据索引的值构建一棵平衡树，这个树的每个节点代表表中的一个数据行。通过树的结构，数据库可以更快速地定位和检索数据，而不必逐个扫描整个表。

### MD5
MD5（Message Digest Algorithm 5）是一种广泛使用的哈希函数，用于将任意长度的输入数据转换为固定长度（128位或16字节）的哈希值。MD5 最初由 Ronald Rivest 在1991年设计，用于产生数字摘要，用于校验数据的完整性和验证数据的一致性。
然而，随着时间的推移，发现了一些关于 MD5 安全性的漏洞，包括碰撞（Collision）攻击。碰撞是指两个不同的输入产生了相同的哈希值，这可能导致安全性问题，特别是在需要保护密码或验证数据完整性的场景下。由于这些漏洞，MD5 不再被认为是安全的哈希算法，不应再用于安全敏感的应用，如密码存储。
在现代应用中，推荐使用更安全的哈希算法，如SHA-256（Secure Hash Algorithm 256位版本）来代替 MD5，因为SHA-256具有更高的安全性和抗碰撞能力。在密码存储等需要安全性的场景下，还应结合使用盐（salt）等技术来提高密码保护的级别。

## Catogory Table
Entities - id, parent_id, name, status and etc.
* This table is a recusive table since there can be one catogory below another catogory.
* Use `parent_id` to record the father level `id`. Root level's `parent_id` is set as 0.

## Product Table
Entities - id, category_id, name, subtitle, main_image and etc.
* Use `main_image` to store the relative URL. The advantage is that after server migaration, it can still find the image with new URL origin.
* Use `decimal(20, 2)` type for price to indicate there are 18 digits integer and 2 digits decimals.

# Project Initialization
## MyBatis Generator
MyBatis generator is installed with Maven by adding configs in the `pom.xml`. It can automatically generate interactive database access objects based on the configurations, such as `pojo`, `dao` and `xml` files. 
* `pojo` files contain the entities of each MySQL tables.
* `dao` files declare interfaces based on the `xml` files and the interfaces can be called by the implementations of `service` files.
* `xml` files are the place to put real SQL codes to interactive with the database.

### generatorConfig.xml
A MyBatis generator config file to import constants from properties files for configuration, such as `jdbc` jar location, user name, password and etc.

### POJO
是“Plain Old Java Object”的缩写，意为“普通的旧Java对象”。这个术语用于描述一个简单的、不受特定框架或库限制的纯Java对象，它通常用于表示业务数据或领域对象。

### DAO
是数据访问对象（Data Access Object）的缩写。DAO 是一种设计模式，用于将数据存取操作（如数据库操作）与业务逻辑分离，从而提高代码的可维护性和灵活性。

### XML
Extensible Markup Language是一种用于描述数据的标记语言。它被设计用来在不同应用之间传输和存储数据，具有自我描述性和可扩展性的特点。XML 文件包含了有意义的标签和文本内容，这些标签可以自定义，从而适应不同的应用和数据结构。

### VO
是“Value Object”的缩写，也称为“数据传输对象”（DTO）。VO是一种特定类型的对象，用于封装一组相关的数据，通常是为了在不同层之间传递数据或封装查询结果。

## Spring
Spring is a comprehensive and widely used open-source Java framework for building enterprise-level applications. It provides various modules that address different aspects of application development, including dependency injection, aspect-oriented programming, transaction management, data access, and more. Spring's core concept is the Inversion of Control (IoC), where the framework manages object instantiation and dependency injection, allowing for loosely coupled and easily testable code.

### web.xml
* Use `<filter>` to forward all requests to `CharacterEncodingFilter` to ensure using `UTF-8`.
* Use `<lisener>` which inherited from `eventlistener` to monitor the start and stop of web container.
* Use `<servlet>` to forward all `*.do` requests to `dispatcherServlet` for controllers.

### applicationContext-datasource.xml
* Use `propertyConfigurer` to let Spring configuration import constants from properties files, such as url, username and password.
* Similar as above, `dataSource` can also read constants from properties for DBCP and SQl configs, such as maximum connections and idle time.
* SQL configs are recorded in `sqlSessionFactory`, such as maximum connections, the config of connecting all mappers, how Mybatis implement SQL.
* `mapperScannerConfigurer` is used to scan all `dao` files to provide interfaces.

## SpringMVC
Spring MVC is a module within the Spring Framework that focuses on building web applications following the Model-View-Controller architectural pattern. It separates the application into three main components:

* Model: Represents the data and business logic of the application.
* View: Handles the presentation and user interface.
* Controller: Manages the communication between the model and the view, handling user requests and controlling the application flow.

### dispatcher-servlet.xml
* Use `component-scan` to scan all controllers.
* Use JackonHttpMessageConverter` in ``message-converters` and set default header of the response as `application/json;charset=UTF-8`.

# Call Flow
1. End user visit `http://localhost:8080/user/select.do?productId=100`
2. The request is forwared to `dispatcherServlet` and mapped to specific controller:
    ```
    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse<CartVo> select(HttpSession session,Integer productId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(),productId,Const.Cart.CHECKED);
    }
    ```
    because of `@RequestMapping("select.do")`
    `productId` of `select()` is mapped as `100` from the request URL parameter. Inside the function, the user is get from `session`. Then `user.getId()`, `productId` and an enum value `Const.Cart.CHECKED` are passed to `iCartService.selectOrUnSelect`.
3. `iCartService.selectOrUnSelect` is an interface under `service`. The implementation if at `server/impl` `CartServiceImpl.java`:
    ```
    public ServerResponse<CartVo> selectOrUnSelect (Integer userId,Integer productId,Integer checked){
        cartMapper.checkedOrUncheckedProduct(userId,productId,checked);
        return this.list(userId);
    }
    ```
    In `selectOrUnSelect()`, it calls `cartMapper.checkedOrUncheckedProduct()` from `CartMapper.java` under `dao` folder to interact with database.

4. The `checkedOrUncheckedProduct()` of `dao` is just an interface, the implementation is in the corresponding `CartMapper.xml` under `resources` folder:
    ```
    <update id="checkedOrUncheckedProduct" parameterType="map">
      UPDATE  mmall_cart
      set checked = #{checked},
      update_time = now()
      where user_id = #{userId}
      <if test="productId != null">
        and product_id = #{productId}
      </if>
    </update>
    ```
    which contains the real SQL codes.
5. After calling `cartMapper.checkedOrUncheckedProduct()`, `selectOrUnSelect()` returns the result of `this.list(userId)` which is defined as:
    ```
    public ServerResponse<CartVo> list (Integer userId){
        CartVo cartVo = this.getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }
    ```
    The functionality of this method is to call `dao` and `xml` to check user's shopping cart information with `userId` and returns a `ServerResponse<CartVo>`.
6. `CartVo` is defined under `vo` folder and contains the information of a shopping cart that required by front-end:
    ```
    private List<CartProductVo> cartProductVoList;
    private BigDecimal cartTotalPrice;
    private Boolean allChecked;
    private String imageHost;
    ```
7. `ServerResponse` is a reusable service response class, which is inherited from `Serialize` class and uses generic type `T`. It uses `Jackson` to serialize `CartVo` to a json and put it into the http response with the default header set in `dispatcher-servlet.xml`.
