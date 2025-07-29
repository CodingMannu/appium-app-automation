package base;

import driver.DriverManager;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.MobileActions;
import utils.ScreenshotUtil;
import utils.WaitUtils;

import java.time.Duration;

public abstract class TestBase {

    protected final Logger logger = LogManager.getLogger(this.getClass());
    protected AppiumDriver driver;

    public TestBase(AppiumDriver driver) {
        this.driver = driver;
        PageFactory.initElements(new AppiumFieldDecorator(this.driver), this);
        logger.debug("Initialized page: {}", this.getClass().getSimpleName());
    }

    // Thread-safe driver access
    protected AppiumDriver driver() {
        return DriverManager.getDriver();
    }

    // Method to safely return AndroidDriver instance
    protected AndroidDriver androidDriver() {
        AppiumDriver driver = driver();  // Get the generic AppiumDriver
        if (driver instanceof AndroidDriver) {
            return (AndroidDriver) driver;  // Safe cast if it's an instance of AndroidDriver
        } else {
            logger.error("Driver is not an instance of AndroidDriver.");
            throw new RuntimeException("Driver is not of type AndroidDriver.");
        }
    }

    protected WaitUtils waitUtils() {
        return new WaitUtils(androidDriver());  // Use AndroidDriver here
    }

    protected MobileActions mobileActions() {
        return new MobileActions(androidDriver());  // Use AndroidDriver here
    }

    protected void captureScreenshot(String fileName) {
        ScreenshotUtil.captureScreen(driver(), fileName);
    }

}

