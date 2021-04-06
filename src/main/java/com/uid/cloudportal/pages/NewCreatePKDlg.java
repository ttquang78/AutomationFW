package com.uid.cloudportal.pages;

import com.uid.common.utils.SelCheckUtil;
import com.uid.common.utils.SelUtil;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class NewCreatePKDlg
{
    final WebDriver driver;

    public NewCreatePKDlg(WebDriver driver)
    {
        this.driver = driver;
    }

    //*********************************************************************//
    //                    WEB ELEMENTS                                     //
    //*********************************************************************//

    // [Create PK Dialog]
    @FindBy(css = "div.create-privacy-key")
    WebElement dlgCreatePrivacyKey;

    // [PK input]
    @FindBy(css = "div.create-privacy-key input[data-id='form-text-field-input'][placeholder='Enter Privacy Key (min 8 characters)']")
    WebElement txtEnterPK;

    // [Confirm Privacy Key input]
    @FindBy(css = "div.create-privacy-key input[data-id='form-text-field-input'][placeholder='Confirm Privacy Key']")
    WebElement txtConfirmPrivacyKey;

    // [Disable Continue button]
    @FindBy(css = "div.create-privacy-key button[data-id='signOn'][disabled='']")
    WebElement btnDisableContinue;

    // [Enable Continue button]
    @FindBy(css = "div.create-privacy-key button[data-id='signOn'][class='ellipsis-loader-button primary']")
    WebElement btnEnableContinue;

    // [Validate icon]
    @FindBy(css = "div.create-privacy-key span[class='validate success']")
    WebElement lblValidate;

    // [Cancel button]
    @FindBy(css = "div.create-privacy-key a.cancel-link")
    WebElement lnkCancel;

    //*********************************************************************//
    //                    ACTIONS                                          //
    //*********************************************************************//

    public void clickContinue()
    {
        SelUtil.waitElementClickableAndClick(driver, btnEnableContinue);
    }

    public void closeCreatePKPopup()
    {
        SelUtil.waitElementClickableAndClick(driver, lnkCancel);
    }

    public void createPrivacyKey(String key)
    {
        SelUtil.sendKeyToElement(driver, txtEnterPK, key);
        SelUtil.sendKeyToElement(driver, txtConfirmPrivacyKey, key);
        SelUtil.waitElementClickableAndClick(driver, btnEnableContinue);
    }

    public void fillPK(String key)
    {
        SelUtil.sendKeyToElement(driver, txtEnterPK, key);
    }

    public void fillConfirmPK(String key)
    {
        SelUtil.sendKeyToElement(driver, txtConfirmPrivacyKey, key);
    }

    public boolean isDisableContinueButtonDisplay()
    {
        return SelCheckUtil.isElementVisibility(driver, btnDisableContinue);
    }

    public boolean isEnableContinueButtonDisplay()
    {
        return SelCheckUtil.isElementVisibility(driver, btnEnableContinue);
    }

    public boolean isValidateIconDisplay()
    {
        return SelCheckUtil.isElementVisibility(driver, lblValidate);
    }

    public boolean waitDlgClose()
    {
        return SelCheckUtil.waitForElementChangeToInvisible(driver, dlgCreatePrivacyKey, 20);
    }

    public boolean waitDlgLoad()
    {
        return SelCheckUtil.isElementVisibility(driver, lnkCancel, 30);
    }

}
