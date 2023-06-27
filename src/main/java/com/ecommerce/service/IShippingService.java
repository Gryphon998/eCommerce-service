package com.ecommerce.service;

import com.ecommerce.common.ServerResponse;
import com.ecommerce.pojo.Shipping;
import com.github.pagehelper.PageInfo;

public interface IShippingService {

    ServerResponse add(Integer userId, Shipping shipping);

    ServerResponse<String> del(Integer userId, Integer shippingId);

    ServerResponse update(Integer userId, Shipping shipping);

    ServerResponse<Shipping> select(Integer userId, Integer shippingId);

    ServerResponse<PageInfo<Shipping>> list(Integer userId, int pageNum, int pageSize);
}
