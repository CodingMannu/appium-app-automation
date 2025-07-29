package base;

import driver.DriverFactory;
import driver.DriverManager;
import io.appium.java_client.android.AndroidDriver;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.LogContextUtil;
import utils.WaitUtils;

public class Hooks {

    private static final Logger logger = LogManager.getLogger(Hooks.class);

    @Before
    public void setUp(Scenario scenario) throws InterruptedException {
        try {
            // Set logging context: logFilename, app, platform
            LogContextUtil.setupLogContext(scenario);

            logger.info("=== BEFORE SCENARIO: {} ===", scenario.getName());

            // Create driver using factory
            AndroidDriver driver = new DriverFactory().createDriver();
            DriverManager.setDriver(driver);

            WaitUtils.executionDelay(10);

            logger.info("Driver initialized and app launch ready.");
        } catch (Exception exception) {
            logger.error("Error during setup for scenario: {}", scenario.getName(), exception);
            throw exception;
        }
    }

    @After
    @SuppressWarnings("ConstantConditions")
    public void tearDown(Scenario scenario) {
        try {
            logger.info("=== AFTER SCENARIO: {} ===", scenario.getName());
            logger.info("Scenario status: {}", scenario.getStatus());

            if (DriverManager.getDriver() != null) {
                DriverManager.getDriver().quit();
                logger.info("AndroidDriver quit successfully.");
            }
        } catch (Exception e) {
            logger.error("Error during teardown of scenario: {}", scenario.getName(), e);
        } finally {
            DriverManager.unload();
            LogContextUtil.clearLogContext();
            logger.info("Thread context and driver cleaned up.");
        }
    }
}
