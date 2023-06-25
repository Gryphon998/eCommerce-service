package com.ecommerce.service.impl;

import com.ecommerce.common.Const;
import com.ecommerce.common.ServerResponse;
import com.ecommerce.common.TokenCache;
import com.ecommerce.dao.UserMapper;
import com.ecommerce.pojo.User;
import com.ecommerce.service.IUserService;
import com.ecommerce.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("iUserService")
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        // Check if the username exists in the database
        int resultCount = userMapper.checkUserName(username);

        if (resultCount == 0) {
            // The username does not correspond to any account
            return ServerResponse.creatByErrorMessage("The username does not correspond to any account.");
        }

        // Encrypt the password
        String md5Password = MD5Util.MD5EncodeUtf8(password);

        // Retrieve the user based on the username and encrypted password
        User user = userMapper.selectLogin(username, md5Password);

        if (user == null) {
            // The password does not match the account
            return ServerResponse.creatByErrorMessage("The password does not correspond to any account.");
        }

        // Remove the password from the user object
        user.setPassword(StringUtils.EMPTY);

        return ServerResponse.creatBySuccess("Successfully logged in", user);
    }

    public ServerResponse<String> register(User user) {
        // Check if the username is valid
        ServerResponse validResponse = this.checkValid(user.getUsername(), Const.USERNAME);
        if (!validResponse.isSuccess()) {
            return validResponse;
        }

        // Check if the email is valid
        validResponse = this.checkValid(user.getEmail(), Const.EMAIL);
        if (!validResponse.isSuccess()) {
            return validResponse;
        }

        user.setRole(Const.Role.ROLE_CUSTOMER);

        // Encrypt the password
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int resultCount = userMapper.insert(user);

        if (resultCount == 0) {
            return ServerResponse.creatByErrorMessage("Register failed");
        }

        return ServerResponse.creatByErrorMessage("Successfully registered");
    }

    public ServerResponse securityQuestion(String username) {
        // Check if the username is valid
        ServerResponse validResponse = this.checkValid(username, Const.USERNAME);
        if (validResponse.isSuccess()) {
            // The username does not exist
            return ServerResponse.creatByErrorMessage("The username doesn't exist");
        }

        // Retrieve the security question for the given username
        String question = userMapper.fetchQuestionByUsername(username);

        if (StringUtils.isNotBlank(question)) {
            // Security question found
            return ServerResponse.creatBySuccess(question);
        }

        // Security question is not set for the user
        return ServerResponse.creatByErrorMessage("Security question is not set");
    }

    public ServerResponse<String> checkValid(String str, String type) {
        if (StringUtils.isNotBlank(type)) {
            if (Const.USERNAME.equals(type)) {
                // Check if the username already exists
                int resultCount = userMapper.checkUserName(str);

                if (resultCount > 0) {
                    return ServerResponse.creatByErrorMessage("The username already exists.");
                }
            }
            if (Const.EMAIL.equals(type)) {
                // Check if the email already exists
                int resultCount = userMapper.checkEmail(str);

                if (resultCount > 0) {
                    return ServerResponse.creatByErrorMessage("The user email already exists.");
                }
            }
        } else {
            return ServerResponse.creatByErrorMessage("Wrong parameter");
        }

        return ServerResponse.creatBySuccessMessage("Verification succeeded");
    }

    public ServerResponse<String> securityAnswerCheck(String username, String question, String answer) {
        // Check if the provided answer matches the security question for the given username
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if (resultCount > 0) {
            // Generate a forget token and store it in the cache
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, forgetToken);
            return ServerResponse.creatBySuccess(forgetToken);
        }

        return ServerResponse.creatByErrorMessage("The answer does not correspond to your security question.");
    }

    public ServerResponse<String> forgetResetPassword(String username, String newPassword, String forgetToken) {
        if (StringUtils.isBlank(forgetToken)) {
            return ServerResponse.creatByErrorMessage("Token is empty");
        }

        // Check if the username exists
        ServerResponse validResponse = this.checkValid(username, Const.USERNAME);

        if (validResponse.isSuccess()) {
            return ServerResponse.creatByErrorMessage("The account does not exist");
        }

        // Retrieve the stored token from the cache
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);

        if (StringUtils.isBlank(token)) {
            return ServerResponse.creatByErrorMessage("Token is invalid or expired");
        }

        if (StringUtils.equals(forgetToken, token)) {
            // Token is valid, update the password
            String md5Password = MD5Util.MD5EncodeUtf8(newPassword);
            int rowCount = userMapper.updatePasswordByUsernameInt(username, md5Password);

            if (rowCount > 0) {
                return ServerResponse.creatBySuccessMessage("Your password has been successfully updated");
            }
        } else {
            return ServerResponse.creatByErrorMessage("Wrong token, please try again");
        }

        return ServerResponse.creatByErrorMessage("Failed to update the password, please try again");
    }

    public ServerResponse<String> checkValid(String str, String type) {
        if (StringUtils.isNotBlank(type)) {
            if (Const.USERNAME.equals(type)) {
                // Check if the username already exists
                int resultCount = userMapper.checkUserName(str);

                if (resultCount > 0) {
                    return ServerResponse.creatByErrorMessage("The username already exists.");
                }
            }
            if (Const.EMAIL.equals(type)) {
                // Check if the email already exists
                int resultCount = userMapper.checkEmail(str);

                if (resultCount > 0) {
                    return ServerResponse.creatByErrorMessage("The user email already exists.");
                }
            }
        } else {
            return ServerResponse.creatByErrorMessage("Wrong parameter");
        }

        // Verification succeeded
        return ServerResponse.creatBySuccessMessage("Verification succeeded");
    }

    public ServerResponse<String> securityAnswerCheck(String username, String question, String answer) {
        // Check if the provided answer matches the security question for the given username
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if (resultCount > 0) {
            // Generate a forget token and store it in the cache
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, forgetToken);
            return ServerResponse.creatBySuccess(forgetToken);
        }

        // The provided answer does not match the security question
        return ServerResponse.creatByErrorMessage("The answer does not correspond to your security question.");
    }

    public ServerResponse<String> forgetResetPassword(String username, String newPassword, String forgetToken) {
        if (StringUtils.isBlank(forgetToken)) {
            return ServerResponse.creatByErrorMessage("Token is empty");
        }

        // Check if the username exists
        ServerResponse validResponse = this.checkValid(username, Const.USERNAME);

        if (validResponse.isSuccess()) {
            return ServerResponse.creatByErrorMessage("The account does not exist");
        }

        // Retrieve the stored token from the cache
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);

        if (StringUtils.isBlank(token)) {
            return ServerResponse.creatByErrorMessage("Token is invalid or expired");
        }

        if (StringUtils.equals(forgetToken, token)) {
            // Token is valid, update the password
            String md5Password = MD5Util.MD5EncodeUtf8(newPassword);
            int rowCount = userMapper.updatePasswordByUsernameInt(username, md5Password);

            if (rowCount > 0) {
                return ServerResponse.creatBySuccessMessage("Your password has been successfully updated");
            }
        } else {
            return ServerResponse.creatByErrorMessage("Wrong token, please try again");
        }

        return ServerResponse.creatByErrorMessage("Failed to update the password, please try again");
    }
}
