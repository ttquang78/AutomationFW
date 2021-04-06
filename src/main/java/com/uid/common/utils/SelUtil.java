package com.uid.common.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
// To retrieve date
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.uid.common.config.Setup;
import com.uid.common.config.Verification;
import com.uid.common.config.WebdriverManager;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SelUtil
{
    private static Logger log = Logger.getLogger(SelUtil.class.getName());

    private SelUtil() {}

    //============ SELENIUM CLICK COMMANDS=====================

    //Click commands

    public static void clickElement(WebDriver driver, By by)
    {
        WebElement element = driver.findElement(by);
        clickElement(driver, element);
    }

    public static void clickElement(WebDriver driver, WebElement element)
    {
        log.info("Click element: " + element.toString());
        if (Setup.getBrowserType().equalsIgnoreCase("ie"))
        {
            JavascriptExecutor executor = (JavascriptExecutor)driver;
            executor.executeScript("arguments[0].click();", element);
        }
        else
        {
            element.click();
        }
        log.info("Clicked element: " + element.toString());
    }

    public static void waitElementClickableAndClick(WebDriver driver, By by)
    {
        waitElementClickableAndClick(driver, by, Setup.DEFAULT_TIME_OUT);
    }

    public static void waitElementClickableAndClick(WebDriver driver, By by, int timeout)
    {
        SelCheckUtil.isElementClickable(driver, by, timeout);
        clickElement(driver, by);
    }

    public static void waitElementClickableAndClick(WebDriver driver, WebElement element)
    {
        waitElementClickableAndClick(driver, element, Setup.DEFAULT_TIME_OUT);
    }

    public static void waitElementClickableAndClick(WebDriver driver, WebElement element,
            int timeout)
    {
        SelCheckUtil.isElementClickable(driver, element, timeout);
        clickElement(driver, element);
    }

    //Sendkey commands

    public static void sendKeyToElement(WebDriver driver, WebElement element, String value)
    {
        sendKeyToElement(driver, element, value, Setup.DEFAULT_TIME_OUT);
    }

    public static void sendKeyToElement(WebDriver driver, WebElement element, String value,
            int timeout)
    {
        SelCheckUtil.isElementVisibility(driver, element, timeout);

        log.info("Sendkey \"" + value + "\" to element: " + element.toString());
        ((JavascriptExecutor)driver).executeScript("arguments[0].value ='';", element);
        element.sendKeys(value);
    }

    //Find commands

    public static List<WebElement> findElements(WebDriver driver, By by)
    {
        return findElements(driver, by, Setup.DEFAULT_TIME_OUT);
    }

    public static List<WebElement> findElements(WebDriver driver, By by, int timeout)
    {
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));

        return driver.findElements(by);
    }

    public static WebElement findElementByDataId(WebDriver driver, String dataId)
    {
        String xpathStr = String.format(".//*[@data-id='%s']", dataId);
        return waitForElementVisibility(driver, By.xpath(xpathStr));
    }

    public static List<WebElement> findElementsByDataId(WebDriver driver, String dataId)
    {
        return findElementsByDataId(driver, "*", dataId);
    }

    public static List<WebElement> findElementsByDataId(SearchContext context, String tagName,
            String dataId)
    {
        String xpath = String.format(".//%s[@data-id='%s']", tagName, dataId);
        return context.findElements(By.xpath(xpath));
    }

    public static WebElement waitForElementPresence(WebDriver driver, By by)
    {
        return waitForElementPresence(driver, by, Setup.DEFAULT_TIME_OUT);
    }

    public static WebElement waitForElementPresence(WebDriver driver, By by, int timeout)
    {
        int retryCount = 0;
        while (retryCount < 3)
        {
            try
            {
                WebDriverWait wait = new WebDriverWait(driver, timeout);
                wait.until(ExpectedConditions.presenceOfElementLocated(by));
                break;
            }
            catch (Exception e)
            {
                retryCount++;
            }
        }

        return driver.findElement(by);
    }

    public static void waitForElementVisibility(WebDriver driver, WebElement element)
    {
        waitForElementVisibility(driver, element, Setup.DEFAULT_TIME_OUT);
    }

    public static void waitForElementVisibility(WebDriver driver, WebElement element, int timeout)
    {
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    public static WebElement waitForElementVisibility(WebDriver driver, By by)
    {
        return waitForElementVisibility(driver, by, Setup.DEFAULT_TIME_OUT);
    }

    public static WebElement waitForElementVisibility(WebDriver driver, By by, int timeout)
    {
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        wait.until(ExpectedConditions.visibilityOfElementLocated(by));

        return driver.findElement(by);
    }

    //Get commands

    public static String getAttribute(WebElement element, String attribute)
    {
        WebdriverManager.setErrorExpectedTrue();

        String value;
        try
        {
            value = element.getAttribute(attribute);
        }
        catch (Exception e)
        {
            value = "";
        }

        WebdriverManager.setErrorExpectedFalse();
        return value;
    }

    private static String standardizeURL(String url)
    {
        int index = url.indexOf('?');
        if (index > -1)
        {
            url = url.substring(0, index);
        }
        if (url.startsWith("https"))
        {
            url = url.replace("https", "http");
        }
        if (url.endsWith("/"))
        {
            url = url.substring(0, url.length() - 1);
        }

        return url;
    }

    public static String getRedirectedURL(String url)
    {
        String newUrl;

        String chromeDriverPath = Setup.getChromeDriverPath();
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);

        int count = 0;
        WebDriver chromeDriver;

        do
        {
            chromeDriver = new ChromeDriver();
            chromeDriver.manage().timeouts().pageLoadTimeout(180, TimeUnit.SECONDS);
            try
            {
                WebdriverManager.setErrorExpectedTrue();
                chromeDriver.get(url);
                break;
            }
            catch (Exception e)
            {
                chromeDriver.close();
            }
            count++;
            WebdriverManager.setErrorExpectedFalse();
        }
        while (count < 3);

        //Delete string after question mark
        newUrl = chromeDriver.getCurrentUrl();
        String rtnURL = newUrl;

        newUrl = standardizeURL(newUrl);

        url = standardizeURL(url);

        chromeDriver.quit();

        if (!url.contentEquals(newUrl))
        {
            return rtnURL;
        }
        else
        {
            return null;
        }
    }

    public static String getText(WebElement element)
    {
        WebdriverManager.setErrorExpectedTrue();

        String value;
        try
        {
            value = element.getText();
        }
        catch (Exception e)
        {
            value = "";
        }

        WebdriverManager.setErrorExpectedFalse();
        return value;
    }

    public static WebElement getWebElementFromApplicationData(WebDriver driver,
            ApplicationData data, String field)
    {
        WebdriverManager.setErrorExpectedTrue();

        WebElement webElement;
        int frameIndex;
        String cssLocator;
        String xpathLocator;

        if (field.equalsIgnoreCase("username"))
        {
            frameIndex = data.getLearnedDocIdUserName();
            cssLocator = data.getLearnedUserName();
            xpathLocator = data.getLearnedUserNameXpath();
        }
        else if (field.equalsIgnoreCase("password"))
        {
            frameIndex = data.getLearnedDocIdPsw();
            cssLocator = data.getLearnedPsw();
            xpathLocator = data.getLearnedPswXpath();
        }
        else
        {
            frameIndex = data.getLearnedDocIdSubmit();
            cssLocator = data.getLearnedSubmit();
            xpathLocator = data.getLearnedSubmitXpath();
        }

        if (frameIndex > -1)
        {
            driver.switchTo().frame(frameIndex);
        }

        if (SelCheckUtil.isElementVisibility(driver, By.cssSelector(cssLocator), 3))
        {
            webElement = SelUtil.waitForElementVisibility(driver, By.cssSelector(cssLocator), 3);
        } else {
            webElement = SelUtil.waitForElementVisibility(driver, By.xpath(xpathLocator), 3);
        }

        driver.switchTo().defaultContent();

        WebdriverManager.setErrorExpectedFalse();

        return webElement;
    }

    //Other commands

    public static int countVisibleElements(List<WebElement> elements)
    {
        int count = 0;

        for (WebElement webElement : elements)
        {
            if (webElement.isDisplayed())
            {
                count++;
            }
        }

        return count;
    }

    public static void dragElement(WebDriver driver, WebElement element, int xOffset, int yOffset)
    {
        Actions acts = new Actions(driver);
        acts.dragAndDropBy(element, xOffset, yOffset).perform();
    }

    public static WebDriver initWebDriver() throws MalformedURLException
    {
        WebDriver driver;

        Verification.logStep("Launch second browser: Chrome");
        ChromeOptions options = new ChromeOptions();
        options.addExtensions(new File(Setup.getExtensionDir() + Setup.getChromeExtName()));

        DesiredCapabilities chromeCapabilities = DesiredCapabilities.chrome();
        chromeCapabilities.setCapability(ChromeOptions.CAPABILITY, options);
        driver = new RemoteWebDriver(new URL("http://" + Setup.getGridServer() + ":4441/wd/hub"),
                chromeCapabilities);
        SelWindowUtil.maximizeWindow(driver);

        return driver;
    }

    public static void wait(int second, String reason)
    {
        try
        {
            log.info("WARN: Start to hard wait in " + second + "s - " + reason);
            Thread.sleep(second * 1000L);
        }
        catch (InterruptedException e)
        {
            log.error(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    public static void selectCategory(WebDriver driver, WebElement lstCategory, String option)
    {
        if (SelCheckUtil.isElementVisibility(driver, lstCategory))
        {
            Select categoryList = new Select(lstCategory);
            categoryList.selectByVisibleText(option);
        }
    }
}
