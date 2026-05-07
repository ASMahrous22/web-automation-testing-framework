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
 * <p>Manages the full test lifecycle:</p>
 * <ul>
 *   <li>{@link #setUp()} — reads {@code config.properties}, launches the
 *       browser, opens the base URL, and maximizes the window before each test</li>
 *   <li>{@link #tearDown(ITestResult)} — on failure captures a screenshot and
 *       attaches it to the Allure report via {@link AllureHelper}, then always
 *       closes the browser</li>
 * </ul>
 *
 * <p>The {@code driver} field is {@code protected} so every test subclass can
 * pass it to page object constructors:</p>
 * <pre>{@code
 * public class LoginTests extends BaseTest {
 *
 *     @Test
 *     public void loginWithValidCredentials() throws IOException {
 *         LoginPage loginPage = new LoginPage(driver);
 *         loginPage.open();
 *         loginPage.login("user@example.com", "secret");
 *         loginPage.saveScreenshot("TC01_AfterLogin", driver);
 *
 *         assertTrue(loginPage.urlContains("dashboard"));
 *     }
 * }
 * }</pre>
 *
 * @author ASMahrous
 */
public class BaseTest
{
    /** Shared driver — pass this to every page object constructor. */
    protected ASM_Framework driver;

    /** Loaded once at class-load time from {@code src/test/resources/config.properties}. */
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
    // Lifecycle
    // ========================

    /**
     * Runs before every {@code @Test} method.
     * Launches the browser, maximizes it, and opens the base URL.
     */
    @BeforeMethod
    public void setUp()
    {
        String  browserName = CONFIG.getProperty("browser",  "chrome");
        String  baseUrl     = CONFIG.getProperty("base.url", "https://automationexercise.com");
        boolean headless    = Boolean.parseBoolean(CONFIG.getProperty("headless", "false"));

        ASM_Framework.BrowserOptions options = new ASM_Framework.BrowserOptions();
        if (headless) options.headless();

        driver = new ASM_Framework(browserName, options);
        driver.manageScreenSize("maximize");
        driver.goToURL(baseUrl);
    }

    /**
     * Runs after every {@code @Test} method, regardless of pass or fail.
     *
     * <p>On failure: calls {@link AllureHelper#saveScreenshot(String, ASM_Framework)}
     * to capture and attach the screenshot to the Allure report.</p>
     * <p>Always: quits the browser.</p>
     *
     * @param result TestNG result — used to detect failure and get the test name
     */
    @AfterMethod
    public void tearDown(ITestResult result)
    {
        if (result.getStatus() == ITestResult.FAILURE)
        {
            try
            {
                AllureHelper.saveScreenshot("FAILED_" + result.getMethod().getMethodName(), driver);
            }
            catch (IOException e)
            {
                System.out.println("[BaseTest] Could not attach failure screenshot to Allure: "
                        + e.getMessage());
            }
        }

        if (driver != null)
            driver.closeAllTabs();
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