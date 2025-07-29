package apps.tta.android.pages;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.URL;

public class BrowserStackTest {

    public static final String USERNAME = "manojkumar_vHbMUx"; // Replace with your BrowserStack username
    public static final String ACCESS_KEY = "BwzKychFa4BwbFcWdS6P"; // Replace with your BrowserStack access key
    public static final String APP_URL = "bs://2d07987dc9c692c7585918cc892a71bcb3968279"; // Replace with your uploaded app URL or ID

    public static void main(String[] args) {
        try {
            DesiredCapabilities caps = new DesiredCapabilities();

            // BrowserStack credentials
            caps.setCapability("browserstack.user", USERNAME);
            caps.setCapability("browserstack.key", ACCESS_KEY);

            // Uploaded App
            caps.setCapability("app", APP_URL);

            // Device details
            caps.setCapability("device", "Samsung Galaxy S23 Ultra");
            caps.setCapability("os_version", "14.0");

            // Project details
            caps.setCapability("project", "First Appium Project");
            caps.setCapability("build", "Java Appium BrowserStack");
            caps.setCapability("name", "first_test");

            // Appium version (W3C compliant)
            caps.setCapability("browserstack.appium_version", "2.0.1");

            // W3C Protocol
            caps.setCapability("bstack:options", new java.util.HashMap<String, Object>());

            // Start session
            AppiumDriver driver = new AndroidDriver(
                    new URL("https://hub-cloud.browserstack.com/wd/hub"), caps
            );

            Thread.sleep(5000); // Let the app launch

            System.out.println("Launched App on BrowserStack successfully");

            // TODO: Add your test steps here, e.g., driver.findElement(By.id("elementId")).click();

            driver.quit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
