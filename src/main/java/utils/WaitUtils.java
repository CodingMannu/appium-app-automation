package utils;

import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;

import java.time.Duration;

/**
 * Professional wait utility class for mobile automation.
 * Works for Android and iOS (TTA/Pro App).
 */
public class WaitUtils {

    private static final Logger logger = LogManager.getLogger(WaitUtils.class);

    private final AppiumDriver driver;
    private final int defaultTimeoutSeconds;
    private final int defaultPollingMillis;

    public WaitUtils(AppiumDriver driver) {
        this.driver = driver;
        this.defaultTimeoutSeconds = Integer.parseInt(
                ConfigReader.getInstance().getProperty("default.wait.seconds", "10"));
        this.defaultPollingMillis = Integer.parseInt(
                ConfigReader.getInstance().getProperty("default.wait.polling.millis", "500"));
    }

    private FluentWait<AppiumDriver> createFluentWait(int seconds) {
        return new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(seconds))
                .pollingEvery(Duration.ofMillis(defaultPollingMillis))
                .ignoring(Exception.class);
    }


    public WebElement waitForElementToBeVisible(By locator, int seconds) {
        try {
            WebElement el = createFluentWait(seconds).until(ExpectedConditions.visibilityOfElementLocated(locator));
            logger.info("Element visible: {}", locator);
            return el;
        } catch (TimeoutException e) {
            logger.error("Timeout waiting for visibility of: {}", locator, e);
            throw e;
        }
    }

    public WebElement waitForElementToBeVisible(By locator) {
        return waitForElementToBeVisible(locator, defaultTimeoutSeconds);
    }


    public WebElement waitForElementToBeVisible(WebElement element, int seconds) {
        try {
            WebElement el = createFluentWait(seconds).until(ExpectedConditions.visibilityOf(element));
            logger.info("WebElement visible: {}", element);
            return el;
        } catch (TimeoutException e) {
            logger.error("Timeout waiting for WebElement visibility of: {}", element, e);
            throw e;
        }
    }

    public WebElement waitForElementToBeVisible(WebElement element) {
        return waitForElementToBeVisible(element, defaultTimeoutSeconds);
    }


    public WebElement waitForElementToBeClickable(By locator, int seconds) {
        try {
            WebElement el = createFluentWait(seconds).until(ExpectedConditions.elementToBeClickable(locator));
            logger.info("Element clickable: {}", locator);
            return el;
        } catch (TimeoutException e) {
            logger.error("Timeout waiting for clickability of: {}", locator, e);
            throw e;
        }
    }

    public WebElement waitForElementToBeClickable(By locator) {
        return waitForElementToBeClickable(locator, defaultTimeoutSeconds);
    }


    public WebElement waitForElementToBeClickable(WebElement element, int seconds) {
        try {
            WebElement el = createFluentWait(seconds).until(ExpectedConditions.elementToBeClickable(element));
            logger.info("WebElement clickable: {}", element);
            return el;
        } catch (TimeoutException e) {
            logger.error("Timeout waiting for WebElement clickability of: {}", element);
            throw e;
        }
    }


    public WebElement waitForElementToBeClickable(WebElement element) {
        return waitForElementToBeClickable(element, defaultTimeoutSeconds);
    }


    public boolean isElementVisible(By locator, int seconds) {
        try {
            createFluentWait(seconds).until(ExpectedConditions.visibilityOfElementLocated(locator));
            logger.info("Element is visible: {}", locator);
            return true;
        } catch (TimeoutException e) {
            logger.info("Element not visible in {} seconds: {}", seconds, locator);
            return false;
        }
    }

    public boolean isElementClickable(By locator, int seconds) {
        try {
            createFluentWait(seconds).until(ExpectedConditions.elementToBeClickable(locator));
            logger.info("Element is clickable: {}", locator);
            return true;
        } catch (TimeoutException e) {
            logger.info("Element not clickable in {} seconds: {}", seconds, locator);
            return false;
        }
    }

    public boolean waitForInvisibility(By locator, int seconds) {
        try {
            createFluentWait(seconds).until(ExpectedConditions.invisibilityOfElementLocated(locator));
            logger.info("Element is invisible: {}", locator);
            return true;
        } catch (TimeoutException e) {
            logger.info("Element still visible after {} seconds: {}", seconds, locator);
            return false;
        }
    }

    public WebElement waitForPresenceOfElement(By locator, int seconds) {
        try {
            WebElement el = createFluentWait(seconds).until(ExpectedConditions.presenceOfElementLocated(locator));
            logger.info("Element present in DOM: {}", locator);
            return el;
        } catch (TimeoutException e) {
            logger.error("Timeout waiting for presence of element: {}", locator, e);
            throw e;
        }
    }

    public boolean waitForTextToBe(By locator, String text, int seconds) {
        try {
            boolean result = createFluentWait(seconds).until(ExpectedConditions.textToBe(locator, text));
            logger.info("Text '{}' is present in element: {}", text, locator);
            return result;
        } catch (TimeoutException e) {
            logger.error("Timeout waiting for text '{}' in element: {}", text, locator, e);
            return false;
        }
    }

    public boolean waitForAttributeToContain(By locator, String attribute, String value, int seconds) {
        try {
            boolean result = createFluentWait(seconds).until(ExpectedConditions.attributeContains(locator, attribute, value));
            logger.info("Attribute '{}' contains '{}' in element: {}", attribute, value, locator);
            return result;
        } catch (TimeoutException e) {
            logger.error("Timeout waiting for attribute '{}' to contain '{}' in element: {}", attribute, value, locator, e);
            return false;
        }
    }

    public <T> T waitForCondition(ExpectedCondition<T> condition, int seconds) {
        try {
            T result = createFluentWait(seconds).until(condition);
            logger.info("Custom wait condition succeeded.");
            return result;
        } catch (TimeoutException e) {
            logger.error("Timeout waiting for custom condition.", e);
            throw e;
        }
    }


    //working on this
    public By waitForAnyElementVisible(By[] locators, int seconds) {
        long startTime = System.currentTimeMillis();

        while ((System.currentTimeMillis() - startTime) < seconds * 1000L) {
            for (By locator : locators) {
                if (isElementVisible(locator, 2)) {
                    logger.info("Found visible element: {}", locator);
                    return locator;
                }
            }
        }
        logger.warn("None of the locators were visible after {} seconds.", seconds);
        return null;
    }
    //================


    public static void executionDelay(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
            logger.info("Execution delayed for {} seconds.", seconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Static wait interrupted in executionDelay.");
        }
    }
}
