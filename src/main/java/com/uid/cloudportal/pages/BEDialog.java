package com.uid.cloudportal.pages;

import com.uid.common.utils.SelCheckUtil;
import com.uid.common.utils.SelUtil;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.uid.common.config.Verification;

public class BEDialog
{

    final WebDriver driver;

    public BEDialog(WebDriver driver)
    {
        this.driver = driver;
    }

    //*********************************************************************//
    //                    WEB ELEMENTS / METHODS                           //
    //*********************************************************************//

    // [Skip link]
    @FindBy(id = "finish-plugin-modal")
    WebElement lnkSkipInstall;

    // [Dialog Content]
    @FindBy(xpath = "//div[@id='pingone-dialog-authentication']//div[@class='pingone-header-text']")
    WebElement dlgAuthenticationHeader;

    // [Dialog Error Message]
    @FindBy(css = "div.pingone-error-message")
    WebElement dlgPrivacyKeyErrorMsg;

    // [PrivacyKey text-input]
    @FindBy(css = "FORM.pingone-form-enter-privacy-key INPUT")
    WebElement txtPrivacyKey;

    // [Save / Sign In button]
    @FindBy(css = "FORM.pingone-form-enter-privacy-key BUTTON")
    WebElement btnInputPKSave;

    // [Confirm delete dialog]
    @FindBy(id = "pingone-pm-detail-link1")
    WebElement lnkClearPKey;

    // [PrivacyKey]
    @FindBy(css = "FORM.pingone-form-create-privacy-key INPUT[placeholder='Enter Privacy Key']")
    WebElement txtMainPrivacyKey;

    // [PrivacyKey Again text-input]
    @FindBy(css = "FORM.pingone-form-create-privacy-key INPUT[placeholder='Confirm Privacy Key']")
    WebElement txtPivacyKeyConfirm;

    // [Save / Sign In button]
    @FindBy(css = "FORM.pingone-form-create-privacy-key BUTTON")
    WebElement btnNewPKSave;

    // [Dialog Content]
    @FindBy(id = "pingone-dialog-authentication")
    WebElement dlgConfirmPrivacyKey;

    // [Close X button]
    @FindBy(xpath = "//div[@id='pingone-dialog-authentication']//div[@class='pingone-header']//span[contains(@class, 'pingone-icon-close')]")
    WebElement btnClose;

    // [Confirm Privacy Key text field]
    @FindBy(xpath = "//div[@id='pingone-dialog-authentication']//input[@type='password']")
    WebElement txtConfirmPrivacyKey;

    // [Confirm Privacy Key button]
    @FindBy(xpath = "//div[@id='pingone-dialog-authentication']//button[@type='submit']")
    WebElement btnConfirmPrivacyKey;

    @FindBy(xpath = "//form[@class='pingone-form-reset-privacy-key']/button[@type='submit']")
    WebElement btnConfirm;

    @FindBy(xpath = "//form[@class='pingone-form-reset-privacy-key']/button[@type='reset']")
    WebElement btnCancel;

    //*********************************************************************//
    //                    ACTIONS                                          //
    //*********************************************************************//

    public void clickSkipInstall()
    {
        SelUtil.waitElementClickableAndClick(driver, lnkSkipInstall);
    }

    void reloadPage()
    {
        for (int i = 0; i < 3; i++)
        {
            if (SelCheckUtil.isElementVisibility(driver, lnkSkipInstall, 5))
            {
                driver.navigate().refresh();
            }
            else
            {
                break;
            }
        }
    }

    public boolean waitBEPopupLoad()
    {
        return SelCheckUtil.isElementVisibility(driver, lnkSkipInstall);
    }

    public boolean waitBEPopupUnLoad()
    {
        return SelCheckUtil.waitForElementChangeToInvisible(driver, lnkSkipInstall);
    }

    public String getAuthenticationHeader()
    {
        return SelUtil.getText(dlgAuthenticationHeader);
    }

    public String getDialogErrorMsg()
    {
        SelCheckUtil.isTextDisplayOnElement(driver, dlgPrivacyKeyErrorMsg, "That is not your privacy key. Please try again.");
        return SelUtil.getText(dlgPrivacyKeyErrorMsg);
    }

    public boolean waitForConfirmClearPKDlgLoad()
    {
        return SelCheckUtil.isElementVisibility(driver, dlgAuthenticationHeader);
    }

    public boolean isClearPKDisplay()
    {
        return SelCheckUtil.isElementVisibility(driver, lnkClearPKey);
    }

    private void setInputPKPrivacyKey(String privacyKey)
    {
        SelUtil.sendKeyToElement(driver, txtPrivacyKey, privacyKey);
    }

    public void clickInputPKSave()
    {
        SelUtil.waitElementClickableAndClick(driver, btnInputPKSave);
    }

    public void clickClearPKeyLink()
    {
        Verification.logStep("Click Clear PK link");
        SelUtil.waitElementClickableAndClick(driver, lnkClearPKey);
    }

    /**
     * [SIGNINYKEY:] /* -Workflow to signin with an existing key when the
     * overlay displays
     * /*-----------------------------------------------------------
     */
    public void signInKey(String key)
    {
        Verification.logSubStep("Input PK: " + key);
        setInputPKPrivacyKey(key);
        clickInputPKSave();
    }

    public boolean isCreatePKDisplayed()
    {
        return SelCheckUtil.isElementVisibility(driver, txtPivacyKeyConfirm, 20);
    }

    public void setMainPrivacyKey(String privacyKey)
    {
        SelUtil.sendKeyToElement(driver, txtMainPrivacyKey, privacyKey);
    }

    public void setConfirmPrivacyKey(String privacyKey)
    {
        SelUtil.sendKeyToElement(driver, txtPivacyKeyConfirm, privacyKey);
    }

    public void clickNewPKSave()
    {
        SelUtil.waitElementClickableAndClick(driver, btnNewPKSave);
    }

    public void clearPrivacyKeyFields()
    {
        SelCheckUtil.isElementVisibility(driver, txtMainPrivacyKey);
        txtMainPrivacyKey.clear();
        SelCheckUtil.isElementVisibility(driver, txtPivacyKeyConfirm);
        txtPivacyKeyConfirm.clear();
    }

    private void clearPrivacyKey()
    {
        Verification.logSubStep("Clear PK");
        clickClearPKeyLink();
        clickConfirmClear();
    }

    /**
     * [CREATEPRIVACYKEY:] /* -Workflow to create a privacy key when the overlay
     * displays /*-----------------------------------------------------------
     */
    public void createPrivacyKey(String key)
    {
        Verification.logSubStep("Create PK with key: " + key);

        setMainPrivacyKey(key);
        setConfirmPrivacyKey(key);
        clickNewPKSave();
    }

    public boolean isConFirmPrivacyKeyDisplay()
    {
        return SelCheckUtil.isElementVisibility(driver, dlgConfirmPrivacyKey, 3);
    }

    public void fillConfirmPrivacyKeyInput(String value)
    {
        SelUtil.sendKeyToElement(driver, txtConfirmPrivacyKey, value);
    }

    public void clickConfirmPrivacyKeyButton()
    {
        SelUtil.waitElementClickableAndClick(driver, btnConfirmPrivacyKey);
    }

    public void closeConfirmPrivacyKeyByClickOnCloseIcon()
    {
        SelUtil.waitElementClickableAndClick(driver, btnClose);
    }

    public boolean isInvalidErrorDisplay()
    {
        return SelCheckUtil.isElementVisibility(driver, dlgPrivacyKeyErrorMsg, 3);
    }

    public void clickConfirmClear()
    {
        Verification.logStep("Click Confirm");
        SelUtil.waitElementClickableAndClick(driver, btnConfirm);
    }

    public void clickCancelClear()
    {
        SelUtil.waitElementClickableAndClick(driver, btnCancel);
    }

    /**
     * [WAITCONFIRMDIALOG] /*--------------------------
     */
    public void waitConfirmDialog()
    {
        SelCheckUtil.isElementVisibility(driver, btnConfirm);
    }

    /**
     * [PASSPKEYOVERLAY:] /* - Wait for overlay, if exist pick appropriate
     * workflow /*-----------------------------------------------------------
     */
    public void passPKeyOverlay(String key)
    {
        Verification.logSubStep("Fill PK (opt)");
        if (SelCheckUtil.isElementVisibility(driver, dlgAuthenticationHeader, 5))
        {
            // Key never been created
            if (SelCheckUtil.isElementVisibility(driver, txtPivacyKeyConfirm))
            {
                createPrivacyKey(key);
            }
            else// Sign in with existing key
            {
                signInKey(key);
                if (SelCheckUtil.isElementVisibility(driver, dlgPrivacyKeyErrorMsg))
                {
                    clearPrivacyKey();
                    createPrivacyKey(key);
                }
            }
        }
    }

    // *********
    // END CLASS
}
