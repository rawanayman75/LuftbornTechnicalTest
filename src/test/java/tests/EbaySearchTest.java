package tests;

import com.aventstack.extentreports.ExtentTest;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.SearchResultsPage;
import utils.JsonReader;

public class EbaySearchTest extends BaseTest {

    @Test
    public void searchMazdaAndApplyFilter() {
        String url = JsonReader.get("url");
        String keyword = JsonReader.get("searchKeyword");
        String filterName = JsonReader.get("filterName");
        String filterValue = JsonReader.get("filterValue");
        String ignoredTitle = JsonReader.get("ignoredTitle");

        ExtentTest log = TestListener.getTest();

        // 1. Navigate
        HomePage homePage = new HomePage();
        homePage.openHomePage(url);
        log.info("Opened " + url);

        // 2. Validate main page
        Assert.assertTrue(homePage.isHomePageDisplayed(url), "Not on the eBay main page");
        log.pass("Landed on the eBay main page");

        // 3. Search
        homePage.searchFor(keyword);
        log.info("Searched for: " + keyword);

        // 4. Validate results
        SearchResultsPage results = new SearchResultsPage();
        int before = results.getResultsCount();
        Assert.assertTrue(before > 0, "Search returned no results");
        Assert.assertTrue(results.areResultsRelevant(keyword, ignoredTitle), "Some results do not match the keyword");
        log.pass("All displayed result titles match the keyword");

        // 5. Log the number of results
        String heading = results.getResultsHeadingText();
        System.out.println("Results heading: " + heading);
        System.out.println("Results count for '" + keyword + "': " + before);
        log.info("Results heading: " + heading);
        log.info("Results count: " + before);

        // 6. Filter (Transmission -> Manual)
        if (!results.isFilterAvailable(filterName)) {
            String msg = "Filter '" + filterName + "' is not displayed on eBay for this region/layout. See README.";
            log.warning(msg);
            throw new SkipException(msg);
        }
        results.applyFilter(filterName, filterValue);
        log.info("Applied filter: " + filterName + " -> " + filterValue);

        int after = results.getResultsCount();
        System.out.println("Results count after filter: " + after);
        log.info("Results count after filter: " + after);
        Assert.assertTrue(after < before, "Filter did not reduce the results");
        log.pass("Filter reduced the number of results");
    }
}