package base;

import driver.AppiumServerManager;
import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AppiumServerHooks {

    private static final Logger logger  = LogManager.getLogger(AppiumServerHooks.class);

    @BeforeAll
    public static void beforeAll() {
        logger .info("🚀 Starting Appium Server before all scenarios...");
        AppiumServerManager.getInstance().startServer();
    }

    @AfterAll
    public static void afterAll() {
        logger .info("🛑 Stopping Appium Server after all scenarios...");
        AppiumServerManager.getInstance().stopServer();
    }
}
