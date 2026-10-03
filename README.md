# Web_Test_Automation: eBay UI Automation Framework

A Selenium WebDriver test automation framework (Java) that automates a search flow on [eBay](https://www.ebay.com/). It follows the **Page Object Model (POM)** pattern, reads all test data from an external **JSON** file, and produces an **Extent HTML execution report**.

---

## 1. Automated scenario

| # | Step | Implemented in |
|---|------|----------------|
| 1 | Navigate to `https://www.ebay.com/` | `HomePage.openHomePage()` |
| 2 | Validate that the main page is displayed | `HomePage.isHomePageDisplayed()` |
| 3 | Search for `mazda mx-5` | `HomePage.searchFor()` |
| 4 | Validate the results obtained | `SearchResultsPage.areResultsRelevant()` |
| 5 | Print / log the number of results | `SearchResultsPage.getResultsCount()` + console + Extent report |
| 6 | Apply left-hand filter **Transmission → Manual** | `SearchResultsPage.applyFilter()` (see [Known Limitations](#7-known-limitations--deviations-from-the-task)) |

---

## 2. Tech stack

| Tool | Version | Purpose |
|------|---------|---------|
| Java | 17+ | Language |
| Maven | 3.x | Build and dependency management |
| Selenium WebDriver | 4.25.0 | Browser automation |
| TestNG | 7.10.2 | Test runner, assertions, listeners |
| ExtentReports | 5.1.2 | HTML execution report |
| Jackson Databind | 2.17.2 | Reading the JSON test data |
| WebDriverManager | 5.9.2 | Automatic ChromeDriver download and setup |

---

## 3. Project structure

```
Web_Test_Automation
├── pom.xml
├── testng.xml                         # Suite definition + listener registration
├── README.md
├── docs/
│   └── screenshots/                   # Evidence for the Known Limitations section
├── reports/
│   └── ExtentReport.html              # Generated after each run
└── src
    ├── main
    │   └── java
    │       ├── base
    │       │   ├── DriverFactory.java     # Opens/closes Chrome (ThreadLocal driver)
    │       │   └── BasePage.java          # Common browser actions with explicit waits
    │       ├── pages
    │       │   ├── HomePage.java          # Page Object: eBay home page
    │       │   └── SearchResultsPage.java # Page Object: search results page
    │       └── utils
    │           ├── JsonReader.java        # Reads values from testdata.json
    │           └── ExtentManager.java     # Builds the Extent report
    └── test
        ├── java
        │   └── tests
        │       ├── BaseTest.java          # @BeforeMethod / @AfterMethod (browser lifecycle)
        │       ├── EbaySearchTest.java    # The test scenario
        │       └── TestListener.java      # Logs results to Extent, screenshot on failure
        └── resources
            └── testdata.json              # External test data
```

### How the pieces fit together

```
testdata.json ──> JsonReader ─────────────┐
                                          ├──> EbaySearchTest
DriverFactory ──> BasePage ──> Pages ─────┘
                                          └──> TestListener ──> ExtentManager ──> ExtentReport.html
```

---

## 4. Framework features

### Page Object Model
Each page has its own class that holds its **locators** and **actions**. If eBay changes an element, the locator is updated in one place only. Tests never call Selenium directly; they call page methods.

### Browser common actions (`BasePage`)
All pages inherit these from `BasePage`, which uses explicit waits (`WebDriverWait`, 15 s):

`open(url)`, `waitForVisible(by)`, `click(by)`, `type(by, text)`, `getText(by)`, `isDisplayed(by)`, `findAll(by)`, `scrollTo(by)`, `jsClick(by)`, `getTitle()`, `getCurrentUrl()`

### Browser lifecycle (`DriverFactory`)
Sets up ChromeDriver through WebDriverManager, starts Chrome maximized, and quits it after each test. The driver is stored in a `ThreadLocal`, so it is safe for parallel execution.

### Externalised test data
No test data is hardcoded in the test or page classes. Everything is read from `src/test/resources/testdata.json` through `JsonReader`.

### Execution report
`TestListener` records every test and its steps in an Extent report, attaches a **Base64 screenshot on failure**, and writes `reports/ExtentReport.html`. Skipped tests show their reason.

---

## 5. Test data

File: `src/test/resources/testdata.json`

```json
{
  "url": "https://www.ebay.com/",
  "searchKeyword": "mazda mx-5",
  "filterName": "Transmission",
  "filterValue": "Manual",
  "ignoredTitle": "Shop on eBay"
}
```

| Key | Meaning |
|-----|---------|
| `url` | Page to open and to validate as the main page |
| `searchKeyword` | Text typed into the search box and used to validate result titles |
| `filterName` | Name of the filter to open (e.g. `Transmission`) |
| `filterValue` | Option to select inside that filter (e.g. `Manual`) |
| `ignoredTitle` | Placeholder card that eBay injects into the results, skipped during title validation |

To run the same flow with different data (for example another keyword), edit this file only. No code changes are needed.

---

## 6. Running the tests

### Prerequisites
- JDK 17 or newer
- Maven 3.x (or the Maven bundled with IntelliJ IDEA)
- Google Chrome (latest)
- Internet access (WebDriverManager downloads the matching ChromeDriver on first run)

### Command line
```bash
git clone <repository-url>
cd Web_Test_Automation
mvn clean test
```

### IntelliJ IDEA
Open the Maven panel, then **Lifecycle → clean**, then **Lifecycle → test**.

### What happens
1. Chrome opens and maximizes.
2. The test runs the scenario from section 1.
3. Chrome closes.
4. The report is written to `reports/ExtentReport.html`. Open it in any browser.

### Reading the results
The console prints lines such as:

```
Checked 60 titles, all matched.
Results heading: 684 results for mazda mx-5
Results count for 'mazda mx-5': 684
```

The final Maven summary reports `Tests run: 1, Failures: 0, Skipped: 1` while the filter step is skipped (see below).

---

## 7. Known limitations / deviations from the task

The task asks for: search `mazda mx-5`, then apply **Transmission → Manual** from the left-hand filter panel.

While building this against the live site (https://www.ebay.com/, region: Egypt) the following was observed:

1. **No Transmission filter after a normal search.** After searching `mazda mx-5` from the home page, the left-hand panel shows category links and general filters (Delivery Options, Price, Condition), but no *Transmission* filter. A page search for the word "Transmission" only matched a *category link* ("Transmission & Drivetrain" under Car & Truck Parts), which is not a filter.
2. **The filter exists only on the Cars & Trucks category page.** There it is a dropdown button above the results (Automatic / Manual / Semi-Automatic / Not Specified), not an entry in the left-hand panel.
3. **Searching inside that page discards the filter.** Applying Manual and then searching `mazda mx-5` returned to the general results (`7,000+`) with the filter cleared. Searching first and filtering afterwards is not possible either, because the Transmission button disappears once a keyword search is made.

Because of this, the filter step cannot be completed on this layout. The framework handles it as follows:

- `SearchResultsPage.isFilterAvailable()` checks whether the filter is present.
- If it is **not** present, the test is marked **SKIPPED** with a clear message, and a warning is written to the report. It is not reported as a failure.
- If the filter **is** present, `applyFilter()` opens the dropdown, selects the value from `testdata.json`, and the test asserts that the result count decreased.

> `applyFilter()` is written against the dropdown markup observed on the Cars & Trucks page. Because the filter never appeared in the automated search flow, this path has **not** been exercised end to end. eBay's layout can also vary by region and over time.

Evidence is stored in `docs/screenshots/`.

### Other notes
- **The result count changes between runs.** It is whatever eBay displays at that moment (for example `7,000+` in one session and `684` in another). The test therefore only asserts that the count is greater than zero and never compares it with a fixed number.
- **Result-title validation.** Every displayed title must contain every word of the keyword, ignoring hyphens, spaces and case, so `Mazda MX-5`, `Mazda MX5` and `Mazda Mx 5` all match. The placeholder card named in `ignoredTitle` is skipped.
- **Bot detection.** eBay occasionally shows a CAPTCHA to automated browsers. If that happens, re-run the test after a short wait.
- **Locators.** They match eBay's current markup and may need updating if the site changes. They are all declared at the top of each page class.

---

## 8. Troubleshooting

| Problem | Likely cause / fix |
|---------|--------------------|
| `Cannot resolve symbol 'testng'` | Maven dependencies not loaded. Click **Reload** in the Maven panel. |
| `Suite file ... testng.xml is not a valid file` | File is missing or misnamed. It must be called exactly `testng.xml` and sit next to `pom.xml`. |
| `reports` folder not visible in the IDE | Right-click the project and choose **Reload from Disk**, or open the folder in the file explorer. |
| Test fails on title validation | The failing title is printed as `Title does not match: [...]` in the console. |
| Unexpected build errors with a very new JDK | Use JDK 17 or 21. |

---

## 9. Repository checklist

- [x] Page Object Model
- [x] Common browser actions in `BasePage`
- [x] External test data (JSON)
- [x] Execution report (Extent) with screenshot on failure
- [x] Documentation (this file)
- [ ] Screenshots added to `docs/screenshots/`
- [ ] Sample `ExtentReport.html` or screenshot added to `docs/`
