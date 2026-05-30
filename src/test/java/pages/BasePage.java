package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utils.AdsHelper;
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
 * Every interaction goes through AdsHelper so ads are killed before
 * @author ASMahrous
 */

public class BasePage
{
    public ASM_Framework driver;

    public BasePage(ASM_Framework driver) { this.driver = driver; }
    public BasePage(String browserName)   { this.driver = new ASM_Framework(browserName); }

    // ── Convenience accessors ─────────────────────────────────────────────

    protected WebDriver wd()              { return driver.getDriver(); }

    // ── Ad-safe primitives used by every page subclass ────────────────────

    /** Kill ads + JS click — use for ALL navigation / navbar links. */
    protected void jsClick(By locator)    { AdsHelper.jsClick(wd(), locator); }

    /** Kill ads + JS click on a WebElement directly. */
    protected void jsClick(WebElement el) { AdsHelper.jsClick(wd(), el); }

    /**
     * Kill ads + regular click with retry, JS click as last resort.
     * Use for form buttons that are inside the page body (not nav links).
     */
    protected void safeClick(By locator)  { AdsHelper.killAdsAndClick(wd(), locator); }

    /** Kill ads then wait for element — use before every assertion. */
    protected void waitFor(By locator)    { AdsHelper.waitForElement(wd(), locator); }

    /** Kill ads, wait, and return the element. */
    protected WebElement waitAndGet(By locator) { return AdsHelper.waitForElementAndGet(wd(), locator); }

    /** Kill ads only — call before writing into a field. */
    protected void killAds()             { AdsHelper.killAds(wd()); }

    // ── URL / title helpers ───────────────────────────────────────────────

    public String  readPageURL()               { return driver.getCurrentPageURL(); }
    public boolean urlContains(String fragment){ return driver.getCurrentPageURL().contains(fragment); }
    public boolean titleContains(String text)  { return driver.getCurrentPageTitle().contains(text); }
    public void    closePage()                 { driver.closeCurrentTab(); }

    // ── Screenshot ────────────────────────────────────────────────────────

    public void saveScreenshot(String fileName, ASM_Framework driver) throws IOException
    {
        AllureHelper.saveScreenshot(fileName, driver);
    }
}
