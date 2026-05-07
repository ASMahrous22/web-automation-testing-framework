package pages;

import utils.ASM_Framework;
import utils.AllureHelper;

import java.io.IOException;

/**
 * BasePage — Parent class for all Page Object Model page classes.
 *
 * <p>Provides two ways to instantiate a page:</p>
 * <ul>
 *   <li><b>From a test</b> — pass the {@link ASM_Framework} instance created
 *       by {@code BaseTest}. All tests use this path so the browser session
 *       is shared across the full test method.</li>
 *   <li><b>Standalone</b> — pass just a browser name and the page boots its
 *       own driver. Useful for quick scripts or debugging a page in isolation
 *       without a full test class.</li>
 * </ul>
 *
 * <p>The {@code driver} field is {@code public} so subclasses and tests can
 * access the full {@link ASM_Framework} API directly — there is no reason
 * to duplicate wrapper methods here for things the driver already exposes.</p>
 *
 * <p>The only methods defined here are things that either don't exist on
 * {@code ASM_Framework} (URL/title checks) or that combine multiple
 * framework calls into a single page-level operation (screenshot + Allure).</p>
 *
 * <p><b>Usage from a test (most common):</b></p>
 * <pre>{@code
 * // driver is inherited from BaseTest
 * LoginPage loginPage = new LoginPage(driver);
 * loginPage.open();
 * loginPage.login("user@example.com", "secret");
 * }</pre>
 *
 * <p><b>Standalone usage:</b></p>
 * <pre>{@code
 * LoginPage loginPage = new LoginPage("chrome");
 * loginPage.open();
 * loginPage.login("user@example.com", "secret");
 * loginPage.driver.closeAllTabs();
 * }</pre>
 *
 * @author ASMahrous
 */
public class BasePage
{
    /** The framework driver — accessible directly from any page subclass or test. */
    public ASM_Framework driver;

    // ========================
    // Constructors
    // ========================

    /**
     * Primary constructor — used by all test classes.
     * Receives the {@link ASM_Framework} instance that {@code BaseTest} created
     * so the same browser session is shared for the full test method.
     *
     * @param driver the active {@link ASM_Framework} instance from {@code BaseTest}
     */
    public BasePage(ASM_Framework driver)
    {
        this.driver = driver;
    }

    /**
     * Standalone constructor — boots a fresh browser session for this page.
     * Use this when running a page in isolation outside of a test class,
     * e.g. for quick debugging or a standalone script.
     *
     * <p>Remember to call {@code driver.closeAllTabs()} when done.</p>
     *
     * @param browserName the browser to launch: {@code "chrome"}, {@code "firefox"},
     *                    {@code "edge"}, or {@code "safari"}
     */
    public BasePage(String browserName)
    {
        this.driver = new ASM_Framework(browserName);
    }

    // ========================
    // Common Page Methods
    // ========================

    /**
     * Returns the full URL of the current page.
     *
     * @return the current page URL string
     */
    public String readPageURL()
    {
        return driver.getCurrentPageURL();
    }

    /**
     * Returns {@code true} if the current URL contains {@code fragment}.
     *
     * @param fragment substring to look for in the current URL
     * @return {@code true} if the URL contains {@code fragment}
     */
    public boolean urlContains(String fragment)
    {
        return driver.getCurrentPageURL().contains(fragment);
    }

    /**
     * Returns {@code true} if the current page title contains {@code text}.
     *
     * @param text substring to look for in the page title
     * @return {@code true} if the title contains {@code text}
     */
    public boolean titleContains(String text)
    {
        return driver.getCurrentPageTitle().contains(text);
    }

    /**
     * Closes the current browser tab.
     */
    public void closePage()
    {
        driver.closeCurrentTab();
    }

    // ========================
    // Screenshots
    // ========================

    /**
     * Captures a screenshot and attaches it to the active Allure test result.
     *
     * <p>Delegates to {@link AllureHelper#saveScreenshot(String, ASM_Framework)}
     * which handles both saving the file to disk and streaming it into Allure.</p>
     *
     * <p>Example:</p>
     * <pre>{@code
     * loginPage.saveScreenshot("TC01_AfterLogin", driver);
     * }</pre>
     *
     * @param fileName the attachment label shown in the Allure report
     * @param driver   the active {@link ASM_Framework} instance
     * @throws IOException if the screenshot file cannot be read
     */
    public void saveScreenshot(String fileName, ASM_Framework driver) throws IOException
    {
        AllureHelper.saveScreenshot(fileName, driver);
    }
}