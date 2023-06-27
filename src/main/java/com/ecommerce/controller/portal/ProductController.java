package com.ecommerce.controller.portal;

import com.ecommerce.common.ServerResponse;
import com.ecommerce.service.IProductService;
import com.ecommerce.vo.ProductDetailVo;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller class for handling product-related operations in the portal.
 */
@Controller
public class ProductController {

    @Autowired
    private IProductService iProductService;

    /**
     * API for retrieving product details.
     *
     * @param productId The ID of the product
     * @return ServerResponse object containing the product details
     */
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailVo> detail(Integer productId) {
        return iProductService.getProductDetail(productId);
    }

    /**
     * API for retrieving a list of products based on search criteria.
     *
     * @param keyword    The keyword to search for in product names or descriptions (optional)
     * @param categoryId The ID of the category to filter products by (optional)
     * @param pageNum    The page number
     * @param pageSize   The number of products to retrieve per page
     * @param orderBy    The field to sort the products by (optional)
     * @return ServerResponse object containing the list of products and pagination information
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(@RequestParam(value = "keyword", required = false) String keyword,
                                         @RequestParam(value = "categoryId", required = false) Integer categoryId,
                                         @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                         @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                         @RequestParam(value = "orderBy", defaultValue = "") String orderBy) {
        return iProductService.getProductByKeywordCategory(keyword, categoryId, pageNum, pageSize, orderBy);
    }

}
