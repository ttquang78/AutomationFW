package com.uid.cloudportal.pages;

import com.uid.common.config.Setup;
import com.uid.common.utils.SelCheckUtil;
import com.uid.common.utils.SelUtil;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.uid.common.config.Verification;

public class NewInputCredDialog
{
    final WebDriver driver;

    public NewInputCredDialog(WebDriver driver)
    {
        this.driver = driver;
    }

    //*********************************************************************//
    //                    WEB ELEMENTS                                     //
    //*********************************************************************//

    //[Username text field]
    @FindBy(css = "div.auth-dialog input[data-id='username-input']")
    WebElement txtUsername;

    //[Password text field]
    @FindBy(css = "div.auth-dialog input[data-id='pword-field-input']")
    WebElement txtPassword;

    //[Cancel button]
    @FindBy(css = "div.auth-dialog a")
    WebElement lnkCancel;

    //[Save button disabled]
    @FindBy(css = "div.auth-dialog input[class='primary'][disabled='']")
    WebElement btnSaveDisabled;

    //[Save button enabled]
    @FindBy(css = "div.auth-dialog input.primary")
    WebElement btnSaveEnabled;

    //*********************************************************************//
    //                    METHODS                                          //
    //*********************************************************************//

    public void closeInputCredDlg()
    {
        SelUtil.waitElementClickableAndClick(driver, lnkCancel);
    }

    public boolean isDisableSaveButtonDisplay()
    {
        return SelCheckUtil.isElementVisibility(driver, btnSaveDisabled);
    }

    public boolean isEnableSaveButtonDisplay()
    {
        return SelCheckUtil.isElementVisibility(driver, btnSaveEnabled);
    }

    public void fillUserName(String userNameValue)
    {
        SelUtil.sendKeyToElement(driver, txtUsername, userNameValue);
    }

    public void fillPassword(String passWordValue)
    {
        SelUtil.sendKeyToElement(driver, txtPassword, passWordValue);
    }

    /**
     * [LEARN CREDENTIALS then SAVE]
     * /* Enter username, password and then click Save button
     * /*---------------------------------------------------------------------------
     */
    public void inputCredentialsAndSave(String userName, String passWord)
    {
        if (waitDlgLoad(3))
        {
            Verification.logSubStep(
                    "Fill username: " + userName + " and password: " + passWord + ". Saved.");
            SelUtil.sendKeyToElement(driver, txtUsername, userName);
            SelUtil.sendKeyToElement(driver, txtPassword, passWord);
            SelUtil.waitElementClickableAndClick(driver, btnSaveEnabled);
        }
    }

    /**
     * [LEARN CREDENTIALS then CANCEL]
     * /* Enter username, password and then click Cancel button
     * /*---------------------------------------------------------------------------
     */
    public void inputCredentialsAndCancel(String userName, String passWord)
    {
        Verification.logSubStep("Step: Fill credential and click Cancel");

        SelUtil.sendKeyToElement(driver, txtUsername, userName);
        SelUtil.sendKeyToElement(driver, txtPassword, passWord);
        closeInputCredDlg();
    }

    public boolean waitDlgLoad()
    {
        return waitDlgLoad(Setup.DEFAULT_TIME_OUT);
    }

    public boolean waitDlgLoad(int second)
    {
        return SelCheckUtil.isElementVisibility(driver, lnkCancel, second);
    }

}
