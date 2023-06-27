package com.ecommerce.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Utility class for reading properties from a configuration file.
 */
public class PropertiesUtil {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

    private static final Properties props;

    static {
        String fileName = "ecommerce.properties";
        props = new Properties();
        try {
            props.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName), StandardCharsets.UTF_8));
        } catch (IOException e) {
            logger.error("Exception occurred while reading the configuration file", e);
        }
    }

    /**
     * Retrieves the value of the specified property key.
     *
     * @param key The property key.
     * @return The value of the property, or null if the key is not found.
     */
    public static String getProperty(String key) {
        String value = props.getProperty(key.trim());
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return value.trim();
    }

    /**
     * Retrieves the value of the specified property key. If the key is not found, returns the specified default value.
     *
     * @param key          The property key.
     * @param defaultValue The default value to be returned if the key is not found.
     * @return The value of the property, or the default value if the key is not found.
     */
    public static String getProperty(String key, String defaultValue) {
        String value = props.getProperty(key.trim());
        if (StringUtils.isBlank(value)) {
            value = defaultValue;
        }
        return value.trim();
    }
}
