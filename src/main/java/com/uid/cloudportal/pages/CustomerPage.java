package com.uid.cloudportal.pages;

import com.uid.common.utils.SelCheckUtil;
import com.uid.common.utils.SelUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.uid.common.config.Verification;

public class CustomerPage
{
    final WebDriver driver;

    static final String TEXT_CONTENT_ATTR = "textContent";

    public CustomerPage(WebDriver driver)
    {
        this.driver = driver;
    }

    // [Injected label]
    @FindBy(xpath = "//div[starts-with(@id,'pingone-extension')]")
    WebElement lblBERunType;

    String lblBERunTypeXPath = "//div[starts-with(@id,'pingone-extension')]";

    // [Continue ay your own risk button]
    @FindBy(xpath = "//input[@type='button'][normalize-space(@value)='Continue at your own risk']")
    WebElement btnContinueAtYourOwnRisk;

    // [FD app - Select all checkbox]
    @FindBy(css = "table#tblLogs th#th-checkbox input")
    WebElement chkFDToolAll;

    // [FD app - Delete selected button]
    @FindBy(css = "div#navbar form.navbar-form button.btn-delete-selected")
    WebElement btnFDToolDeleteSelectedItems;

    @FindBy(css = "table#tblLogs tbody tr")
    WebElement tblFDToolAppData;

    public void deleteAllItemInFDTool()
    {
        waitForDataDisplayInFDTool();
        SelUtil.waitElementClickableAndClick(driver, chkFDToolAll);
        SelUtil.waitElementClickableAndClick(driver, btnFDToolDeleteSelectedItems);
        waitForDataClearInFDTool();
    }

    public String getBEPlayResult()
    {
        String rtnValue;

        if (SelCheckUtil.waitForCondition(driver,
                driver1 -> (!SelUtil.getAttribute(lblBERunType, TEXT_CONTENT_ATTR).isEmpty()), 60))
        {
            rtnValue = lblBERunType.getAttribute(TEXT_CONTENT_ATTR);
        }
		else if (driver.getPageSource().contains("This site can’t be reached")
					|| driver.getPageSource().contains("This site can’t provide a secure connection")
					|| driver.getPageSource().contains("This page isn’t working"))
		{
			rtnValue = "broken";
		}
        else
        {
            if (SelCheckUtil.isElementPresence(driver,
                    By.xpath(lblBERunTypeXPath), 3))
            {
                rtnValue = lblBERunType.getAttribute(TEXT_CONTENT_ATTR);
            }
            else
            {
                rtnValue = "noinject";
            }
        }

        Verification.logSubStep("BE Result: " + rtnValue);
        return rtnValue;
    }

    public String getBEVersion()
    {
        SelUtil.waitForElementPresence(driver,
                By.xpath(lblBERunTypeXPath), 20);
        if (SelUtil.getAttribute(lblBERunType, "version") != null)
        {
            return SelUtil.getAttribute(lblBERunType, "version");
        }
        return "null";
    }

    public String getBEEnv()
    {
        SelUtil.waitForElementPresence(driver,
                By.xpath(lblBERunTypeXPath), 20);
        if (SelUtil.getAttribute(lblBERunType, "env") != null)
        {
            return SelUtil.getAttribute(lblBERunType, "env");
        }
        return "null";
    }

    public boolean isBEPlayByTraining()
    {
        return SelCheckUtil.waitForCondition(driver,
                driver1 -> (SelUtil.getAttribute(lblBERunType, TEXT_CONTENT_ATTR)
                        .equals("clickUsingTraining")), 30);
    }

    public boolean isBEPlayByFD()
    {
        return SelCheckUtil.waitForCondition(driver,
                driver1 -> (SelUtil.getAttribute(lblBERunType, TEXT_CONTENT_ATTR)
                        .equals("clickUsingFD")), 30);
    }

    public boolean isBEPlay(String url)
    {
        boolean result = false;

        if (SelCheckUtil.waitForCondition(driver,
                driver1 -> (!SelUtil.getAttribute(lblBERunType, TEXT_CONTENT_ATTR).isEmpty()), 30))
        {
            result = true;
        }
        else if (!SelCheckUtil.isElementPresence(driver, By.xpath(lblBERunTypeXPath), 3))
        {
            String newUrl = SelUtil.getRedirectedURL(url);
            if (newUrl == null)
            {
                result = true;
            }
        }

        return result;
    }

    boolean passKMSSecurity()
    {
        if (SelCheckUtil.isElementVisibility(driver, btnContinueAtYourOwnRisk, 3))
        {
            Verification.logSubStep("By pass KMS Security");
            SelUtil.waitElementClickableAndClick(driver, btnContinueAtYourOwnRisk);
            SelCheckUtil.waitForElementChangeToInvisible(driver, btnContinueAtYourOwnRisk, 20);
            return true;
        }
        else
        {
            return false;
        }
    }

    private void waitForDataDisplayInFDTool()
    {
        SelCheckUtil.isElementVisibility(driver, tblFDToolAppData, 20);
    }

    private void waitForDataClearInFDTool()
    {
        SelCheckUtil.waitForElementChangeToInvisible(driver, tblFDToolAppData, 20);
    }

}
