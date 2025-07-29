package driver;

import com.fasterxml.jackson.databind.JsonNode;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class AppiumServiceBuilderFactory {

    private static final Logger logger = LogManager.getLogger(AppiumServiceBuilderFactory.class);

    public static AppiumServiceBuilder create(JsonNode config) {
        String host = config.get("host").asText();
        int port = config.get("port").asInt();

        AppiumServiceBuilder builder = new AppiumServiceBuilder()
                .withIPAddress(host)
                .usingPort(port)
                .withArgument(GeneralServerFlag.LOG_LEVEL, "info")
                .withArgument(GeneralServerFlag.LOG_TIMESTAMP)
                .withArgument(GeneralServerFlag.LOG_NO_COLORS);

        if (config.has("sessionOverride") && config.get("sessionOverride").asBoolean()) {
            builder.withArgument(GeneralServerFlag.SESSION_OVERRIDE);

        }

        if (config.has("adbExecTimeout")) {
//            builder.withArgument(() -> "--adb-exec-timeout", config.get("adbExecTimeout").asText());
        }

        return builder;
    }

    private static void ensureFolderExists(String logPath) {
        File file = new File(logPath);
        File folder = file.getParentFile();

        if (folder != null && !folder.exists()) {
            boolean created = folder.mkdirs();
            if (created) {
                logger.info("Created folder for Appium logs: {}", folder.getAbsolutePath());
            } else {
                logger.warn("Could not create Appium log folder: {}", folder.getAbsolutePath());
            }
        }
    }
}
