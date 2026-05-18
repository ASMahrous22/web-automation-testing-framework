# Web Automation Testing Framework

A scalable, reusable Selenium WebDriver framework built in Java.
Website-agnostic at its core — extended with TestNG for test management,
Allure for HTML reporting with screenshot attachment, Gson for data-driven
testing, and a clean Page Object Model layer.

---

## Table of Contents

1. [Tech Stack](#tech-stack)
2. [Project Structure](#project-structure)
3. [What Each File Does](#what-each-file-does)
4. [Setup & Installation](#setup--installation)
5. [Configuration](#configuration)
6. [How to Add a Page](#how-to-add-a-page)
7. [How to Add a Test Class](#how-to-add-a-test-class)
8. [How to Add Test Data](#how-to-add-test-data)
9. [How to Configure the Test Suite XML](#how-to-configure-the-test-suite-xml)
10. [Running Tests](#running-tests)
11. [Viewing the Allure Report](#viewing-the-allure-report)
12. [Framework API Reference](#framework-api-reference)

---

## Tech Stack

| Tool               | Version | Purpose                              |
|--------------------|---------|--------------------------------------|
| Java               | 24      | Language                             |
| Selenium WebDriver | 4.40.0  | Browser automation                   |
| TestNG             | 7.12.0  | Test runner, lifecycle, suite XML    |
| Allure TestNG      | 2.34.0  | HTML reports + screenshot attachment |
| Gson               | 2.14.0  | JSON test data deserialization       |
| Maven              | 3.x     | Build and dependency management      |

---

## Project Structure

```
web-automation-testing-framework/
│
├── src/
│   ├── main/
│   │   └── java/
│   │       └── utils/
│   │           ├── ASM_Framework.java            ← single driver entry point
│   │           └── framework/                    ← internal managers (do not edit)
│   │               ├── ActionsManager.java
│   │               ├── AlertManager.java
│   │               ├── BrowserManager.java
│   │               ├── DropdownManager.java
│   │               ├── ElementFinder.java
│   │               ├── ElementInteractions.java
│   │               ├── FrameManager.java
│   │               ├── ScreenshotManager.java
│   │               ├── WaitManager.java
│   │               └── WindowManager.java
│   │
│   └── test/
│       ├── java/
│       │   ├── pages/
│       │   │   ├── BasePage.java                 ← parent for all page classes
│       │   │   └── ...                           ← your page classes go here
│       │   │
│       │   ├── tests/
│       │   │   ├── BaseTest.java                 ← parent for all test classes
│       │   │   └── ...                           ← your test classes go here
│       │   │
│       │   └── utils/
│       │       ├── AllureHelper.java             ← screenshot → Allure attachment
│       │       └── DataReader.java               ← JSON test data reader (Gson)
│       │
│       └── resources/
│           ├── config.properties                 ← browser / URL / headless config
│           └── testdata/                         ← JSON data files go here
│
├── testng-suites/
│   └── testng.xml                                ← register test classes here
│
├── Screenshots/                                  ← auto-created at runtime
├── allure-results/                               ← auto-created by Allure
├── pom.xml
└── README.md
```

---

## What Each File Does

### `ASM_Framework` — `src/main/java/utils/`
The single object your pages and tests interact with. Wraps ten internal
managers behind one clean API:

| Manager | Responsibility |
|---|---|
| `BrowserManager` | Launch browser, navigate, window size, close |
| `WaitManager` | Implicit, explicit, and fluent waits |
| `ElementFinder` | Locate elements by id / name / class / xpath / css |
| `ElementInteractions` | Click, type, clear, get text, check state |
| `ActionsManager` | Hover, double-click, right-click, drag-drop, scroll, checkbox, radio |
| `DropdownManager` | Select / deselect by index, value, text, partial text |
| `WindowManager` | Switch tabs and windows, get handles, close windows |
| `AlertManager` | Accept, dismiss, read, type into JS dialogs |
| `FrameManager` | Switch into and out of iframes |
| `ScreenshotManager` | Capture timestamped PNG, save to `Screenshots/`, return `Path` |

---

### `BasePage` — `src/test/java/pages/`
Parent class for all your page objects. Has two constructors:

```java
// Used in tests — share the driver BaseTest already created
new LoginPage(getDriver());

// Standalone — page boots its own browser, useful for debugging
new LoginPage("chrome");
```

The `driver` field is `public` so every page subclass accesses the full
`ASM_Framework` API directly. The only methods `BasePage` defines are things
that either don't exist on `driver` or meaningfully combine calls:

| Method | What it does |
|---|---|
| `readPageURL()` | Returns the current page URL |
| `urlContains(fragment)` | Checks if the current URL contains a substring |
| `titleContains(text)` | Checks if the page title contains a substring |
| `closePage()` | Closes the current browser tab |
| `saveScreenshot(fileName, driver)` | Captures screenshot + attaches it to Allure report |

Everything else — clicking, typing, waiting, dropdowns, alerts, windows,
frames — is accessed directly through `driver` inside each page subclass.

---

### `BaseTest` — `src/test/java/tests/`
Parent class for all your test classes. Manages the TestNG lifecycle:

- **`@BeforeMethod setUp()`** — reads `config.properties`, creates `ASM_Framework`,
  maximizes the window, navigates to `base.url` before every `@Test`
- **`@AfterMethod tearDown(ITestResult)`** — on failure: calls
  `AllureHelper.saveScreenshot()` to capture and attach the screenshot to Allure.
  Always: closes the browser

The `getDriver()` method returns the ThreadLocal-isolated driver so every test subclass passes it to page constructors.

---

### `AllureHelper` — `src/test/java/utils/`
One static method that combines screenshot capture and Allure attachment:

```java
AllureHelper.saveScreenshot("label", driver);
// → driver.takeScreenshot("label")   saves file, returns Path
// → Allure.addAttachment(...)        streams file into the report
```

Called in two places:
- `BasePage.saveScreenshot()` delegates here — used from page methods or tests manually
- `BaseTest.tearDown()` calls it directly for automatic failure screenshots

---

### `DataReader` — `src/test/java/utils/`
Reads JSON files from `src/test/resources/testdata/` and returns typed Java objects
using Gson. Two methods:

```java
DataReader.read("users.json", UserData.class);         // single object
DataReader.readList("users.json", UserData.class);     // array of objects
```

---

### `config.properties` — `src/test/resources/`
Controls every test run without touching code:

```properties
browser=chrome          # chrome | firefox | edge | safari
base.url=https://automationexercise.com
headless=false          # true for CI / no visible window
```

Read in `BaseTest` automatically. Access any value in a test with `getConfig("key")`.

---

### `testng.xml` — `testng-suites/`
Tells Maven Surefire which test classes to run and in what order.
Add one `<class>` entry every time you create a new test class.

---

## Setup & Installation

### Prerequisites
- Java 24+
- Maven 3.x
- IntelliJ IDEA
- Chrome / Firefox / Edge installed

### Steps

```bash
# 1. Clone
git clone https://github.com/YOUR_USERNAME/web-automation-testing-framework.git
cd web-automation-testing-framework

# 2. Open in IntelliJ
#    File → Open → select folder → Open as Project

# 3. Mark source roots (if IntelliJ doesn't auto-detect)
#    Right-click src/test/java       → Mark Directory as → Test Sources Root
#    Right-click src/test/resources  → Mark Directory as → Test Resources Root

# 4. Load Maven dependencies
#    Click "Load Maven Changes" in the top-right banner, or run:
mvn dependency:resolve

# 5. Verify everything compiles cleanly
mvn test-compile
```

---

## Configuration

Open `src/test/resources/config.properties` and set your values:

```properties
browser=chrome
base.url=https://automationexercise.com
headless=false
```

To read a config value inside a test class:

```java
String url = getConfig("base.url");
```

---

## How to Add a Page

1. Create a new `.java` file in `src/test/java/pages/`
2. Extend `BasePage`
3. Declare locators as `private final By` fields
4. Add an `open()` method that navigates to the page
5. Add one method per user action — no assertions inside page methods

### Full Example — `LoginPage.java`

```java
package pages;

import org.openqa.selenium.By;
import utils.ASM_Framework;

import java.io.IOException;

public class LoginPage extends BasePage
{
    // ── Locators ──────────────────────────────────────────────────────────
    private final By emailField    = By.cssSelector("[data-qa='login-email']");
    private final By passwordField = By.cssSelector("[data-qa='login-password']");
    private final By loginButton   = By.cssSelector("[data-qa='login-button']");
    private final By errorMessage  = By.cssSelector(".login-form p");
    private final By signupName    = By.cssSelector("[data-qa='signup-name']");
    private final By signupEmail   = By.cssSelector("[data-qa='signup-email']");
    private final By signupButton  = By.cssSelector("[data-qa='signup-button']");

    // ── Constructors ──────────────────────────────────────────────────────

    /** Use this constructor in test classes — shares the BaseTest driver. */
    public LoginPage(ASM_Framework driver)
    {
        super(driver);
    }

    /** Use this constructor for standalone debugging without a test class. */
    public LoginPage(String browserName)
    {
        super(browserName);
    }

    // ── Navigation ────────────────────────────────────────────────────────
    public void open()
    {
        driver.goToURL("https://automationexercise.com/login");
    }

    // ── Actions ───────────────────────────────────────────────────────────
    public void login(String email, String password)
    {
        driver.writeInElement(emailField, email);
        driver.writeInElement(passwordField, password);
        driver.clickElement(loginButton);
    }

    public void signUp(String name, String email)
    {
        driver.writeInElement(signupName, name);
        driver.writeInElement(signupEmail, email);
        driver.clickElement(signupButton);
    }

    // ── Data Retrieval (let the test assert, not the page) ────────────────
    public String getErrorMessage()
    {
        return driver.getElementText(errorMessage);
    }

    public boolean isAt()
    {
        return urlContains("login");
    }
}
```

**Rules:**
- Locators are `private final` — never expose `By` fields publicly
- Page methods never contain assertions — return data, let the test assert
- Both constructors must call `super(...)` — that's what wires up `driver`
- One file per page — `LoginPage`, `ProductsPage`, `CartPage`, etc.

---

## How to Add a Test Class

1. Create a new `.java` file in `src/test/java/tests/`
2. Extend `BaseTest`
3. Use the `getDriver()` method to instantiate page objects
4. Annotate each test with `@Test` and `@Description`
5. Register the class in `testng.xml`

### Full Example — `LoginTests.java`

```java
package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.LoginPage;
import utils.DataReader;

import java.io.IOException;

public class LoginTests extends BaseTest
{
    // ── Test Case 1 ───────────────────────────────────────────────────────
    @Test
    @Description("TC01 — Login with valid email and password")
    @Severity(SeverityLevel.CRITICAL)
    public void loginWithValidCredentials() throws IOException
    {
        // 1. Load test data
        UserData user = DataReader.read("users.json", UserData.class);

        // 2. Instantiate the page — call getDriver() — the ThreadLocal-backed driver from BaseTest
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.open();
        loginPage.login(user.email, user.password);

        // 3. Manual screenshot checkpoint — saved to disk AND attached to Allure
        loginPage.saveScreenshot("TC01_AfterLogin", getDriver());

        // 4. Assert
        Assert.assertTrue(
            loginPage.titleContains("Automation Exercise"),
            "Title should contain 'Automation Exercise' after successful login"
        );
    }

    // ── Test Case 2 ───────────────────────────────────────────────────────
    @Test
    @Description("TC02 — Login with incorrect email and password")
    @Severity(SeverityLevel.NORMAL)
    public void loginWithInvalidCredentials()
    {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.open();
        loginPage.login("wrong@email.com", "wrongpassword");

        Assert.assertTrue(
            loginPage.getErrorMessage().contains("Your email or password is incorrect"),
            "Error message should appear for invalid credentials"
        );
    }

    // ── Test Case 3 ───────────────────────────────────────────────────────
    @Test
    @Description("TC03 — Verify login page URL and form are visible")
    @Severity(SeverityLevel.MINOR)
    public void verifyLoginPageIsVisible()
    {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.open();

        Assert.assertTrue(loginPage.isAt(), "URL should contain 'login'");
    }

    // ── Test data POJO ────────────────────────────────────────────────────
    // Can also live as its own file under src/test/java/testdata/
    public static class UserData
    {
        public String email;
        public String password;
    }
}
```

**Rules:**
- `driver` comes from `BaseTest` — never create `ASM_Framework` manually in a test
- Every `@Test` method creates its own page object — no shared state between tests
- `@BeforeMethod` and `@AfterMethod` are handled by `BaseTest` — do not override
  them unless you call `super.setUp()` / `super.tearDown(result)` first
- Assertions belong in the test, not in the page
- After writing the class, register it in `testng.xml` (see below)

---

## How to Add Test Data

### 1. Create the JSON file

Place it in `src/test/resources/testdata/`.

**Single object** — `users.json`:
```json
{
  "email": "testuser@example.com",
  "password": "Test@1234",
  "name": "Test User"
}
```

**Array of objects** — `users.json` (for multiple data sets):
```json
[
  { "email": "user1@example.com", "password": "Pass@1", "name": "User One" },
  { "email": "user2@example.com", "password": "Pass@2", "name": "User Two" }
]
```

### 2. Create a matching POJO

Field names must match the JSON keys exactly — Gson maps them by name.

```java
// As a standalone file: src/test/java/testdata/UserData.java
// Or as a static inner class inside the test class
public class UserData {
    public String email;
    public String password;
    public String name;
}
```

### 3. Read it in your test

```java
// Single object
UserData user = DataReader.read("users.json", UserData.class);
loginPage.login(user.email, user.password);

// List — iterate for data-driven testing
List<UserData> users = DataReader.readList("users.json", UserData.class);
for (UserData u : users) {
    loginPage.login(u.email, u.password);
}
```

---

## How to Configure the Test Suite XML

`testng-suites/testng.xml` is the master switch — Maven Surefire reads it to
know which classes to run. Every time you create a new test class, add a
`<test>` block here.

### Full annotated example

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">

<suite name="AutomationExercise Test Suite" verbose="1">
<!--
    verbose="0"  → silent
    verbose="1"  → test names only  (recommended)
    verbose="2"  → every method
-->

    <test name="Login Tests">
        <classes>
            <class name="tests.LoginTests"/>
        </classes>
    </test>

    <test name="Register Tests">
        <classes>
            <class name="tests.RegisterTests"/>
        </classes>
    </test>

</suite>
```

### Skip a class temporarily

Wrap its `<test>` block in a comment:

```xml
<!--
<test name="Cart Tests">
    <classes>
        <class name="tests.CartTests"/>
    </classes>
</test>
-->
```

### Run specific methods only

```xml
<test name="Login Tests">
    <classes>
        <class name="tests.LoginTests">
            <methods>
                <include name="loginWithValidCredentials"/>
                <include name="verifyLoginPageIsVisible"/>
                <!-- loginWithInvalidCredentials is excluded and won't run -->
            </methods>
        </class>
    </classes>
</test>
```

### Run tests in parallel

Each `@Test` method gets its own browser instance via `@BeforeMethod`, so
parallel-by-methods is safe out of the box:

```xml
<suite name="AutomationExercise Test Suite" verbose="1"
       parallel="methods" thread-count="3">

    <test name="Login Tests">
        <classes>
            <class name="tests.LoginTests"/>
        </classes>
    </test>

</suite>
```

---

## Running Tests

```bash
# Run everything in testng.xml
mvn test

# Run a specific test class only
mvn test -Dtest=LoginTests

# Run a single test method
mvn test -Dtest=LoginTests#loginWithValidCredentials
```

---

## Viewing the Allure Report

```bash
# Generate report and open it in the browser (recommended)
mvn allure:serve

# Generate static HTML files without opening the browser
mvn allure:report
# Output: target/site/allure-maven-plugin/index.html
```

The report shows:
- Pass / fail / skip breakdown by test class
- Full stack trace for every failure
- Screenshots attached directly to the failing test result
- Manually captured screenshots attached at the step where `saveScreenshot()` was called
- Timeline view of execution order and duration

**Screenshots are also saved to disk** at `Screenshots/` regardless of whether
you open the Allure report, using this naming pattern:

```
Screenshots/
├── TC01_AfterLogin_2025-07-21_14-35-22-123.png
└── FAILED_loginWithInvalidCredentials_2025-07-21_14-36-01-456.png
```

---

## Framework API Reference

Everything below is accessed through the `driver` field (inherited from `BasePage`
in page classes, or from `BaseTest` in test classes). Waits are handled internally
— never add `Thread.sleep()`.

### Navigation
```java
driver.goToURL("https://example.com");
driver.manageNavigationButtons("back");     // "back" | "forward" | "refresh"
driver.getCurrentPageTitle();
driver.getCurrentPageURL();
driver.manageScreenSize("maximize");        // "maximize" | "minimize" | fullscreen
```

### Finding Elements
```java
// locatorType: "id" | "name" | "class" | "xpath" | "css"
WebElement el = driver.findElement("css", "[data-qa='login-email']");
By         by = driver.getBy("id", "submit-btn");
```

### Element Interactions
```java
driver.clickElement(locator);               // waits until clickable, then clicks
driver.clickElement(element);
driver.writeInElement(locator, "text");     // clears first, then types
driver.writeInElement(element, "text");
driver.clearElementText(locator);
driver.getElementText(locator);
driver.getElementText(element);
```

### Element State
```java
driver.validateElementIsDisplayed(element);   // returns boolean
driver.validateElementIsEnabled(element);
driver.validateElementIsSelected(element);
```

### Waiting
```java
driver.setExplicitWait(locator, 15);                             // wait up to 15s for DOM presence
driver.setExplicitWait(locator, Duration.ofSeconds(15));
driver.setFluentWait(locator, 10, 500, "Timeout message");       // poll every 500ms for 10s
driver.setImplicitWait(5);                                       // global implicit wait (seconds)
driver.setImplicitWait(Duration.ofSeconds(5));
```

### Dropdowns
```java
driver.selectFromDropDownMenu(locator, "visible", "Egypt");      // by visible text
driver.selectFromDropDownMenu(locator, "contains", "Egy");       // by partial text
driver.selectFromDropDownMenu(locator, "value",   "eg");         // by value attribute
driver.selectFromDropDownMenu(locator, "index",   "2");          // by 0-based index
driver.deselectFromDropDownMenu(locator, "all",   "");           // deselect all (multi-select)
```

### Checkboxes & Radio Buttons
```java
driver.checkCheckbox(locator);       // checks only if not already checked
driver.uncheckCheckbox(locator);     // unchecks only if currently checked
driver.selectRadioButton(locator);   // selects only if not already selected
```

### Advanced Interactions
```java
driver.hoverOverElement(locator);
driver.hoverOverElement(element);
driver.doubleClick(locator);
driver.doubleClick(element);
driver.rightClick(locator);
driver.dragAndDrop(sourceLocator, targetLocator);
driver.scrollToElement(locator);     // centers element in viewport
driver.scrollToElement(element);
```

### Windows & Tabs
```java
String main = driver.getCurrentWindowHandle();
driver.switchToNewWindow(main);                          // switch to newly opened window
driver.switchToWindowByHandle(handle);
driver.switchToWindowByIndex(1);                        // 0-based index
driver.closeCurrentWindowAndSwitchTo(main);
driver.getAllWindowHandles();                            // returns List<String>
driver.getWindowCount();
```

### Alerts
```java
driver.acceptAlert();
driver.dismissAlert();
String msg = driver.getAlertText();
driver.typeInAlert("input text");    // types in prompt then accepts
```

### iFrames
```java
driver.switchToIFrame(locator);
driver.switchToIFrame(element);
driver.switchToIFrameByIndex(0);
driver.switchToIFrameByNameOrId("frameName");
driver.switchToDefaultContent();     // exit iframe back to main page
driver.switchToParentFrame();        // exit one level up (nested iframes)
```

### Screenshots
```java
// From a page object or test — saves to disk AND attaches to Allure
loginPage.saveScreenshot("TC01_AfterLogin", getDriver());

// From BaseTest tearDown — happens automatically on any test failure
// FAILED_<testMethodName>_<timestamp>.png
```

---

## Author

**ASMahrous** — Built as part of the EDGES Software Testing Diploma automation final project.
Feel free to fork, use, or contribute!
