package driver;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DriverManager {

    private static final Logger logger = LogManager.getLogger(DriverManager.class);
    private static final ThreadLocal<AndroidDriver> driverThreadLocal = new ThreadLocal<>();

    public static void setDriver(AndroidDriver driver) {
        logger.debug("Setting driver for thread: {}", Thread.currentThread().threadId());
        driverThreadLocal.set(driver);
    }

    public static AppiumDriver getDriver() {
        AppiumDriver driver = driverThreadLocal.get();
        if (driver == null) {
            throw new IllegalStateException("Driver not initialized for current thread. Call setDriver() first.");
        }
        return driver;
//        return driverThreadLocal.get();
    }

    public static void unload() {
        if (driverThreadLocal.get() != null) {
            driverThreadLocal.remove();
            logger.info("Driver instance unloaded from the current thread.");
        }
    }
}
