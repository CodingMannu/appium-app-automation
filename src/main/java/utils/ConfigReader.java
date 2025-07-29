package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads properties from config.properties file as a singleton.
 * Thread-safe for parallel execution.
 */
public class ConfigReader {

    private static final Logger logger = LogManager.getLogger(ConfigReader.class);
    private static volatile ConfigReader instance;
    public static boolean SHOW_APPIUM_LOGS = Boolean.parseBoolean(ConfigReader.getInstance().getProperty("show.appium.logs")); //working on this

    private final Properties properties;

    public ConfigReader() {
        properties = new Properties();
        loadProperties();
    }


    public static ConfigReader getInstance() {
        if (instance == null) {
            synchronized (ConfigReader.class) {
                if (instance == null) {
                    instance = new ConfigReader();
                }
            }
        }
        return instance;
    }


    private void loadProperties() {
        String path = System.getProperty("user.dir") + "/src/test/resources/config/config.properties";
        File configFile = new File(path);

        if (!configFile.exists()) {
            throw new RuntimeException("Config file not found: " + configFile.getAbsolutePath());
        }

        try (InputStream fis = new FileInputStream(configFile)) {
            properties.load(fis);
            logger.info("config.properties loaded from: {}", configFile.getAbsolutePath());
        } catch (Exception e) {
            logger.error("Failed to load config.properties", e);
            throw new RuntimeException("Cannot load config.properties", e);
        }
    }


    public String getProperty(String key) {
        return properties.getProperty(key);
    }


    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public JsonNode loadAppiumJsonConfig(String env) {
        try {
            String path = System.getProperty("user.dir") + "/src/test/resources/appium/server-configs/" + env + ".json";
            File file = new File(path);

            if (!file.exists()) {
                throw new RuntimeException("Appium server config file not found: " + path);
            }

            logger.info("Appium JSON config loaded from: {}", path);
            return new ObjectMapper().readTree(file);

        } catch (Exception e) {
            throw new RuntimeException("Failed to load Appium server config for env: " + env, e);
        }
    }

    public boolean isFileExists(String path) {
        return new File(path).exists();
    }
}
