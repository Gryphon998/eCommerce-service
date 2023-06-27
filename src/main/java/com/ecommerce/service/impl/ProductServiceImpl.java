package com.ecommerce.service.impl;

import com.ecommerce.common.Const;
import com.ecommerce.common.ResponseCode;
import com.ecommerce.common.ServerResponse;
import com.ecommerce.dao.CategoryMapper;
import com.ecommerce.dao.ProductMapper;
import com.ecommerce.pojo.Category;
import com.ecommerce.pojo.Product;
import com.ecommerce.service.ICategoryService;
import com.ecommerce.service.IProductService;
import com.ecommerce.util.DateTimeUtil;
import com.ecommerce.util.PropertiesUtil;
import com.ecommerce.vo.ProductDetailVo;
import com.ecommerce.vo.ProductListVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("iProductService")
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ICategoryService iCategoryService; // Calling a service from the same level

    /**
     * If the id is null, it means updating a product. If the id is not null, it means adding a product.
     *
     * @param product
     * @return
     */
    public ServerResponse saveOrUpdateProduct(Product product) {
        if (product != null) {
            if (StringUtils.isNotBlank(product.getSubImages())) {
                String[] imageArray = product.getSubImages().split(",");
                if (imageArray.length > 0) {
                    product.setMainImage(imageArray[0]);
                }
            }
            if (product.getId() == null) {
                // Adding a product
                int insert = productMapper.insert(product);
                if (insert > 0) {
                    return ServerResponse.createBySuccessMessage("Added successfully");
                } else {
                    return ServerResponse.createByErrorMessage("Failed to add");
                }
            } else {
                // Updating a product
                int updateResult = productMapper.updateByPrimaryKeySelective(product);
                if (updateResult > 0) {
                    return ServerResponse.createBySuccessMessage("Updated successfully");
                } else {
                    return ServerResponse.createByErrorMessage("Failed to update");
                }
            }
        } else {
            return ServerResponse.createByErrorMessageCode(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
    }

    /**
     * Modify the sale status of a product
     *
     * @param productId
     * @param state
     * @return
     */
    public ServerResponse setSaleStatus(Integer productId, Integer state) {
        if (productId == null || state == null) {
            return ServerResponse.createByErrorMessageCode(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        } else {
            Product product = new Product();
            product.setId(productId);
            product.setStatus(state);
            int updateResult = productMapper.updateByPrimaryKeySelective(product);
            if (updateResult > 0) {
                return ServerResponse.createBySuccessMessage("Successfully modified product sale status");
            } else {
                return ServerResponse.createByErrorMessage("Failed to modify product sale status");
            }
        }
    }

    /**
     * Get product details by product ID and return the data as a VO
     *
     * @param productId
     * @return
     */
    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId) {
        if (productId == null) {
            return ServerResponse.createByErrorMessageCode(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        } else {
            Product product = productMapper.selectByPrimaryKey(productId);
            if (product == null) {
                return ServerResponse.createByErrorMessage("Product has been deleted or taken off the shelves");
            } else {
                return ServerResponse.createBySuccess(assembleProductDetailVo(product));
            }
        }
    }

    /**
     * Get a list of products
     *
     * @param pageNum  Page number
     * @param pageSize Number of items per page
     * @return
     */
    public ServerResponse<PageInfo> manageProductList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> products = productMapper.selectList();
        List<ProductListVo> productListVos = Lists.newArrayList();
        for (Product productItem : products) {
            productListVos.add(assembleProductListVo(productItem));
        }
        PageInfo pageInfo = new PageInfo(products);
        pageInfo.setList(productListVos);
        return ServerResponse.createBySuccess(pageInfo);
    }

    /**
     * Search for products by product name or ID and return a list of products
     *
     * @param productName
     * @param productId
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ServerResponse<PageInfo> productSearch(String productName, int productId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> products = productMapper.selectByNameAndProductId(productName, productId);
        List<ProductListVo> productListVos = Lists.newArrayList();
        for (Product productItem : products) {
            productListVos.add(assembleProductListVo(productItem));
        }
        PageInfo pageInfo = new PageInfo(products);
        pageInfo.setList(productListVos);
        return ServerResponse.createBySuccess(pageInfo);
    }

    /**
     * Assemble Product into ProductDetailVo
     *
     * @param product
     * @return
     */
    public ProductDetailVo assembleProductDetailVo(Product product) {
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setName(product.getName());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setPrice(product.getPrice());
        // Query the category of the product
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category == null || category.getParentId() == null) {
            productDetailVo.setParentCategoryId(0);
        } else {
            productDetailVo.setParentCategoryId(category.getParentId());
        }
        // Set image host
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.ecommerce-service.com/"));

        // Set time, because we use the millisecond value of the time queried from the MyBatis db, which is not convenient for viewing, so it needs to be formatted
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return productDetailVo;
    }

    /**
     * Assemble Product into ProductListVo
     *
     * @param product
     * @return
     */
    public ProductListVo assembleProductListVo(Product product) {
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setPrice(product.getPrice());
        productListVo.setMainImage(product.getMainImage());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setStatus(product.getStatus());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.ecommerce-service.com/"));
        return productListVo;
    }

    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId) {
        if (productId == null) {
            return ServerResponse.createByErrorMessageCode(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createBySuccessMessage("The product has been taken off the shelves or deleted");
        }
        if (product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()) {
            return ServerResponse.createBySuccessMessage("The product has been taken off the shelves or deleted");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId, int pageNum, int pageSize, String orderBy) {
        if (StringUtils.isBlank(keyword) && categoryId == null) {
            return ServerResponse.createByErrorMessageCode(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        List<Integer> categoryIdList = new ArrayList<Integer>();

        if (categoryId != null) {
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if (category == null && StringUtils.isBlank(keyword)) {
                // If the category does not exist and there is no keyword, return an empty result set without an error
                PageHelper.startPage(pageNum, pageSize);
                List<ProductListVo> productListVoList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVoList);
                return ServerResponse.createBySuccess(pageInfo);
            }
            categoryIdList = iCategoryService.selectCategoryAndChildrenById(category.getId()).getData();
        }
        if (StringUtils.isNotBlank(keyword)) {
            keyword = "%" + keyword + "%";
        }

        PageHelper.startPage(pageNum, pageSize);
        // Handle sorting
        if (StringUtils.isNotBlank(orderBy)) {
            if (Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)) {
                String[] orderByArray = orderBy.split("_");
                // Use "price asc" format to sort by price
                PageHelper.orderBy(orderByArray[0] + " " + orderByArray[1]);
            }
        }
        // The following select method will be paginated. Points to note for MyBatis PageHelper pagination
        List<Product> productList = productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword) ? null : keyword, categoryIdList.size() == 0 ? null : categoryIdList);

        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product product : productList) {
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }

        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

}
