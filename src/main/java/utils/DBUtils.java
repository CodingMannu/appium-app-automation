package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

/**
 * Utility class for DB operations.
 * This class is purely static and has no dependency on Appium or TestBase.
 */
public class DBUtils {

    private static final Logger logger = LogManager.getLogger(DBUtils.class);

    /**
     * Establishes a DB connection using config values.
     */
    private static Connection connectToDB() throws SQLException {
        String url = ConfigReader.getInstance().getProperty("db.url");
        String user = ConfigReader.getInstance().getProperty("db.user");
        String password = ConfigReader.getInstance().getProperty("db.pass");

        logger.info("Connecting to DB: {}", url);
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Fetches the OTP for the given mobile number with retry support.
     */
    public static String getOTP(String mobileNumber) {
        String otp = null;

        int maxRetries = Integer.parseInt(
                ConfigReader.getInstance().getProperty("otp.max.retries", "3"));
        int delaySeconds = Integer.parseInt(
                ConfigReader.getInstance().getProperty("otp.retry.delay.seconds", "1"));

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            if (attempt > 1) {
                sleep(delaySeconds);  // Small wait before next attempt
            }

            try (Connection connection = connectToDB();
                 PreparedStatement ps = connection.prepareStatement(
                         "SELECT TOP 1 OtpNumber " +
                                 "FROM [Demographics].MessageOTP " +
                                 "WHERE MobileNumber = ? " +
                                 "ORDER BY ModifiedDate DESC")) {

                ps.setString(1, mobileNumber);
                logger.debug("Executing OTP query for mobile: {}", mobileNumber);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        otp = rs.getString("OtpNumber");
                        logger.info("OTP fetched successfully on attempt {}: {}", attempt, otp);
                        return otp;
                    } else {
                        logger.warn("Attempt {}: No OTP found for mobile number: {}", attempt, mobileNumber);
                    }
                }

            } catch (SQLException e) {
                logger.error("DB error fetching OTP (Attempt {}) for mobile {}: {}", attempt, mobileNumber, e.getMessage(), e);
            }
        }

        throw new DatabaseOperationException(
                "OTP not found after " + maxRetries + " attempts for mobile number: " + mobileNumber, null);
    }

    /**
     * Updates a user's wallet amount in the DB based on phone number.
     */
    public static void updateWalletAmountByPhone(String mobileNumber, String amount) {
        try (Connection connection = connectToDB()) {

            int phoneNumberId = getPhoneNumberId(connection, mobileNumber);
            int userLoginId = getUserLoginId(connection, phoneNumberId);

            try (PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE [Transaction].[UserCalculatedWallet] " +
                            "SET Amount = ? WHERE Currency = 'INR' AND UserLoginId = ?")) {
                stmt.setString(1, amount);
                stmt.setInt(2, userLoginId);

                int updatedRows = stmt.executeUpdate();
                if (updatedRows > 0) {
                    logger.info("Wallet updated successfully for UserLoginId {} with amount {}", userLoginId, amount);
                } else {
                    logger.warn("Wallet update failed. No matching record for UserLoginId: {}", userLoginId);
                }
            }

        } catch (SQLException e) {
            logger.error("Error updating wallet for mobile: {}", mobileNumber, e);
            throw new DatabaseOperationException(
                    "Failed to update wallet for mobile number: " + mobileNumber, e);
        }
    }

    /**
     * Helper to fetch PhoneNumberId.
     */
    private static int getPhoneNumberId(Connection connection, String mobileNumber) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT PhoneNumberId FROM [Demographics].[PhoneNumber] WHERE Number = ?")) {
            stmt.setString(1, mobileNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("PhoneNumberId");
                    logger.info("PhoneNumberId fetched: {}", id);
                    return id;
                } else {
                    throw new DatabaseOperationException("PhoneNumberId not found for number: " + mobileNumber, null);
                }
            }
        }
    }

    /**
     * Helper to fetch UserLoginId.
     */
    private static int getUserLoginId(Connection connection, int phoneNumberId) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT UserLoginId FROM [User].[UserLogin] WHERE PhoneNumberId = ?")) {
            stmt.setInt(1, phoneNumberId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("UserLoginId");
                    logger.info("UserLoginId fetched: {}", id);
                    return id;
                } else {
                    throw new DatabaseOperationException(
                            "UserLoginId not found for PhoneNumberId: " + phoneNumberId, null);
                }
            }
        }
    }

    /**
     * Local sleep method for retry logic.
     */
    private static void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
            logger.info("Waiting {} seconds before next DB retry...", seconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Sleep interrupted during DB retry wait.");
        }
    }
}
