package pages;

import base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class SearchResultsPage extends BasePage {

    // ---------- Locators ----------
    private final By resultsCountHeading = By.id("srp-results-heading");
    private final By resultTitles = By.cssSelector(".s-card__title .su-styled-text.primary");

    // ---------- Actions ----------

    // بيقرا الرقم من جملة زي: "7,000+ results for mazda mx-5"
    public String getResultsHeadingText() {
        wait.until(d -> d.findElement(resultsCountHeading).getText().toLowerCase().contains("results"));
        return getText(resultsCountHeading);
    }

    public int getResultsCount() {
        String text = getResultsHeadingText();
        String number = text.split(" ")[0].replaceAll("[^0-9]", "");
        return Integer.parseInt(number);
    }

    // بيتأكد إن كل عنوان فيه كل كلمة من البحث (بأي ترتيب، وبتتجاهل الشرطة)
    public boolean areResultsRelevant(String keyword, String ignoredTitle) {
        List<WebElement> titles = findAll(resultTitles);
        if (titles.isEmpty()) return false;

        // كلمات البحث بعد شيل الشرطة: mazda, mx5
        String[] words = keyword.toLowerCase().replace("-", "").split("\\s+");
        int checked = 0;
        for (WebElement t : titles) {
            String raw = t.getAttribute("textContent");
            if (raw == null || raw.trim().isEmpty()) continue;
            if (raw.trim().equalsIgnoreCase(ignoredTitle)) continue;

            checked++;
            // العنوان بعد شيل الشرطة والمسافات: "hotwheelsmazdamx5miata..."
            String title = raw.toLowerCase().replaceAll("[\\s-]", "");
            for (String w : words) {
                if (!title.contains(w)) {
                    System.out.println("Title does not match: [" + raw.trim() + "]");
                    return false;
                }
            }
        }
        System.out.println("Checked " + checked + " titles, all matched.");
        return checked > 0;
    }

    // بترجّع true لو زرار الفلتر (مثلا Transmission) ظاهر في الصفحة
    public boolean isFilterAvailable(String filterName) {
        return !findAll(filterButton(filterName)).isEmpty();
    }

    // بتفتح الـ dropdown بتاع الفلتر وتختار القيمة (مثلا Manual)
    public void applyFilter(String filterName, String filterValue) {
        By option = By.xpath("//div[contains(@class,'filter-menu-button__menu')]"
                + "//span[contains(@class,'textual-display') and normalize-space()='" + filterValue + "']");

        click(filterButton(filterName));
        waitForVisible(option);
        jsClick(option);
    }

    // ---------- Helpers ----------
    private By filterButton(String filterName) {
        return By.xpath("//button[contains(@class,'filter-menu-button__button')]"
                + "[contains(normalize-space(), '" + filterName + "')]");
    }
}