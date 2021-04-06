package com.uid.cloudportal.pages;

import com.uid.common.utils.SelCheckUtil;
import com.uid.common.utils.SelUtil;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.uid.common.config.Verification;

public class NewConfirmPKDialog
{
    final WebDriver driver;

    public NewConfirmPKDialog(WebDriver driver)
    {
        this.driver = driver;
    }

    //*********************************************************************//
    //                    WEB ELEMENTS                                     //
    //*********************************************************************//

    // [Confirm privacy key dialog]
    @FindBy(css = "div.confirm-privacykey-modal")
    WebElement dlgConfirmPrivacyKey;

    // [Close X button]
    @FindBy(css = "div.confirm-privacykey-modal span.close-modal")
    WebElement btnClose;

    // [Confirm Privacy Key text field]
    @FindBy(css = "div.confirm-privacykey-modal input[data-id='form-text-field-input']")
    WebElement txtConfirmPK;

    // [Confirm Privacy Key button]
    @FindBy(css = "div.confirm-privacykey-modal button[data-id='confirm']")
    WebElement btnConfirm;

    // [Cancel Privacy Key link]
    @FindBy(css = "div.confirm-privacykey-modal a.cancel-link")
    WebElement lnkCancel;

    // [Invalid Error Icon]
    @FindBy(css = "div.confirm-privacykey-modal div[data-id='form-text-field-error-message-icon']")
    WebElement icoErrorMsg;

    // [Invalid Error Message]
    @FindBy(css = "div.confirm-privacykey-modal div[data-id='form-text-field-error-message']")
    WebElement lblErrorMsg;

    //*********************************************************************//
    //                    ACTIONS                                          //
    //*********************************************************************//

    public void clickCancel()
    {
        Verification.logSubStep("Cancel Confirm Privacy Key dialog");
        SelUtil.waitElementClickableAndClick(driver, lnkCancel);
    }

    private void clickConfirm()
    {
        SelUtil.waitElementClickableAndClick(driver, btnConfirm);
    }

    public void closeConfirmPKDlg()
    {
        Verification.logSubStep("Close Confirm Privacy Key dialog");
        SelUtil.waitElementClickableAndClick(driver, btnClose);
    }

    private void fillPrivacyKey(String value)
    {
        SelUtil.sendKeyToElement(driver, txtConfirmPK, value);
    }

    public void fillPKAndConfirm(String pKey)
    {
        Verification.logSubStep("Fill valid privacy key");
        fillPrivacyKey(pKey);
        clickConfirm();
    }

    public Boolean isInvalidErrorDisplay()
    {
        SelUtil.waitElementClickableAndClick(driver, icoErrorMsg);
        return SelCheckUtil.isElementVisibility(driver, lblErrorMsg);
    }

    public boolean waitDlgClose()
    {
        return SelCheckUtil.waitForElementChangeToInvisible(driver, dlgConfirmPrivacyKey, 20);
    }

    public boolean waitDlgLoad()
    {
        return SelCheckUtil.isElementVisibility(driver, lnkCancel);
    }

}
