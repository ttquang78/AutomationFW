package com.uid.cloudportal.pages;

import com.uid.common.utils.SelCheckUtil;
import com.uid.common.utils.SelWindowUtil;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.uid.common.utils.SelUtil;
import com.uid.common.config.Verification;

public class NewPKDlg
{
    final WebDriver driver;

    public NewPKDlg(WebDriver driver)
    {
        this.driver = driver;
    }

    //*********************************************************************//
    //                    WEB ELEMENTS                                     //
    //*********************************************************************//

    // [PK Dialog]
    @FindBy(css = "div.enter-key")
    WebElement dlgPrivacyKey;

    // [Clear All Password link]
    @FindBy(css = "div.enter-key a[data-id='action-btn']")
    WebElement lnkClearAllPassword;

    // [Privacy Key input]
    @FindBy(css = "div.enter-key input[data-id='form-text-field-input']")
    WebElement txtPrivacyKey;

    // [Disable Sign On button]
    @FindBy(css = "div.enter-key button[data-id='signOn'][disabled='']")
    WebElement btnDisableSignOn;

    // [Enable Sign On button]
    @FindBy(css = "div.enter-key button[data-id='signOn'][class='ellipsis-loader-button primary']")
    WebElement btnEnableSignOn;

    // [Cancel button]
    @FindBy(css = "div.enter-key a.cancel-link")
    WebElement lnkCancel;

    // [Confirm button]
    @FindBy(css = "div.enter-key div.confirm-delete-password div.details-content input")
    WebElement btnConfirm;

    // [Cancel link]
    @FindBy(css = "div.enter-key div.confirm-delete-password div.details-content a")
    WebElement lnkCancelConfirm;

    // [Error Icon]
    @FindBy(css = "div.enter-key div.input-message__icon")
    WebElement icoErrorMsg;

    // [Invalid Error Message]
    @FindBy(css = "div.enter-key div.input-message__text")
    WebElement lblErrorMsg;

    //*********************************************************************//
    //                    METHODS                                     //
    //*********************************************************************//

    public void clearAllPasswords()
    {
        Verification.logSubStep("Clearing privacy key");
        clickClearAllPasswords();
        clickConfirm();
    }

    public void clickClearAllPasswords()
    {
        SelWindowUtil.scrollToElement(driver, lnkClearAllPassword);
        SelUtil.waitElementClickableAndClick(driver, lnkClearAllPassword);
    }

    private void clickConfirm()
    {
        SelUtil.waitElementClickableAndClick(driver, btnConfirm);
    }

    private void clickSignOn()
    {
        SelUtil.waitElementClickableAndClick(driver, btnEnableSignOn);
    }

    /**
     * [CLOSEDLG]:
     * /*-------------------------------------------------------------------------------
     */
    public void closePKDlg()
    {
        Verification.logSubStep("Close PK dialog");
        SelUtil.waitElementClickableAndClick(driver, lnkCancel);
    }

    /**
     * [CLOSECONFIRMDLG]:
     * /*-------------------------------------------------------------------------------
     */
    public void closeConfirmDlg()
    {
        SelUtil.waitElementClickableAndClick(driver, lnkCancelConfirm);
    }

    public void fillPK(String key)
    {
        SelUtil.sendKeyToElement(driver, txtPrivacyKey, key);
    }

    public void fillPKAndSignOn(String key)
    {
        fillPK(key);
        clickSignOn();
    }

    public boolean isDisableSignOnButtonDisplay()
    {
        return SelCheckUtil.isElementVisibility(driver, btnDisableSignOn);
    }

    public boolean isInvalidErrorDisplay()
    {
        if (SelCheckUtil.isElementVisibility(driver, icoErrorMsg))
        {
            SelUtil.waitElementClickableAndClick(driver, icoErrorMsg);
        }
        return SelCheckUtil.isElementVisibility(driver, lblErrorMsg);
    }

    /**
     * [WAITPKDIALOGLOAD]:
     * /* -Wait for dialog to load by monitoring visibility of certain elements
     * /*-------------------------------------------------------------------------------
     */
    public boolean waitPKDlgLoad()
    {
        return SelCheckUtil.isElementVisibility(driver, lnkCancel);
    }

    public boolean waitPKDlgClose()
    {
        return SelCheckUtil.waitForElementChangeToInvisible(driver, dlgPrivacyKey, 20);
    }

    /**
     * [WAITCONFIRMDIALOGLOAD]:
     * /* -Wait for dialog to load by monitoring visibility of certain elements
     * /*-------------------------------------------------------------------------------
     */
    public boolean waitConfirmDlgLoad()
    {
        return SelCheckUtil.isElementVisibility(driver, btnConfirm);
    }

}
