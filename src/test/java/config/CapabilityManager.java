package config;

import io.appium.java_client.android.options.UiAutomator2Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.ConfigReader;
import utils.data.JsonReader;

import java.util.Map;

public class CapabilityManager {

    private static final Logger logger = LogManager.getLogger(CapabilityManager.class);

    public UiAutomator2Options getAndroidCapabilities() {
        String deviceType = ConfigReader.getInstance().getProperty("device.type");
        String deviceName = ConfigReader.getInstance().getProperty("device.name");

        if (deviceType == null || deviceName == null) {
            logger.error("Device type or name not specified in config.properties - deviceType: {}, deviceName: {}", deviceType, deviceName);
            throw new RuntimeException("Device type or name not specified in config.properties - deviceType: " + deviceType + ", deviceName: " + deviceName);
        }

        logger.info("Loaded capabilities for deviceType: {}, deviceName: {}", deviceType, deviceName);

        // Load device-specific capabilities from JSON
        Map<String, Object> deviceCapabilities = JsonReader.readAndroidDeviceConfig(deviceType, deviceName);
        if (deviceCapabilities == null || deviceCapabilities.isEmpty()) {
            logger.error("No capabilities found for device type: {}, device name: {}", deviceType, deviceName);
            throw new RuntimeException("No capabilities found for device type: " + deviceType + ", device name: " + deviceName);
        }

        // Load global capabilities
        Map<String, Object> globalCapabilities = JsonReader.readGlobalSettings();
        if (globalCapabilities == null || globalCapabilities.isEmpty()) {
            logger.error("No global-settings capabilities found in android-devices.json");
            throw new RuntimeException("No global-settings capabilities found in android-devices.json");
        }

        UiAutomator2Options options = new UiAutomator2Options();
        deviceCapabilities.forEach(options::setCapability);
        globalCapabilities.forEach(options::setCapability);

        // Ensure that all required capabilities are present
        String[] requiredKeys = {
                "platformName", "deviceName", "platformVersion", "udid",
                "automationName", "app", "appPackage", "appActivity"
        };

        StringBuilder missingKeys = new StringBuilder();
        for (String key : requiredKeys) {
            if (!deviceCapabilities.containsKey(key)) {
                missingKeys.append(key).append(", ");
            }
        }

        if (missingKeys.length() > 0) {
            logger.error("Missing required capabilities: {}", missingKeys.substring(0, missingKeys.length() - 2));
            throw new RuntimeException("Missing required capabilities: " + missingKeys.toString());
        }

        return options;
    }


    // METHOD TO SUPPORT BROWSERSTACK CAPABILITIES FOR ANDROID
    public UiAutomator2Options getBrowserStackAndroidCapabilities() {
        String browserStackUser = ConfigReader.getInstance().getProperty("browserstack.user");
        String browserStackKey = ConfigReader.getInstance().getProperty("browserstack.key");

        if (browserStackUser == null || browserStackKey == null) {
            logger.error("BrowserStack credentials not specified in config.properties - browserStackUser: {}, browserStackKey: {}", browserStackUser, browserStackKey);
            throw new RuntimeException("BrowserStack credentials not specified in config.properties - browserStackUser: " + browserStackUser + ", browserStackKey: " + browserStackKey);
        }

        logger.info("Loaded BrowserStack credentials successfully - browserStackUser: {}, browserStackKey: {}", browserStackUser, browserStackKey);

        // Fetch the device name, platform version, and app ID (uploaded to BrowserStack)
        String browserStackPlatformName = ConfigReader.getInstance().getProperty("browserstack.platform.name", "Android");
        String browserStackDeviceName = ConfigReader.getInstance().getProperty("browserstack.device.name");
        String browserStackPlatformVersion = ConfigReader.getInstance().getProperty("browserstack.platform.version");
        String appUrl = ConfigReader.getInstance().getProperty("browserstack.app.url");
//        String appiumLogLevel = ConfigReader.getInstance().getProperty("appium.log.level", "info");
        String browserStackDebug = ConfigReader.getInstance().getProperty("browserstack.debug", "false");

        if (browserStackPlatformName == null || browserStackDeviceName == null || browserStackPlatformVersion == null || appUrl == null) {
            logger.error("Platform Name, Device Name, Platform Version, or App ID not specified for BrowserStack - platformName: {}, deviceName: {}, platformVersion: {}, appId: {}", browserStackPlatformName, browserStackDeviceName, browserStackPlatformVersion, appUrl);
            throw new RuntimeException("Device name, platform version, or app ID not specified for BrowserStack - platformName: " + browserStackPlatformName + ", deviceName: " + browserStackDeviceName + ", platformVersion: " + browserStackPlatformVersion + ", appId: " + appUrl);
        }

        // Create BrowserStack specific capabilities using UiAutomator2Options
        UiAutomator2Options options = new UiAutomator2Options();

        options.setCapability("browserstack.user", browserStackUser);
        options.setCapability("browserstack.key", browserStackKey);
        options.setCapability("platformName", browserStackPlatformName);
        options.setCapability("deviceName", browserStackDeviceName);
        options.setCapability("platformVersion", browserStackPlatformVersion);
        options.setCapability("app", appUrl);
        options.setCapability("project", "Astroyogi App Automation");
        options.setCapability("build", "Build_001");
        options.setCapability("name", "TTA Login Test");
//        options.setCapability("appium:logLevel", appiumLogLevel);
        options.setCapability("browserstack.debug", Boolean.parseBoolean(browserStackDebug));

        logger.info("BrowserStack capabilities configured successfully.");

        return options;
    }

}
