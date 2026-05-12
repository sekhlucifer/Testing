package mcpdemo.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.AriaRole;
import org.testng.Reporter;
import java.nio.file.Paths;

public class AdminPage {
    private final Page page;
    private final Locator addButton;
    private final Locator userRoleDropdown;
    private final Locator statusDropdown;
    private final Locator employeeNameInput;
    private final Locator usernameInput;
    private final Locator passwordInput;
    private final Locator confirmPasswordInput;
    private final Locator saveButton;
    private final Locator successNotification;
    private final Locator adminHeading;
    
    public AdminPage(Page page) {
        this.page = page;
        this.addButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add"));
        
        // Target only the select input text node to avoid strict-mode multi-match.
        this.userRoleDropdown = page.locator(
                "xpath=//label[normalize-space()='User Role']/ancestor::div[contains(@class,'oxd-input-group')]//div[contains(@class,'oxd-select-text-input')]");
        this.statusDropdown = page.locator(
                "xpath=//label[normalize-space()='Status']/ancestor::div[contains(@class,'oxd-input-group')]//div[contains(@class,'oxd-select-text-input')]");
        this.employeeNameInput = page.getByPlaceholder("Type for hints...");
        this.usernameInput = page.locator("xpath=//label[text()='Username']/../following-sibling::div//input");
        this.passwordInput = page.locator("xpath=//label[text()='Password']/../following-sibling::div//input");
        this.confirmPasswordInput = page.locator("xpath=//label[text()='Confirm Password']/../following-sibling::div//input");
        
        this.saveButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Save"));
        this.successNotification = page.locator(".oxd-toast-container-text");
        this.adminHeading = page.locator("h6").filter(new Locator.FilterOptions().setHasText("Admin"));
    }
    
    public boolean isAdminPageLoaded() {
        try {
            adminHeading.waitFor(new Locator.WaitForOptions().setTimeout(10000));
        } catch (Exception e) {
            // ignore
        }
        return adminHeading.isVisible();
    }
    
    public void clickAddButton() {
        addButton.waitFor();
        addButton.click();
    }
    
    public void selectUserRole(String role) {
        userRoleDropdown.waitFor();
        userRoleDropdown.click();
        page.waitForTimeout(1000);
        page.locator(".oxd-select-option").filter(new Locator.FilterOptions().setHasText(role)).click();
    }
    
    public void selectStatus(String status) {
        statusDropdown.waitFor();
        statusDropdown.click();
        page.waitForTimeout(1000);
        page.locator(".oxd-select-option").filter(new Locator.FilterOptions().setHasText(status)).click();
    }
    
    public void enterEmployeeName(String name) {
        employeeNameInput.waitFor();
        employeeNameInput.click();
        employeeNameInput.fill(name);
        page.waitForTimeout(3000); // Wait for autocomplete to fetch and render
        Locator options = page.locator(".oxd-autocomplete-option");
        options.first().waitFor(new Locator.WaitForOptions().setTimeout(10000));
        options.first().click();
    }
    
    public void enterUsername(String username) {
        usernameInput.waitFor();
        usernameInput.fill(username);
    }
    
    public void enterPassword(String password) {
        passwordInput.waitFor();
        passwordInput.fill(password);
    }
    
    public void enterConfirmPassword(String password) {
        confirmPasswordInput.waitFor();
        confirmPasswordInput.fill(password);
    }
    
    public void clickSave() {
        saveButton.waitFor();
        saveButton.click();
    }
    
    public String getSuccessNotification() {
        return successNotification.textContent();
    }
    
    public boolean isSuccessNotificationVisible() {
        try {
            successNotification.waitFor(new Locator.WaitForOptions().setTimeout(15000));
        } catch (Exception e) {
            // ignore
        }
        return successNotification.isVisible();
    }
    
    public void addUser(String employeeName, String username, String password) {
        clickAddButton();
        selectUserRole("Admin");
        selectStatus("Enabled");
        enterEmployeeName(employeeName);
        enterUsername(username);
        enterPassword(password);
        enterConfirmPassword(password);
        clickSave();
        takeScreenshot("addUser");
    }

    private void takeScreenshot(String stepName) {
        String path = "target/screenshots/" + stepName + "_" + System.currentTimeMillis() + ".png";
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(path)));
        String uri = Paths.get(path).toAbsolutePath().toUri().toString();
        Reporter.log("<br><img src='" + uri + "' height='400' width='400'></img></br>");
    }
}