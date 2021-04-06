package com.uid.webportal.pages;

import com.uid.common.utils.SelCheckUtil;
import com.uid.common.utils.SelUtil;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class CustomersPage
{

    final WebDriver driver;

    public CustomersPage(WebDriver driver)
    {
        this.driver = driver;
    }

    //*********************************************************************//
    //                    WEB ELEMENTS / METHODS                           //
    //*********************************************************************//

    @FindBy(xpath = "//a[@data-id='impersonate']")
    WebElement lnkImpersonate;

    @FindBy(xpath = "//button[@data-id='invite-customer-button']")
    WebElement btnInviteCustomer;

    @FindBy(xpath = "//a[@data-id='expand-btn']")
    WebElement btnExpand;

    @FindBy(xpath = "//div[@data-id='details-body']")
    WebElement dlgAccessConfirm;

    @FindBy(xpath = "//input[@data-id='confirm-action']")
    WebElement btnAccessAccount;

    @FindBy(xpath = "//button[@wicketpath='topRibbon_exitImpersonationMode']")
    WebElement btnExitImpersonate;

    //*********************************************************************//
    //                             FUNCTIONS                               //
    //*********************************************************************//

    public Boolean isCustomersDisplayed()
    {
        return SelCheckUtil.isElementVisibility(driver, btnInviteCustomer);
    }

    public void clickFirstRowDetail()
    {
        btnExpand.click();
        SelUtil.waitForElementVisibility(driver, lnkImpersonate);
    }

    public Boolean isExpandUserDetail()
    {
        return SelCheckUtil.isElementVisibility(driver, lnkImpersonate);
    }

    public void accessImpersonateMode()
    {
        SelUtil.waitElementClickableAndClick(driver, lnkImpersonate);
        SelUtil.waitForElementVisibility(driver, dlgAccessConfirm);
        SelUtil.waitElementClickableAndClick(driver, btnAccessAccount);
        SelUtil.waitForElementVisibility(driver, btnExitImpersonate);
    }
    //*********
    //END CLASS
}
