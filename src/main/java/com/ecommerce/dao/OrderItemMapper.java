package com.ecommerce.dao;

import com.ecommerce.pojo.OrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);

    OrderItem selectByUserIdAndOrderNo(@Param("userId") Integer userId, @Param("orderNo") Long orderNo);

    OrderItem selectByOrderNo(Long orderNo);

    List<OrderItem> selectByUserId(Integer userId);

    List<OrderItem> selectAllOrder();

    List<OrderItem> getByOrderNo(Long orderNo);

    List<OrderItem> getByOrderNoUserId(Long orderNo, Integer userId);
}