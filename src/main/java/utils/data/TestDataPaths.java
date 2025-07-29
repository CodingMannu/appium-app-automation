package utils.data;

import java.nio.file.Paths;

/**
 * Holds file paths to test data JSON files.
 * Ensures all test data file paths are centralized.
 */
public final class TestDataPaths {

    private static final String BASE_PATH =
            Paths.get(System.getProperty("user.dir"), "src", "test", "resources", "testdata").toString();

    public static final String LOGIN_PATH =
            Paths.get(BASE_PATH, "Login.json").toString();
    public static final String PROMO_PATH =
            Paths.get(BASE_PATH, "Promo.json").toString();
    public static final String RECHARGE_PATH =
            Paths.get(BASE_PATH, "Recharge.json").toString();
    public static final String GLOBAL_PATH =
            Paths.get(BASE_PATH, "GlobalToastMessage.json").toString();
    public static final String ASTROLOGER_PATH =
            Paths.get(BASE_PATH, "Astrologer.json").toString();

    private TestDataPaths() {
        // Prevent instantiation
    }
}
