package driver;

import config.CapabilityManager;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.ConfigReader;
import utils.DeviceUtils;

import java.net.URI;
import java.net.URL;

public class DriverFactory {

    private static final Logger logger = LogManager.getLogger(DriverFactory.class);

    public AndroidDriver createDriver() {
        try {
            String environment = ConfigReader.getInstance().getProperty("appium.server.env");

            if (environment == null || environment.isEmpty()) {
                throw new RuntimeException("Server environment is not specified in config.properties - appium.server.env");
            }


            if ("browserstack".equals(environment)) {
                return createBrowserStackDriver();
            } else if ("local".equalsIgnoreCase(environment)) {
                return createLocalDriver();
            } else {
                throw new RuntimeException("Unsupported Appium server environment: " + environment);
            }
        } catch (Exception e) {
            logger.error("Failed to initialize driver", e);
            throw new RuntimeException("Failed to initialize driver", e);
        }
    }

    // Method to handle BrowserStack driver creation
    private static AndroidDriver createBrowserStackDriver() {
        try {

            String browserStackUrl = ConfigReader.getInstance().getProperty("appium.server.url");
            logger.info("BrowserStack URL = {}", browserStackUrl);


            if (browserStackUrl == null || browserStackUrl.isEmpty()) {
                throw new RuntimeException("BrowserStack URL is not specified in config.properties");
            } else {
                logger.info("BrowserStack URL found: {}", browserStackUrl);

                URI uri = URI.create(browserStackUrl); // BrowserStack URL: "https://hub-cloud.browserstack.com/wd/hub"
                URL url = uri.toURL();

                UiAutomator2Options options = new CapabilityManager().getBrowserStackAndroidCapabilities();
                logger.info("Final capabilities: {}", options.asMap());


                // Get BrowserStack-specific capabilities
                AndroidDriver driver = new AndroidDriver(url, options );

                logger.info("Successfully connected to BrowserStack and created AndroidDriver.");
                return driver;
            }
        } catch (Exception e) {
            logger.error("Failed to create BrowserStack driver", e);
            throw new RuntimeException("Failed to create BrowserStack driver on runtime", e);
        }
    }

    // Method to handle Local Appium driver creation
    private static AndroidDriver createLocalDriver() {
        System.setProperty("log4j.configurationFile", "src/test/resources/config/log4j2.xml");
        System.setProperty("log4j.info", "true");
        try {

            if (!DeviceUtils.isAnyDeviceConnected()) {
                System.out.println("Logger effective level: " + logger.getLevel());

                logger.info("No Android device connected. Aborting driver creation by logger");
                throw new RuntimeException("No Android device connected. Aborting driver creation.");
            } else {

                logger.info("Android device detected. Proceeding to create AndroidDriver.");

                String host = ConfigReader.getInstance().getProperty("appium.host");
                String port = ConfigReader.getInstance().getProperty("appium.port");
                String serverUrl = "http://" + host + ":" + port;  // Ensure the correct URL path

                URI uri = URI.create(serverUrl);
                URL url = uri.toURL();

                // Dynamically loading local capabilities
                AndroidDriver driver = new AndroidDriver(url, new CapabilityManager().getAndroidCapabilities());

                logger.info("Successfully created local AndroidDriver.");
                return driver;
            }
        } catch (Exception e) {
            logger.error("Failed to create local AndroidDriver", e);
            throw new RuntimeException("Failed to create local AndroidDriver", e);
        }
    }

}
