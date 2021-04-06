package com.uid.webportal.pages;

import com.uid.common.utils.SelCheckUtil;
import com.uid.common.utils.SelUtil;
import com.uid.common.utils.SelWindowUtil;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.uid.common.config.Verification;

public class SetupDockPage
{
    final WebDriver driver;

    public SetupDockPage(WebDriver driver)
    {
        this.driver = driver;
    }

    //*********************************************************************//
    //  WEB ELEMENTS / METHODS                                             //
    //*********************************************************************//

    //[UpgradeDock]
    @FindBy(xpath = "//div[@class='dock-settings']//input[@value='Upgrade Dock']")
    WebElement txtUpgradeDock;

    //[Save]
    @FindBy(xpath = "//button[@data-id='save']")
    WebElement btnSave;

    //[Dock link]
    @FindBy(xpath = "//a[@data-id='currentDockUrl']")
    WebElement lnkDockLink;

    @FindBy(xpath = "//a[text()='click here']")
    WebElement lnkClickHere;

    @FindBy(css = "span.icon-help-rounded")
    WebElement lnkDockUrlHelp;

    @FindBy(xpath = "//input[@value='I Understand']")
    WebElement txtIUnderstand;

    //*********************************************************************//
    //  FUNCTIONS                                                          //
    //*********************************************************************//

    public void clickDockLink()
    {
        SelWindowUtil.scrollToTop(driver);
        SelUtil.waitElementClickableAndClick(driver, lnkDockLink);
    }

    //-------------------------------------------------------------------------------
    //[SwitchToLegacy]:
    //-Wait for page to load by monitoring visibility of certain elements
    //-------------------------------------------------------------------------------
    public void switchToLegacy()
    {
        Verification.logSubStep("Revert to legacy dock");
        SelUtil.waitElementClickableAndClick(driver, lnkDockUrlHelp);
        SelUtil.waitElementClickableAndClick(driver, lnkClickHere);
        SelUtil.waitElementClickableAndClick(driver, txtIUnderstand);
        SelUtil.waitElementClickableAndClick(driver, btnSave);
        SelCheckUtil.isElementVisibility(driver, txtUpgradeDock);
    }

    //-------------------------------------------------------------------------------
    //[UpgradeDock]:
    //-Wait for page to load by monitoring visibility of certain elements
    //-------------------------------------------------------------------------------
    public void upgradeDock()
    {
        if (SelCheckUtil.isElementVisibility(driver, txtUpgradeDock))
        {
            Verification.logSubStep("Upgrade dock");
            SelUtil.waitElementClickableAndClick(driver, txtUpgradeDock);
            SelUtil.waitElementClickableAndClick(driver, btnSave);
            SelCheckUtil.waitForElementChangeToInvisible(driver, txtUpgradeDock, 5);
        }
    }

    public boolean waitPageLoad()
    {
        return SelCheckUtil.isElementVisibility(driver, btnSave, 5);
    }

}
