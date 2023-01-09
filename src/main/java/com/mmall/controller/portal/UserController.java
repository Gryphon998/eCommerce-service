package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping
public class UserController {
    @Autowired
    private IUserService iUserService;

//    用户登录
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String  password, HttpSession session){
        ServerResponse<User> response = iUserService.login(username,password);
            if (response.isSuccess()){
                session.setAttribute(Const.CURRENT_USER,response.getData());
            }

            return  response;
    }

    @RequestMapping(value = "logout.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.creatBySuccess();
    }

    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user){
        return iUserService.register(user);
    }

    @RequestMapping(value = "check_valid.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str, String type){
        return iUserService.checkValid(str, type);
    }

    @RequestMapping(value = "get_user_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);

        if (user != null){
            return ServerResponse.creatBySuccess(user);
        }

        return ServerResponse.creatByErrorMessage("Can not find user information");
    }

    @RequestMapping(value = "fetch_security_question.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> fetchSecurityQuestion(String username){
        return iUserService.securityQuestion(username);
    }

    @RequestMapping(value = "security_answer_check.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> securityAnswerCheck(String username, String question, String answer){
        return iUserService.securityAnswerCheck(username, question, answer);
    }

    @RequestMapping(value = "forget_reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetResetPassword(String username, String newPassword, String forgetToken){
        return iUserService.forgetResetPassword(username, newPassword, forgetToken);
    }

    @RequestMapping(value = "reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpSession session, String password, String newPassword){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.creatByErrorMessage("Please login first");
        }
        return iUserService.resetPassword(password, newPassword, user);
    }

    @RequestMapping(value = "update_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateInfo(HttpSession session, User user){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null){
            return ServerResponse.creatByErrorMessage("Please login first");
        }

        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse<User> response = iUserService.updateInfo(user);

        if (response.isSuccess()){
            response.getData().setUsername(currentUser.getUsername());
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }

        return  response;
    }

    @RequestMapping(value = "show_user_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> showUserInfo(HttpSession session){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null){
            return ServerResponse.creatByErrorMessageCode(ResponseCode.NEED_LOGIN.getCode(), "Please login first, status=10");
        }

        return iUserService.showUserInfo(currentUser.getId());
    }
}