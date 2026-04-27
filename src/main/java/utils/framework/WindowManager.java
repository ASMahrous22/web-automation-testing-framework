package utils.framework;

import org.openqa.selenium.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * WindowManager — Manages browser windows and tabs.
 *
 * <p>Provides switching by handle, index, or auto-detection of new windows,
 * as well as closing windows and querying open window counts.</p>
 *
 * @author ASMahrous
 */
public class WindowManager
{
    private final WebDriver browser;

    /**
     * @param browser the active WebDriver session
     */
    public WindowManager(WebDriver browser)
    {
        this.browser = browser;
    }

    // ========================
    // Query
    // ========================

    /**
     * Returns the handle (unique ID) of the currently focused browser window or tab.
     *
     * <p>Store this handle before opening new windows so you can switch back later:</p>
     * <pre>{@code
     * String main = driver.getCurrentWindowHandle();
     * driver.clickElement(By.id("openPopup"));
     * driver.switchToNewWindow(main);
     * }</pre>
     *
     * @return the current window handle string
     */
    public String getCurrentWindowHandle()
    {
        return browser.getWindowHandle();
    }

    /**
     * Returns all open window/tab handles as an ordered list.
     *
     * <p>Windows are returned in the order they were opened, making
     * index-based switching predictable.</p>
     *
     * @return a {@link List} of all open window handle strings
     */
    public List<String> getAllWindowHandles()
    {
        return new ArrayList<>(browser.getWindowHandles());
    }

    /**
     * Returns the total number of currently open windows or tabs.
     *
     * @return the count of open window handles
     */
    public int getWindowCount()
    {
        return browser.getWindowHandles().size();
    }

    // ========================
    // Switch
    // ========================

    /**
     * Switches focus to a window or tab by its handle string.
     *
     * @param windowHandle the handle of the target window
     */
    public void switchToWindowByHandle(String windowHandle)
    {
        browser.switchTo().window(windowHandle);
    }

    /**
     * Switches focus to a window or tab by its 0-based position index.
     *
     * <p>Index {@code 0} is always the first opened window/tab.</p>
     *
     * @param index the 0-based index of the target window
     * @throws IndexOutOfBoundsException if the index exceeds the number of open windows
     */
    public void switchToWindowByIndex(int index)
    {
        List<String> handles = getAllWindowHandles();
        browser.switchTo().window(handles.get(index));
    }

    /**
     * Switches focus to the newest window or tab opened after the given parent handle.
     *
     * <p>Typical usage — click a link that opens a popup, then switch to it:</p>
     * <pre>{@code
     * String parent = driver.getCurrentWindowHandle();
     * driver.clickElement(By.linkText("Open in new tab"));
     * driver.switchToNewWindow(parent);
     * }</pre>
     *
     * @param parentHandle the handle of the originating window to exclude
     * @throws RuntimeException if no new window is detected
     */
    public void switchToNewWindow(String parentHandle)
    {
        Set<String> allHandles = browser.getWindowHandles();
        for (String handle : allHandles)
        {
            if (!handle.equals(parentHandle))
            {
                browser.switchTo().window(handle);
                return;
            }
        }
        throw new RuntimeException("No new window found after switching from handle: " + parentHandle);
    }

    // ========================
    // Close
    // ========================

    /**
     * Closes the currently focused window or tab, then switches focus to the given handle.
     *
     * <p>Useful for closing a popup and returning to the main window:</p>
     * <pre>{@code
     * driver.closeCurrentWindowAndSwitchTo(mainWindow);
     * }</pre>
     *
     * @param windowHandleToSwitchTo the handle to focus after closing the current window
     */
    public void closeCurrentWindowAndSwitchTo(String windowHandleToSwitchTo)
    {
        browser.close();
        browser.switchTo().window(windowHandleToSwitchTo);
    }
}