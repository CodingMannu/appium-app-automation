package utils;

import io.cucumber.java.Scenario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import java.io.File;

public class LogContextUtil {

    private static final Logger logger = LogManager.getLogger(LogContextUtil.class);

    public static void setupLogContext(Scenario scenario) {
        // Get feature file URI to detect app/platform
        String featureUri = scenario.getUri().toString();

        String appName;
        String platform;

        if (featureUri.contains("/tta/")) {
            appName = "TTA";
        } else if (featureUri.contains("/pro/")) {
            appName = "Pro";
        } else {
            appName = "UnknownApp";
        }

        if (featureUri.contains("/android/")) {
            platform = "Android";
        } else if (featureUri.contains("/ios/")) {
            platform = "iOS";
        } else {
            platform = "UnknownPlatform";
        }

        // Clean scenario name to safe file name
        String scenarioName = scenario.getName().replaceAll("[^a-zA-Z0-9]", "_");

        // These values are critical for Routing Appender to resolve file paths
        ThreadContext.put("app", appName);
        ThreadContext.put("platform", platform);
        ThreadContext.put("logFilename", scenarioName);

        // Ensure folders exist
        String logFolderPath = String.format("./test-output-result/logs/%s/%s/", appName, platform);
        File dir = new File(logFolderPath);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (created) {
                logger.info("Log folder created at: {}", dir.getAbsolutePath());
            } else {
                logger.error("Failed to create log folder: {}", dir.getAbsolutePath());
            }
        }
    }

    public static void clearLogContext() {
        ThreadContext.clearAll();
    }
}
