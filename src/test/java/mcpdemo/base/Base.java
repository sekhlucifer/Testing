package mcpdemo.base;

import com.microsoft.playwright.*;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

public class Base {

    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;

    protected static final String BASE_URL = "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login";

    @BeforeMethod
    @Parameters("browser")
    public void setup(@Optional("chromium") String browserName) {
        playwright = Playwright.create();
        boolean isHeadless = System.getenv("CI") != null
                || Boolean.parseBoolean(System.getProperty("headless", "true"));
        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions().setHeadless(isHeadless);

        switch (browserName.toLowerCase()) {
            case "firefox":
                browser = playwright.firefox().launch(options);
                break;
            case "webkit":
                browser = playwright.webkit().launch(options);
                break;
            case "chromium":
            default:
                browser = playwright.chromium().launch(options);
                break;
        }

        System.out.println("Launched browser: " + browser.browserType().name());
        context = browser.newContext();
        page = context.newPage();
    }

    @AfterMethod
    public void tearDown() {
        if (context != null) {
            context.close();
        }
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
}