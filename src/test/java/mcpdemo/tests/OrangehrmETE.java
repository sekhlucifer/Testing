package mcpdemo.tests;

import mcpdemo.base.Base;
import mcpdemo.pages.*;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
// import com.microsoft.playwright.Page;

import static org.testng.Assert.*;
import org.testng.Reporter;

public class OrangehrmETE extends Base {

    private void loginAsAdmin() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.navigateTo(BASE_URL);

        Properties config = loadConfigProperties();
        String username = config.getProperty("admin.username",
                config.getProperty("valid.username", "Admin"));
        String password = config.getProperty("admin.password",
                config.getProperty("valid.password", "admin123"));
        loginPage.loginWithValidCredentials(username, password);
    }

    private Properties loadConfigProperties() {
        Properties prop = new Properties();
        try (InputStream fis = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (fis != null) {
                prop.load(fis);
            }
        } catch (IOException e) {
            // e.printStackTrace();
            System.out.println("Could not load config.properties, using defaults");
        }
        return prop;
    }

    @Test(priority = 1, description = "Test invalid login scenario")
    public void testInvalidLogin() {
        Reporter.log("Starting test: Invalid Login", true);
        LoginPage loginPage = new LoginPage(page);

        Reporter.log("Navigating to OrangeHRM login page: " + BASE_URL, true);
        loginPage.navigateTo(BASE_URL);

        Reporter.log("Entering invalid credentials", true);
        loginPage.loginWithInvalidCredentials("invaliduser", "invalidpass");

        Reporter.log("Verifying error message is displayed", true);
        assertTrue(loginPage.isErrorMessageVisible(), "Error message should be displayed for invalid credentials");
        String errorMessage = loginPage.getErrorMessage();
        assertTrue(errorMessage.contains("Invalid"), "Error message should contain 'Invalid'");
        Reporter.log("Invalid login test passed successfully.", true);
    }

    @Test(priority = 2, description = "Test valid login and dashboard verification")
    public void testValidLogin() {
        Reporter.log("Starting test: Valid Login", true);
        LoginPage loginPage = new LoginPage(page);

        Reporter.log("Navigating to OrangeHRM login page", true);
        loginPage.navigateTo(BASE_URL);

        Reporter.log("Entering valid credentials", true);
        loginPage.loginWithValidCredentials("Admin", "admin123");

        Reporter.log("Verifying redirection to dashboard", true);
        assertTrue(page.url().contains("dashboard"), "URL should contain 'dashboard'");

        Reporter.log("Verifying Dashboard heading is visible", true);
        DashboardPage dashboardPage = new DashboardPage(page);
        assertTrue(dashboardPage.isDashboardVisible(), "Dashboard heading should be visible");
        assertEquals(dashboardPage.getDashboardText(), "Dashboard");
        Reporter.log("Valid login test passed successfully.", true);
    }

    @Test(priority = 3, description = "Test navigation to Admin module")
    public void testNavigateToAdmin() {
        Reporter.log("Starting test: Navigate to Admin", true);
        Reporter.log("Logging in as Admin", true);
        loginAsAdmin();
        DashboardPage dashboardPage = new DashboardPage(page);

        Reporter.log("Clicking on Admin module from sidebar", true);
        AdminPage adminPage = dashboardPage.navigateToAdmin();

        Reporter.log("Verifying Admin page is fully loaded", true);
        assertTrue(adminPage.isAdminPageLoaded(), "Admin page should be loaded");
        Reporter.log("Navigate to Admin test passed successfully.", true);
    }

    @Test(priority = 4, description = "Test adding a new user")
    public void testAddNewUser() {
        Reporter.log("Starting test: Add New User", true);
        Reporter.log("Logging in as Admin", true);
        loginAsAdmin();
        Properties config = loadConfigProperties();

        DashboardPage dashboardPage = new DashboardPage(page);

        Reporter.log("Navigating to Admin module", true);
        AdminPage adminPage = dashboardPage.navigateToAdmin();
        String employeeFullName = null;
        String username = null;
        String password = null;
        try {
            FileInputStream fis = new FileInputStream("src/test/resources/config.properties");
            config.load(fis);
            employeeFullName = config.getProperty("employee.name", employeeFullName);
            username = config.getProperty("user.username", username);
            password = config.getProperty("user.password", password);
        } catch (IOException e) {
            Reporter.log("Warning: Could not load config.properties, using defaults", true);
            System.out.println("Could not load config.properties, using defaults");
        }
        Reporter.log("Clicking Add button", true);
        adminPage.clickAddButton();
        
        // Use instance method page.waitForTimeout instead of static Page.waitForTimeout
        page.waitForTimeout(3000);

        String uniqueUsername = username + System.currentTimeMillis();
        Reporter.log("Adding a new user with generated unique username: " + uniqueUsername, true);
        adminPage.addUser(employeeFullName, uniqueUsername, password);

        Reporter.log("Verifying 'Successfully Saved' toast notification", true);
        assertTrue(adminPage.isSuccessNotificationVisible(), "Success notification should be displayed");
        String notification = adminPage.getSuccessNotification();
        assertTrue(notification.contains("Successfully Saved"), "Notification should contain 'Successfully Saved'");
        Reporter.log("Add New User test passed successfully.", true);
    }

    @Test(priority = 5, description = "Test logout functionality")
    public void testLogout() {
        Reporter.log("Starting test: Logout", true);
        Reporter.log("Logging in as Admin", true);
        loginAsAdmin();
        DashboardPage dashboardPage = new DashboardPage(page);

        Reporter.log("Clicking on logout button", true);
        dashboardPage.logout();

        Reporter.log("Verifying redirection back to login page", true);
        assertTrue(page.url().contains("auth/login"), "Should be redirected to login page");
        Reporter.log("Logout test passed successfully.", true);
    }
}