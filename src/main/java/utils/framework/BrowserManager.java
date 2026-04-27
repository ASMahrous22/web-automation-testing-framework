package utils.framework;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.safari.SafariDriver;

import java.util.ArrayList;
import java.util.List;

/**
 * BrowserManager — Manages browser lifecycle, navigation, and window size.
 *
 * <p>Responsible for launching the correct browser driver, providing
 * page-level navigation (URL, back/forward/refresh), controlling
 * the window size, and closing the session.</p>
 *
 * @author ASMahrous
 */
public class BrowserManager
{
    private final WebDriver browser;

    // ========================
    // Browser Options Builder
    // ========================

    /**
     * Fluent builder for configuring browser launch options.
     *
     * <p>Pass an instance to {@link BrowserManager#BrowserManager(String, BrowserOptions)}.
     * Only Chrome, Firefox, and Edge support options; Safari uses default config.</p>
     *
     * <p><b>Example:</b></p>
     * <pre>{@code
     * BrowserManager.BrowserOptions opts = new BrowserManager.BrowserOptions()
     *     .headless()
     *     .maximized()
     *     .withArgument("--disable-notifications");
     * }</pre>
     */
    public static class BrowserOptions
    {
        boolean headless    = false;
        boolean kiosk       = false;
        boolean maximized   = false;
        String  userDataDir = null;
        final List<String> extraArguments = new ArrayList<>();

        /** Runs the browser with no visible UI window. Ideal for CI environments. */
        public BrowserOptions headless()
        {
            this.headless = true;
            return this;
        }

        /**
         * Launches the browser in full-screen borderless kiosk mode.
         * Kiosk takes precedence over headless when both are set.
         */
        public BrowserOptions kiosk()
        {
            this.kiosk = true;
            return this;
        }

        /** Launches the browser in a maximized window. */
        public BrowserOptions maximized()
        {
            this.maximized = true;
            return this;
        }

        /**
         * Sets a custom user-data directory so the browser loads a specific profile.
         *
         * @param path absolute path to the profile directory
         */
        public BrowserOptions withUserDataDir(String path)
        {
            this.userDataDir = path;
            return this;
        }

        /**
         * Appends an arbitrary browser argument (e.g., {@code "--disable-infobars"}).
         *
         * @param argument the full argument string including leading dashes
         */
        public BrowserOptions withArgument(String argument)
        {
            this.extraArguments.add(argument);
            return this;
        }
    }

    // ========================
    // Constructors
    // ========================

    /**
     * Launches the specified browser with default settings.
     *
     * <p>Supported values (case-insensitive): {@code "chrome"}, {@code "firefox"},
     * {@code "edge"}, {@code "safari"}. Defaults to Chrome if unrecognized.</p>
     *
     * @param browserName the browser to launch
     */
    public BrowserManager(String browserName)
    {
        this(browserName, new BrowserOptions());
    }

    /**
     * Launches the specified browser with custom options.
     *
     * <p>Supported values (case-insensitive): {@code "chrome"}, {@code "firefox"},
     * {@code "edge"}, {@code "safari"}. Defaults to Chrome if unrecognized.
     * Safari ignores the {@code options} parameter.</p>
     *
     * @param browserName the browser to launch
     * @param options     a {@link BrowserOptions} instance configuring the launch
     */
    public BrowserManager(String browserName, BrowserOptions options)
    {
        switch (browserName.toLowerCase())
        {
            case "edge":
                browser = new EdgeDriver(buildEdgeOptions(options));
                break;

            case "safari":
                browser = new SafariDriver();
                break;

            case "firefox":
                browser = new FirefoxDriver(buildFirefoxOptions(options));
                break;

            default:
                browser = new ChromeDriver(buildChromeOptions(options));
        }
    }

    // ========================
    // Driver Access
    // ========================

    /**
     * Returns the underlying {@link WebDriver} instance.
     * Used by all other managers that need direct driver access.
     *
     * @return the active WebDriver session
     */
    public WebDriver getDriver()
    {
        return browser;
    }

    // ========================
    // Browser Lifecycle
    // ========================

    /**
     * Closes the currently focused browser tab.
     * If only one tab is open, this effectively closes the browser.
     */
    public void closeCurrentTab()
    {
        browser.close();
    }

    /**
     * Closes all open browser tabs and ends the WebDriver session.
     * Always call this at the end of your test to release resources.
     */
    public void closeAllTabs()
    {
        browser.quit();
    }

    // ========================
    // Navigation
    // ========================

    /**
     * Navigates the browser to the given URL.
     *
     * @param url the full URL to open (e.g., "https://example.com")
     */
    public void goToURL(String url)
    {
        browser.get(url);
    }

    /**
     * Returns the title of the current page (the text shown in the browser tab).
     *
     * @return the current page title
     */
    public String getCurrentPageTitle()
    {
        return browser.getTitle();
    }

    /**
     * Returns the full URL of the current page.
     *
     * @return the current page URL
     */
    public String getCurrentPageURL()
    {
        return browser.getCurrentUrl();
    }

    /**
     * Simulates clicking browser navigation buttons.
     *
     * <p>Supported values (case-insensitive):
     * <ul>
     *   <li>{@code "back"} — go to the previous page</li>
     *   <li>{@code "forward"} — go to the next page</li>
     *   <li>{@code "refresh"} — reload the current page</li>
     * </ul>
     * </p>
     *
     * @param button the navigation action to perform
     */
    public void manageNavigationButtons(String button)
    {
        switch (button.toLowerCase())
        {
            case "back":
                browser.navigate().back();
                break;

            case "refresh":
                browser.navigate().refresh();
                break;

            case "forward":
                browser.navigate().forward();
                break;

            default:
                System.out.println("Unknown navigation button: " + button);
        }
    }

    // ========================
    // Window Size
    // ========================

    /**
     * Controls the browser window size.
     *
     * <p>Supported values (case-insensitive):
     * <ul>
     *   <li>{@code "minimize"} / {@code "min"} — minimize the window</li>
     *   <li>{@code "maximize"} / {@code "max"} — maximize the window</li>
     *   <li>anything else — enter fullscreen mode</li>
     * </ul>
     * </p>
     *
     * @param action the window size action to perform
     */
    public void manageScreenSize(String action)
    {
        switch (action.toLowerCase())
        {
            case "min":
            case "minimize":
                browser.manage().window().minimize();
                break;

            case "max":
            case "maximize":
                browser.manage().window().maximize();
                break;

            default:
                browser.manage().window().fullscreen();
        }
    }

    // ========================
    // Options Builders (private)
    // ========================

    private ChromeOptions buildChromeOptions(BrowserOptions opts)
    {
        ChromeOptions chromeOptions = new ChromeOptions();

        if (opts.kiosk)
            chromeOptions.addArguments("--kiosk");
        else if (opts.headless)
            chromeOptions.addArguments("--headless=new");

        if (opts.maximized)
            chromeOptions.addArguments("--start-maximized");

        if (opts.userDataDir != null && !opts.userDataDir.isEmpty())
            chromeOptions.addArguments("--user-data-dir=" + opts.userDataDir);

        for (String arg : opts.extraArguments)
            chromeOptions.addArguments(arg);

        return chromeOptions;
    }

    private FirefoxOptions buildFirefoxOptions(BrowserOptions opts)
    {
        FirefoxOptions firefoxOptions = new FirefoxOptions();

        if (opts.headless && !opts.kiosk)
            firefoxOptions.addArguments("--headless");

        if (opts.maximized)
            firefoxOptions.addArguments("--start-maximized");

        if (opts.userDataDir != null && !opts.userDataDir.isEmpty())
            firefoxOptions.addArguments("-profile", opts.userDataDir);

        for (String arg : opts.extraArguments)
            firefoxOptions.addArguments(arg);

        return firefoxOptions;
    }

    private EdgeOptions buildEdgeOptions(BrowserOptions opts)
    {
        EdgeOptions edgeOptions = new EdgeOptions();

        if (opts.kiosk)
            edgeOptions.addArguments("--kiosk");
        else if (opts.headless)
            edgeOptions.addArguments("--headless=new");

        if (opts.maximized)
            edgeOptions.addArguments("--start-maximized");

        if (opts.userDataDir != null && !opts.userDataDir.isEmpty())
            edgeOptions.addArguments("--user-data-dir=" + opts.userDataDir);

        for (String arg : opts.extraArguments)
            edgeOptions.addArguments(arg);

        return edgeOptions;
    }
}