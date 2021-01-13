package urlshortener.new_integration;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;


public class StepDefinitions {
    WebDriver driver;

    @Given("Open the Chrome and launch the application")
    public void openChromeAndLaunch() throws Throwable {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("http://localhost:8080/");
    }

    @And("^Welcome page will be displayed$")
    public void verifyWelcomeMessage() throws Throwable {
        String actualString = driver.findElement(By.className("col-md-6")).getText();
        assertTrue(actualString.contains("Recorta rápidamente\ntus URLs!"));
    }

    @When("^User visits login page$")
    public void visitLoginPage() throws Throwable {
        driver.findElement(By.className("login")).click();
        assertEquals(driver.getCurrentUrl(), "http://localhost:8080/login");
    }

    @And("^User input and submit signup form$")
    public void enterAndSubmitSingupForm() throws Throwable {
        Thread.sleep(100);
        driver.findElement(By.id("signUp")).click();
        driver.findElement(By.id("register-username")).sendKeys("prueba");
        driver.findElement(By.id("register-password")).sendKeys("1234");
        driver.findElement(By.id("register-confirm-password")).sendKeys("1234");
        driver.findElement(By.id("register")).click();
    }

    @And("^User input and submit login form$")
    public void enterAndSubmitLoginForm() throws Throwable {
        driver.findElement(By.id("login-username")).sendKeys("prueba");
        driver.findElement(By.id("login-password")).sendKeys("1234");
        driver.findElement(By.id("login-button")).click();
    }

    @Then("^User is registered$")
    public void redirectsToLoginPage() throws Throwable {
        assertEquals(driver.getCurrentUrl(), "http://localhost:8080/login");
    }
    @Then("^Panel page will be displayed$")
    public void redirectsToPanel() throws Throwable {
        Thread.sleep(2000);
        assertEquals(driver.getCurrentUrl(), "http://localhost:8080/panel");
    }

    @When("User input and submit a valid URL")
    public void userInputAndSubmitValidURL() throws InterruptedException {
        driver.findElement(By.id("id-url-input")).sendKeys("https://www.forocoches.com");
        Thread.sleep(1000);
        driver.findElement(By.className("col-md-4")).click();
    }

    @When("User input and submit an invalid URL")
    public void userInputAndSubmitInvalidURL() throws InterruptedException {
        driver.findElement(By.id("id-url-input")).sendKeys("https://invalid");
        Thread.sleep(1000);
        driver.findElement(By.className("col-md-4")).click();
    }

    @Then("Gets an invalidated shorted URL")
    public void invalidatedShortedURL() throws InterruptedException {
        Thread.sleep(1500);
        WebElement baseTable = driver.findElement(By.className("styled-table"));
        List<WebElement> tableRows = baseTable.findElements(By.tagName("tr"));
        String row = tableRows.get(2).getAttribute("innerHTML");
        String[] fields = row.split("</td>");
        assertEquals(false, fields[1].contains("href"));
    }

    @Then("Gets a validated and shorted URL")
    public void validatedShortedURL() throws InterruptedException {
        Thread.sleep(2000);
        WebElement baseTable = driver.findElement(By.className("styled-table"));
        List<WebElement> tableRows = baseTable.findElements(By.tagName("tr"));
        assertFalse(tableRows.isEmpty());
        String row = tableRows.get(1).getAttribute("innerHTML");
        String[] fields = row.split("</td>");
        assertEquals(true, fields[1].contains("href"));
    }

    @Then("Is redirected to target URL successfully")
    public void validURLredirectsToTarget() throws InterruptedException {
        Thread.sleep(2000);
        WebElement baseTable = driver.findElement(By.className("styled-table"));
        List<WebElement> tableRows = baseTable.findElements(By.tagName("tr"));
        String url = tableRows.get(1).getText().split(" ")[1];
        driver.get(url);
        assertEquals(driver.getCurrentUrl(), "https://www.forocoches.com/");
    }

    @And("Number of clicks is incremented by 1")
    public void nClickIsIncrementedByOne() throws InterruptedException {
        Thread.sleep(2000);
        WebElement baseTable = driver.findElement(By.className("styled-table"));
        List<WebElement> tableRows = baseTable.findElements(By.tagName("tr"));
        String nClicks = tableRows.get(1).getText().split(" ")[2];
        assertEquals("1", nClicks);
    }

    @When("Uploads a CSV file")
    public void uploadsACSVFile() throws InterruptedException {
        Thread.sleep(2000);
        File file = new File("src/test/resources/urls.csv");
        System.out.println(file.getAbsolutePath());
        driver.findElement(By.id("upload")).sendKeys(file.getAbsolutePath());
    }

    @Then("All URL's from the CSV are properly loaded")
    public void allURLSareProperlyLoaded() throws InterruptedException {
        Thread.sleep(10000);
        WebElement baseTable = driver.findElement(By.className("styled-table"));
        List<WebElement> tableRows = baseTable.findElements(By.tagName("tr"));
        assertEquals(tableRows.size(), 12);

        for (int i = 0; i < tableRows.size(); i++) {
            String row = tableRows.get(i).getAttribute("innerHTML");
            String[] fields = row.split("</td>");
            assertEquals(true, fields[1].contains("href"));
        }
    }


    @Then("^Closes Chrome$")
    public void closeChrome() throws Throwable {
        driver.close();
    }



}