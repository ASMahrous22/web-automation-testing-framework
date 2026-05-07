package utils.framework;

import org.openqa.selenium.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ScreenshotManager — Captures and saves timestamped browser screenshots.
 *
 * <p>Screenshots are saved to a {@code Screenshots/} folder at the project root.
 * Each file is named using your label and a full timestamp to prevent overwrites.</p>
 *
 * @author ASMahrous
 */
public class ScreenshotManager
{
    private final WebDriver browser;

    /** Folder where all screenshots are saved — relative to the project root. */
    public static final Path SCREENSHOTS_FOLDER =
            Paths.get(System.getProperty("user.dir"), "Screenshots");

    /**
     * @param browser the active WebDriver session
     */
    public ScreenshotManager(WebDriver browser)
    {
        this.browser = browser;
    }

    // ========================
    // Screenshot Capture
    // ========================

    /**
     * Captures a screenshot of the current browser state, saves it to
     * {@code Screenshots/} at the project root, and returns the full
     * {@link Path} of the saved file.
     *
     * <p>The returned {@link Path} is consumed by
     * {@code AllureHelper.saveScreenshot()} to stream the image directly
     * into the Allure report as an attachment.</p>
     *
     * <p>Example:</p>
     * <pre>{@code
     * Path shot = driver.takeScreenshot("LoginPage");
     * AllureHelper.saveScreenshot("LoginPage", shot);
     * }</pre>
     *
     * @param screenshotLabel a short label used as the filename prefix
     *                        (e.g., "LoginPage"). Spaces are replaced with
     *                        underscores automatically.
     * @return the {@link Path} of the saved screenshot file
     * @throws RuntimeException if the screenshot cannot be saved due to an I/O error
     */
    public Path takeScreenshot(String screenshotLabel)
    {
        try
        {
            Files.createDirectories(SCREENSHOTS_FOLDER);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to create Screenshots directory: " + SCREENSHOTS_FOLDER, e);
        }

        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss-SSS"));
        String sanitizedLabel = screenshotLabel.trim().replace(" ", "_");
        Path destination = SCREENSHOTS_FOLDER.resolve(sanitizedLabel + "_" + timestamp + ".png");

        File sourceFile = ((TakesScreenshot) browser).getScreenshotAs(OutputType.FILE);
        try
        {
            Files.copy(sourceFile.toPath(), destination);
            System.out.println("Screenshot saved: " + destination);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to save screenshot to: " + destination, e);
        }

        return destination;
    }
}