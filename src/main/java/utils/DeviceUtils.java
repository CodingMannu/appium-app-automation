package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Utility class for interacting with connected Android devices via ADB.
 */
public class DeviceUtils {

    private static final Logger logger = LogManager.getLogger(DeviceUtils.class);

    private static final Object ADB_LOCK = new Object();


    private DeviceUtils() {
        throw new UnsupportedOperationException("Utility class — cannot be instantiated.");
    }

    /**
     * Checks whether any Android device is connected via ADB.
     *
     * @return true if at least one device is connected; false otherwise
     */
    public static boolean isAnyDeviceConnected() {
        synchronized (ADB_LOCK) {
            Process process = null;
            try {
                ProcessBuilder builder = new ProcessBuilder("adb", "devices");
                builder.redirectErrorStream(true);
                process = builder.start();

                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()))) {

                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.endsWith("\tdevice")) {
                            logger.info("Found connected device.");
                            return true;
                        }
                    }
                }

                logger.warn("No devices connected.");
                return false;

            } catch (Exception e) {
                logger.error("Error while checking connected devices via ADB.", e);
                return false;
            } finally {
                if (process != null) {
                    process.destroy();
                }
            }
        }
    }
}