package dev.romahn.scraper.abhl;

import dev.romahn.scraper.AppointmentScraper;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


@Singleton
public class ABHLAppointmentScraper implements AppointmentScraper {

    @Value("${selenium.chrome.server:`http://127.0.0.1:4444`}")
    private String seleniumChromeServer;

    @Value("${abhl.load.timeout:`10`}")
    private int loadTimeout;

    @Value("${abhl.appointment.url:`https://www.leipzig.de/fachanwendungen/termine/abholung-aufenthaltstitel.html`}")
    private String abhlAppointmentUrl;

    @Override
    public Map<LocalDate, Integer> getAvailableAppointments() throws Exception {
        RemoteWebDriver driver = new RemoteWebDriver(new URL(seleniumChromeServer), new ChromeOptions());
        try {
            navigateToAppointments(driver);
            return extractAppointments(driver);
        } finally {
            driver.quit();
        }
    }

    private void navigateToAppointments(RemoteWebDriver driver) {
        driver.get(abhlAppointmentUrl);
        driver.manage().window().maximize();
        driver.switchTo().frame(0);
        driver.findElement(By.id("agreement_accept")).click();
        driver.findElement(By.id("action_infopage_next")).click();
        driver.findElement(By.cssSelector(".buttonTreeviewExpand")).click();
        driver.findElement(By.id("action_officeselect_termnew_prefix1600340740349")).click();
        driver.findElement(By.id("id_1600340740368")).click();
        {
            WebElement dropdown = driver.findElement(By.id("id_1600340740368"));
            dropdown.findElement(By.xpath("//option[. = '1']")).click();
        }
        driver.findElement(By.id("action_concernselect_next")).click();
        driver.findElement(By.id("action_concerncomments_next")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(loadTimeout));
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.className("ekolCalendarMonthTable")));
    }

    private Map<LocalDate, Integer> extractAppointments(RemoteWebDriver driver) {

        Map<LocalDate, Integer> result = new HashMap<>();

        List<WebElement> monthTables = driver.findElements(By.className("ekolCalendarMonthTable"));

        for (WebElement monthTable : monthTables) {
            String monthYear = monthTable.findElement(By.tagName("caption")).getText();

            for (WebElement freeButton : monthTable.findElements(By.className("eKOLCalendarButtonDayFreeX"))) {
                String day = freeButton.findElement(By.className("ekolCalendarDayNumberInRange")).getText();
                LocalDate date = LocalDate.parse(day + " " + monthYear, DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.GERMAN));

                Integer freeSlots = Integer.valueOf(freeButton.findElement(By.className("ekolCalendarFreeTimeContainer")).getText().split(" ")[0]);

                result.put(date, freeSlots);
            }
        }
        return result;
    }
}
