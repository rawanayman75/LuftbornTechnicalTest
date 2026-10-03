package base;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

public class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage() {
        this.driver = DriverFactory.getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void open(String url) { driver.get(url); }

    public WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    public void type(By locator, String text) {
        WebElement el = waitForVisible(locator);
        el.clear();
        el.sendKeys(text);
    }

    public String getText(By locator) { return waitForVisible(locator).getText(); }

    public boolean isDisplayed(By locator) {
        try { return driver.findElement(locator).isDisplayed(); }
        catch (NoSuchElementException e) { return false; }
    }

    public List<WebElement> findAll(By locator) { return driver.findElements(locator); }

    public void scrollTo(By locator) {
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block:'center'});", waitForVisible(locator));
    }

    public void jsClick(By locator) {
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].click();", driver.findElement(locator));
    }

    public String getTitle() { return driver.getTitle(); }
    public String getCurrentUrl() { return driver.getCurrentUrl(); }
}
