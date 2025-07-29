package setup;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.List;


public class SetupForAndroid_one {

    private static final Logger logger = LogManager.getLogger(SetupForAndroid_one.class);


    public static void main(String[] args) {
        new SetupForAndroid_one().validateSetup();
    }

    public void validateSetup() {
        try {
            // Appium server URL
            String serverUrl = "http://127.0.0.1:4723/";

            // Device and app configuration
            String platformName = "Android";
            String deviceName = "Redmi 12 5G";
            String platformVersion = "15";
            String udid = "1356388158a9";
            String automationName = "UiAutomator2";
            boolean noReset = true;
            int newCommandTimeout = 60;
            int newAdbExecTimeout = 60;
            String appPath = new File("src/test/resources/apps/tta/android/app-debug.apk").getAbsolutePath();

            String appPackage = "com.netway.phone.advice";
            String appActivity = "com.netway.phone.advice.javaclass.newSplashScreen";

            // 1. Check Appium server running
            if (!isAppiumServerRunning(serverUrl)) {
                logger.error("Appium server is NOT running at: {}", serverUrl);
                return;
            }
            logger.info("Appium server is running at: {}", serverUrl);



            // 2. Check if device connected
            if (!isAnyDeviceConnected()) {
                logger.error("No Android devices connected via ADB!");
                return;
            }
            logger.info("Android device is connected via ADB.");

            // 3. Check if APK exists
            if (!isFileExists(appPath)) {
                logger.error("APK file does NOT exist at: {}", appPath);
                return;
            }
            logger.info("APK file exists at: {}", appPath);

            // 4. Set capabilities
            UiAutomator2Options options = new UiAutomator2Options()
                    .setPlatformName(platformName)
                    .setDeviceName(deviceName)
                    .setPlatformVersion(platformVersion)
                    .setUdid(udid)
                    .setAutomationName(automationName)
                    .setNoReset(noReset)
                    .setNewCommandTimeout(Duration.ofSeconds(newCommandTimeout))
                    .setAdbExecTimeout(Duration.ofSeconds(newAdbExecTimeout))
                    .setIgnoreHiddenApiPolicyError(true)
                    .setApp(appPath)
                    .setAppPackage(appPackage)
                    .setAppWaitPackage(appPackage)
                    .setAppActivity(appActivity)
                    .setAppWaitActivity(appActivity)
                    .setAppWaitDuration(Duration.ofSeconds(90));

            logger.info("Desired Capabilities set successfully: {}", options);



            AndroidDriver driver;
            try {
                URI uri = URI.create(serverUrl);
                URL url = uri.toURL();
                driver = new AndroidDriver(url, options);
                logger.info("AndroidDriver session created successfully at: {}", serverUrl);

                //Ensure app is closed if running
                if (driver.isAppInstalled("com.netway.phone.advice")) {
                    driver.terminateApp("com.netway.phone.advice");
                    logger.info("App terminated if running.");
                }

                //Activate the app again
                driver.activateApp("com.netway.phone.advice");
                logger.info("App activated again.");

                Thread.sleep(10000);
            } catch (MalformedURLException e) {
                logger.error("Failed to create AndroidDriver session", e);
                return;
            }

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
//            Thread.sleep(5000);

            logger.info("Current activity: {}", driver.currentActivity());
            logger.info("Trying to enter mobile number...");

            // Locators
            By mobileInputLocator = By.id("com.netway.phone.advice:id/etvMobileNumber");
            By signUpButtonLocator = By.id("com.netway.phone.advice:id/account");

            // Check if mobile input already visible → means we’re on Sign Up page
            List<WebElement> mobileInputs = driver.findElements(mobileInputLocator);
            if (!mobileInputs.isEmpty()) {
                logger.info("We are already on Sign Up screen. Mobile input found: {}", mobileInputs.getFirst().getText());
            } else {
                logger.info("We are NOT on Sign Up screen. Trying to click Sign Up button on home screen...");


                try {
                    WebElement signUpButton = wait.until(ExpectedConditions.elementToBeClickable(signUpButtonLocator));
                    signUpButton.click();

                    logger.info("Current activity: {}", driver.currentActivity());
                    logger.info("Clicked Sign Up button from home page.");

                    //Wait for Sign Up page to load
                    wait.until(ExpectedConditions.visibilityOfElementLocated(mobileInputLocator));
                    logger.info("Sign Up screen loaded after clicking footer button.");

                } catch (Exception e) {
                    logger.error("Failed to click Sign Up button: {}", e.getMessage());
                    // optionally, exit here or proceed depending on your logic
                    driver.quit();
                    return;
                }
            }

            // Now proceed with Sign Up flow (same steps for both cases)
            logger.info("Trying to enter mobile number...");

            WebElement mobileInput = wait.until(ExpectedConditions.visibilityOfElementLocated(mobileInputLocator));
            mobileInput.sendKeys("9289656187");
            logger.info("Mobile number entered successfully.");

            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("com.netway.phone.advice:id/tvLogin")));
            loginButton.click();
            logger.info("Login button clicked.");

            WebElement otpInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("com.netway.phone.advice:id/lineField")));
            otpInput.sendKeys("123456");
            logger.info("OTP entered successfully.");

            Thread.sleep(5000);

            driver.quit();
            logger.info("AndroidDriver session closed cleanly.");

        } catch (Exception e) {
            logger.error("Setup validation failed", e);
        }
    }

    private boolean isAppiumServerRunning(String serverUrl) {
        try {
//            URL url = new URL(serverUrl + "status");
            URI uri = URI.create(serverUrl + "status");
            URL url = uri.toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(3000);
            connection.connect();
            return connection.getResponseCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isAnyDeviceConnected() {
        try {
            ProcessBuilder builder = new ProcessBuilder("adb", "devices");
            Process process = builder.start();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.endsWith("\tdevice")) {
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isFileExists(String path) {
        return new File(path).exists();
    }




/*

public class DriverFactory {

    private static final Logger logger = LogManager.getLogger(DriverFactory.class);

    public static AndroidDriver createDriver() {
        try {
            // Check if we are using BrowserStack or Local device
            String environment = ConfigReader.getInstance().getProperty("appium.server.env");

            if ("browserstack".equals(environment)) {
                // Use BrowserStack for testing
                logger.info("Initializing BrowserStack driver");

                URL browserStackURL = new URL(ConfigReader.getInstance().getProperty("appium.server.url"));

                // Use BrowserStack credentials and capabilities
                CapabilityManager capabilityManager = new CapabilityManager();
                UiAutomator2Options options = capabilityManager.getBrowserStackAndroidCapabilities();

                return new AndroidDriver(browserStackURL, options);
            } else {
                // Fallback to local Appium server for device testing
                String host = ConfigReader.getInstance().getProperty("appium.host");
                String port = ConfigReader.getInstance().getProperty("appium.port");
                String serverUrl = "http://" + host + ":" + port;

                URI uri = URI.create(serverUrl);
                URL url = uri.toURL();

                // Use local device capabilities
                return new AndroidDriver(url, new CapabilityManager().getAndroidCapabilities());
            }
        } catch (Exception e) {
            logger.error("Failed to initialize AndroidDriver", e);
            throw new RuntimeException("Failed to initialize AndroidDriver", e);
        }
    }
}

*/











}




