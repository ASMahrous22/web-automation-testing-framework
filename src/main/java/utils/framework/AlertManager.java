package utils.framework;

import org.openqa.selenium.*;

/**
 * AlertManager — Handles JavaScript alert, confirm, and prompt dialogs.
 *
 * <p>All methods automatically wait for the dialog to appear before acting,
 * using the shared {@link WaitManager}.</p>
 *
 * @author ASMahrous
 */
public class AlertManager
{
    private final WaitManager waitManager;

    /**
     * @param waitManager shared {@link WaitManager} for alert-presence waits
     */
    public AlertManager(WaitManager waitManager)
    {
        this.waitManager = waitManager;
    }

    // ========================
    // Alert Actions
    // ========================

    /**
     * Accepts (clicks OK on) the currently open JavaScript alert, confirm, or prompt.
     *
     * <ul>
     *   <li>Alert — closes the dialog</li>
     *   <li>Confirm — clicks OK (returns {@code true} to the page)</li>
     *   <li>Prompt — submits the current input text (or empty string)</li>
     * </ul>
     */
    public void acceptAlert()
    {
        waitManager.waitForAlert().accept();
    }

    /**
     * Dismisses (clicks Cancel on) the currently open JavaScript confirm or prompt dialog.
     *
     * <p>Note: dismissing a plain alert has the same effect as accepting it.</p>
     */
    public void dismissAlert()
    {
        waitManager.waitForAlert().dismiss();
    }

    /**
     * Returns the message text displayed inside the current JavaScript alert dialog.
     *
     * @return the alert message string
     */
    public String getAlertText()
    {
        return waitManager.waitForAlert().getText();
    }

    /**
     * Types text into a JavaScript prompt dialog, then accepts it.
     *
     * <p>Example:</p>
     * <pre>{@code
     * driver.typeInAlert("John Doe"); // enters name and clicks OK
     * }</pre>
     *
     * @param text the text to enter into the prompt input field
     */
    public void typeInAlert(String text)
    {
        Alert alert = waitManager.waitForAlert();
        alert.sendKeys(text);
        alert.accept();
    }
}