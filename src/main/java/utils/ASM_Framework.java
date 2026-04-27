package utils;

import org.openqa.selenium.*;
import utils.framework.*;

import java.time.Duration;
import java.util.List;

/**
 * ASM_Framework — A Selenium WebDriver wrapper that simplifies browser automation.
 *
 * <p>This class is the single entry point for all test scripts. Internally it
 * delegates every responsibility to a dedicated manager:</p>
 *
 * <ul>
 *   <li>{@link utils.framework.BrowserManager}    — browser launch, navigation, window size</li>
 *   <li>{@link utils.framework.WaitManager}        — implicit, explicit, fluent waits</li>
 *   <li>{@link utils.framework.ElementFinder}      — locating elements in the DOM</li>
 *   <li>{@link utils.framework.ElementInteractions}— click, type, clear, getText, state checks</li>
 *   <li>{@link utils.framework.ActionsManager}     — hover, right-click, double-click, drag, scroll</li>
 *   <li>{@link utils.framework.DropdownManager}    — select / deselect from {@code <select>}</li>
 *   <li>{@link utils.framework.WindowManager}      — tab / window switching</li>
 *   <li>{@link utils.framework.AlertManager}       — JavaScript alert / confirm / prompt</li>
 *   <li>{@link utils.framework.FrameManager}       — iframe context switching</li>
 *   <li>{@link utils.framework.ScreenshotManager}  — timestamped PNG capture</li>
 * </ul>
 *
 * <p><b>Basic usage:</b></p>
 * <pre>{@code
 * ASM_Framework driver = new ASM_Framework("chrome");
 * driver.goToURL("https://example.com");
 * driver.manageScreenSize("maximize");
 *
 * WebElement usernameField = driver.findElement("id", "username");
 * driver.writeInElement(usernameField, "myUser");
 *
 * driver.closeAllTabs();
 * }</pre>
 *
 * <p><b>Usage with browser options:</b></p>
 * <pre>{@code
 * ASM_Framework driver = new ASM_Framework("chrome",
 *     new BrowserManager.BrowserOptions()
 *         .headless()
 *         .maximized()
 *         .withArgument("--disable-notifications")
 * );
 * }</pre>
 *
 * @author ASMahrous
 * @version 3.0
 */
public class ASM_Framework
{
    /**
     * Default timeout used by all internal explicit-wait calls.
     * Shared across all managers so behaviour is consistent throughout the framework.
     */
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    // ========================
    // Manager Instances
    // ========================

    private final BrowserManager       browserManager;
    private final WaitManager          waitManager;
    private final ElementFinder        elementFinder;
    private final ElementInteractions  elementInteractions;
    private final ActionsManager       actionsManager;
    private final DropdownManager      dropdownManager;
    private final WindowManager        windowManager;
    private final AlertManager         alertManager;
    private final FrameManager         frameManager;
    private final ScreenshotManager    screenshotManager;

    // ========================
    // Public BrowserOptions alias
    // ========================

    /**
     * Re-export of {@link BrowserManager.BrowserOptions} so callers can write
     * {@code new ASM_Framework.BrowserOptions()} without an extra import.
     */
    public static class BrowserOptions extends BrowserManager.BrowserOptions { }

    // ========================
    // Constructors
    // ========================

    /**
     * Initialises the framework with the specified browser using default settings.
     *
     * <p>Supported values (case-insensitive): {@code "chrome"}, {@code "firefox"},
     * {@code "edge"}, {@code "safari"}. Defaults to Chrome if unrecognised.</p>
     *
     * @param browserName the browser to launch (e.g., "chrome", "firefox")
     */
    public ASM_Framework(String browserName)
    {
        this(browserName, new BrowserManager.BrowserOptions());
    }

    /**
     * Initialises the framework with the specified browser and custom launch options.
     *
     * @param browserName the browser to launch (e.g., "chrome", "firefox")
     * @param options     a {@link BrowserManager.BrowserOptions} instance
     */
    public ASM_Framework(String browserName, BrowserManager.BrowserOptions options)
    {
        browserManager      = new BrowserManager(browserName, options);
        WebDriver driver    = browserManager.getDriver();

        waitManager         = new WaitManager(driver, DEFAULT_TIMEOUT);
        elementFinder       = new ElementFinder(waitManager);
        elementInteractions = new ElementInteractions(waitManager);
        actionsManager      = new ActionsManager(driver, waitManager);
        dropdownManager     = new DropdownManager(waitManager);
        windowManager       = new WindowManager(driver);
        alertManager        = new AlertManager(waitManager);
        frameManager        = new FrameManager(driver, waitManager);
        screenshotManager   = new ScreenshotManager(driver);
    }

    // ========================
    // Browser Management
    // ========================

    /** @see BrowserManager#closeCurrentTab() */
    public void closeCurrentTab()
    {
        browserManager.closeCurrentTab();
    }

    /** @see BrowserManager#closeAllTabs() */
    public void closeAllTabs()
    {
        browserManager.closeAllTabs();
    }

    /** @see BrowserManager#goToURL(String) */
    public void goToURL(String url)
    {
        browserManager.goToURL(url);
    }

    /** @see BrowserManager#getCurrentPageTitle() */
    public String getCurrentPageTitle()
    {
        return browserManager.getCurrentPageTitle();
    }

    /** @see BrowserManager#getCurrentPageURL() */
    public String getCurrentPageURL()
    {
        return browserManager.getCurrentPageURL();
    }

    /** @see BrowserManager#manageNavigationButtons(String) */
    public void manageNavigationButtons(String button)
    {
        browserManager.manageNavigationButtons(button);
    }

    /** @see BrowserManager#manageScreenSize(String) */
    public void manageScreenSize(String action)
    {
        browserManager.manageScreenSize(action);
    }

    // ========================
    // Element Finding
    // ========================

    /** @see ElementFinder#getBy(String, String) */
    public By getBy(String locatorType, String locator)
    {
        return elementFinder.getBy(locatorType, locator);
    }

    /** @see ElementFinder#findElement(String, String) */
    public WebElement findElement(String locatorType, String locator)
    {
        return elementFinder.findElement(locatorType, locator);
    }

    /** @see ElementFinder#findElement(String, String, long) */
    public WebElement findElement(String locatorType, String locator, long timeoutSeconds)
    {
        return elementFinder.findElement(locatorType, locator, timeoutSeconds);
    }

    // ========================
    // Element Interaction
    // ========================

    /** @see ElementInteractions#clickElement(By) */
    public void clickElement(By locator)
    {
        elementInteractions.clickElement(locator);
    }

    /** @see ElementInteractions#clickElement(WebElement) */
    public void clickElement(WebElement element)
    {
        elementInteractions.clickElement(element);
    }

    /** @see ElementInteractions#writeInElement(By, String) */
    public void writeInElement(By locator, String text)
    {
        elementInteractions.writeInElement(locator, text);
    }

    /** @see ElementInteractions#writeInElement(WebElement, String) */
    public void writeInElement(WebElement element, String text)
    {
        elementInteractions.writeInElement(element, text);
    }

    /** @see ElementInteractions#clearElementText(By) */
    public void clearElementText(By locator)
    {
        elementInteractions.clearElementText(locator);
    }

    /** @see ElementInteractions#clearElementText(WebElement) */
    public void clearElementText(WebElement element)
    {
        elementInteractions.clearElementText(element);
    }

    /** @see ElementInteractions#getElementText(By) */
    public String getElementText(By locator)
    {
        return elementInteractions.getElementText(locator);
    }

    /** @see ElementInteractions#getElementText(WebElement) */
    public String getElementText(WebElement element)
    {
        return elementInteractions.getElementText(element);
    }

    // ========================
    // Element State
    // ========================

    /** @see ElementInteractions#validateElementIsDisplayed(WebElement) */
    public boolean validateElementIsDisplayed(WebElement element)
    {
        return elementInteractions.validateElementIsDisplayed(element);
    }

    /** @see ElementInteractions#validateElementIsEnabled(WebElement) */
    public boolean validateElementIsEnabled(WebElement element)
    {
        return elementInteractions.validateElementIsEnabled(element);
    }

    /** @see ElementInteractions#validateElementIsSelected(WebElement) */
    public boolean validateElementIsSelected(WebElement element)
    {
        return elementInteractions.validateElementIsSelected(element);
    }

    // ========================
    // Actions
    // ========================

    /** @see ActionsManager#rightClick(By) */
    public void rightClick(By locator)
    {
        actionsManager.rightClick(locator);
    }

    /** @see ActionsManager#doubleClick(By) */
    public void doubleClick(By locator)
    {
        actionsManager.doubleClick(locator);
    }

    /** @see ActionsManager#doubleClick(WebElement) */
    public void doubleClick(WebElement element)
    {
        actionsManager.doubleClick(element);
    }

    /** @see ActionsManager#hoverOverElement(By) */
    public void hoverOverElement(By locator)
    {
        actionsManager.hoverOverElement(locator);
    }

    /** @see ActionsManager#hoverOverElement(WebElement) */
    public void hoverOverElement(WebElement element)
    {
        actionsManager.hoverOverElement(element);
    }

    /** @see ActionsManager#dragAndDrop(By, By) */
    public void dragAndDrop(By sourceLocator, By targetLocator)
    {
        actionsManager.dragAndDrop(sourceLocator, targetLocator);
    }

    /** @see ActionsManager#scrollToElement(By) */
    public void scrollToElement(By locator)
    {
        actionsManager.scrollToElement(locator);
    }

    /** @see ActionsManager#scrollToElement(WebElement) */
    public void scrollToElement(WebElement element)
    {
        actionsManager.scrollToElement(element);
    }

    // ========================
    // Checkbox & Radio
    // ========================

    /** @see ActionsManager#checkCheckbox(By) */
    public void checkCheckbox(By locator)
    {
        actionsManager.checkCheckbox(locator);
    }

    /** @see ActionsManager#uncheckCheckbox(By) */
    public void uncheckCheckbox(By locator)
    {
        actionsManager.uncheckCheckbox(locator);
    }

    /** @see ActionsManager#selectRadioButton(By) */
    public void selectRadioButton(By locator)
    {
        actionsManager.selectRadioButton(locator);
    }

    // ========================
    // Dropdown
    // ========================

    /** @see DropdownManager#selectFromDropDownMenu(By, String, String) */
    public void selectFromDropDownMenu(By locator, String selectBy, String selectInput)
    {
        dropdownManager.selectFromDropDownMenu(locator, selectBy, selectInput);
    }

    /** @see DropdownManager#selectFromDropDownMenu(WebElement, String, String) */
    public void selectFromDropDownMenu(WebElement element, String selectBy, String selectInput)
    {
        dropdownManager.selectFromDropDownMenu(element, selectBy, selectInput);
    }

    /** @see DropdownManager#deselectFromDropDownMenu(By, String, String) */
    public void deselectFromDropDownMenu(By locator, String deselectBy, String deselectInput)
    {
        dropdownManager.deselectFromDropDownMenu(locator, deselectBy, deselectInput);
    }

    /** @see DropdownManager#deselectFromDropDownMenu(WebElement, String, String) */
    public void deselectFromDropDownMenu(WebElement element, String deselectBy, String deselectInput)
    {
        dropdownManager.deselectFromDropDownMenu(element, deselectBy, deselectInput);
    }

    // ========================
    // Wait Management
    // ========================

    /** @see WaitManager#setImplicitWait(Duration) */
    public void setImplicitWait(Duration duration)
    {
        waitManager.setImplicitWait(duration);
    }

    /** @see WaitManager#setImplicitWait(long) */
    public void setImplicitWait(long seconds)
    {
        waitManager.setImplicitWait(seconds);
    }

    /** @see WaitManager#setImplicitWait(String, long) */
    public void setImplicitWait(String durationIn, long waitingTime)
    {
        waitManager.setImplicitWait(durationIn, waitingTime);
    }

    /** @see WaitManager#setExplicitWait(By, long) */
    public void setExplicitWait(By locator, long timeoutSeconds)
    {
        waitManager.setExplicitWait(locator, timeoutSeconds);
    }

    /** @see WaitManager#setExplicitWait(By, Duration) */
    public void setExplicitWait(By locator, Duration timeout)
    {
        waitManager.setExplicitWait(locator, timeout);
    }

    /** @see WaitManager#setFluentWait(By, long, long, String) */
    public void setFluentWait(By locator, long timeoutSeconds, long pollingMillis, String timeoutMessage)
    {
        waitManager.setFluentWait(locator, timeoutSeconds, pollingMillis, timeoutMessage);
    }

    /** @see WaitManager#setFluentWait(By, Duration, Duration, String) */
    public void setFluentWait(By locator, Duration timeout, Duration pollingInterval, String timeoutMessage)
    {
        waitManager.setFluentWait(locator, timeout, pollingInterval, timeoutMessage);
    }

    // ========================
    // Window Handling
    // ========================

    /** @see WindowManager#getCurrentWindowHandle() */
    public String getCurrentWindowHandle()
    {
        return windowManager.getCurrentWindowHandle();
    }

    /** @see WindowManager#getAllWindowHandles() */
    public List<String> getAllWindowHandles()
    {
        return windowManager.getAllWindowHandles();
    }

    /** @see WindowManager#switchToWindowByHandle(String) */
    public void switchToWindowByHandle(String windowHandle)
    {
        windowManager.switchToWindowByHandle(windowHandle);
    }

    /** @see WindowManager#switchToWindowByIndex(int) */
    public void switchToWindowByIndex(int index)
    {
        windowManager.switchToWindowByIndex(index);
    }

    /** @see WindowManager#switchToNewWindow(String) */
    public void switchToNewWindow(String parentHandle)
    {
        windowManager.switchToNewWindow(parentHandle);
    }

    /** @see WindowManager#closeCurrentWindowAndSwitchTo(String) */
    public void closeCurrentWindowAndSwitchTo(String windowHandleToSwitchTo)
    {
        windowManager.closeCurrentWindowAndSwitchTo(windowHandleToSwitchTo);
    }

    /** @see WindowManager#getWindowCount() */
    public int getWindowCount()
    {
        return windowManager.getWindowCount();
    }

    // ========================
    // Alert Handling
    // ========================

    /** @see AlertManager#acceptAlert() */
    public void acceptAlert()
    {
        alertManager.acceptAlert();
    }

    /** @see AlertManager#dismissAlert() */
    public void dismissAlert()
    {
        alertManager.dismissAlert();
    }

    /** @see AlertManager#getAlertText() */
    public String getAlertText()
    {
        return alertManager.getAlertText();
    }

    /** @see AlertManager#typeInAlert(String) */
    public void typeInAlert(String text)
    {
        alertManager.typeInAlert(text);
    }

    // ========================
    // IFrame Handling
    // ========================

    /** @see FrameManager#switchToIFrame(By) */
    public void switchToIFrame(By locator)
    {
        frameManager.switchToIFrame(locator);
    }

    /** @see FrameManager#switchToIFrame(WebElement) */
    public void switchToIFrame(WebElement iFrameElement)
    {
        frameManager.switchToIFrame(iFrameElement);
    }

    /** @see FrameManager#switchToIFrameByIndex(int) */
    public void switchToIFrameByIndex(int index)
    {
        frameManager.switchToIFrameByIndex(index);
    }

    /** @see FrameManager#switchToIFrameByNameOrId(String) */
    public void switchToIFrameByNameOrId(String nameOrId)
    {
        frameManager.switchToIFrameByNameOrId(nameOrId);
    }

    /** @see FrameManager#switchToDefaultContent() */
    public void switchToDefaultContent()
    {
        frameManager.switchToDefaultContent();
    }

    /** @see FrameManager#switchToParentFrame() */
    public void switchToParentFrame()
    {
        frameManager.switchToParentFrame();
    }

    // ========================
    // Screenshots
    // ========================

    /** @see ScreenshotManager#takeScreenshot(String) */
    public void takeScreenshot(String screenshotLabel)
    {
        screenshotManager.takeScreenshot(screenshotLabel);
    }
}