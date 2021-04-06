package com.uid.common.utils;

import com.uid.common.config.Setup;
import com.uid.common.config.Verification;
import com.uid.common.config.WebdriverManager;
import org.apache.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.Set;

public class SelWindowUtil
{
    private static Logger log = Logger.getLogger(SelWindowUtil.class.getName());

    private SelWindowUtil() {}

    public static void switchWindowByIndex(WebDriver driver, int index, List<String> windowHdls)
    {
        driver.switchTo().window(windowHdls.get(index));
        Verification.logSubStep("Switch to window -> " + driver.getWindowHandle());
    }

    public static void switchToNewWindow(WebDriver driver, List windowHdls)
    {
        isNewWindowDisplay(driver, windowHdls);
        Set<String> set = driver.getWindowHandles();
        for (String handler : set)
        {
            if (!windowHdls.contains(handler))
            {
                Verification.logSubStep("Switch to new window -> " + handler);
                driver.switchTo().window(handler);
                maximizeWindow(driver);
                windowHdls.add(driver.getWindowHandle());
                break;
            }
        }
    }

    public static void switchToNewTab(WebDriver driver)
    {
        Verification.logSubStep("Switch to new tab");
        SelCheckUtil.waitForCondition(driver, driver1 -> driver1.getWindowHandles().size() > 1);

        // Get both open tabs
        Set<String> set = driver.getWindowHandles();

        // Switch to new tab (second tab)
        String currentHandler = driver.getWindowHandle();
        for (String handler : set)
        {
            if (!handler.equals(currentHandler))
            {
                driver.switchTo().window(handler);
                break;
            }
        }
    }

    public static void switchBackMainTab(WebDriver driver)
    {
        Verification.logSubStep("Switch back to main tab");
        driver.close();
        Set<String> set = driver.getWindowHandles();
        driver.switchTo().window((String)set.toArray()[0]);
    }

    public static void storeWindowHandler(WebDriver driver, List windowHdls)
    {
        String currentHandler = driver.getWindowHandle();
        windowHdls.add(currentHandler);
    }

    public static void maximizeWindow(WebDriver driver)
    {
        if (!Setup.getBrowserType().equalsIgnoreCase("chrome"))
        {
            log.info("Maximize window");
            driver.manage().window().maximize();
        }
    }

    public static boolean isNewWindowDisplay(WebDriver driver, List windowHdls)
    {
        return SelCheckUtil.waitForCondition(driver, driver1 -> {
            int numberOfWindow = driver1.getWindowHandles().size();
            return numberOfWindow > windowHdls.size();
        }, 30);
    }

    public static void closeNewWindow(WebDriver driver, List windowHdls)
    {
        String currentWindow = driver.getWindowHandle();
        switchToNewWindow(driver, windowHdls);
        closeCurrentWindow(driver, windowHdls);
        driver.switchTo().window(currentWindow);
    }

    public static void closeCurrentWindowAndSwitchTo(WebDriver driver, List windowHdls,
            int windowIndex)
    {
        closeCurrentWindow(driver, windowHdls);
        switchWindowByIndex(driver, windowIndex, windowHdls);
    }

    public static void closeCurrentWindow(WebDriver driver, List windowHdls)
    {
        String currentHandler = driver.getWindowHandle();

        Verification.logSubStep("Close current window " + currentHandler);

        windowHdls.remove(currentHandler);

        // Close window handler
        try
        {
            WebdriverManager.setErrorExpectedTrue();
            driver.close();
        }
        catch (Exception e)
        {
            closeAlert(driver, 5);
            driver.close();
        }
        WebdriverManager.setErrorExpectedFalse();
    }

    public static void closeAlert(WebDriver driver, int timeout)
    {
        WebdriverManager.setErrorExpectedTrue();

        try
        {
            WebDriverWait wait = new WebDriverWait(driver, timeout);
            wait.until(ExpectedConditions.alertIsPresent());

            log.info("Close Alert dialog");
            Alert alert = driver.switchTo().alert();
            alert.accept();

            driver.switchTo().defaultContent();
        }
        catch (Exception e)
        {
            log.info("No alert dialog");
        }

        WebdriverManager.setErrorExpectedFalse();
    }

    public static void scrollToElement(WebDriver driver, WebElement element)
    {
        JavascriptExecutor je = (JavascriptExecutor)driver;
        je.executeScript("arguments[0].scrollIntoView(true);", element);
    }

    public static void scrollToTop(WebDriver driver)
    {
        JavascriptExecutor je = (JavascriptExecutor)driver;
        je.executeScript("window.scrollTo(0, 0);");
    }

    public static void moveToFrameByIndex(WebDriver driver, int index)
    {
        if (index > -1)
        {
            log.info("Move to frame #" + index);
            driver.switchTo().frame(index);
        }
    }
}
