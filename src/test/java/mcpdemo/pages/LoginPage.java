package mcpdemo.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;
import org.testng.Reporter;
import java.nio.file.Paths;

public class LoginPage {
    private final Page page;
    private final Locator usernameInput;
    private final Locator passwordInput;
    private final Locator loginButton;
    private final Locator errorMessage;
    
    public LoginPage(Page page) {
        this.page = page;
        this.usernameInput = page.locator("input[name='username']");
        this.passwordInput = page.locator("input[name='password']");
        this.loginButton = page.locator("button[type='submit']");
        this.errorMessage = page.locator(".oxd-alert-content-text");
    }
    
    public void navigateTo(String url) {
        page.navigate(url, new Page.NavigateOptions().setWaitUntil(com.microsoft.playwright.options.WaitUntilState.DOMCONTENTLOADED));
    }
    
    public void enterUsername(String username) {
        usernameInput.fill(username);
    }
    
    public void enterPassword(String password) {
        passwordInput.fill(password);
    }
    
    public void clickLogin() {
        loginButton.click();
    }
    
    public String getErrorMessage() {
        return errorMessage.textContent();
    }
    
    public boolean isErrorMessageVisible() {
        try {
            errorMessage.waitFor(new Locator.WaitForOptions().setTimeout(5000));
        } catch (Exception e) {
            // ignore
        }
        return errorMessage.isVisible();
    }
    
    public void loginWithInvalidCredentials(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
        takeScreenshot("loginWithInvalidCredentials");
    }
    
    public void loginWithValidCredentials(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
        takeScreenshot("loginWithValidCredentials");
    }

    private void takeScreenshot(String stepName) {
        String path = "target/screenshots/" + stepName + "_" + System.currentTimeMillis() + ".png";
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(path)));
        String uri = Paths.get(path).toAbsolutePath().toUri().toString();
        Reporter.log("<br><img src='" + uri + "' height='400' width='400'></img></br>");
    }
}