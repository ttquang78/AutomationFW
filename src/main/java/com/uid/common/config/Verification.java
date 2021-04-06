package com.uid.common.config;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.Reporter;

public class Verification
{
    private static Logger log = Logger.getLogger(Verification.class.getName());
    static String screenshotPath;  //Specified within Setup

    private Verification() {}

    //*********************************************************************//
    //                          FUNCTIONS                                  //
    //*********************************************************************//

    /**
     * [STARTCOLLAPSEHTML]:
     * /*- Collapse function for html report (+/- to show hide the test case stestps)
     * /*-------------------------------------------------
     */
    public static void startCollapseHtml()
    {
        Reporter.log("<details>");
        Reporter.log("<summary class=\"heading\">" + Setup.getTestName() + "</summary>");
    }

    /**
     * [ENDCOLLAPSEHTML]:
     * /*- Close the fnal DIV of the test case steps loging
     * /*-------------------------------------------------
     */
    public static void endCollapseHtml()
    {
        Reporter.log("<br></details>");
    }

    /**
     * [TITLE]:
     * /*- Log the step of a test case
     * /*-------------------------------------------------
     */
    public static void title(String title)
    {
        Reporter.log(
                "<br><h3 style=\"color:#045FB4;background-color:lightgray\"> " + title + "</h3>");
    }

    /**
     * [LOGSTEP]:
     * /*- Log the step of a test case
     * /*-------------------------------------------------
     */
    public static void logStep(String step)
    {
        log.info(step);
        Reporter.log("<br><h3>&raquo; " + step + "</h3>");
    }

    public static void logSubStep(String step)
    {
        log.info(step);
        Reporter.log(" - " + step + "<br>");
    }

    /**
     * [CUSTOM EXCEPTIONERROR]:
     * /*- This function is called by the WebDriverEventListener (errorListener),
     * /*- whenever an exception occurs (throwable) and is not caught by any of the SelUtil functions
     * //------------------------------------------------------------------------
     */
    public static void exceptionError(WebDriver driver, String msg, String throwable)
    {
        WebdriverManager.getErrorBuffer().append(throwable);
        String screenshotLink =
                " - <font color='red'>Exception Failure:</font>  <a href=" + captureScreen(
                        driver) + " target='_blank'>" + "[View Screenshot] </a> -- " + msg + "<br>";
        Reporter.log(screenshotLink);
        checkForVerificationErrors();
    }

    /**
     * [CUSTOM VERIFYTRUE]:
     * /*- Checks a boolean expression, and will log a defined message with screenshot
     * /*  if result is false (else do nothing)
     * /*---------------------------------------------------------------------------
     */
    public static void verifyTrue(WebDriver driver, Boolean b, String msg,
            boolean... optionalContinueTest)
    {
        try
        {
            Assert.assertTrue(b);
            Reporter.log("- Check point: " + msg + ": <font color='green'>Pass</font><br>");
        }
        catch (Exception e)
        {
            WebdriverManager.getErrorBuffer().append(e);
            Reporter.log(" - " + msg);
            String screenshotLink =
                    ": <font color='red'>FAIL:</font>  <a href=" + captureScreen(driver)
                            + " target='_blank'>" + "[View Screenshot]</a><br>";
            Reporter.log(screenshotLink);

            //Stop Test if optionalContinueTest is set to False
            if (optionalContinueTest.length > 0 && !optionalContinueTest[0])
            {
                checkForVerificationErrors();
            }
        }
    }

    /**
     * [CUSTOM VERIFYFALSE]:
     * /*- Checks a boolean expression, and will log a defined message with screenshot
     * /*  if result is true (else do nothing)
     * /*---------------------------------------------------------------------------
     */
    public static void verifyFalse(WebDriver driver, Boolean b, String msg,
            boolean... optionalContinueTest)
    {
        try
        {
            Assert.assertFalse(b);
            Reporter.log(" - " + msg + ": <font color='green'>Pass</font><br>");
        }
        catch (Exception e)
        {
            WebdriverManager.getErrorBuffer().append(e);
            Reporter.log(" - " + msg);
            String screenshotLink =
                    ": <font color='red'>FAIL:</font>  <a href=" + captureScreen(driver)
                            + " target='_blank'>" + "[View Screenshot] </a><br>";
            Reporter.log(screenshotLink);

            //Stop Test if optionalContinueTest is set to False
            if (optionalContinueTest.length > 0 && !optionalContinueTest[0])
            {
                checkForVerificationErrors();
            }
        }
    }

    public static void checkForVerificationErrors()
    {

        String verificationErrorString = WebdriverManager.getErrorBuffer().toString();

        WebdriverManager.setErrorBuffer();

        //Fail test case if needed
        if (!verificationErrorString.isEmpty())
        {
            log.info("Result: Failed");
            Assert.fail(verificationErrorString);
        }
        else
        {
            log.info("Result: Passed");
        }

    }

    /**
     * [CAPTURE SCREENSHOT]
     * /*- file is copied to pre-defined path (see Private String screenshotPath)
     * /*- Filename as date and timestamp
     * /*---------------------------------------------------------------------
     */
    private static synchronized String captureScreen(WebDriver driver, String... appName)
    {
        WebdriverManager.setErrorExpectedTrue();
        String path;

        try
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMdd(HH-mm-ss)");
            String dateString = dateFormat.format(Calendar.getInstance().getTime());
            if (appName.length > 0)
            {
                path = screenshotPath + appName[0] + ".png";
            }
            else
            {
                path = screenshotPath + dateString + ".png";
            }

            File source = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(source, new File(path));
        }
        catch (Exception e)
        {
            path = "Failed_To_Capture_Screenshot:_" + e.getMessage();
        }

        WebdriverManager.setErrorExpectedTrue();
        //Return the absolute path of the file for the html file to display correctly  
        return "../../../" + path;
    }

    public static void takeScreenshot(WebDriver driver, String description, String... appName)
    {
        if (appName.length > 0)
        {
            Reporter.log(
                    " - --- Verify screenshot of " + description + ". Click here: <a href="
                            + captureScreen(driver, appName[0]) + " target='_blank'>"
                            + "[View Screenshot]</a>" + "<br>");
        }
        else
        {
            Reporter.log(
                    " - --- Verify screenshot of " + description + ". Click here: <a href="
                            + captureScreen(driver) + " target='_blank'>" + "[View Screenshot]</a>"
                            + "<br>");
        }
    }

    //#END CLASS 
}