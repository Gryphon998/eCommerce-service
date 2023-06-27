package com.ecommerce.service;

import com.ecommerce.common.ServerResponse;
import com.ecommerce.pojo.Product;
import com.ecommerce.vo.ProductDetailVo;
import com.github.pagehelper.PageInfo;

public interface IProductService {

    ServerResponse saveOrUpdateProduct(Product product);

    ServerResponse setSaleStatus(Integer productId, Integer state);

    ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);

    ServerResponse<PageInfo> manageProductList(Integer pageNum, Integer pageSize);

    ServerResponse<PageInfo> productSearch(String productName, int productId, int pageNum, int pageSize);

    ServerResponse<ProductDetailVo> getProductDetail(Integer productId);

    ServerResponse<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId, int pageNum, int pageSize, String orderBy);
}
