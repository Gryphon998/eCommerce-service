package com.ecommerce.controller.backend;

import com.ecommerce.common.Const;
import com.ecommerce.common.ResponseCode;
import com.ecommerce.common.ServerResponse;
import com.ecommerce.pojo.Product;
import com.ecommerce.pojo.User;
import com.ecommerce.service.IFileService;
import com.ecommerce.service.IProductService;
import com.ecommerce.service.IUserService;
import com.ecommerce.util.PropertiesUtil;
import com.ecommerce.vo.ProductDetailVo;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller class for managing products in the backend.
 * Handles requests related to adding, updating, and retrieving product information.
 * This class is responsible for handling the backend operations of the product management.
 */
@Controller
@RequestMapping("/manage/product")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private IFileService iFileService;

    /**
     * API for adding or updating a product.
     * If the product id is null, it represents an update operation. If the product id is not null, it represents an add operation.
     *
     * @param session The HttpSession object
     * @param product The Product object to be added or updated
     * @return ServerResponse object containing a String message
     */
    @ResponseBody
    @RequestMapping(value = "saveOrUpdateProduct.do", method = RequestMethod.POST)
    public ServerResponse<String> saveOrUpdateProduct(HttpSession session, Product product) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessageCode(ResponseCode.NEED_LOGIN.getCode(), "Please log in first.");
        }
        // Check if the user is an administrator
        ServerResponse<String> checkRoleResult = iUserService.checkUserRole(user);
        if (checkRoleResult.isSuccess()) {
            // Add or update product information
            return iProductService.saveOrUpdateProduct(product);
        } else {
            return ServerResponse.createByErrorMessage("Sorry, you do not have administrator privileges.");
        }
    }

    /**
     * API for modifying the sales status of a product.
     *
     * @param session   The HttpSession object
     * @param productId The ID of the product
     * @param state     The new sales status of the product
     * @return ServerResponse object
     */
    @ResponseBody
    @RequestMapping(value = "setSaleStatus.do", method = RequestMethod.POST)
    public ServerResponse setSaleStatus(HttpSession session, Integer productId, Integer state) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessageCode(ResponseCode.NEED_LOGIN.getCode(), "Please log in first.");
        }
        // Check if the user is an administrator
        ServerResponse<String> checkRoleResult = iUserService.checkUserRole(user);
        if (checkRoleResult.isSuccess()) {
            // Modify the sales status of the product
            return iProductService.setSaleStatus(productId, state);
        } else {
            return ServerResponse.createByErrorMessage("Sorry, you do not have administrator privileges.");
        }
    }

    /**
     * API for retrieving product details.
     *
     * @param session   The HttpSession object
     * @param productId The ID of the product
     * @return ServerResponse object containing a ProductDetailVo object
     */
    @ResponseBody
    @RequestMapping(value = "getDetail.do", method = RequestMethod.POST)
    public ServerResponse<ProductDetailVo> getDetail(HttpSession session, Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessageCode(ResponseCode.NEED_LOGIN.getCode(), "Please log in first.");
        }
        // Check if the user is an administrator
        ServerResponse<String> checkRoleResult = iUserService.checkUserRole(user);
        if (checkRoleResult.isSuccess()) {
            // Retrieve product details
            return iProductService.manageProductDetail(productId);
        } else {
            return ServerResponse.createByErrorMessage("Sorry, you do not have administrator privileges.");
        }
    }

    /**
     * API for retrieving a list of products.
     *
     * @param session  The HttpSession object
     * @param pageNum  The page number
     * @param pageSize The number of products to be displayed per page
     * @return ServerResponse object containing a list of products
     */
    @ResponseBody
    @RequestMapping(value = "getList.do", method = RequestMethod.POST)
    public ServerResponse getList(HttpSession session,
                                  @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                  @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessageCode(ResponseCode.NEED_LOGIN.getCode(), "Please log in first.");
        }
        // Check if the user is an administrator
        ServerResponse<String> checkRoleResult = iUserService.checkUserRole(user);
        if (checkRoleResult.isSuccess()) {
            // Retrieve a list of products
            return iProductService.manageProductList(pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorMessage("Sorry, you do not have administrator privileges.");
        }
    }

    /**
     * API for searching products by name or ID and returning a list of matching products.
     *
     * @param session     The HttpSession object
     * @param productName The name of the product
     * @param productId   The ID of the product
     * @param pageNum     The page number
     * @param pageSize    The number of products to be displayed per page
     * @return ServerResponse object containing a list of matching products
     */
    @ResponseBody
    @RequestMapping(value = "productSearch.do", method = RequestMethod.POST)
    public ServerResponse productSearch(HttpSession session, String productName, int productId,
                                        @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessageCode(ResponseCode.NEED_LOGIN.getCode(), "Please log in first.");
        }
        // Check if the user is an administrator
        ServerResponse<String> checkRoleResult = iUserService.checkUserRole(user);
        if (checkRoleResult.isSuccess()) {
            // Search for products by name or ID
            return iProductService.productSearch(productName, productId, pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorMessage("Sorry, you do not have administrator privileges.");
        }
    }

    /**
     * API for uploading a product image.
     *
     * @param session The HttpSession object
     * @param file    The MultipartFile object representing the image file
     * @param request The HttpServletRequest object
     * @return ServerResponse object containing a map with the URL of the uploaded image
     */
    @ResponseBody
    @RequestMapping(value = "upload.do", method = RequestMethod.POST)
    public ServerResponse upload(HttpSession session, @RequestParam(value = "upload_file", required = false) MultipartFile file, HttpServletRequest request) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessageCode(ResponseCode.NEED_LOGIN.getCode(), "Please log in first.");
        }
        // Check if the user is an administrator
        ServerResponse<String> checkRoleResult = iUserService.checkUserRole(user);
        if (checkRoleResult.isSuccess()) {
            // Upload the image file
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file, path);
            if (StringUtils.isBlank(targetFileName)) {
                return ServerResponse.createByErrorMessage("Failed to upload image.");
            }
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;

            Map<String, String> fileMap = new HashMap<>();
            fileMap.put("uri", targetFileName);
            fileMap.put("url", url);

            return ServerResponse.createBySuccess(fileMap);
        } else {
            return ServerResponse.createByErrorMessage("Sorry, you do not have administrator privileges.");
        }
    }

    /**
     * API for rich text editor image upload.
     *
     * @param session  The HttpSession object
     * @param file     The MultipartFile object representing the image file
     * @param request  The HttpServletRequest object
     * @param response The HttpServletResponse object
     * @return A Map containing the URL of the uploaded image and more information
     */
    @ResponseBody
    @RequestMapping(value = "richtextImgUpload.do", method = RequestMethod.POST)
    public Map richtextImgUpload(HttpSession session, @RequestParam(value = "upload_file", required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> resultMap = Maps.newHashMap();
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            resultMap.put("success", false);
            resultMap.put("msg", "Please log in first.");
            return resultMap;
        }
        // Check if the user is an administrator
        ServerResponse<String> checkRoleResult = iUserService.checkUserRole(user);
        if (checkRoleResult.isSuccess()) {
            // Upload the image file
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file, path);
            if (StringUtils.isBlank(targetFileName)) {
                resultMap.put("success", false);
                resultMap.put("msg", "Failed to upload image.");
                return resultMap;
            }
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;

            resultMap.put("success", true);
            resultMap.put("msg", "Image uploaded successfully.");
            resultMap.put("file_path", url);

            response.addHeader("Access-Control-Allow-Headers", "X-File-Name");

            return resultMap;
        } else {
            resultMap.put("success", false);
            resultMap.put("msg", "Sorry, you do not have administrator privileges.");
            return resultMap;
        }
    }
}
