package pages;

import base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

public class HomePage extends BasePage {

    // Locators
    private final By searchBox = By.id("gh-ac");

    // Actions
    public void openHomePage(String url) {
        open(url);
    }

    public boolean isHomePageDisplayed(String expectedUrl) {
        String actual = getCurrentUrl();
        return actual.equals(expectedUrl)
                || actual.equals(expectedUrl.replaceAll("/$", ""));
    }

    public void searchFor(String keyword) {
        type(searchBox, keyword);
        driver.findElement(searchBox).sendKeys(Keys.ENTER);
    }
}