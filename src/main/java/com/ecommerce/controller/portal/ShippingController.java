package com.ecommerce.controller.portal;

import com.ecommerce.common.Const;
import com.ecommerce.common.ResponseCode;
import com.ecommerce.common.ServerResponse;
import com.ecommerce.pojo.Shipping;
import com.ecommerce.pojo.User;
import com.ecommerce.service.IShippingService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;


@Controller
@RequestMapping("/shipping/")
public class ShippingController {

    @Autowired
    private IShippingService iShippingService;

    /**
     * API for adding a new shipping address.
     *
     * @param session  HttpSession object
     * @param shipping Shipping object containing the shipping address details
     * @return ServerResponse indicating the success or failure of the operation
     */
    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse add(HttpSession session, Shipping shipping) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessageCode(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.add(user.getId(), shipping);
    }

    /**
     * API for deleting a shipping address.
     *
     * @param session    HttpSession object
     * @param shippingId The ID of the shipping address to delete
     * @return ServerResponse indicating the success or failure of the operation
     */
    @RequestMapping("del.do")
    @ResponseBody
    public ServerResponse del(HttpSession session, Integer shippingId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessageCode(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.del(user.getId(), shippingId);
    }

    /**
     * API for updating a shipping address.
     *
     * @param session  HttpSession object
     * @param shipping Shipping object containing the updated shipping address details
     * @return ServerResponse indicating the success or failure of the operation
     */
    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse update(HttpSession session, Shipping shipping) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessageCode(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.update(user.getId(), shipping);
    }

    /**
     * API for selecting a shipping address.
     *
     * @param session    HttpSession object
     * @param shippingId The ID of the shipping address to select
     * @return ServerResponse containing the selected shipping address
     */
    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse<Shipping> select(HttpSession session, Integer shippingId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessageCode(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.select(user.getId(), shippingId);
    }

    /**
     * API for retrieving a list of shipping addresses.
     *
     * @param pageNum  The page number
     * @param pageSize The number of shipping addresses to retrieve per page
     * @param session  HttpSession object
     * @return ServerResponse containing the list of shipping addresses and pagination information
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo<Shipping>> list(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                                   @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                   HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessageCode(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.list(user.getId(), pageNum, pageSize);
    }
}
