package com.ecommerce.controller.backend;

import com.ecommerce.common.Const;
import com.ecommerce.common.ResponseCode;
import com.ecommerce.common.ServerResponse;
import com.ecommerce.pojo.Category;
import com.ecommerce.pojo.User;
import com.ecommerce.service.ICategoryService;
import com.ecommerce.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;

    /**
     * API for adding a category.
     *
     * @param session      HttpSession object
     * @param categoryName Name of the category to be added
     * @param parentId     ID of the parent category (default value is 0 if not specified)
     * @return ServerResponse object with the result of the operation
     */
    @RequestMapping(value = "addCategory.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse addCategory(HttpSession session, String categoryName,
                                      @RequestParam(value = "parentId", defaultValue = "0") Integer parentId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessageCode(ResponseCode.NEED_LOGIN.getCode(), "Please log in first");
        }
// Check if the user is an administrator
        ServerResponse<String> checkRoleResult = iUserService.checkUserRole(user);
        if (checkRoleResult.isSuccess()) {
// Add the category
            return iCategoryService.addCategory(categoryName, parentId);
        } else {
            return ServerResponse.createByErrorMessage("Sorry, you do not have administrator privileges");
        }
    }

    /**
     * API for updating category information.
     *
     * @param session      HttpSession object
     * @param categoryName Updated name of the category
     * @param categoryId   ID of the category to be updated
     * @return ServerResponse object with the result of the operation
     */
    @RequestMapping(value = "updateCategoryInfo.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse updateCategoryInfo(HttpSession session, String categoryName, Integer categoryId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessageCode(ResponseCode.NEED_LOGIN.getCode(), "Please log in first");
        }
// Check if the user is an administrator
        ServerResponse<String> checkRoleResult = iUserService.checkUserRole(user);
        if (checkRoleResult.isSuccess()) {
// Update the category information
            return iCategoryService.updateCategoryInfo(categoryName, categoryId);
        } else {
            return ServerResponse.createByErrorMessage("Sorry, you do not have administrator privileges");
        }
    }

    /**
     * API for getting child categories of a specified parent category.
     *
     * @param session  HttpSession object
     * @param parentId ID of the parent category (default value is 0 if not specified)
     * @return ServerResponse object with the list of child categories
     */
    @RequestMapping(value = "getChildrenParallelCategory.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Category>> getChildrenParallelCategory(HttpSession session,
                                                                      @RequestParam(value = "parentId", defaultValue = "0") Integer parentId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessageCode(ResponseCode.NEED_LOGIN.getCode(), "Please log in first");
        }
// Check if the user is an administrator
        ServerResponse<String> checkRoleResult = iUserService.checkUserRole(user);
        if (checkRoleResult.isSuccess()) {
// Get the child categories
            return iCategoryService.getChildrenParallelCategory(parentId);
        } else {
            return ServerResponse.createByErrorMessage("Sorry, you do not have administrator privileges");
        }
    }

    /**
     * API for getting the category and its deep children categories.
     *
     * @param session    HttpSession object
     * @param categoryId ID of the category (default value is 0 if not specified)
     * @return ServerResponse object with the category and its deep children categories
     */
    @RequestMapping(value = "getCategoryAndDeepChildrenCategory.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getCategoryAndDeepChildrenCategory(HttpSession session, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessageCode(ResponseCode.NEED_LOGIN.getCode(), "User is not logged in, please log in");
        }
        if (iUserService.checkUserRole(user).isSuccess()) {
// Retrieve the ID of the current node and the recursive IDs of the child nodes
            return iCategoryService.selectCategoryAndChildrenById(categoryId);
        } else {
            return ServerResponse.createByErrorMessage("No permission to perform this operation, administrator privileges required");
        }
    }
}