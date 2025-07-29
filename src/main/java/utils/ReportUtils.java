package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;

public class ReportUtils {

    private static final Logger logger = LogManager.getLogger(ReportUtils.class);

    public static String getLatestReportFilePath() {
        String reportsDir = Paths.get(System.getProperty("user.dir"), "test-output-result", "reports").toString();
        File folder = new File(reportsDir);

        if (!folder.exists() || folder.listFiles() == null) {
            logger.error("Reports directory not found: {}", reportsDir);
            return null;
        }

        return Arrays.stream(folder.listFiles((dir, name) -> name.endsWith(".html")))
                .max(Comparator.comparingLong(File::lastModified))
                .map(File::getAbsolutePath)
                .orElse(null);
    }
}
