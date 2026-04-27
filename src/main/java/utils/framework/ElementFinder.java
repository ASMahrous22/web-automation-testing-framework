package utils.framework;

import org.openqa.selenium.*;

/**
 * ElementFinder — Resolves locator strategies and finds WebElements.
 *
 * <p>Converts human-readable locator strategy strings into Selenium {@link By}
 * objects, and locates elements in the DOM using {@link WaitManager}.</p>
 *
 * @author ASMahrous
 */
public class ElementFinder
{
    private final WaitManager waitManager;

    /**
     * @param waitManager shared {@link WaitManager} for DOM-presence waits
     */
    public ElementFinder(WaitManager waitManager)
    {
        this.waitManager = waitManager;
    }

    // ========================
    // Locator Strategy
    // ========================

    /**
     * Converts a locator strategy string and value into a Selenium {@link By} object.
     *
     * <p>Supported strategies (case-insensitive):
     * {@code "id"}, {@code "name"}, {@code "class"} or {@code "class name"},
     * {@code "xpath"}, {@code "css"} or {@code "css selector"}</p>
     *
     * @param locatorType the strategy name (e.g., "id", "css", "xpath")
     * @param locator     the locator value (e.g., "loginBtn")
     * @return the corresponding {@link By} object
     * @throws IllegalArgumentException if the strategy is not recognized
     */
    public By getBy(String locatorType, String locator)
    {
        switch (locatorType.toLowerCase())
        {
            case "id":
                return By.id(locator);

            case "name":
                return By.name(locator);

            case "class name":
            case "class":
                return By.className(locator);

            case "xpath":
                return By.xpath(locator);

            case "css selector":
            case "css":
                return By.cssSelector(locator);

            default:
                throw new IllegalArgumentException("Invalid locator type: " + locatorType);
        }
    }

    // ========================
    // Element Finding
    // ========================

    /**
     * Waits for the element to be present in the DOM using the default timeout,
     * then returns it.
     *
     * @param locatorType the locator strategy (e.g., "id", "css", "xpath")
     * @param locator     the locator value
     * @return the found WebElement
     * @throws TimeoutException         if not found within the default timeout
     * @throws IllegalArgumentException if the locator type is not recognized
     */
    public WebElement findElement(String locatorType, String locator)
    {
        By by = getBy(locatorType, locator);
        return waitManager.waitForElementToBePresent(by);
    }

    /**
     * Waits up to {@code timeoutSeconds} for the element to be present in the DOM,
     * then returns it.
     *
     * @param locatorType    the locator strategy
     * @param locator        the locator value
     * @param timeoutSeconds custom timeout in seconds
     * @return the found WebElement
     * @throws TimeoutException         if not found within the given timeout
     * @throws IllegalArgumentException if the locator type is not recognized
     */
    public WebElement findElement(String locatorType, String locator, long timeoutSeconds)
    {
        By by = getBy(locatorType, locator);
        return waitManager.waitForElementToBePresent(by, timeoutSeconds);
    }
}