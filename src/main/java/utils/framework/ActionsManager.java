package utils.framework;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;

/**
 * ActionsManager — Handles advanced user-gesture interactions.
 *
 * <p>Wraps Selenium's {@link Actions} API to provide right-click, double-click,
 * hover, drag-and-drop, scroll, checkbox, and radio-button operations.</p>
 *
 * @author ASMahrous
 */
public class ActionsManager
{
    private final WebDriver   browser;
    private final WaitManager waitManager;

    /**
     * @param browser     the active WebDriver session
     * @param waitManager shared {@link WaitManager} for pre-interaction waits
     */
    public ActionsManager(WebDriver browser, WaitManager waitManager)
    {
        this.browser     = browser;
        this.waitManager = waitManager;
    }

    // ========================
    // Mouse Gestures
    // ========================

    /**
     * Performs a right-click (context click) on the element located by {@code locator}.
     *
     * @param locator the By locator of the element to right-click
     */
    public void rightClick(By locator)
    {
        WebElement element = waitManager.waitForElementToBeVisible(locator);
        new Actions(browser)
                .contextClick(element)
                .perform();
    }

    /**
     * Performs a double-click on the element located by {@code locator}.
     *
     * @param locator the By locator of the element to double-click
     */
    public void doubleClick(By locator)
    {
        WebElement element = waitManager.waitForElementToBeClickable(locator);
        new Actions(browser)
                .doubleClick(element)
                .perform();
    }

    /**
     * Performs a double-click on the given WebElement.
     *
     * @param element the WebElement to double-click
     */
    public void doubleClick(WebElement element)
    {
        validateNotNull(element);
        new Actions(browser)
                .doubleClick(element)
                .perform();
    }

    /**
     * Hovers the mouse over the element located by {@code locator}
     * (triggers tooltips, hover menus, etc.).
     *
     * @param locator the By locator of the element to hover over
     */
    public void hoverOverElement(By locator)
    {
        WebElement element = waitManager.waitForElementToBeVisible(locator);
        new Actions(browser)
                .moveToElement(element)
                .perform();
    }

    /**
     * Hovers the mouse over the given WebElement.
     *
     * @param element the WebElement to hover over
     */
    public void hoverOverElement(WebElement element)
    {
        validateNotNull(element);
        new Actions(browser)
                .moveToElement(element)
                .perform();
    }

    // ========================
    // Drag & Drop
    // ========================

    /**
     * Drags the source element and drops it onto the target element.
     *
     * @param sourceLocator the By locator of the element to drag
     * @param targetLocator the By locator of the drop target
     */
    public void dragAndDrop(By sourceLocator, By targetLocator)
    {
        WebElement source = waitManager.waitForElementToBeVisible(sourceLocator);
        WebElement target = waitManager.waitForElementToBeVisible(targetLocator);
        new Actions(browser)
                .dragAndDrop(source, target)
                .perform();
    }

    // ========================
    // Scroll
    // ========================

    /**
     * Scrolls the element into the vertical and horizontal center of the view.
     *
     * <p>Uses JavaScript's {@code scrollIntoView} with
     * {@code block: "center", inline: "center"} so the element lands in the
     * middle of the screen rather than at the edge — making it easy to see
     * and less likely to be obscured by sticky headers or footers.</p>
     *
     * @param locator the By locator of the element to scroll to
     */
    public void scrollToElement(By locator)
    {
        WebElement element = waitManager.waitForElementToBePresent(locator);
        scrollToCenter(element);
    }

    /**
     * Scrolls the element into the vertical and horizontal center of the view.
     *
     * @param element the WebElement to scroll to
     */
    public void scrollToElement(WebElement element)
    {
        validateNotNull(element);
        scrollToCenter(element);
    }

    /**
     * Executes the JavaScript that centers the element in the view.
     *
     * <p>{@code block} controls vertical alignment and {@code inline} controls
     * horizontal alignment. Both are set to {@code "center"}.</p>
     */
    private void scrollToCenter(WebElement element)
    {
        ((JavascriptExecutor) browser).executeScript
                ("arguments[0].scrollIntoView({block: 'center', inline: 'center'});", element );
    }

    // ========================
    // Checkbox & Radio
    // ========================

    /**
     * Checks a checkbox if it is not already checked.
     *
     * @param locator the By locator of the checkbox element
     */
    public void checkCheckbox(By locator)
    {
        WebElement checkbox = waitManager.waitForElementToBeClickable(locator);
        if (!checkbox.isSelected())
        {
            checkbox.click();
        }
    }

    /**
     * Unchecks a checkbox if it is currently checked.
     *
     * @param locator the By locator of the checkbox element
     */
    public void uncheckCheckbox(By locator)
    {
        WebElement checkbox = waitManager.waitForElementToBeClickable(locator);
        if (checkbox.isSelected())
        {
            checkbox.click();
        }
    }

    /**
     * Selects a radio button if it is not already selected.
     *
     * @param locator the By locator of the radio button element
     */
    public void selectRadioButton(By locator)
    {
        WebElement radioButton = waitManager.waitForElementToBeClickable(locator);
        if (!radioButton.isSelected())
        {
            radioButton.click();
        }
    }

    // ========================
    // Private Helpers
    // ========================

    private void validateNotNull(WebElement element)
    {
        if (element == null)
        {
            throw new RuntimeException("Error: element is null — check your locator or findElement call!");
        }
    }
}