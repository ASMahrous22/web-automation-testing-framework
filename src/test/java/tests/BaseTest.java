package tests;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import utils.ASM_Framework;
import utils.AllureHelper;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * BaseTest — Parent class for all TestNG test classes.
 *
 * <p>Manages the full test lifecycle and is safe for parallel test execution
 * at any level (methods, classes, or suites) via a {@link ThreadLocal} driver.</p>
 *
 * <h3>Why ThreadLocal?</h3>
 * <p>Each thread gets its own isolated {@link ASM_Framework} instance stored in
 * {@code DRIVER_THREAD_LOCAL}. This prevents threads from sharing or overwriting
 * each other's browser session when tests run in parallel.</p>
 *
 * <h3>Lifecycle:</h3>
 * <ul>
 *   <li>{@link #setUp()} — creates a new {@link ASM_Framework} for the current
 *       thread, maximizes the window, and navigates to the base URL</li>
 *   <li>{@link #tearDown(ITestResult)} — on failure captures a screenshot and
 *       attaches it to Allure; always quits the browser and removes the driver
 *       from the thread-local to prevent memory leaks</li>
 * </ul>
 *
 * <h3>Accessing the driver in test subclasses:</h3>
 * <pre>{@code
 * public class LoginTests extends BaseTest {
 *
 *     @Test
 *     public void loginWithValidCredentials() throws IOException {
 *         LoginPage loginPage = new LoginPage(getDriver());
 *         loginPage.open();
 *         loginPage.login("user@example.com", "secret");
 *         loginPage.saveScreenshot("TC01_AfterLogin", getDriver());
 *
 *         assertTrue(loginPage.urlContains("dashboard"));
 *     }
 * }
 * }</pre>
 *
 * <h3>Enabling parallel execution in testng.xml:</h3>
 * <pre>{@code
 * <suite name="Suite" parallel="methods" thread-count="3">
 *     <test name="Login Tests">
 *         <classes>
 *             <class name="tests.LoginTests"/>
 *         </classes>
 *     </test>
 * </suite>
 * }</pre>
 *
 * @author ASMahrous
 */
public class BaseTest
{
    /**
     * Thread-local storage for the driver — each thread gets its own
     * isolated {@link ASM_Framework} instance, making parallel execution safe.
     *
     * <p>Always access via {@link #getDriver()} — never reference this field
     * directly from subclasses.</p>
     */
    private static final ThreadLocal<ASM_Framework> DRIVER_THREAD_LOCAL = new ThreadLocal<>();

    /** Loaded once at class-load time — read-only, so thread-safe as static. */
    private static final Properties CONFIG = loadConfig();

    // ========================
    // Config
    // ========================

    private static Properties loadConfig()
    {
        Properties props = new Properties();
        props.setProperty("browser",  "chrome");
        props.setProperty("base.url", "https://automationexercise.com");
        props.setProperty("headless", "false");

        String configPath = System.getProperty("user.dir")
                + "/src/test/resources/config.properties";

        try (FileInputStream fis = new FileInputStream(configPath))
        {
            props.load(fis);
        }
        catch (IOException e)
        {
            System.out.println("[BaseTest] config.properties not found — using defaults. ("
                    + e.getMessage() + ")");
        }

        return props;
    }

    // ========================
    // Driver Access
    // ========================

    /**
     * Returns the {@link ASM_Framework} instance for the current thread.
     *
     * <p>Use this in every test subclass instead of a raw field reference:</p>
     * <pre>{@code
     * LoginPage loginPage = new LoginPage(getDriver());
     * }</pre>
     *
     * @return the current thread's driver instance
     * @throws IllegalStateException if called before {@link #setUp()} has run
     */
    protected ASM_Framework getDriver()
    {
        ASM_Framework driver = DRIVER_THREAD_LOCAL.get();
        if (driver == null)
            throw new IllegalStateException(
                    "[BaseTest] Driver not initialized — getDriver() called before setUp().");
        return driver;
    }

    // ========================
    // Lifecycle
    // ========================

    /**
     * Runs before every {@code @Test} method.
     *
     * <p>Creates a new {@link ASM_Framework} for the current thread,
     * stores it in {@link #DRIVER_THREAD_LOCAL}, maximizes the window,
     * and navigates to the configured base URL.</p>
     */
    @BeforeMethod
    public void setUp()
    {
        String  browserName = CONFIG.getProperty("browser",  "chrome");
        String  baseUrl     = CONFIG.getProperty("base.url", "https://automationexercise.com");
        boolean headless    = Boolean.parseBoolean(CONFIG.getProperty("headless", "false"));

        ASM_Framework.BrowserOptions options = new ASM_Framework.BrowserOptions();
        if (headless) options.headless();

        ASM_Framework driver = new ASM_Framework(browserName, options);
        driver.manageScreenSize("maximize");
        driver.goToURL(baseUrl);

        DRIVER_THREAD_LOCAL.set(driver);
    }

    /**
     * Runs after every {@code @Test} method, regardless of pass or fail.
     *
     * <p>On failure: captures a screenshot and attaches it to Allure.</p>
     * <p>Always: quits the browser and calls {@link ThreadLocal#remove()}
     * to prevent memory leaks in long-running parallel suites.</p>
     *
     * @param result TestNG result — used to detect failure and get the test name
     */
    @AfterMethod
    public void tearDown(ITestResult result)
    {
        ASM_Framework driver = DRIVER_THREAD_LOCAL.get();

        if (driver != null)
        {
            if (result.getStatus() == ITestResult.FAILURE)
            {
                try
                {
                    AllureHelper.saveScreenshot(
                            "FAILED_" + result.getMethod().getMethodName(), driver);
                }
                catch (IOException e)
                {
                    System.out.println("[BaseTest] Could not attach failure screenshot: "
                            + e.getMessage());
                }
            }

            driver.closeAllTabs();
        }

        DRIVER_THREAD_LOCAL.remove();   // prevent memory leak
    }

    // ========================
    // Config Accessor
    // ========================

    /**
     * Retrieves a value from {@code config.properties} by key.
     *
     * @param key the property key (e.g., {@code "base.url"})
     * @return the property value, or {@code null} if not found
     */
    protected String getConfig(String key)
    {
        return CONFIG.getProperty(key);
    }
}