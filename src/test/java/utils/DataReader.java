package utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * DataReader — Reads JSON test data files using Gson and returns typed objects.
 *
 * <p>All test data files live under {@code src/test/resources/testdata/}.
 * Pass just the filename (with extension) and the target class — DataReader
 * handles the path resolution, file reading, and deserialization.</p>
 *
 * <p><b>Single-object file:</b></p>
 * <pre>{@code
 * // testdata/user.json → { "email": "...", "password": "..." }
 * UserData user = DataReader.read("user.json", UserData.class);
 * }</pre>
 *
 * <p><b>Array file:</b></p>
 * <pre>{@code
 * // testdata/users.json → [ { "email": "..." }, { "email": "..." } ]
 * List<UserData> users = DataReader.readList("users.json", UserData.class);
 * }</pre>
 *
 * @author ASMahrous
 */
public class DataReader
{
    /** Base path for all test data JSON files. */
    private static final String TEST_DATA_PATH =
            System.getProperty("user.dir") + "/src/test/resources/testdata/";

    private static final Gson GSON = new Gson();

    // Private constructor — utility class, no instances needed
    private DataReader() {}

    // ========================
    // Read Single Object
    // ========================

    /**
     * Reads a JSON file and deserializes it into a single instance of {@code T}.
     *
     * <p>The JSON file must contain a single object (not an array):</p>
     * <pre>
     * {
     *   "email": "user@example.com",
     *   "password": "secret123"
     * }
     * </pre>
     *
     * @param fileName  the JSON filename, e.g. {@code "user.json"} —
     *                  resolved relative to {@code src/test/resources/testdata/}
     * @param dataClass the class to deserialize into
     * @param <T>       the target type
     * @return a populated instance of {@code T}
     * @throws RuntimeException if the file cannot be read or parsed
     */
    public static <T> T read(String fileName, Class<T> dataClass)
    {
        String fullPath = TEST_DATA_PATH + fileName;

        try (FileReader reader = new FileReader(fullPath))
        {
            return GSON.fromJson(reader, dataClass);
        }
        catch (IOException e)
        {
            throw new RuntimeException(
                    "[DataReader] Failed to read test data file: " + fullPath, e);
        }
    }

    // ========================
    // Read List
    // ========================

    /**
     * Reads a JSON file and deserializes it into a {@link List} of {@code T}.
     *
     * <p>The JSON file must contain an array of objects:</p>
     * <pre>
     * [
     *   { "email": "user1@example.com", "password": "pass1" },
     *   { "email": "user2@example.com", "password": "pass2" }
     * ]
     * </pre>
     *
     * @param fileName  the JSON filename, e.g. {@code "users.json"} —
     *                  resolved relative to {@code src/test/resources/testdata/}
     * @param dataClass the class of each element in the list
     * @param <T>       the element type
     * @return a {@link List} of populated {@code T} instances
     * @throws RuntimeException if the file cannot be read or parsed
     */
    public static <T> List<T> readList(String fileName, Class<T> dataClass)
    {
        String fullPath = TEST_DATA_PATH + fileName;
        Type   listType = TypeToken.getParameterized(List.class, dataClass).getType();

        try (FileReader reader = new FileReader(fullPath))
        {
            return GSON.fromJson(reader, listType);
        }
        catch (IOException e)
        {
            throw new RuntimeException(
                    "[DataReader] Failed to read test data file: " + fullPath, e);
        }
    }
}