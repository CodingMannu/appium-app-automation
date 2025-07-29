package utils;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility for capturing screenshots from WebDriver or AppiumDriver instances.
 */
public final class ScreenshotUtil {

    private static final Logger logger = LogManager.getLogger(ScreenshotUtil.class);

    private static final String SCREENSHOT_DIR =
            System.getProperty("user.dir") + "/test-output-result/screenshots/";

    private ScreenshotUtil() {
        // Prevent instantiation
    }

    /**
     * Captures a screenshot from a driver instance that implements TakesScreenshot.
     *
     * @param driver          any driver implementing TakesScreenshot
     * @param screenshotName  desired name for the screenshot file
     * @return path to the saved screenshot file, or null if failure
     */
    public static String captureScreen(TakesScreenshot driver, String screenshotName) {
        String timestamp = new SimpleDateFormat("dd-MMM-yy_HH-mm-ss").format(new Date());

        Path dirPath = Paths.get(SCREENSHOT_DIR);
        Path screenshotPath = dirPath.resolve(timestamp + "_" + screenshotName + ".png");

        try {
            File dir = dirPath.toFile();
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (created) {
                    logger.info("Created screenshot directory: {}", SCREENSHOT_DIR);
                }
            }

            File srcFile = driver.getScreenshotAs(OutputType.FILE);
            File destFile = screenshotPath.toFile();

            FileUtils.copyFile(srcFile, destFile);
            logger.info("Screenshot saved at: {}", screenshotPath.toAbsolutePath());

            return screenshotPath.toAbsolutePath().toString();

        } catch (IOException e) {
            logger.error("Failed to save screenshot due to IO error: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error capturing screenshot: {}", e.getMessage(), e);
        }
        return null;
    }
}
