package utils;

import org.openqa.selenium.JavascriptExecutor;
import org.json.JSONObject;
import io.appium.java_client.android.AndroidDriver;

public class BrowserStackUtils {

    // Method to mark the session name
    public static void setSessionName(AndroidDriver driver, String testName) {
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        JSONObject executorObject = new JSONObject();
        JSONObject argumentsObject = new JSONObject();
        argumentsObject.put("name", testName);  // Your test name
        executorObject.put("action", "setSessionName");
        executorObject.put("arguments", argumentsObject);
        jse.executeScript(String.format("browserstack_executor: %s", executorObject));
    }

    // Method to mark session status as passed or failed
    public static void setSessionStatus(AndroidDriver driver, String status, String reason) {
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        JSONObject executorObject = new JSONObject();
        JSONObject argumentsObject = new JSONObject();
        argumentsObject.put("status", status); // "passed" or "failed"
        argumentsObject.put("reason", reason);  // Failure reason (if applicable)
        executorObject.put("action", "setSessionStatus");
        executorObject.put("arguments", argumentsObject);
        jse.executeScript(String.format("browserstack_executor: %s", executorObject));
    }
}
