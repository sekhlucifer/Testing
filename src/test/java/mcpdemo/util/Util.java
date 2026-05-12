package mcpdemo.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Util {
    
    private static final Properties properties;
    
    static {
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream("src/test/resources/config.properties")) {
            properties.load(fis);
        } catch (IOException e) {
            System.out.println("Config file not found, using default values");
        }
    }
    
    public static String getConfigValue(String key) {
        return properties.getProperty(key);
    }
    
    public static String getBaseUrl() {
        return properties.getProperty("base.url", "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login");
    }
    
    public static String getValidUsername() {
        return properties.getProperty("valid.username", "Admin");
    }
    
    public static String getValidPassword() {
        return properties.getProperty("valid.password", "admin123");
    }
    
    public static String getInvalidUsername() {
        return properties.getProperty("invalid.username", "invaliduser");
    }
    
    public static String getInvalidPassword() {
        return properties.getProperty("invalid.password", "invalidpass");
    }
}