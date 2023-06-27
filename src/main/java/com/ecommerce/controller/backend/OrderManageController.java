package com.ecommerce.controller.backend;

import com.ecommerce.common.Const;
import com.ecommerce.common.ResponseCode;
import com.ecommerce.common.ServerResponse;
import com.ecommerce.pojo.User;
import com.ecommerce.service.IOrderService;
import com.ecommerce.service.IUserService;
import com.ecommerce.vo.OrderVo;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/order")
public class OrderManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IOrderService iOrderService;

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo<OrderVo>> orderList(HttpSession session,
                                                       @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                                       @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessageCode(ResponseCode.NEED_LOGIN.getCode(), "User not logged in. Please log in as an administrator.");
        }
        if (iUserService.checkUserRole(user).isSuccess()) {
            return iOrderService.manageList(pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorMessage("Unauthorized operation");
        }
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<OrderVo> orderDetail(HttpSession session, Long orderNo) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessageCode(ResponseCode.NEED_LOGIN.getCode(), "User not logged in. Please log in as an administrator.");
        }
        if (iUserService.checkUserRole(user).isSuccess()) {
            return iOrderService.manageDetail(orderNo);
        } else {
            return ServerResponse.createByErrorMessage("Unauthorized operation");
        }
    }

    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo<OrderVo>> orderSearch(HttpSession session, Long orderNo,
                                                         @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                                         @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessageCode(ResponseCode.NEED_LOGIN.getCode(), "User not logged in. Please log in as an administrator.");
        }
        if (iUserService.checkUserRole(user).isSuccess()) {
            return iOrderService.manageSearch(orderNo, pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorMessage("Unauthorized operation");
        }
    }

    @RequestMapping("send_goods.do")
    @ResponseBody
    public ServerResponse<String> orderSendGoods(HttpSession session, Long orderNo) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessageCode(ResponseCode.NEED_LOGIN.getCode(), "User not logged in. Please log in as an administrator.");
        }
        if (iUserService.checkUserRole(user).isSuccess()) {
            return iOrderService.manageSendGoods(orderNo);
        } else {
            return ServerResponse.createByErrorMessage("Unauthorized operation");
        }
    }
}
