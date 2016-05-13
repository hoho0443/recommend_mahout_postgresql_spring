package com.daidalos.developer.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import com.google.common.io.Closeables;

public class Util {
    private static Properties prop = new Properties();
    static {
        InputStream is = null;
        try {
            is = Util.class.getResourceAsStream("/application.properties");
            prop.load(is);
        } catch (IOException e) {
            throw new RuntimeException("This should not be happended. do not found application.properties", e);
        } finally {
            Closeables.closeQuietly(is);
        }
    }
    public static String prop(String key) {
        return prop.getProperty(key, "");
    }
}
