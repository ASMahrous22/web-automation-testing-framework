package utils.framework;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * WaitManager — Centralizes all Selenium waiting strategies.
 *
 * <p>Provides implicit, explicit, and fluent wait configurations,
 * plus internal helpers used by other framework managers to wait
 * for element visibility and clickability.</p>
 *
 * @author ASMahrous
 */
public class WaitManager
{
    private final WebDriver browser;
    private final Duration  defaultTimeout;

    /**
     * @param browser        the active WebDriver session
     * @param defaultTimeout timeout used by all internal explicit wait helpers
     */
    public WaitManager(WebDriver browser, Duration defaultTimeout)
    {
        this.browser        = browser;
        this.defaultTimeout = defaultTimeout;
    }

    // ========================
    // Implicit Wait
    // ========================

    /**
     * Sets a global Implicit Wait using a {@link Duration} object.
     *
     * <p><b>Warning:</b> Avoid mixing Implicit and Explicit Waits —
     * this can cause unpredictable timeout behavior in Selenium.</p>
     *
     * @param duration the wait duration (e.g., {@code Duration.ofSeconds(5)})
     */
    public void setImplicitWait(Duration duration)
    {
        browser.manage().timeouts().implicitlyWait(duration);
    }

    /**
     * Sets a global Implicit Wait using a plain seconds value.
     *
     * @param seconds number of seconds to wait
     */
    public void setImplicitWait(long seconds)
    {
        browser.manage().timeouts().implicitlyWait(Duration.ofSeconds(seconds));
    }

    /**
     * Sets a global Implicit Wait using a human-readable time unit string.
     *
     * <p>Supported units (case-insensitive):
     * {@code "seconds"/"sec"}, {@code "minutes"/"min"}, {@code "hours"/"hour"},
     * {@code "days"/"day"}, {@code "ms"/"mili"}, {@code "ns"/"nano"}</p>
     *
     * @param durationIn  the time unit string (e.g., "seconds", "ms")
     * @param waitingTime the numeric amount to wait
     * @throws IllegalArgumentException if the time unit is not recognized
     */
    public void setImplicitWait(String durationIn, long waitingTime)
    {
        switch (durationIn.toLowerCase())
        {
            case "day":
            case "days":
                browser.manage().timeouts().implicitlyWait(Duration.ofDays(waitingTime));
                break;

            case "hour":
            case "hours":
                browser.manage().timeouts().implicitlyWait(Duration.ofHours(waitingTime));
                break;

            case "min":
            case "mins":
            case "minute":
            case "minutes":
                browser.manage().timeouts().implicitlyWait(Duration.ofMinutes(waitingTime));
                break;

            case "sec":
            case "secs":
            case "second":
            case "seconds":
                browser.manage().timeouts().implicitlyWait(Duration.ofSeconds(waitingTime));
                break;

            case "ms":
            case "msec":
            case "mili":
            case "milis":
                browser.manage().timeouts().implicitlyWait(Duration.ofMillis(waitingTime));
                break;

            case "ns":
            case "nsec":
            case "nano":
            case "nanos":
                browser.manage().timeouts().implicitlyWait(Duration.ofNanos(waitingTime));
                break;

            default:
                throw new IllegalArgumentException("Invalid time unit: " + durationIn);
        }
    }

    // ========================
    // Explicit Wait
    // ========================

    /**
     * Explicitly waits for the presence of an element in the DOM (seconds overload).
     *
     * @param locator        the By locator of the element
     * @param timeoutSeconds how long to wait before throwing {@link TimeoutException}
     */
    public void setExplicitWait(By locator, long timeoutSeconds)
    {
        new WebDriverWait(browser, Duration.ofSeconds(timeoutSeconds))
                .until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /**
     * Explicitly waits for the presence of an element in the DOM ({@link Duration} overload).
     *
     * @param locator the By locator of the element
     * @param timeout how long to wait before throwing {@link TimeoutException}
     */
    public void setExplicitWait(By locator, Duration timeout)
    {
        new WebDriverWait(browser, timeout)
                .until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    // ========================
    // Fluent Wait
    // ========================

    /**
     * Waits for an element using a Fluent Wait strategy (seconds overload).
     *
     * @param locator        the By locator of the element
     * @param timeoutSeconds maximum time to wait
     * @param pollingMillis  polling interval in milliseconds
     * @param timeoutMessage custom message on timeout
     */
    public void setFluentWait(By locator, long timeoutSeconds, long pollingMillis, String timeoutMessage)
    {
        new FluentWait<>(browser)
                .withTimeout(Duration.ofSeconds(timeoutSeconds))
                .pollingEvery(Duration.ofMillis(pollingMillis))
                .ignoring(NoSuchElementException.class)
                .withMessage(timeoutMessage)
                .until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /**
     * Waits for an element using a Fluent Wait strategy ({@link Duration} overload).
     *
     * @param locator         the By locator of the element
     * @param timeout         maximum time to wait
     * @param pollingInterval polling interval
     * @param timeoutMessage  custom message on timeout
     */
    public void setFluentWait(By locator, Duration timeout, Duration pollingInterval, String timeoutMessage)
    {
        new FluentWait<>(browser)
                .withTimeout(timeout)
                .pollingEvery(pollingInterval)
                .ignoring(NoSuchElementException.class)
                .withMessage(timeoutMessage)
                .until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    // ========================
    // Internal Helpers
    // ========================

    /**
     * Waits until the element located by {@code locator} is clickable.
     *
     * @param locator By locator strategy
     * @return the clickable WebElement
     */
    public WebElement waitForElementToBeClickable(By locator)
    {
        return new WebDriverWait(browser, defaultTimeout)
                .until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Waits until the element located by {@code locator} is clickable.
     *
     * @param locator By locator strategy
     * @param timeInSeconds  how long to wait in seconds
     * @return the clickable WebElement
     */
    public WebElement waitForElementToBeClickable(By locator, long timeInSeconds)
    {
        return new WebDriverWait(browser, Duration.ofSeconds(timeInSeconds))
                .until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Waits until the given WebElement is clickable.
     *
     * @param element the WebElement to wait for
     * @return the clickable WebElement
     */
    public WebElement waitForElementToBeClickable(WebElement element)
    {
        return new WebDriverWait(browser, defaultTimeout)
                .until(ExpectedConditions.elementToBeClickable(element));
    }

    /**
     * Waits until the given WebElement is clickable.
     *
     * @param element the WebElement to wait for
     * @param timeInSeconds  how long to wait in seconds
     * @return the clickable WebElement
     */
    public WebElement waitForElementToBeClickable(WebElement element, long timeInSeconds)
    {
        return new WebDriverWait(browser, Duration.ofSeconds(timeInSeconds))
                .until(ExpectedConditions.elementToBeClickable(element));
    }

    /**
     * Waits until the element located by {@code locator} is visible on the page.
     *
     * @param locator By locator strategy
     * @return the visible WebElement
     */
    public WebElement waitForElementToBeVisible(By locator)
    {
        return new WebDriverWait(browser, defaultTimeout)
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Waits until the element located by {@code locator} is visible on the page.
     *
     * @param locator By locator strategy
     * @param timeInSeconds  how long to wait in seconds
     * @return the visible WebElement
     */
    public WebElement waitForElementToBeVisible(By locator, long timeInSeconds)
    {
        return new WebDriverWait(browser, Duration.ofSeconds(timeInSeconds))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Waits until the given WebElement is visible on the page.
     *
     * @param element the WebElement to wait for
     * @return the visible WebElement
     */
    public WebElement waitForElementToBeVisible(WebElement element)
    {
        return new WebDriverWait(browser, defaultTimeout)
                .until(ExpectedConditions.visibilityOf(element));
    }

    /**
     * Waits until the given WebElement is visible on the page.
     *
     * @param element the WebElement to wait for
     * @param timeInSeconds  how long to wait in seconds
     * @return the visible WebElement
     */
    public WebElement waitForElementToBeVisible(WebElement element, long timeInSeconds)
    {
        return new WebDriverWait(browser, Duration.ofSeconds(timeInSeconds))
                .until(ExpectedConditions.visibilityOf(element));
    }

    /**
     * Waits until the element located by {@code locator} is present in the DOM.
     *
     * @param locator  By locator strategy
     * @return the found WebElement
     */
    public WebElement waitForElementToBePresent(By locator)
    {
        return new WebDriverWait(browser, defaultTimeout)
                .until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /**
     * Waits until the element located by {@code locator} is present in the DOM.
     *
     * @param locator  By locator strategy
     * @param timeout  how long to wait
     * @return the found WebElement
     */
    public WebElement waitForElementToBePresent(By locator, Duration timeout)
    {
        return new WebDriverWait(browser, timeout)
                .until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /**
     * Waits until the element located by {@code locator} is present in the DOM.
     *
     * @param locator  By locator strategy
     * @param timeInSeconds  how long to wait in seconds
     * @return the found WebElement
     */
    public WebElement waitForElementToBePresent(By locator, long timeInSeconds)
    {
        return new WebDriverWait(browser, Duration.ofSeconds(timeInSeconds))
                .until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /**
     * Waits for a JavaScript alert dialog to appear.
     *
     * @return the {@link Alert} once present
     */
    public Alert waitForAlert()
    {
        return new WebDriverWait(browser, defaultTimeout)
                .until(ExpectedConditions.alertIsPresent());
    }

    /**
     * Waits for a JavaScript alert dialog to appear.
     *
     * @param timeInSeconds  how long to wait in seconds
     * @return the {@link Alert} once present
     */
    public Alert waitForAlert(long timeInSeconds)
    {
        return new WebDriverWait(browser, Duration.ofSeconds(timeInSeconds))
                .until(ExpectedConditions.alertIsPresent());
    }
}