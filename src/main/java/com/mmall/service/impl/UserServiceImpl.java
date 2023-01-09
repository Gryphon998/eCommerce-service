package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
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
        int resultCount = userMapper.checkUserName(username);

        if (resultCount == 0){
            return ServerResponse.creatByErrorMessage("The user name does not correspond to any account at Madas.com.");
        }

        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, md5Password);

        if (user == null){
            return ServerResponse.creatByErrorMessage("The password does not correspond to any account at Madas.com.");
        }

        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.creatBySuccess("Successfully login", user);
    }

    public ServerResponse<String> register(User user){
        ServerResponse validResponse = this.checkValid(user.getUsername(), Const.USERNAME);
        if (!validResponse.isSuccess()){
            return validResponse;
        }
        validResponse = this.checkValid(user.getEmail(), Const.EMAIL);
        if (!validResponse.isSuccess()){
            return validResponse;
        }

        user.setRole(Const.Role.ROLE_CUSTOMER);
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount= userMapper.insert(user);

        if (resultCount == 0){
            return ServerResponse.creatByErrorMessage("Register Failed");
        }

        return ServerResponse.creatByErrorMessage("Successfully Registered");
    }

    public ServerResponse securityQuestion(String username){
        ServerResponse validResponse = this.checkValid(username, Const.USERNAME);
        if (validResponse.isSuccess()){
            return ServerResponse.creatByErrorMessage("The username doesn't exist");
        }
        String question = userMapper.fetchQuestionByUsername(username);

        if (StringUtils.isNotBlank(question)){
            return ServerResponse.creatBySuccess(question);
        }

        return ServerResponse.creatByErrorMessage("Security question is not set");
    }

    public ServerResponse<String> checkValid(String str, String type){
        if (StringUtils.isNotBlank(type)){
            if (Const.USERNAME.equals(type)){
                int resultCount = userMapper.checkUserName(str);

                if (resultCount > 0){
                    return ServerResponse.creatByErrorMessage("The user name is already exist at Madas.com.");
                }
            }
            if (Const.EMAIL.equals(type)){
                int resultCount = userMapper.checkEmail(str);

                if (resultCount > 0){
                    return ServerResponse.creatByErrorMessage("The user Email is already exist at Madas.com.");
                }
            }
        } else {
            return ServerResponse.creatByErrorMessage("Wrong Parameter");
        }
        return ServerResponse.creatBySuccessMessage("Verify succeeded");
    }

    public ServerResponse<String> securityAnswerCheck(String username, String question, String answer){
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if (resultCount > 0){
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username, forgetToken);
            return ServerResponse.creatBySuccess(forgetToken);
        }
        return ServerResponse.creatByErrorMessage("The answer does not correspond to your security question.");
    }

    public ServerResponse<String> forgetResetPassword(String username, String newPassword, String forgetToken) {
        if (StringUtils.isBlank(forgetToken)) {
            return ServerResponse.creatByErrorMessage("Token is empty");
        }
        ServerResponse validResponse = this.checkValid(username, Const.USERNAME);

        if (validResponse.isSuccess()) {
            return ServerResponse.creatByErrorMessage("the account does not exist");
        }

        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);

        if (StringUtils.isBlank(token)) {
            return ServerResponse.creatByErrorMessage("Token invalid or expired");
        }

        if (StringUtils.equals(forgetToken, token)) {
            String md5Password = MD5Util.MD5EncodeUtf8(newPassword);
            int rowCount = userMapper.updatePasswordByUsernameInt(username, md5Password);

            if (rowCount > 0) {
                return ServerResponse.creatBySuccessMessage("Your password is successfully updated");
            }
        } else {
            return ServerResponse.creatByErrorMessage("Wrong token, please try again");
        }

        return ServerResponse.creatByErrorMessage("Failed to update the password, please try again");
    }

    public  ServerResponse<String> resetPassword(String password, String newPassword, User user){
        int resultCount = userMapper.checkPassword(password, user.getId());

        if (resultCount == 0){
            return ServerResponse.creatByErrorMessage("Please confirm your current password");
        }

        user.setPassword(MD5Util.MD5EncodeUtf8(newPassword));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if (updateCount == 0){
            return ServerResponse.creatByErrorMessage("Update error");
        }

        return ServerResponse.creatBySuccessMessage("update password success");
    }

    public ServerResponse<User> updateInfo(User user){
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(), user.getId());

        if (resultCount > 0){
            ServerResponse.creatByErrorMessage("The email is already registered");
        }

        User updatedUser = new User();
        updatedUser.setEmail(user.getEmail());
        updatedUser.setId(user.getId());
        updatedUser.setPhone(user.getPhone());
        updatedUser.setQuestion(user.getQuestion());
        updatedUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updatedUser);
        if (updateCount > 0){
            return ServerResponse.creatBySuccess("Your information id successfully updated", updatedUser);
        }

        return ServerResponse.creatByErrorMessage("update failed");
    }

    public ServerResponse<User> showUserInfo(Integer userid){
        User currentUser = userMapper.selectByPrimaryKey(userid);
        if (currentUser == null){
            return ServerResponse.creatByErrorMessage("Login error");
        }

        currentUser.setPassword(StringUtils.EMPTY);
        return ServerResponse.creatBySuccess(currentUser);
    }

}
