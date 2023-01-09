package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
    int checkUserName(String username);
    int checkEmail(String email);
    int checkEmailByUserId(@Param("email")String email, @Param("id")Integer id);
    int checkAnswer(@Param("username")String username,@Param("question")String question,@Param("answer")String answer);
    int checkPassword(@Param("password") String password, @Param("id")Integer id);
    String fetchQuestionByUsername(String username);
    User selectLogin(@Param("username") String username, @Param("password")String password);
    int updatePasswordByUsernameInt(@Param("username")String username, @Param("newPassword")String newPassword);
}