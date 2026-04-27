package utils.framework;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;

/**
 * DropdownManager — Handles HTML {@code <select>} dropdown interactions.
 *
 * <p>Supports selecting and deselecting options by index, value attribute,
 * exact visible text, or partial visible text.</p>
 *
 * @author ASMahrous
 */
public class DropdownManager
{
    private final WaitManager waitManager;

    /**
     * @param waitManager shared {@link WaitManager} for visibility waits
     */
    public DropdownManager(WaitManager waitManager)
    {
        this.waitManager = waitManager;
    }

    // ========================
    // Select
    // ========================

    /**
     * Selects an option from an HTML {@code <select>} dropdown using a WebElement.
     *
     * <p>Supported strategies (case-insensitive):
     * <ul>
     *   <li>{@code "index"} — 0-based option index, as index of 0 will select the first option</li>
     *   <li>{@code "value"} — option {@code value} attribute</li>
     *   <li>{@code "visible"} / {@code "visible text"} — exact visible text</li>
     *   <li>{@code "contains"} / {@code "contains text"} — partial visible text</li>
     * </ul>
     * </p>
     *
     * @param element     the dropdown WebElement
     * @param selectBy    the selection strategy
     * @param selectInput the value to select by
     */
    public void selectFromDropDownMenu(WebElement element, String selectBy, String selectInput)
    {
        validateNotNull(element);
        Select dropdown = new Select(element);

        switch (selectBy.toLowerCase())
        {
            case "index":
                dropdown.selectByIndex(Integer.parseInt(selectInput));
                break;

            case "value":
                dropdown.selectByValue(selectInput);
                break;

            case "visible text":
            case "visibletext":
            case "visible":
                dropdown.selectByVisibleText(selectInput);
                break;

            case "contains visible text":
            case "containsvisibletext":
            case "contains text":
            case "containstext":
            case "contains":
                dropdown.selectByContainsVisibleText(selectInput);
                break;

            default:
                System.out.println("Invalid selection type: " + selectBy);
        }
    }

    /**
     * Finds the dropdown by locator, then selects an option using the specified strategy.
     *
     * @param locator     the By locator of the dropdown element
     * @param selectBy    the selection strategy
     * @param selectInput the value to select by
     */
    public void selectFromDropDownMenu(By locator, String selectBy, String selectInput)
    {
        selectFromDropDownMenu(waitManager.waitForElementToBeVisible(locator), selectBy, selectInput);
    }

    // ========================
    // Deselect
    // ========================

    /**
     * Deselects an option from a multi-select {@code <select>} dropdown using a WebElement.
     *
     * <p>Supported strategies (case-insensitive):
     * <ul>
     *   <li>{@code "index"} — 0-based option index, as the index of 0 will deselect the first option</li>
     *   <li>{@code "value"} — option {@code value} attribute</li>
     *   <li>{@code "visible"} / {@code "visible text"} — exact visible text</li>
     *   <li>{@code "contains"} / {@code "contains text"} — partial visible text</li>
     *   <li>{@code "all"} — deselect every selected option</li>
     * </ul>
     * </p>
     *
     * @param element       the dropdown WebElement
     * @param deselectBy    the deselection strategy
     * @param deselectInput the value to deselect by (ignored when strategy is "all")
     */
    public void deselectFromDropDownMenu(WebElement element, String deselectBy, String deselectInput)
    {
        validateNotNull(element);
        Select dropdown = new Select(element);

        switch (deselectBy.toLowerCase())
        {
            case "index":
                dropdown.deselectByIndex(Integer.parseInt(deselectInput));
                break;

            case "value":
                dropdown.deselectByValue(deselectInput);
                break;

            case "visible text":
            case "visibletext":
            case "visible":
                dropdown.deselectByVisibleText(deselectInput);
                break;

            case "contains visible text":
            case "containsvisibletext":
            case "contains text":
            case "containstext":
            case "contains":
                dropdown.deSelectByContainsVisibleText(deselectInput);
                break;

            case "all":
                dropdown.deselectAll();
                break;

            default:
                System.out.println("Invalid deselection type: " + deselectBy);
        }
    }

    /**
     * Finds the dropdown by locator, then deselects an option using the specified strategy.
     *
     * @param locator       the By locator of the dropdown element
     * @param deselectBy    the deselection strategy
     * @param deselectInput the value to deselect by (ignored when strategy is "all")
     */
    public void deselectFromDropDownMenu(By locator, String deselectBy, String deselectInput)
    {
        deselectFromDropDownMenu(waitManager.waitForElementToBeVisible(locator), deselectBy, deselectInput);
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