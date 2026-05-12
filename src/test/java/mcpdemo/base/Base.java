package mcpdemo.base;

import com.microsoft.playwright.*;

// import java.util.List;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class Base {
    
    protected Playwright playwright;
    protected Browser[] browsers;
    protected BrowserContext context;
    protected Page page;
    
    protected static final String BASE_URL = "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login";
    
    @BeforeMethod
    public void setup() {
        playwright = Playwright.create();
        browsers = new Browser[]{
            playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false)),
            playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(false)),
            playwright.webkit().launch(new BrowserType.LaunchOptions().setHeadless(false))
        };
        for (Browser browser : browsers) {
            System.out.println("Launched browser: " + browser.browserType().name());
             context = browser.newContext();
            page = context.newPage();
        }

       
    }
    
    @AfterMethod
    public void tearDown() {
        if (context != null) {
            context.close();
        }
        if (browsers != null) {
            for (Browser browser : browsers) {
                if (browser != null) {
                    browser.close();
                }
            }
        }
        if (playwright != null) {
            playwright.close();
        }
    }
}