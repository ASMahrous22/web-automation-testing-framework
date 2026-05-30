package utils;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * AdsHelper — Centralizes all ad-killing, safe-click, and wait logic
 * needed to interact reliably with automationexercise.com.
 *
 * <p>Three core operations:</p>
 * <ul>
 *   <li>{@link #killAds(WebDriver)}              — strips ad/vignette DOM nodes before every action</li>
 *   <li>{@link #jsClick(WebDriver, By)}           — JS click that bypasses any overlay</li>
 *   <li>{@link #waitForElement(WebDriver, By)}    — explicit wait + ad-kill before asserting</li>
 * </ul>
 *
 * <p>No {@code Thread.sleep} anywhere — all timing is handled by
 * {@link WebDriverWait} so waits are condition-driven, not time-driven.</p>
 *
 * @author ASMahrous
 */
public class AdsHelper
{
    /** Timeout for clickability / visibility waits between retries. */
    private static final Duration RETRY_WAIT  = Duration.ofMillis(500);
    /** Timeout for each poll inside {@link #waitForElement}. */
    private static final Duration POLL_WAIT   = Duration.ofSeconds(1);
    /** Overall timeout for {@link #waitForElement} outer loop. */
    private static final Duration OUTER_WAIT  = Duration.ofSeconds(15);
    /** Timeout for single explicit waits (clickable / visible). */
    private static final Duration DEFAULT_WAIT= Duration.ofSeconds(10);
    /** Max retries for {@link #killAdsAndClick}. */
    private static final int      MAX_RETRIES = 3;

    private AdsHelper() {}

    // ========================
    // Kill Ads
    // ========================

    /**
     * Removes every known ad/vignette element from the live DOM via JavaScript.
     * Safe to call multiple times — no-ops if elements are already gone.
     *
     * <p>Targets:</p>
     * <ul>
     *   <li>Google AdSense {@code ins.adsbygoogle} blocks</li>
     *   <li>Google ad iframes ({@code aswift_*}, {@code google_ads_*})</li>
     *   <li>Generic {@code div[id*="ad-"]} containers</li>
     *   <li>Google vignette overlay ({@code #google-vignette})</li>
     *   <li>Full-page body-level iframes</li>
     * </ul>
     *
     * @param driver active WebDriver session
     */
    public static void killAds(WebDriver driver)
    {
        try
        {
            ((JavascriptExecutor) driver).executeScript(
                    "document.querySelectorAll('ins.adsbygoogle').forEach(el => el.remove());" +
                            "document.querySelectorAll('iframe[id*=\"aswift\"]').forEach(el => el.remove());" +
                            "document.querySelectorAll('iframe[id*=\"google_ads\"]').forEach(el => el.remove());" +
                            "document.querySelectorAll('div[id*=\"ad-\"]').forEach(el => el.remove());" +
                            "document.querySelectorAll('ins[id*=\"aswift\"]').forEach(el => el.remove());" +
                            "var vignette = document.querySelector('#google-vignette'); if(vignette) vignette.remove();" +
                            "var overlay  = document.querySelector('body > iframe'); if(overlay) overlay.remove();"
            );
        }
        catch (Exception ignored) { /* page not yet ready — safe to skip */ }
    }

    // ========================
    // JS Click
    // ========================

    /**
     * Kills ads, waits for the element to be visible, then clicks via JavaScript.
     * Use for navbar links and any element a regular click fails on due to overlays.
     *
     * @param driver  active WebDriver session
     * @param locator the By locator of the target element
     */
    public static void jsClick(WebDriver driver, By locator)
    {
        killAds(driver);
        waitForVisible(driver, locator, DEFAULT_WAIT);
        WebElement el = driver.findElement(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
    }

    /**
     * Kills ads, then clicks the given WebElement directly via JavaScript.
     *
     * @param driver  active WebDriver session
     * @param element the target WebElement
     */
    public static void jsClick(WebDriver driver, WebElement element)
    {
        killAds(driver);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    // ========================
    // Kill-ads-and-click (with retry)
    // ========================

    /**
     * Attempts a regular click up to {@value MAX_RETRIES} times.
     * Before each attempt it kills ads and waits for the element to be
     * clickable using {@link WebDriverWait} — no fixed sleep between tries.
     * On the final attempt falls back to a JavaScript click.
     *
     * @param driver  active WebDriver session
     * @param locator the By locator of the target element
     */
    public static void killAdsAndClick(WebDriver driver, By locator)
    {
        for (int i = 0; i < MAX_RETRIES; i++)
        {
            killAds(driver);
            try
            {
                // Condition-driven wait — returns as soon as element is clickable
                waitForClickable(driver, locator, RETRY_WAIT);
                driver.findElement(locator).click();
                return;
            }
            catch (Exception e)
            {
                if (i == MAX_RETRIES - 1)
                {
                    // Last attempt — force JS click
                    jsClick(driver, locator);
                    return;
                }
                // Not the last attempt — wait for clickability with short timeout,
                try { waitForClickable(driver, locator, RETRY_WAIT); }
                catch (Exception ignored) {}
            }
        }
    }

    // ========================
    // Wait for element
    // ========================

    /**
     * Waits up to {@code OUTER_WAIT} for the element to be visible,
     * killing ads before each internal poll.
     *
     * <p>Internally uses a {@link WebDriverWait} with a {@code POLL_WAIT}
     * timeout per attempt — no {@code Thread.sleep} anywhere in the loop.</p>
     *
     * @param driver  active WebDriver session
     * @param locator the By locator of the element to wait for
     * @throws RuntimeException if the element is not visible within {@code OUTER_WAIT}
     */
    public static void waitForElement(WebDriver driver, By locator)
    {
        long deadlineMs = System.currentTimeMillis() + OUTER_WAIT.toMillis();

        while (System.currentTimeMillis() < deadlineMs)
        {
            killAds(driver);
            try
            {
                // Each poll: condition-driven wait up to POLL_WAIT
                WebElement el = new WebDriverWait(driver, POLL_WAIT)
                        .until(ExpectedConditions.visibilityOfElementLocated(locator));
                if (el.isDisplayed()) return;
            }
            catch (Exception ignored)
            {
                // Not visible yet — kill ads again and retry until deadline
            }
        }

        throw new RuntimeException(
                "Element not visible after " + OUTER_WAIT.getSeconds() + "s: " + locator);
    }

    /**
     * Same as {@link #waitForElement(WebDriver, By)} but returns the element
     * so callers can chain assertions directly.
     *
     * @param driver  active WebDriver session
     * @param locator the By locator
     * @return the visible {@link WebElement}
     */
    public static WebElement waitForElementAndGet(WebDriver driver, By locator)
    {
        waitForElement(driver, locator);
        return driver.findElement(locator);
    }

    // ========================
    // Dismiss browser popups
    // ========================

    /**
     * Dismisses browser-native popups (e.g. "Save password?" bubble)
     * by sending ESC to the currently focused element.
     * Safe to call when no popup is present.
     *
     * @param driver active WebDriver session
     */
    public static void dismissBrowserPopups(WebDriver driver)
    {
        try { driver.switchTo().activeElement().sendKeys(Keys.ESCAPE); }
        catch (Exception ignored) {}
    }

    // ========================
    // Internal helpers — WebDriverWait only, no Thread.sleep
    // ========================

    /**
     * Waits up to {@code timeout} for the element to be clickable.
     * Throws if not clickable within that window.
     */
    private static void waitForClickable(WebDriver driver, By locator, Duration timeout)
    {
        new WebDriverWait(driver, timeout)
                .until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Waits up to {@code timeout} for the element to be visible.
     * Throws if not visible within that window.
     */
    private static void waitForVisible(WebDriver driver, By locator, Duration timeout)
    {
        new WebDriverWait(driver, timeout)
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
}