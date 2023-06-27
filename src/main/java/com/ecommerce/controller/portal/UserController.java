package com.ecommerce.controller.portal;

import com.ecommerce.common.Const;
import com.ecommerce.common.ResponseCode;
import com.ecommerce.common.ServerResponse;
import com.ecommerce.pojo.User;
import com.ecommerce.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;

    /**
     * User login API
     *
     * @param username User's username
     * @param password User's password
     * @param session  HttpSession object to store user's session
     * @return ServerResponse containing User object if login is successful
     */
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session) {
        ServerResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return response;
    }

    /**
     * User logout API
     *
     * @param session HttpSession object to invalidate user's session
     * @return ServerResponse indicating success
     */
    @RequestMapping(value = "logout.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session) {
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    /**
     * User registration API
     *
     * @param user User object containing registration details
     * @return ServerResponse indicating success
     */
    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user) {
        return iUserService.register(user);
    }

    /**
     * Check if a given value is valid for a specific type
     *
     * @param str  The value to be checked
     * @param type The type to check against
     * @return ServerResponse indicating if the value is valid
     */
    @RequestMapping(value = "check_valid.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str, String type) {
        return iUserService.checkValid(str, type);
    }

    /**
     * Get user information API
     *
     * @param session HttpSession object to retrieve user's session
     * @return ServerResponse containing User object if user is logged in
     */
    @RequestMapping(value = "get_user_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null) {
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("Cannot find user information");
    }

    /**
     * Get security question for password recovery API
     *
     * @param username User's username
     * @return ServerResponse containing the security question
     */
    @RequestMapping(value = "forget_get_question.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username) {
        return iUserService.securityQuestion(username);
    }

    /**
     * Check the answer to the security question for password recovery API
     *
     * @param username User's username
     * @param question Security question
     * @param answer   User's answer to the security question
     * @return ServerResponse indicating if the answer is correct
     */
    @RequestMapping(value = "forget_check_answer.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {
        return iUserService.securityAnswerCheck(username, question, answer);
    }

    /**
     * Reset password after successful security question verification API
     *
     * @param username    User's username
     * @param passwordNew New password to be set
     * @param forgetToken Forget token for validation
     * @return ServerResponse indicating if the password reset was successful
     */
    @RequestMapping(value = "forget_reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        return iUserService.forgetResetPassword(username, passwordNew, forgetToken);
    }

    /**
     * Reset password for the currently logged-in user API
     *
     * @param session     HttpSession object to retrieve user's session
     * @param passwordOld Old password
     * @param passwordNew New password to be set
     * @return ServerResponse indicating if the password reset was successful
     */
    @RequestMapping(value = "reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpSession session, String passwordOld, String passwordNew) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage("Please login first");
        }
        return iUserService.resetPassword(passwordOld, passwordNew, user);
    }

    /**
     * Update user information for the currently logged-in user API
     *
     * @param session HttpSession object to retrieve user's session
     * @param user    User object containing updated information
     * @return ServerResponse containing the updated User object
     */
    @RequestMapping(value = "update_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateInformation(HttpSession session, User user) {
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null) {
            return ServerResponse.createByErrorMessage("Please login first");
        }
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse<User> response = iUserService.updateInfo(user);
        if (response.isSuccess()) {
            response.getData().setUsername(currentUser.getUsername());
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return response;
    }

    /**
     * Get user information for the currently logged-in user API
     *
     * @param session HttpSession object to retrieve user's session
     * @return ServerResponse containing the User object
     */
    @RequestMapping(value = "get_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getInformation(HttpSession session) {
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null) {
            return ServerResponse.createByErrorMessageCode(ResponseCode.NEED_LOGIN.getCode(), "Please login first, status=10");
        }
        return iUserService.showUserInfo(currentUser.getId());
    }
}
