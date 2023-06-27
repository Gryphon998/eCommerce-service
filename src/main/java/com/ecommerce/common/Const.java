package com.ecommerce.common;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Constants class that contains various constants used in the application.
 */
public class Const {

    // Current user session key
    public static final String CURRENT_USER = "currentUser";
    public static final String EMAIL = "email";
    public static final String USERNAME = "username";

    /**
     * Enum representing product status.
     */
    public enum ProductStatusEnum {
        ON_SALE(1, "Online");  // Product is online and available for sale

        private final int code;
        private final String value;

        ProductStatusEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

    /**
     * Enum representing order status.
     */
    public enum OrderStatusEnum {
        CANCELED(0, "Cancelled"),       // Order is cancelled
        NO_PAY(10, "Unpaid"),           // Order has not been paid
        PAID(20, "Paid"),               // Order has been paid
        SHIPPED(40, "Shipped"),         // Order has been shipped
        ORDER_SUCCESS(50, "Order Completed"),   // Order has been successfully completed
        ORDER_CLOSE(60, "Order Closed");         // Order has been closed

        private final int code;
        private final String value;

        OrderStatusEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public static OrderStatusEnum codeOf(int code) {
            for (OrderStatusEnum orderStatusEnum : values()) {
                if (orderStatusEnum.getCode() == code) {
                    return orderStatusEnum;
                }
            }
            throw new RuntimeException("No corresponding enum found");
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

    /**
     * Enum representing payment platforms.
     */
    public enum PayPlatformEnum {
        ALIPAY(1, "Alipay");  // Alipay payment platform

        private final int code;
        private final String value;

        PayPlatformEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

    /**
     * Enum representing payment types.
     */
    public enum PaymentTypeEnum {
        ONLINE_PAY(1, "Online Payment");  // Online payment type

        private final int code;
        private final String value;

        PaymentTypeEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public static PaymentTypeEnum codeOf(int code) {
            for (PaymentTypeEnum paymentTypeEnum : values()) {
                if (paymentTypeEnum.getCode() == code) {
                    return paymentTypeEnum;
                }
            }
            throw new RuntimeException("No corresponding enum found");
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

    /**
     * Constants related to the shopping cart.
     */
    public interface Cart {
        int CHECKED = 1;         // Cart item is checked (selected)
        int UN_CHECKED = 0;     // Cart item is unchecked (not selected)

        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";          // Cart item quantity limit exceeded
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";    // Cart item quantity limit not exceeded
    }

    /**
     * Constants related to product list ordering.
     */
    public interface ProductListOrderBy {
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc", "price_asc");  // Set of valid ordering options for product list (price_desc, price_asc)
    }

    /**
     * Keys for properties used in the application.
     */
    public interface PropertiesKey {
        String PASSWORD_SALT = "password.salt";                           // Password salt
        String FTP_SERVER_HTTP_PREFIX = "ftp.server.http.prefix";         // FTP server HTTP prefix
        String ALIPAY_CALLBACK_URL = "alipay.callback.url";               // Alipay callback URL
    }

    /**
     * User roles.
     */
    public interface Role {
        int ROLE_CUSTOMER = 0;  // Regular customer role
        int ROLE_ADMIN = 1;     // Administrator role
    }

    /**
     * Constants related to Alipay callback.
     */
    public interface AlipayCallback {
        String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";     // Alipay trade status: waiting for buyer to pay
        String TRADE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";       // Alipay trade status: trade success

        String RESPONSE_SUCCESS = "success";   // Alipay callback response: success
        String RESPONSE_FAILED = "failed";     // Alipay callback response: failed
    }
}
