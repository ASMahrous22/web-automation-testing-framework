package tests;

import io.qameta.allure.Allure;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
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
 * <h3>Lifecycle order for the entire suite run:</h3>
 * <pre>
 *   @BeforeSuite  suiteStart()               ← once when `mvn test` begins
 *
 *     @BeforeClass  classSetUp()             ← once when CartTests starts
 *       @BeforeMethod  setUp()               ← fresh browser for every @Test
 *         @Test  yourTestMethod()
 *       @AfterMethod   tearDown()            ← screenshot + quit browser
 *     @AfterClass   classTearDown()          ← once when CartTests finishes
 *
 *     @BeforeClass  classSetUp()             ← once when CheckoutTests starts
 *       ... and so on for every test class
 *     @AfterClass   classTearDown()
 *
 *   @AfterSuite   suiteEnd()                 ← once when all classes finish
 * </pre>
 *
 * @author ASMahrous
 */
public class BaseTest
{
    private static final ThreadLocal<ASM_Framework> DRIVER_THREAD_LOCAL = new ThreadLocal<>();
    private static final Properties CONFIG = loadConfig();

    /** Tracks suite start time so @AfterSuite can print total duration. */
    private static long suiteStartTimeMs;

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
    // Suite-level Lifecycle
    // ========================

    /**
     * Runs exactly once before any test class or method in the entire suite.
     *
     * <p>Prints the full environment configuration so the console log
     * has a single clear header for the whole run.</p>
     */
    @BeforeSuite
    public void suiteStart()
    {
        suiteStartTimeMs = System.currentTimeMillis();

        String browser  = CONFIG.getProperty("browser",  "chrome");
        String baseUrl  = CONFIG.getProperty("base.url", "https://automationexercise.com");
        String headless = CONFIG.getProperty("headless", "false");

        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println(  "║          AutomationExercise — Full Test Suite                ║");
        System.out.println(  "╠══════════════════════════════════════════════════════════════╣");
        System.out.printf(   "║  Browser  : %-49s║%n", browser + " (headless=" + headless + ")");
        System.out.printf(   "║  Base URL : %-49s║%n", baseUrl);
        System.out.printf(   "║  Started  : %-49s║%n", new java.util.Date());
        System.out.println(  "╚══════════════════════════════════════════════════════════════╝\n");
    }

    /**
     * Runs exactly once after every test class and method in the suite has finished.
     *
     * <p>Prints total elapsed time so you know immediately how long the
     * full 28-test run took without digging through Maven output.</p>
     */
    @AfterSuite
    public void suiteEnd()
    {
        long elapsedSec = (System.currentTimeMillis() - suiteStartTimeMs) / 1000;
        long minutes    = elapsedSec / 60;
        long seconds    = elapsedSec % 60;

        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println(  "║          AutomationExercise — Suite Completed                ║");
        System.out.println(  "╠══════════════════════════════════════════════════════════════╣");
        System.out.printf(   "║  Finished : %-49s║%n", new java.util.Date());
        System.out.printf(   "║  Duration : %d min %d sec%-39s║%n", minutes, seconds, "");
        System.out.println(  "╚══════════════════════════════════════════════════════════════╝\n");
    }

    // ========================
    // Class-level Lifecycle
    // ========================

    /**
     * Runs once before the first @Test in each test class (e.g. CartTests, CheckoutTests).
     *
     * <p>Logs the class name and environment to stdout and attaches
     * them to the Allure report so every class run is clearly identified.</p>
     */
    @BeforeClass
    public void classSetUp()
    {
        String className = getClass().getSimpleName();
        String browser   = CONFIG.getProperty("browser",  "chrome");
        String baseUrl   = CONFIG.getProperty("base.url", "https://automationexercise.com");
        String headless  = CONFIG.getProperty("headless", "false");

        System.out.println("  ╔══════════════════════════════════════════════════╗");
        System.out.printf( "  ║  ▶ Starting : %-35s║%n", className);
        System.out.printf( "  ║  Browser    : %-35s║%n", browser + " (headless=" + headless + ")");
        System.out.printf( "  ║  URL        : %-35s║%n", baseUrl);
        System.out.println("  ╚══════════════════════════════════════════════════╝");

        Allure.description(
                "**Class:** `" + className + "`  \n" +
                        "**Browser:** "  + browser  + "  \n" +
                        "**Headless:** " + headless + "  \n" +
                        "**Base URL:** " + baseUrl
        );
    }

    /**
     * Runs once after the last @Test in each test class.
     *
     * <p>Prints a completion marker so the console log clearly shows
     * where one test class ends and the next begins.</p>
     */
    @AfterClass
    public void classTearDown()
    {
        String className = getClass().getSimpleName();

        System.out.println("  ╔══════════════════════════════════════════════════╗");
        System.out.printf( "  ║  ✔ Finished : %-35s║%n", className);
        System.out.println("  ╚══════════════════════════════════════════════════╝\n");
    }

    // ========================
    // Method-level Lifecycle
    // ========================

    /**
     * Runs before every {@code @Test} method.
     *
     * <p>Creates a fresh {@link ASM_Framework} for the current thread,
     * maximizes the window, and navigates to the base URL.</p>
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
     * <p>Always: navigates to blank to drain pending ad requests,
     * quits the browser, and removes the driver from thread-local
     * to prevent memory leaks.</p>
     *
     * @param result TestNG result — used to detect failure and name the screenshot
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

            try { driver.goToURL("about:blank"); } // drain pending ad requests before quit
            catch (Exception ignored) {}

            driver.closeAllTabs();
        }

        DRIVER_THREAD_LOCAL.remove(); // prevent memory leak
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