package utils.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Utility class to fetch test data from JSON files.
 * Designed for use with the test data paths in {@link TestDataPaths}.
 */
public class JsonReader {

    private static final Logger logger = LogManager.getLogger(JsonReader.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    private JsonReader() {
        // Prevent instantiation
    }

    /**
     * Reads a single value from a nested JSON structure.
     *
     * @param filePath   The JSON file path.
     * @param sectionKey The top-level array key.
     * @param objectKey  The object key inside the array.
     * @param nestedKey  The key inside the nested object to fetch.
     * @return The found value, or throws RuntimeException if not found.
     */
    public static String readDataFromJson(String filePath,
                                          String sectionKey,
                                          String objectKey,
                                          String nestedKey) {
        try {
            JsonNode root = mapper.readTree(new File(filePath));
            JsonNode sectionArray = root.get(sectionKey);

            if (sectionArray != null && sectionArray.isArray()) {
                for (JsonNode element : sectionArray) {
                    if (element.has(objectKey)) {
                        JsonNode nestedObj = element.get(objectKey);
                        if (nestedObj.has(nestedKey)) {
                            String value = nestedObj.get(nestedKey).asText();
                            logger.info("Read JSON value from [{} -> {} -> {}]: {}", sectionKey, objectKey, nestedKey, value);
                            return value;
                        }
                    } else if (element.has(nestedKey)) {
                        String value = element.get(nestedKey).asText();
                        logger.info("Read JSON value from [{} -> {}]: {}", sectionKey, nestedKey, value);
                        return value;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error reading JSON value from file: {}", filePath, e);
            throw new RuntimeException("Failed to read value from JSON file [" + filePath + "] for nested key: " + nestedKey, e);
        }

        throw new RuntimeException("Key not found in JSON file: " + nestedKey);
    }

    /**
     * Reads a list of values from a nested array in JSON.
     *
     * @param filePath   The JSON file path.
     * @param sectionKey The top-level array key.
     * @param mainKey    The main key inside the array.
     * @param typeKey    The key whose value is an array of items.
     * @return List of strings (possibly empty if not found).
     */
    public static List<String> readListFromJson(String filePath,
                                                String sectionKey,
                                                String mainKey,
                                                String typeKey) {
        List<String> values = new ArrayList<>();
        try {
            JsonNode root = mapper.readTree(new File(filePath));
            JsonNode sectionArray = root.get(sectionKey);

            if (sectionArray != null && sectionArray.isArray()) {
                for (JsonNode obj : sectionArray) {
                    if (obj.has(mainKey)) {
                        JsonNode inner = obj.get(mainKey);
                        if (inner.has(typeKey)) {
                            JsonNode arrayNode = inner.get(typeKey);
                            if (arrayNode.isArray()) {
                                for (JsonNode item : arrayNode) {
                                    values.add(item.asText());
                                }
                                logger.info("Read list from JSON [{} -> {} -> {}]: {}", sectionKey, mainKey, typeKey, values);
                                return values;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error reading JSON list from file: {}", filePath, e);
            throw new RuntimeException("Failed to read list from JSON file [" + filePath + "] for key: " + typeKey, e);
        }
        logger.warn("No list found in JSON [{} -> {} -> {}]. Returning empty list.", sectionKey, mainKey, typeKey);
        return values;
    }

    /**
     * Reads a simple flat key-value from JSON.
     *
     * @param filePath The JSON file path.
     * @param key      The flat key to fetch.
     * @return The found value, or throws if not found.
     */
    public static String readFlatValue(String filePath, String key) {
        try {
            JsonNode root = mapper.readTree(new File(filePath));

            if (root.has(key)) {
                String value = root.get(key).asText();
                logger.info("Read flat value from JSON [{}]: {}", key, value);
                return value;
            } else {
                logger.warn("Key [{}] not found in JSON file: {}", key, filePath);
                throw new RuntimeException("Key [" + key + "] not found in JSON file: " + filePath);
            }
        } catch (Exception e) {
            logger.error("Error reading flat value from JSON file: {}", filePath, e);
            throw new RuntimeException("Failed to read flat value from JSON file [" + filePath + "] for key: " + key, e);
        }
    }

    /**
     * Reads a specific device config from emulator or real-device section.
     *
     * @param deviceType "emulator" or "real-device"
     * @param deviceKey  e.g. "pixel_9"
     * @return Map of device capabilities
     */
    public static Map<String, Object> readAndroidDeviceConfig(String deviceType, String deviceKey) {
        try {
            String jsonFilePath = Paths.get(System.getProperty("user.dir"),
                    "src", "test", "resources", "appium", "devices", "android-devices.json").toString();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(new File(jsonFilePath));

            JsonNode configsNode = root.get("device-configs");
            if (configsNode == null) {
                throw new RuntimeException("Missing 'device-configs' section.");
            }

            JsonNode deviceTypeNode = configsNode.get(deviceType);
            if (deviceTypeNode == null) {
                throw new RuntimeException("Device type '" + deviceType + "' not found.");
            }

            JsonNode deviceNode = deviceTypeNode.get(deviceKey);
            if (deviceNode == null) {
                throw new RuntimeException("Device key '" + deviceKey + "' not found under type '" + deviceType + "'.");
            }

            return mapper.convertValue(deviceNode, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("Error reading device config for type: " + deviceType +
                    ", device: " + deviceKey, e);
        }
    }

    /**
     * Reads global settings from the android-devices.json file.
     *
     * @return Map of global settings
     */
    public static Map<String, Object> readGlobalSettings() {
        try {
            String jsonFilePath = Paths.get(System.getProperty("user.dir"),
                    "src", "test", "resources", "appium", "devices", "android-devices.json").toString();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(new File(jsonFilePath));
            JsonNode globalNode = root.get("global-settings");

            if (globalNode == null) {
                throw new RuntimeException("Missing 'global-settings' section.");
            }

            return mapper.convertValue(globalNode, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("Error reading global settings from JSON", e);
        }
    }


}





