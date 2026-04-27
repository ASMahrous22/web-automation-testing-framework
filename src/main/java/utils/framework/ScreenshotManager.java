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
 * The folder is created automatically if it does not already exist.
 * Each file is named using your label and a full timestamp to prevent overwrites:</p>
 *
 * <pre>
 * Screenshots/
 * ├── LoginPage_2025-07-21_14-35-22-123.png
 * └── AfterSubmit_2025-07-21_14-35-45-456.png
 * </pre>
 *
 * @author ASMahrous
 */
public class ScreenshotManager
{
    private final WebDriver browser;

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
     * Captures a screenshot of the current browser state and saves it to
     * {@code Screenshots/} at the project root.
     *
     * <p>Example:</p>
     * <pre>{@code
     * driver.takeScreenshot("LoginPage");
     * driver.takeScreenshot("AfterSubmit");
     * }</pre>
     *
     * @param screenshotLabel a short label used as the filename prefix
     *                        (e.g., "LoginPage"). Spaces are replaced with
     *                        underscores automatically.
     * @throws RuntimeException if the screenshot cannot be saved due to an I/O error
     */
    public void takeScreenshot(String screenshotLabel)
    {
        Path screenshotsDir = Paths.get(System.getProperty("user.dir"), "Screenshots");

        try
        {
            Files.createDirectories(screenshotsDir);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to create Screenshots directory: " + screenshotsDir, e);
        }

        String timestamp     = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss-SSS"));
        String sanitizedLabel = screenshotLabel.trim().replace(" ", "_");
        String fileName       = sanitizedLabel + "_" + timestamp + ".png";
        Path   destination    = screenshotsDir.resolve(fileName);

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
    }
}