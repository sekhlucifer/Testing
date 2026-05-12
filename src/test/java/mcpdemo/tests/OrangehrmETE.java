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
        LoginPage loginPage = new LoginPage(page);
        loginPage.navigateTo(BASE_URL);

        // Enter invalid credentials
        loginPage.loginWithInvalidCredentials("invaliduser", "invalidpass");

        // Verify error message is displayed
        assertTrue(loginPage.isErrorMessageVisible(), "Error message should be displayed for invalid credentials");
        String errorMessage = loginPage.getErrorMessage();
        assertTrue(errorMessage.contains("Invalid"), "Error message should contain 'Invalid'");
    }

    @Test(priority = 2, description = "Test valid login and dashboard verification")
    public void testValidLogin() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.navigateTo(BASE_URL);

        // Enter valid credentials
        loginPage.loginWithValidCredentials("Admin", "admin123");

        // Verify URL contains dashboard
        assertTrue(page.url().contains("dashboard"), "URL should contain 'dashboard'");

        // Verify Dashboard page is loaded
        DashboardPage dashboardPage = new DashboardPage(page);
        assertTrue(dashboardPage.isDashboardVisible(), "Dashboard heading should be visible");
        assertEquals(dashboardPage.getDashboardText(), "Dashboard");
    }

    @Test(priority = 3, description = "Test navigation to Admin module")
    public void testNavigateToAdmin() {
        loginAsAdmin();
        DashboardPage dashboardPage = new DashboardPage(page);

        // Navigate to Admin module
        AdminPage adminPage = dashboardPage.navigateToAdmin();

        // Verify Admin page is loaded
        assertTrue(adminPage.isAdminPageLoaded(), "Admin page should be loaded");
    }

    @Test(priority = 4, description = "Test adding a new user")
    public void testAddNewUser() {
        loginAsAdmin();
        Properties config = loadConfigProperties();

        DashboardPage dashboardPage = new DashboardPage(page);

        // Navigate to Admin module
        AdminPage adminPage = dashboardPage.navigateToAdmin();
        String employeeFullName = null;
        String username = null;
        String password = null;
        try{
            FileInputStream fis = new FileInputStream("src/test/resources/config.properties");
            config.load(fis);
            employeeFullName = config.getProperty("employee.name", employeeFullName);
            username = config.getProperty("user.username", username);
            password = config.getProperty("user.password", password);
        }
        catch(IOException e){
            System.out.println("Could not load config.properties, using defaults");
        }

        // Use instance method page.waitForTimeout instead of static Page.waitForTimeout
        page.waitForTimeout(3000); 
        
        // Add new user
        String uniqueUsername = username + System.currentTimeMillis();
        adminPage.addUser(employeeFullName, uniqueUsername, password);
        // Verify success notification
        assertTrue(adminPage.isSuccessNotificationVisible(), "Success notification should be displayed");
        String notification = adminPage.getSuccessNotification();
        assertTrue(notification.contains("Successfully Saved"), "Notification should contain 'Successfully Saved'");
    }

    @Test(priority = 5, description = "Test logout functionality")
    public void testLogout() {
        loginAsAdmin();
        DashboardPage dashboardPage = new DashboardPage(page);

        // Logout
        dashboardPage.logout();

        // Verify redirected to login page
        assertTrue(page.url().contains("auth/login"), "Should be redirected to login page");
    }
}