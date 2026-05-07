package utils;

import io.qameta.allure.Allure;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * AllureHelper — Attaches screenshots to Allure reports.
 *
 * <p>This is the single place where the screenshot-capture and Allure-attachment
 * steps are combined. {@link pages.BasePage#saveScreenshot(String, ASM_Framework)}
 * delegates here, and {@code BaseTest.tearDown()} calls it directly for
 * automatic failure screenshots.</p>
 *
 * <p><b>Usage via BasePage (from any page or test):</b></p>
 * <pre>{@code
 * loginPage.saveScreenshot("TC01_AfterLogin", driver);
 * }</pre>
 *
 * <p><b>Direct usage (from BaseTest teardown or utility scripts):</b></p>
 * <pre>{@code
 * AllureHelper.saveScreenshot("FAILED_loginTest", driver);
 * }</pre>
 *
 * @author ASMahrous
 */
public class AllureHelper
{
    // Private constructor — utility class, no instances needed
    private AllureHelper() {}

    /**
     * Captures a screenshot using the given driver, saves it to {@code Screenshots/},
     * and attaches it to the active Allure test result as a PNG image.
     *
     * @param fileName the attachment label shown in the Allure report,
     *                 also used as the screenshot filename prefix
     * @param driver   the active {@link ASM_Framework} instance to capture from
     * @throws IOException if the saved screenshot file cannot be read back from disk
     */
    public static void saveScreenshot(String fileName, ASM_Framework driver) throws IOException
    {
        Path screenshot = driver.takeScreenshot(fileName);
        Allure.addAttachment(fileName, "image/png", Files.newInputStream(screenshot), ".png");
    }
}