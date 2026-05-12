package mcpdemo.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;
import org.testng.Reporter;
import java.nio.file.Paths;
// import com.microsoft.playwright.options.AriaRole;

public class DashboardPage {
    private final Page page;
    private final Locator dashboardHeading;
    private final Locator adminMenu;
    private final Locator userDropdown;
    private final Locator logoutButton;
    
    public DashboardPage(Page page) {
        this.page = page;
        this.dashboardHeading = page.locator("h6").filter(new Locator.FilterOptions().setHasText("Dashboard"));
        this.adminMenu = page.locator("a[href*='viewAdminModule']");
        this.userDropdown = page.locator(".oxd-userdropdown-tab");
        this.logoutButton = page.locator("a[href*='logout']");
    }
    
    public boolean isDashboardVisible() {
        try {
            dashboardHeading.waitFor(new Locator.WaitForOptions().setTimeout(10000));
        } catch (Exception e) {
            // ignore
        }
        return dashboardHeading.isVisible();
    }
    
    public String getDashboardText() {
        return dashboardHeading.textContent();
    }
    
    public void clickAdminMenu() {
        adminMenu.waitFor();
        adminMenu.click();
    }
    
    public void clickUserDropdown() {
        userDropdown.waitFor();
        userDropdown.click();
    }
    
    public void clickLogout() {
        logoutButton.waitFor();
        logoutButton.click();
    }
    
    public AdminPage navigateToAdmin() {
        clickAdminMenu();
        takeScreenshot("navigateToAdmin");
        return new AdminPage(page);
    }
    
    public void logout() {
        clickUserDropdown();
        clickLogout();
        takeScreenshot("logout");
    }

    private void takeScreenshot(String stepName) {
        String path = "target/screenshots/" + stepName + "_" + System.currentTimeMillis() + ".png";
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(path)));
        String uri = Paths.get(path).toAbsolutePath().toUri().toString();
        Reporter.log("<br><img src='" + uri + "' height='400' width='400'></img></br>");
    }
}