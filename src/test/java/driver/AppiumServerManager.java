package driver;

import com.fasterxml.jackson.databind.JsonNode;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.ConfigReader;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class AppiumServerManager {

    private static final Logger logger = LogManager.getLogger(AppiumServerManager.class);

    private static AppiumServerManager instance;
    private AppiumDriverLocalService service;

    private AppiumServerManager() {
    }

    public static AppiumServerManager getInstance() {
        if (instance == null) {
            synchronized (AppiumServerManager.class) {
                if (instance == null) {
                    instance = new AppiumServerManager();
                }
            }
        }
        return instance;
    }

    //working on this
    public void startServer(boolean showLogs) {
        ConfigReader.SHOW_APPIUM_LOGS = showLogs;
        startServer();  // Reuse your main method
    }


    public void startServer() {

        String env = ConfigReader.getInstance().getProperty("appium.server.env");

        if (env == null || env.isEmpty()) {
            logger.error("Appium server environment is not specified in config.properties - appium.server.env");
            throw new RuntimeException("Appium server environment is not specified in config.properties - appium.server.env");
        }

        // If the environment is set to browserstack, skip local server start
        if ("browserstack".equalsIgnoreCase(env)) {
            logger.info("Using BrowserStack, no need to start a local Appium server.");
            return;  // Skip the server start for BrowserStack
        }

        if ("local".equalsIgnoreCase(env)) {
            try {
                JsonNode config = ConfigReader.getInstance().loadAppiumJsonConfig(env);
                String host = config.get("host").asText();
                int port = config.get("port").asInt();

                if (service == null || !service.isRunning()) {

                    if (isPortInUse(host, port)) {
                        logger.warn("Appium port {} already in use. Assuming Appium Server is already running at {}:{}", port, host, port);
                        return;
                    }

                    AppiumServiceBuilder builder = AppiumServiceBuilderFactory.create(config);

                    //working on this
                    if (!ConfigReader.SHOW_APPIUM_LOGS) {
                        builder.withArgument(() -> "--log-level", "error")
                                .withLogFile(new File("test-output-result/appium_logs/appium-server.log"));;
                    }

                    service = AppiumDriverLocalService.buildService(builder);
                    service.start();

                    if (service.isRunning()) {
                        logger.info("Appium Server started successfully at {}", service.getUrl());
                    } else {
                        logger.error("Appium Server failed to start.");
                        throw new RuntimeException("Appium server failed to start.");
                    }

                } else {
                    logger.warn("Appium Server already running. Skipping start.");
                }
            } catch (Exception e) {
                logger.error("Failed to start Appium server.", e);
                throw new RuntimeException("Failed to start Appium server", e);
            }
        } else {
            logger.error("Invalid appium.server.env value. Expected 'local' or 'browserstack'.");
            throw new RuntimeException("Invalid appium.server.env value. Expected 'local' or 'browserstack'.");
        }
    }

    public void stopServer() {
        String env = ConfigReader.getInstance().getProperty("appium.server.env");

        // For BrowserStack, there's nothing to stop
        if ("browserstack".equalsIgnoreCase(env)) {
            logger.info("Using BrowserStack. No need to stop Appium server.");
            return;
        }

        if (service != null && service.isRunning()) {
            service.stop();
            logger.info("Appium Server stopped.");
        }
    }

    private boolean isPortInUse(String host, int port) {
        try (Socket socket = new Socket(host, port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
