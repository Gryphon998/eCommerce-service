package com.ecommerce.controller.backend;

import com.ecommerce.common.Const;
import com.ecommerce.common.ServerResponse;
import com.ecommerce.pojo.User;
import com.ecommerce.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by wangshufu on 2017/7/30.
 * <p>
 * User management controller for the backend.
 */
@Controller
@RequestMapping("/manage/user")
public class UserManageController {

    @Autowired
    IUserService iUserService;

    /**
     * API for user login.
     *
     * @param session  HttpSession object
     * @param username Username of the user
     * @param password Password of the user
     * @return ServerResponse object with the result of the login operation
     */
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(HttpSession session, String username, String password) {
        ServerResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()) {
            if (response.getData().getRole() != Const.Role.ROLE_ADMIN) {
                return ServerResponse.createByErrorMessage("Sorry, you are not an administrator user");
            } else {
                session.setAttribute(Const.CURRENT_USER, response.getData());
            }
        }
        return response;
    }
}