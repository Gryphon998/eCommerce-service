package com.ecommerce.service.impl;

import com.ecommerce.common.Const;
import com.ecommerce.common.ServerResponse;
import com.ecommerce.dao.*;
import com.ecommerce.pojo.*;
import com.ecommerce.service.IOrderService;
import com.ecommerce.util.BigDecimalUtil;
import com.ecommerce.util.DateTimeUtil;
import com.ecommerce.util.PropertiesUtil;
import com.ecommerce.vo.OrderItemVo;
import com.ecommerce.vo.OrderProductVo;
import com.ecommerce.vo.OrderVo;
import com.ecommerce.vo.ShippingVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service("iOrderService")
public class OrderServiceImpl implements IOrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    @Autowired
    OrderItemMapper orderMapper;
    @Autowired
    OrderItemMapper orderItemMapper;
    @Autowired
    private PayInfoMapper payInfoMapper;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ShippingMapper shippingMapper;

    public ServerResponse createOrder(Integer userId, Integer shippingId) {

        // Get data from the shopping cart
        List<Cart> cartList = cartMapper.selectCheckedCartByUserId(userId);

        // Calculate the total price of this order
        ServerResponse serverResponse = this.getCartOrderItem(userId, cartList);
        if (!serverResponse.isSuccess()) {
            return serverResponse;
        }
        List<OrderItem> orderItemList = (List<OrderItem>) serverResponse.getData();
        BigDecimal payment = this.getOrderTotalPrice(orderItemList);

        // Generate the order
        OrderItem order = this.assembleOrder(userId, shippingId, payment);
        if (order == null) {
            return ServerResponse.createByErrorMessage("Error generating order");
        }
        if (CollectionUtils.isEmpty(orderItemList)) {
            return ServerResponse.createByErrorMessage("The shopping cart is empty");
        }
        for (OrderItem orderItem : orderItemList) {
            orderItem.setOrderNo(order.getOrderNo());
        }
        // Bulk insert using MyBatis
        for (OrderItem o : orderItemList) {
            orderItemMapper.insert(o);
        }

        // Reduce the product stock after successful order generation
        this.reduceProductStock(orderItemList);
        // Clear the shopping cart
        this.cleanCart(cartList);

        // Return data to the frontend
        OrderVo orderVo = assembleOrderVo(order, orderItemList);
        return ServerResponse.createBySuccess(orderVo);
    }

    private OrderVo assembleOrderVo(OrderItem order, List<OrderItem> orderItemList) {
        OrderVo orderVo = new OrderVo();
        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPayment(order.getPayment());
        orderVo.setPaymentType(order.getPaymentType());
        orderVo.setPaymentTypeDesc(Const.PaymentTypeEnum.codeOf(order.getPaymentType()).getValue());

        orderVo.setPostage(order.getPostage());
        orderVo.setStatus(order.getStatus());
        orderVo.setStatusDesc(Const.OrderStatusEnum.codeOf(order.getStatus()).getValue());

        orderVo.setShippingId(order.getShippingId());
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        if (shipping != null) {
            orderVo.setReceiverName(shipping.getReceiverName());
            orderVo.setShippingVo(assembleShippingVo(shipping));
        }

        orderVo.setPaymentTime(DateTimeUtil.dateToStr(order.getPaymentTime()));
        orderVo.setSendTime(DateTimeUtil.dateToStr(order.getSendTime()));
        orderVo.setEndTime(DateTimeUtil.dateToStr(order.getEndTime()));
        orderVo.setCreateTime(DateTimeUtil.dateToStr(order.getCreateTime()));
        orderVo.setCloseTime(DateTimeUtil.dateToStr(order.getCloseTime()));

        orderVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        List<OrderItemVo> orderItemVoList = Lists.newArrayList();

        for (OrderItem orderItem : orderItemList) {
            OrderItemVo orderItemVo = assembleOrderItemVo(orderItem);
            orderItemVoList.add(orderItemVo);
        }
        orderVo.setOrderItemVoList(orderItemVoList);
        return orderVo;
    }

    private OrderItemVo assembleOrderItemVo(OrderItem orderItem) {
        OrderItemVo orderItemVo = new OrderItemVo();
        orderItemVo.setOrderNo(orderItem.getOrderNo());
        orderItemVo.setProductId(orderItem.getProductId());
        orderItemVo.setProductName(orderItem.getProductName());
        orderItemVo.setProductImage(orderItem.getProductImage());
        orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
        orderItemVo.setQuantity(orderItem.getQuantity());
        orderItemVo.setTotalPrice(orderItem.getTotalPrice());

        orderItemVo.setCreateTime(DateTimeUtil.dateToStr(orderItem.getCreateTime()));
        return orderItemVo;
    }

    private ShippingVo assembleShippingVo(Shipping shipping) {
        ShippingVo shippingVo = new ShippingVo();
        shippingVo.setReceiverName(shipping.getReceiverName());
        shippingVo.setReceiverAddress(shipping.getReceiverAddress());
        shippingVo.setReceiverProvince(shipping.getReceiverProvince());
        shippingVo.setReceiverCity(shipping.getReceiverCity());
        shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingVo.setReceiverMobile(shipping.getReceiverMobile());
        shippingVo.setReceiverZip(shipping.getReceiverZip());
        shippingVo.setReceiverPhone(shippingVo.getReceiverPhone());
        return shippingVo;
    }

    private void cleanCart(List<Cart> cartList) {
        for (Cart cart : cartList) {
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
    }

    private void reduceProductStock(List<OrderItem> orderItemList) {
        for (OrderItem orderItem : orderItemList) {
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock() - orderItem.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        }
    }

    private OrderItem assembleOrder(Integer userId, Integer shippingId, BigDecimal payment) {
        OrderItem order = new OrderItem();
        long orderNo = this.generateOrderNo();
        order.setOrderNo(orderNo);
        order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());
        order.setPostage(0);
        order.setPaymentType(Const.PaymentTypeEnum.ONLINE_PAY.getCode());
        order.setPayment(payment);

        order.setUserId(userId);
        order.setShippingId(shippingId);
        // Set other fields such as delivery time, payment time, etc.
        int rowCount = orderMapper.insert(order);
        if (rowCount > 0) {
            return order;
        }
        return null;
    }

    private long generateOrderNo() {
        long currentTime = System.currentTimeMillis();
        return currentTime + new Random().nextInt(100);
    }

    private BigDecimal getOrderTotalPrice(List<OrderItem> orderItemList) {
        BigDecimal payment = new BigDecimal("0");
        for (OrderItem orderItem : orderItemList) {
            payment = BigDecimalUtil.add(payment.doubleValue(), orderItem.getTotalPrice().doubleValue());
        }
        return payment;
    }

    private ServerResponse getCartOrderItem(Integer userId, List<Cart> cartList) {
        List<OrderItem> orderItemList = Lists.newArrayList();
        if (CollectionUtils.isEmpty(cartList)) {
            return ServerResponse.createByErrorMessage("The shopping cart is empty.");
        }

        // Validate the data in the shopping cart, including product status and quantity
        for (Cart cartItem : cartList) {
            OrderItem orderItem = new OrderItem();
            Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
            if (Const.ProductStatusEnum.ON_SALE.getCode() != product.getStatus()) {
                return ServerResponse.createByErrorMessage("Product " + product.getName() + " is not available for sale.");
            }

            // Validate stock
            if (cartItem.getQuantity() > product.getStock()) {
                return ServerResponse.createByErrorMessage("Insufficient stock for product " + product.getName() + ".");
            }

            orderItem.setUserId(userId);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cartItem.getQuantity()));
            orderItemList.add(orderItem);
        }
        return ServerResponse.createBySuccess(orderItemList);
    }

    public ServerResponse<String> cancel(Integer userId, Long orderNo) {
        OrderItem order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("This order does not exist for the user.");
        }
        if (order.getStatus() != Const.OrderStatusEnum.NO_PAY.getCode()) {
            return ServerResponse.createByErrorMessage("OrderItem has been paid for and cannot be canceled.");
        }
        OrderItem updateOrder = new OrderItem();
        updateOrder.setId(order.getId());
        updateOrder.setStatus(Const.OrderStatusEnum.CANCELED.getCode());

        int row = orderMapper.updateByPrimaryKeySelective(updateOrder);
        if (row > 0) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByErrorMessage("Failed to cancel.");
    }

    public ServerResponse getOrderCartProduct(Integer userId) {
        OrderProductVo orderProductVo = new OrderProductVo();
        // Get data from the shopping cart
        List<Cart> cartList = cartMapper.selectCheckedCartByUserId(userId);
        ServerResponse serverResponse = this.getCartOrderItem(userId, cartList);
        if (!serverResponse.isSuccess()) {
            return serverResponse;
        }
        List<OrderItem> orderItemList = (List<OrderItem>) serverResponse.getData();

        List<OrderItemVo> orderItemVoList = Lists.newArrayList();

        BigDecimal payment = new BigDecimal("0");
        for (OrderItem orderItem : orderItemList) {
            payment = BigDecimalUtil.add(payment.doubleValue(), orderItem.getTotalPrice().doubleValue());
            orderItemVoList.add(assembleOrderItemVo(orderItem));
        }
        orderProductVo.setProductTotalPrice(payment);
        orderProductVo.setOrderItemVoList(orderItemVoList);
        orderProductVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return ServerResponse.createBySuccess(orderProductVo);
    }

    public ServerResponse<OrderVo> getOrderDetail(Integer userId, Long orderNo) {
        OrderItem order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order != null) {
            List<OrderItem> orderItemList = orderItemMapper.selectByUserId(userId);
            OrderVo orderVo = assembleOrderVo(order, orderItemList);
            return ServerResponse.createBySuccess(orderVo);
        }
        return ServerResponse.createByErrorMessage("The order was not found.");
    }

    public ServerResponse<PageInfo> getOrderList(Integer userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<OrderItem> orderList = orderMapper.selectByUserId(userId);
        List<OrderVo> orderVoList = assembleOrderVoList(orderList, userId);
        PageInfo pageResult = new PageInfo(orderList);
        pageResult.setList(orderVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    private List<OrderVo> assembleOrderVoList(List<OrderItem> orderList, Integer userId) {
        List<OrderVo> orderVoList = Lists.newArrayList();
        for (OrderItem order : orderList) {
            List<OrderItem> orderItemList = Lists.newArrayList();
            if (userId == null) {
                // Todo: When querying as an administrator, userId is not required to be passed
                orderItemList = orderItemMapper.getByOrderNo(order.getOrderNo());
            } else {
                orderItemList = orderItemMapper.getByOrderNoUserId(order.getOrderNo(), userId);
            }
            OrderVo orderVo = assembleOrderVo(order, orderItemList);
            orderVoList.add(orderVo);
        }
        return orderVoList;
    }

    public ServerResponse pay(Long orderNo, Integer userId, String path) {
        Map<String, String> resultMap = Maps.newHashMap();

        // Check the order number and get order information
        OrderItem order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("The user does not have this order.");
        }
        resultMap.put("orderNo", String.valueOf(order.getOrderNo()));

        // (Required) Unique order number in the merchant's website order system, within 64 characters, can only contain letters, numbers, and underscores,
        // and ensure that it is not duplicated on the merchant system side. It is recommended to generate through database sequence.
        String outTradeNo = order.getOrderNo().toString();

        // (Required) OrderItem title, roughly describing the purpose of the user's payment. For example, "xxx brand xxx store face-to-face payment QR code consumption"
        String subject = "happyecommerce QR code payment, order number:" + outTradeNo;

        // (Required) OrderItem total amount in yuan, not exceeding 1 hundred million yuan
        // If the [discount amount], [non-discount amount], and [order total amount] are passed in at the same time, the following condition must be met: [order total amount] = [discount amount] + [non-discount amount]
        String totalAmount = order.getPayment().toString();

        // (Optional) Non-discountable amount of the order, can be used with merchant platform to configure discount activities. If alcoholic beverages are not eligible for discounts, fill in the corresponding amount here.
        // If this value is not passed in, but [order total amount] and [discount amount] are passed in, this value defaults to [order total amount] - [discount amount]
        String undiscountableAmount = "0";

        // Seller's Alipay account ID, used to support payments to different collection accounts under one signed account (paid to the Alipay account corresponding to sellerId)
        // If this field is empty, it defaults to the PID of the merchant signed with Alipay, which is the PID corresponding to the appid
        String sellerId = "";

        // OrderItem description, can provide a detailed description of the transaction or product, such as "purchase 2 items for a total of 15.00 yuan"
        String body = "OrderItem " + outTradeNo + " purchased a total of " + totalAmount + " yuan";

        // Merchant operator number, adding this parameter can be used for sales statistics of merchant operators
        String operatorId = "test_operator_id";

        // (Required) Merchant store number, through the store number and the merchant background, precise discount information can be configured to the store, please consult Alipay technical support for details
        String storeId = "test_store_id";

        // Payment timeout, defined as 120 minutes
        String timeoutExpress = "120m";

        return null;
    }

    public ServerResponse aliCallback(Map<String, String> params) {
        Long orderNo = Long.parseLong(params.get("out_trade_no"));
        String tradeNo = params.get("trade_no");
        String tradeStatus = params.get("trade_status");
        OrderItem order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("Not an order from HappyMall, ignoring callback");
        }
        if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()) {
            return ServerResponse.createBySuccess("Alipay called repeatedly");
        }
        if (Const.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)) {
            order.setPaymentTime(DateTimeUtil.strToDate(params.get("gmt_payment")));
            order.setStatus(Const.OrderStatusEnum.PAID.getCode());
            orderMapper.updateByPrimaryKeySelective(order);
        }

        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setPayPlatform(Const.PayPlatformEnum.ALIPAY.getCode());
        payInfo.setPlatformNumber(tradeNo);
        payInfo.setPlatformStatus(tradeStatus);

        payInfoMapper.insert(payInfo);

        return ServerResponse.createBySuccess();
    }

    public ServerResponse queryOrderPayStatus(Integer userId, Long orderNo) {
        OrderItem order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("User does not have this order");
        }
        if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByErrorMessage("Failed to query order pay status");
    }

    /**
     * Retrieves a paginated list of orders.
     *
     * @param pageNum  The page number.
     * @param pageSize The number of items per page.
     * @return The server response containing the paginated list of orders.
     */
    public ServerResponse<PageInfo<OrderVo>> manageList(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<OrderItem> orderList = orderMapper.selectAllOrder();
        List<OrderVo> orderVoList = this.assembleOrderVoList(orderList, null);
        // Create a PageInfo object using the original orderList
        PageInfo<OrderVo> pageResult = new PageInfo(orderList);
        // Set the list of orderVo objects in the PageInfo object
        pageResult.setList(orderVoList);

        return ServerResponse.createBySuccess(pageResult);
    }

    /**
     * Retrieves the details of an order.
     *
     * @param orderNo The order number.
     * @return The server response containing the order details.
     */
    public ServerResponse<OrderVo> manageDetail(Long orderNo) {
        OrderItem order = orderMapper.selectByOrderNo(orderNo);
        if (order != null) {
            List<OrderItem> orderItemList = orderItemMapper.getByOrderNo(orderNo);
            OrderVo orderVo = assembleOrderVo(order, orderItemList);

            return ServerResponse.createBySuccess(orderVo);
        }
        // Return an error server response if the order doesn't exist
        return ServerResponse.createByErrorMessage("OrderItem does not exist");
    }

    /**
     * Searches for an order based on the order number and retrieves the details.
     *
     * @param orderNo  The order number.
     * @param pageNum  The page number.
     * @param pageSize The number of items per page.
     * @return The server response containing the searched order details.
     */
    public ServerResponse<PageInfo<OrderVo>> manageSearch(Long orderNo, int pageNum, int pageSize) {
        // Start paging
        PageHelper.startPage(pageNum, pageSize);
        // Retrieve the order from the database using the orderNo
        OrderItem order = orderMapper.selectByOrderNo(orderNo);
        if (order != null) {
            // Retrieve the order items associated with the order
            List<OrderItem> orderItemList = orderItemMapper.getByOrderNo(orderNo);
            // Assemble the orderVo object using the retrieved order and orderItemList
            OrderVo orderVo = assembleOrderVo(order, orderItemList);
            // Create a PageInfo object using a list containing the retrieved order
            PageInfo<OrderVo> pageResult = new PageInfo(Lists.newArrayList(order));
            // Set the list of orderVo objects in the PageInfo object
            pageResult.setList(Lists.newArrayList(orderVo));
            // Return the server response with the PageInfo object
            return ServerResponse.createBySuccess(pageResult);
        }
        // Return an error server response if the order doesn't exist
        return ServerResponse.createByErrorMessage("OrderItem does not exist");
    }

    /**
     * Updates the status of an order and sets the send time, indicating that the goods have been sent.
     *
     * @param orderNo The order number.
     * @return The server response indicating the success or failure of sending the goods.
     */
    public ServerResponse<String> manageSendGoods(Long orderNo) {
        // Retrieve the order from the database using the orderNo
        OrderItem order = orderMapper.selectByOrderNo(orderNo);
        if (order != null) {
            // Check if the order status is PAID
            if (order.getStatus() == Const.OrderStatusEnum.PAID.getCode()) {
                // Update the order status to SHIPPED
                order.setStatus(Const.OrderStatusEnum.SHIPPED.getCode());
                // Set the send time to the current date
                order.setSendTime(new Date());
                // Update the order in the database
                orderMapper.updateByPrimaryKeySelective(order);
                // Return the server response indicating the successful shipment
                return ServerResponse.createBySuccess("发货成功");
            }
        }
        // Return an error server response if the order doesn't exist or the status is not PAID
        return ServerResponse.createByErrorMessage("订单不存在");
    }
}
