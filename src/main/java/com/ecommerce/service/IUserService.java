package com.ecommerce.service;

import com.ecommerce.common.ServerResponse;
import com.ecommerce.pojo.User;

public interface IUserService {
    ServerResponse<User> login(String username, String password);
    ServerResponse<String> register(User user);
    ServerResponse<String> checkValid(String str, String type);
    ServerResponse securityQuestion(String username);
    ServerResponse<String> securityAnswerCheck(String username, String question, String answer);
    ServerResponse<String> forgetResetPassword(String username, String newPassword, String forgetToken);
    ServerResponse<String> resetPassword(String password, String newPassword, User user);
    ServerResponse<User> updateInfo(User user);
    ServerResponse<User> showUserInfo(Integer userid);
}
