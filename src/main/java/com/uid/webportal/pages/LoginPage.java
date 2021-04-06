package com.uid.webportal.pages;

import com.uid.common.utils.SelCheckUtil;
import org.openqa.selenium.*;
import org.openqa.selenium.support.*;

import com.uid.common.utils.SelUtil;
import com.uid.common.config.Verification;

public class LoginPage
{

    final WebDriver driver;

    public LoginPage(WebDriver driver)
    {
        this.driver = driver;
    }

    //*********************************************************************//
    //                    WEB ELEMENTS / METHODS                           //
    //*********************************************************************//

    //[Sign On]
    @FindBy(id = "signOn")
    WebElement btnSignIn;

    //[Username]
    @FindBy(id = "email")
    WebElement txtUsername;

    //[Password]
    @FindBy(id = "password")
    WebElement txtPassword;

    @FindBy(id = "otp")
    WebElement txtInputCode;

    @FindBy(xpath = "//*[@id='otp-form']/div[2]/input")
    WebElement btnSignON;

    //*********************************************************************//
    //                             FUNCTIONS                               //
    //*********************************************************************//

    //-------------------------------------------------------------------------------
    //[LOGIN]:
    //-Type in username/password, and click Sign in button
    //-------------------------------------------------------------------------------
    public void login(String userName, String passWord)
    {
        if (SelCheckUtil.isElementVisibility(driver, txtUsername))
        {
            Verification.logSubStep("Login to Web Portal by " + userName);
            SelUtil.sendKeyToElement(driver, txtUsername, userName);
            SelUtil.sendKeyToElement(driver, txtPassword, passWord);
            SelUtil.waitElementClickableAndClick(driver, btnSignIn);
        }
    }

    //-------------------------------------------------------------------------------
    //[WAITPAGELOAD]:
    //-Wait for page to load by monitoring visibility of certain elements
    //-------------------------------------------------------------------------------
    public boolean waitPageLoad()
    {
        return (SelCheckUtil.isElementVisibility(driver, txtUsername)
                && SelCheckUtil.isElementVisibility(driver, txtPassword)
                && SelCheckUtil.isElementVisibility(driver, btnSignIn));
    }

    public void verifyLoginStep2(String code)
    {
        SelUtil.sendKeyToElement(driver, txtInputCode, code);
        SelUtil.waitElementClickableAndClick(driver, btnSignON);
    }

    public boolean isVerifyStep2()
    {
        return SelCheckUtil.isElementVisibility(driver, txtInputCode);
    }
    //*********
    //END CLASS
}
