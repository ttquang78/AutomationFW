package com.uid.common.utils;

import com.uid.common.config.Setup;
import com.uid.common.config.WebdriverManager;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SelCheckUtil
{
    private static Logger log = Logger.getLogger(SelCheckUtil.class.getName());

    private SelCheckUtil() {}

    public static boolean isElementClickable(WebDriver driver, WebElement element, int timeout)
    {
        boolean isClickable = true;
        WebdriverManager.setErrorExpectedTrue();
        try
        {
            WebDriverWait wait = new WebDriverWait(driver, timeout);
            wait.until(ExpectedConditions.elementToBeClickable(element));
            log.info(element + " is clickable");
        }
        catch (Exception e)
        {
            isClickable = false;
            log.info(element + " is not clickable");
        }

        WebdriverManager.setErrorExpectedFalse();
        return isClickable;
    }

    public static boolean isElementClickable(WebDriver driver, By by)
    {
        return isElementClickable(driver, by, Setup.DEFAULT_TIME_OUT);
    }

    public static boolean isElementClickable(WebDriver driver, By by, int timeout)
    {
        boolean isClickable = true;
        WebdriverManager.setErrorExpectedTrue();
        try
        {
            WebDriverWait wait = new WebDriverWait(driver, timeout);
            wait.until(ExpectedConditions.elementToBeClickable(by));
        }
        catch (Exception e)
        {
            isClickable = false;
        }

        WebdriverManager.setErrorExpectedFalse();
        return isClickable;
    }

    public static boolean isElementCoveredByElement(WebElement coverElement,
            WebElement coveredElement)
    {
        boolean result = false;

        Point coverPoint1 = coverElement.getLocation();
        Dimension coverDimension = coverElement.getSize();
        Point coverPoint2 = new Point(coverPoint1.x + coverDimension.width,
                coverPoint1.y + coverDimension.height);

        Point coveredPoint1 = coveredElement.getLocation();
        Dimension coveredDimension = coveredElement.getSize();
        Point coveredPoint2 = new Point(coveredPoint1.x + coveredDimension.width,
                coveredPoint1.y + coveredDimension.height);

        if (coverPoint1.x <= coveredPoint1.x && coverPoint1.y <= coveredPoint1.y
                && (coverPoint2.x + 5) >= coveredPoint2.x && (coverPoint2.y + 5) >= coveredPoint2.y)
        {
            result = true;
        }

        return result;
    }

    public static boolean isElementPresence(WebDriver driver, By by)
    {
        return isElementPresence(driver, by, Setup.DEFAULT_TIME_OUT);
    }

    public static boolean isElementPresence(WebDriver driver, By by, int timeout)
    {
        boolean isPresence = true;
        WebdriverManager.setErrorExpectedTrue();
        try
        {
            WebDriverWait wait = new WebDriverWait(driver, timeout);
            wait.until(ExpectedConditions.presenceOfElementLocated(by));
        }
        catch (Exception e)
        {
            isPresence = false;
        }

        WebdriverManager.setErrorExpectedFalse();
        return isPresence;
    }

    public static boolean isElementStaleness(WebDriver driver, WebElement element)
    {
        return isElementStaleness(driver, element, Setup.DEFAULT_TIME_OUT);
    }

    public static boolean isElementStaleness(WebDriver driver, WebElement element, int timeout)
    {
        boolean isStale = true;
        WebdriverManager.setErrorExpectedTrue();
        try
        {
            WebDriverWait wait = new WebDriverWait(driver, timeout);
            wait.until(ExpectedConditions.stalenessOf(element));
            log.info(element + " is stale");
        }
        catch (Exception e)
        {
            isStale = false;
            log.info(element + " is not stale");
        }

        WebdriverManager.setErrorExpectedFalse();
        return isStale;
    }

    public static boolean isElementVisibility(WebDriver driver, WebElement element)
    {
        return isElementVisibility(driver, element, Setup.DEFAULT_TIME_OUT);
    }

    public static boolean isElementVisibility(WebDriver driver, WebElement element, int timeout)
    {
        boolean isVisible = true;
        WebdriverManager.setErrorExpectedTrue();
        try
        {
            WebDriverWait wait = new WebDriverWait(driver, timeout);
            wait.until(ExpectedConditions.visibilityOf(element));
        }
        catch (Exception e)
        {
            isVisible = false;
        }

        WebdriverManager.setErrorExpectedFalse();
        return isVisible;
    }

    public static boolean isElementVisibility(WebDriver driver, By by)
    {
        return isElementVisibility(driver, by, Setup.DEFAULT_TIME_OUT);
    }

    public static boolean isElementVisibility(WebDriver driver, By by, int timeout)
    {
        boolean isVisible = true;
        WebdriverManager.setErrorExpectedTrue();
        try
        {
            WebDriverWait wait = new WebDriverWait(driver, timeout);
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
        }
        catch (Exception e)
        {
            isVisible = false;
        }

        WebdriverManager.setErrorExpectedFalse();
        return isVisible;
    }

    public static boolean isTextDisplayOnElement(WebDriver driver, WebElement element, String text)
    {
        return isTextDisplayOnElement(driver, element, text, Setup.DEFAULT_TIME_OUT);
    }

    public static boolean isTextDisplayOnElement(WebDriver driver, WebElement element, String text,
            int timeout)
    {
        boolean isVisible = true;
        WebdriverManager.setErrorExpectedTrue();
        try
        {
            WebDriverWait wait = new WebDriverWait(driver, timeout);
            wait.until(ExpectedConditions.textToBePresentInElement(element, text));
        }
        catch (Exception e)
        {
            isVisible = false;
        }

        WebdriverManager.setErrorExpectedFalse();
        return isVisible;
    }

    public static boolean waitForCondition(WebDriver driver, ExpectedCondition<Boolean> condition)
    {
        return waitForCondition(driver, condition, Setup.DEFAULT_TIME_OUT);
    }

    public static boolean waitForCondition(WebDriver driver, ExpectedCondition<Boolean> condition,
            int waitSeconds)
    {
        Boolean waitCondition = false;
        try
        {
            WebDriverWait wait = new WebDriverWait(driver, waitSeconds);
            waitCondition = wait.until(condition);
        }
        catch (Exception e)
        {
            log.error(e.getMessage());
        }
        return waitCondition;
    }

    public static boolean waitForElementChangeToInvisible(WebDriver driver, By by,
            int... timeoutSec)
    {
        boolean isInvisible = true;
        WebdriverManager.setErrorExpectedTrue();
        try
        {
            WebDriverWait wait = new WebDriverWait(driver, timeoutSec[0]);
            wait.until(ExpectedConditions.invisibilityOfElementLocated(by));
        }
        catch (Exception e)
        {
            isInvisible = false;
        }

        WebdriverManager.setErrorExpectedFalse();
        return isInvisible;
    }

    public static boolean waitForElementChangeToInvisible(WebDriver driver, WebElement element,
            int... timeoutSec)
    {
        int timeout;
        if (timeoutSec.length == 0)
        {
            timeout = Setup.DEFAULT_TIME_OUT;
        }
        else
        {
            timeout = timeoutSec[0];
        }

        boolean isDisappear;
        try
        {
            WebdriverManager.setErrorExpectedTrue();
            WebDriverWait wait = new WebDriverWait(driver, timeout);
            wait.until(driver1 -> {
                boolean rtnValue;
                try
                {
                    rtnValue = !element.isDisplayed();
                }
                catch (Exception e)
                {
                    rtnValue = true;
                }
                return rtnValue;
            });
            isDisappear = true;
        }
        catch (TimeoutException te)
        {
            isDisappear = false;
        }
        WebdriverManager.setErrorExpectedFalse();
        return isDisappear;
    }
}
