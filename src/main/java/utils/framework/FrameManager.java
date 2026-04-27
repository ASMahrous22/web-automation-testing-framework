package utils.framework;

import org.openqa.selenium.*;

/**
 * FrameManager — Handles switching between iframes and the main page context.
 *
 * <p>Supports switching by {@link By} locator, {@link WebElement} reference,
 * 0-based index, or {@code name}/{@code id} attribute. Always call
 * {@link #switchToDefaultContent()} when done working inside a frame.</p>
 *
 * @author ASMahrous
 */
public class FrameManager
{
    private final WebDriver   browser;
    private final WaitManager waitManager;

    /**
     * @param browser     the active WebDriver session
     * @param waitManager shared {@link WaitManager} for visibility waits
     */
    public FrameManager(WebDriver browser, WaitManager waitManager)
    {
        this.browser     = browser;
        this.waitManager = waitManager;
    }

    // ========================
    // Switch Into Frame
    // ========================

    /**
     * Switches the WebDriver context into an iframe located by a {@link By} locator.
     *
     * <p>After calling this, all subsequent element searches are scoped to the iframe.
     * Call {@link #switchToDefaultContent()} when done.</p>
     *
     * @param locator the By locator of the {@code <iframe>} element
     */
    public void switchToIFrame(By locator)
    {
        WebElement iFrame = waitManager.waitForElementToBeVisible(locator);
        browser.switchTo().frame(iFrame);
    }

    /**
     * Switches the WebDriver context into an iframe using a {@link WebElement} reference.
     *
     * @param iFrameElement the WebElement representing the {@code <iframe>}
     */
    public void switchToIFrame(WebElement iFrameElement)
    {
        if (iFrameElement == null)
        {
            throw new RuntimeException("Error: iframe element is null — check your locator!");
        }
        browser.switchTo().frame(iFrameElement);
    }

    /**
     * Switches the WebDriver context into an iframe by its 0-based index on the page.
     *
     * <p>Use when multiple iframes exist and you want to target one by position.</p>
     *
     * @param index the 0-based index of the iframe on the page
     */
    public void switchToIFrameByIndex(int index)
    {
        browser.switchTo().frame(index);
    }

    /**
     * Switches the WebDriver context into an iframe by its {@code name} or {@code id} attribute.
     *
     * @param nameOrId the value of the iframe's {@code name} or {@code id} attribute
     */
    public void switchToIFrameByNameOrId(String nameOrId)
    {
        browser.switchTo().frame(nameOrId);
    }

    // ========================
    // Switch Out of Frame
    // ========================

    /**
     * Switches the WebDriver context back to the top-level page (default content).
     *
     * <p>Always call this after finishing interactions inside an iframe.</p>
     */
    public void switchToDefaultContent()
    {
        browser.switchTo().defaultContent();
    }

    /**
     * Switches the WebDriver context one level up — from a nested iframe to its
     * immediate parent frame (or the main page if not nested).
     */
    public void switchToParentFrame()
    {
        browser.switchTo().parentFrame();
    }
}