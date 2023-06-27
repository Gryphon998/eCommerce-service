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
            return ServerResponse.createByErrorMessage("The username does not correspond to any account.");
        }

        // Encrypt the password
        String md5Password = MD5Util.MD5EncodeUtf8(password);

        // Retrieve the user based on the username and encrypted password
        User user = userMapper.selectLogin(username, md5Password);

        if (user == null) {
            // The password does not match the account
            return ServerResponse.createByErrorMessage("The password does not correspond to any account.");
        }

        // Remove the password from the user object
        user.setPassword(StringUtils.EMPTY);

        return ServerResponse.createBySuccess("Successfully logged in", user);
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
            return ServerResponse.createByErrorMessage("Register failed");
        }

        return ServerResponse.createByErrorMessage("Successfully registered");
    }

    public ServerResponse securityQuestion(String username) {
        // Check if the username is valid
        ServerResponse validResponse = this.checkValid(username, Const.USERNAME);
        if (validResponse.isSuccess()) {
            // The username does not exist
            return ServerResponse.createByErrorMessage("The username doesn't exist");
        }

        // Retrieve the security question for the given username
        String question = userMapper.fetchQuestionByUsername(username);

        if (StringUtils.isNotBlank(question)) {
            // Security question found
            return ServerResponse.createBySuccess(question);
        }

        // Security question is not set for the user
        return ServerResponse.createByErrorMessage("Security question is not set");
    }

    public ServerResponse<String> checkValid(String str, String type) {
        if (StringUtils.isNotBlank(type)) {
            if (Const.USERNAME.equals(type)) {
                // Check if the username already exists
                int resultCount = userMapper.checkUserName(str);

                if (resultCount > 0) {
                    return ServerResponse.createByErrorMessage("The username already exists.");
                }
            }
            if (Const.EMAIL.equals(type)) {
                // Check if the email already exists
                int resultCount = userMapper.checkEmail(str);

                if (resultCount > 0) {
                    return ServerResponse.createByErrorMessage("The user email already exists.");
                }
            }
        } else {
            return ServerResponse.createByErrorMessage("Wrong parameter");
        }

        return ServerResponse.createBySuccessMessage("Verification succeeded");
    }

    public ServerResponse<String> securityAnswerCheck(String username, String question, String answer) {
        // Check if the provided answer matches the security question for the given username
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if (resultCount > 0) {
            // Generate a forget token and store it in the cache
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }

        return ServerResponse.createByErrorMessage("The answer does not correspond to your security question.");
    }

    public ServerResponse<String> forgetResetPassword(String username, String newPassword, String forgetToken) {
        if (StringUtils.isBlank(forgetToken)) {
            return ServerResponse.createByErrorMessage("Token is empty");
        }

        // Check if the username exists
        ServerResponse validResponse = this.checkValid(username, Const.USERNAME);

        if (validResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("The account does not exist");
        }

        // Retrieve the stored token from the cache
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);

        if (StringUtils.isBlank(token)) {
            return ServerResponse.createByErrorMessage("Token is invalid or expired");
        }

        if (StringUtils.equals(forgetToken, token)) {
            // Token is valid, update the password
            String md5Password = MD5Util.MD5EncodeUtf8(newPassword);
            int rowCount = userMapper.updatePasswordByUsernameInt(username, md5Password);

            if (rowCount > 0) {
                return ServerResponse.createBySuccessMessage("Your password has been successfully updated");
            }
        } else {
            return ServerResponse.createByErrorMessage("Wrong token, please try again");
        }

        return ServerResponse.createByErrorMessage("Failed to update the password, please try again");
    }

    @Override
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user) {
        if (StringUtils.isBlank(passwordNew)) {
            return ServerResponse.createByErrorMessage("New password cannot be empty");
        }
        // To prevent horizontal escalation, passwordOld needs to be verified for this user specifically.
        // If we don't specify that it belongs to this user and directly use passwordOld for the query,
        // we may get multiple results because multiple users might have the same password.
        System.out.println("user.getId()=" + user.getId());
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld), user.getId());
        if (resultCount > 0) {
            // Reset the new password
            user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
            int insertSelective = userMapper.updateByPrimaryKeySelective(user);
            if (insertSelective > 0) {
                return ServerResponse.createBySuccessMessage("Password modified successfully");
            }
        } else {
            return ServerResponse.createByErrorMessage("Old password is incorrect");
        }
        return ServerResponse.createByErrorMessage("Failed to modify password");
    }

    @Override
    public ServerResponse<User> updateInfo(User user) {
        // Username cannot be changed
        // When updating information, we also need to verify the email to check if it has been used by someone else
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(), user.getId());
        if (resultCount == 0) {
            User updateUser = new User();
            updateUser.setId(user.getId());
            updateUser.setEmail(user.getEmail());
            updateUser.setPhone(user.getPhone());
            updateUser.setQuestion(user.getQuestion());
            updateUser.setAnswer(user.getAnswer());
            int updateResult = userMapper.updateByPrimaryKeySelective(updateUser);
            if (updateResult > 0) {
                return ServerResponse.createBySuccess("Personal information updated successfully", updateUser);
            } else {
                return ServerResponse.createByErrorMessage("Failed to update information");
            }
        } else {
            return ServerResponse.createByErrorMessage("Sorry, your email is already in use. Please try a different email");
        }
    }

    @Override
    public ServerResponse<User> showUserInfo(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return ServerResponse.createByErrorMessage("Failed to retrieve personal information");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    /**
     * Verify if the user is an administrator
     *
     * @param user
     * @return
     */
    @Override
    public ServerResponse<String> checkUserRole(User user) {
        if (user != null && Const.Role.ROLE_ADMIN == user.getRole()) {
            return ServerResponse.createBySuccessMessage("Verification successful. User is an administrator");
        }
        return ServerResponse.createByErrorMessage("Verification failed");
    }
}
