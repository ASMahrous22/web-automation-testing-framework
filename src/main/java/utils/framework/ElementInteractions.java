package utils.framework;

import org.openqa.selenium.*;

/**
 * ElementInteractions — Handles all direct interactions with WebElements.
 *
 * <p>Covers clicking, typing, clearing, reading text, and validating
 * element state (visible, enabled, selected). Every method waits for
 * the element to be in the correct state before acting.</p>
 *
 * @author ASMahrous
 */
public class ElementInteractions
{
    private final WaitManager waitManager;

    /**
     * @param waitManager shared {@link WaitManager} for visibility / clickability waits
     */
    public ElementInteractions(WaitManager waitManager)
    {
        this.waitManager = waitManager;
    }

    // ========================
    // Click
    // ========================

    /**
     * Waits for the element to be clickable, then clicks it.
     *
     * @param locator the By locator of the element to click
     */
    public void clickElement(By locator)
    {
        waitManager.waitForElementToBeClickable(locator).click();
    }

    /**
     * Waits for the element to be clickable, then clicks it.
     *
     * @param element the target WebElement to click
     */
    public void clickElement(WebElement element)
    {
        validateNotNull(element);
        waitManager.waitForElementToBeClickable(element).click();
    }

    // ========================
    // Write / Clear
    // ========================

    /**
     * Finds the element by locator, clears it, then types the given text.
     *
     * @param locator the By locator of the target input element
     * @param text    the text to type
     */
    public void writeInElement(By locator, String text)
    {
        WebElement element = waitManager.waitForElementToBeVisible(locator);
        element.clear();
        element.sendKeys(text);
    }

    /**
     * Clears any existing text in the element, then types the given text.
     *
     * @param element the target input WebElement
     * @param text    the text to type
     */
    public void writeInElement(WebElement element, String text)
    {
        clearElementText(element);
        element.sendKeys(text);
    }

    /**
     * Finds the element by locator and clears its text content.
     *
     * @param locator the By locator of the target input element
     */
    public void clearElementText(By locator)
    {
        waitManager.waitForElementToBeVisible(locator).clear();
    }

    /**
     * Clears all text content from the given input element.
     *
     * @param element the target input WebElement
     */
    public void clearElementText(WebElement element)
    {
        validateNotNull(element);
        element.clear();
    }

    // ========================
    // Get Text
    // ========================

    /**
     * Finds the element by locator and returns its visible text.
     *
     * @param locator the By locator of the target element
     * @return the element's visible text
     */
    public String getElementText(By locator)
    {
        return waitManager.waitForElementToBeVisible(locator).getText();
    }

    /**
     * Returns the visible text content of the given element.
     *
     * @param element the target WebElement
     * @return the element's visible text
     */
    public String getElementText(WebElement element)
    {
        validateNotNull(element);
        return element.getText();
    }

    // ========================
    // Element State Validation
    // ========================

    /**
     * Checks whether the given element is currently visible on the page.
     *
     * @param element the target WebElement
     * @return {@code true} if displayed, {@code false} otherwise
     */
    public boolean validateElementIsDisplayed(WebElement element)
    {
        validateNotNull(element);
        return element.isDisplayed();
    }

    /**
     * Checks whether the given element is currently enabled and interactable.
     *
     * @param element the target WebElement
     * @return {@code true} if enabled, {@code false} otherwise
     */
    public boolean validateElementIsEnabled(WebElement element)
    {
        validateNotNull(element);
        return element.isEnabled();
    }

    /**
     * Checks whether the given element is currently selected (e.g., checkbox / radio).
     *
     * @param element the target WebElement
     * @return {@code true} if selected, {@code false} otherwise
     */
    public boolean validateElementIsSelected(WebElement element)
    {
        validateNotNull(element);
        return element.isSelected();
    }

    // ========================
    // Private Helpers
    // ========================

    /**
     * Guards against null WebElement references with a clear error message.
     *
     * @param element the WebElement to validate
     * @throws RuntimeException if the element is {@code null}
     */
    private void validateNotNull(WebElement element)
    {
        if (element == null)
        {
            throw new RuntimeException("Error: element is null — check your locator or findElement call!");
        }
    }
}