package utils;

import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.time.Duration;
import java.util.List;

/**
 * Provides reusable mobile actions such as tap, scroll, sendKeys, etc.
 * Uses W3C actions and supports Android. Compatible with Appium Java Client v8+.
 */
public class MobileActions {

    private static final Logger logger = LogManager.getLogger(MobileActions.class);

    private final AppiumDriver driver;
    private final WaitUtils waitUtils;

    /**
     * Constructor to initialize driver and wait utility.
     * @param driver the Appium driver instance
     */
    public MobileActions(AppiumDriver driver) {
        this.driver = driver;
        this.waitUtils = new WaitUtils(driver);
    }

    /** --------------------------- Basic Element Interactions --------------------------- **/

    public WebElement findById(String id) {
        try {
            WebElement el = driver.findElement(By.id(id));
            logger.info("Element found by ID: {}", id);
            return el;
        } catch (Exception e) {
            logger.error("Failed to find element by ID: {}", id, e);
            throw e;
        }
    }

    public WebElement findByXpath(String xpath) {
        try {
            WebElement el = driver.findElement(By.xpath(xpath));
            logger.info("Element found by XPath: {}", xpath);
            return el;
        } catch (Exception e) {
            logger.error("Failed to find element by XPath: {}", xpath, e);
            throw e;
        }
    }

    public void tapElement(WebElement element) {
        try {
            element.click();
            logger.info("Tapped on element: {}", element);
        } catch (Exception e) {
            logger.error("Failed to tap on element: {}", element, e);
            throw e;
        }
    }

    public void tapElement(By locator, int timeoutSeconds) {
        WebElement element = waitUtils.waitForElementToBeVisible(locator, timeoutSeconds);
        tapElement(element);
    }

    public void sendKeys(WebElement element, String text) {
        try {
            element.clear();
            element.sendKeys(text);
            logger.info("Sent text '{}' to element: {}", text, element);
        } catch (Exception e) {
            logger.error("Failed to send text to element: {}", element, e);
            throw e;
        }
    }

    /** --------------------------- Gesture: Tap Anywhere --------------------------- **/

    /**
     * Tap anywhere on screen using W3C PointerInput. Useful for dismissing overlays.
     * @param x X-coordinate
     * @param y Y-coordinate
     */
    public void tapOnCoordinates(int x, int y) {
        try {
            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
            Sequence tap = new Sequence(finger, 0);
            tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y));
            tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            tap.addAction(new Pause(finger, Duration.ofMillis(100)));
            tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

            driver.perform(List.of(tap));
            logger.info("Tapped on screen at coordinates ({}, {})", x, y);
        } catch (Exception e) {
            logger.error("Failed to tap on coordinates ({}, {}): {}", x, y, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Tap center of screen (used to dismiss black overlay)
     */
    public void tapCenterOfScreen() {
        Dimension size = driver.manage().window().getSize();
        int x = size.width / 2;
        int y = size.height / 2;
        tapOnCoordinates(x, y);
    }

    /** --------------------------- Gesture: Scroll/Swipe --------------------------- **/

    public void scrollByCoordinates(int startX, int startY, int endX, int endY, int durationInMs) {
        try {
            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
            Sequence swipe = new Sequence(finger, 0);

            swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
            swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            swipe.addAction(finger.createPointerMove(Duration.ofMillis(durationInMs), PointerInput.Origin.viewport(), endX, endY));
            swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

            driver.perform(List.of(swipe));
            logger.info("Swipe performed from ({}, {}) to ({}, {})", startX, startY, endX, endY);
        } catch (Exception e) {
            logger.error("Scroll failed: {}", e.getMessage(), e);
            throw e;
        }
    }

    public void scrollDown(int distancePixels) {
        Dimension size = driver.manage().window().getSize();
        int startX = size.width / 2;
        int startY = (int) (size.height * 0.8);
        int endY = startY - distancePixels;

        scrollByCoordinates(startX, startY, startX, endY, 500);
    }

    public void scrollUp(int distancePixels) {
        Dimension size = driver.manage().window().getSize();
        int startX = size.width / 2;
        int startY = (int) (size.height * 0.2);
        int endY = startY + distancePixels;

        scrollByCoordinates(startX, startY, startX, endY, 500);
    }
}
