package com.uid.cloudportal.pages;

import com.uid.common.utils.SelCheckUtil;
import com.uid.common.utils.SelUtil;
import com.uid.common.utils.SelWindowUtil;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.uid.common.config.Verification;
import com.uid.common.config.WebdriverManager;

public class ManagePasswordsDialog
{
    private static Logger log = Logger.getLogger(ManagePasswordsDialog.class.getName());

    final WebDriver driver;

    public ManagePasswordsDialog(WebDriver driver)
    {
        this.driver = driver;
    }

    // *********************************************************************//
    // WEB ELEMENTS / METHODS //
    // *********************************************************************//

    @FindBy(id = "pingone-dialog-credentials")
    WebElement dlgManagePasswords;

    // [Close button (x)]
    @FindBy(xpath = "//div[@id='pingone-dialog-credentials']//div[@class='pingone-header']//span[contains(@class, 'pingone-icon-close')]")
    WebElement btnClosex;

    // [username textfield]
    @FindBy(css = "div#pingone-dialog-credentials div.pingone-body form.pingone-form-edit-credentials input[placeholder='Username']")
    WebElement txtUsername;

    // [password textfield]
    @FindBy(css = "div#pingone-dialog-credentials div.pingone-body form.pingone-form-edit-credentials input[placeholder='Password']")
    WebElement txtPassword;

    // [Show password link]
    @FindBy(id = "pingone-show-password")
    WebElement lblShowMyPassword;

    // [Remove Application link]
    @FindBy(id = "pingone-button-remove-credential")
    WebElement lnkRemoveApplication;

    // [Remove button]
    @FindBy(css = "div#pingone-dialog-credentials div.pingone-body form.pingone-form-delete-credentials button[type='submit']")
    WebElement btnRemove;

    // [Cancel button]
    @FindBy(css = "div#pingone-dialog-credentials div.pingone-body form.pingone-form-delete-credentials button[type='reset']")
    WebElement btnCancel;

    // [Save button]
    @FindBy(css = "div#pingone-dialog-credentials div.pingone-body form.pingone-form-edit-credentials button[type='submit']")
    WebElement btnSaveAppSecret;

    // [Privacy Key form]
    @FindBy(css = "form.pingone-form-edit-privacy-key")
    WebElement pnlPrivacyKey;

    // [New Privacy Key]
    @FindBy(id = "pingone-privacy-key-new")
    WebElement txtNewPrivacyKey;

    // [Confirm Privacy Key]
    @FindBy(id = "pingone-privacy-key-new-confirm")
    WebElement txtConfirmPrivacyKey;

    // [Save (key)]
    @FindBy(css = "div#pingone-dialog-credentials div.pingone-body form.pingone-form-edit-privacy-key button[type='submit']")
    WebElement btnSavePrivacyKey;

    // [Confirmation message]
    @FindBy(css = "div#pingone-dialog-credentials div.pingone-status-bar > div")
    WebElement lblPrivacyKeyStatus;

    private String generateAppXpath(String appName)
    {
        return "//div[@id='pingone-dialog-credentials']//div[@class='pingone-body']//div[@class='pingone-aside']//a[contains(text(), '"
                + appName + "')]";
    }

    // *********************************************************************//
    // ACTIONS
    // *********************************************************************//

    public void closeDialog()
    {
        Verification.logSubStep("Close Manage Password dialog");
        SelUtil.waitElementClickableAndClick(driver, btnClosex);
        SelCheckUtil.waitForElementChangeToInvisible(driver, dlgManagePasswords);
    }

    public String getAppUsername()
    {
        return txtUsername.getAttribute("value");
    }

    public String getAppPassword()
    {
        SelCheckUtil.isElementVisibility(driver, txtPassword);
        return txtPassword.getAttribute("value");
    }

    public void showPassword()
    {
        SelUtil.waitElementClickableAndClick(driver, lblShowMyPassword);
    }

    public void selectItemInManagePassword(String name)
    {
        SelUtil.waitElementClickableAndClick(driver, By.linkText(name));
    }

    public String getPrivacyKeyStatus()
    {
        SelCheckUtil.isElementVisibility(driver, lblPrivacyKeyStatus);
        return SelUtil.getText(lblPrivacyKeyStatus);
    }

    public void inputPK(String value)
    {
        if (value.isEmpty())
        {
            txtNewPrivacyKey.clear();
        }
        else
        {
            SelUtil.sendKeyToElement(driver, txtNewPrivacyKey, value);
        }
    }

    public void inputConfirmPK(String value)
    {
        if (value.isEmpty())
        {
            txtConfirmPrivacyKey.clear();
        }
        else
        {
            SelUtil.sendKeyToElement(driver, txtConfirmPrivacyKey, value);
        }
    }

    public void clickSavePK()
    {
        SelUtil.waitElementClickableAndClick(driver, btnSavePrivacyKey);
    }

    public boolean isPKSavingDisplay()
    {
        return SelCheckUtil.isTextDisplayOnElement(driver, lblPrivacyKeyStatus, "Saving new privacy key...");
    }

    public boolean isPKSavedDisplay()
    {
        return SelCheckUtil.isTextDisplayOnElement(driver, lblPrivacyKeyStatus, "New privacy key saved!", 30);
    }

    public boolean isPKStatusInvisibility()
    {
        return SelCheckUtil.waitForElementChangeToInvisible(driver, lblPrivacyKeyStatus, 30);
    }

    public boolean isPKFormDisplay()
    {
        return SelCheckUtil.isElementVisibility(driver, pnlPrivacyKey);
    }

    public boolean isSaveButtonIsDisabled()
    {
        return btnSavePrivacyKey.isEnabled();
    }

    /**
     * [FINDAPP] /* -Specify app name to find and return element
     * /*-----------------------------------------------------------------------
     * ----
     */
    public WebElement findApp(String appName)
    {
        WebdriverManager.setErrorExpectedTrue();
        try
        {
            String sidePanelXpath = generateAppXpath(appName);

            WebElement item = SelUtil.waitForElementVisibility(driver, By.xpath(sidePanelXpath));

            WebdriverManager.setErrorExpectedFalse();
            return item;
        }
        catch (Exception e)
        {
            log.error(e.getMessage());
            WebdriverManager.setErrorExpectedFalse();
            return null;
        }

    }

    /**
     * [REMOVEAPPSECRET] /* -Specify app name to select and remove
     * /*--------------------------------------------------------------------
     */
    public void removeAppSecret(String appName)
    {
        WebElement app = findApp(appName);
        if (app != null)
        {
            SelWindowUtil.scrollToElement(driver, app);

            SelUtil.waitElementClickableAndClick(driver, app);
            SelUtil.waitElementClickableAndClick(driver, lnkRemoveApplication);
            SelUtil.waitElementClickableAndClick(driver, btnRemove);

            SelCheckUtil.waitForElementChangeToInvisible(driver, btnRemove);
        }
    }

    /**
     * [CANCELREMOVEAPPSECRET] /* -Specify app name to select and remove
     * /*--------------------------------------------------------------------
     */
    public void cancelRemoveAppSecret(String appName)
    {
        WebElement app = findApp(appName);
        if (app != null)
        {
            SelUtil.waitElementClickableAndClick(driver, app);
            SelUtil.waitElementClickableAndClick(driver, lnkRemoveApplication);
            SelUtil.waitElementClickableAndClick(driver, btnCancel);
        }
    }

    /**
     * [UPDATEAPPSECRET] /* -Specify app name to select
     * /*-------------------------------------------------------------------
     */
    public void updateAppSecret(String userName, String password)
    {
        if (!userName.isEmpty())
        {
            SelUtil.sendKeyToElement(driver, txtUsername, userName);
        }
        if (!password.isEmpty())
        {
            SelUtil.sendKeyToElement(driver, txtPassword, password);
        }

        SelUtil.waitElementClickableAndClick(driver, btnSaveAppSecret);
    }

    /**
     * [UPDATEPRIVACYKEY] /* -Specify app name to select
     * /*-------------------------------------------------------------------
     */
    public void updatePrivacyKey(String privacyKey)
    {
        Verification.logSubStep("Update PK to " + privacyKey);

        selectItemInManagePassword("Privacy Key");
        SelUtil.sendKeyToElement(driver, txtNewPrivacyKey, privacyKey);
        SelUtil.sendKeyToElement(driver, txtConfirmPrivacyKey, privacyKey);

        SelUtil.waitElementClickableAndClick(driver, btnSavePrivacyKey);
    }

    public boolean isManagePasswordDisplay()
    {
        return SelCheckUtil.isElementVisibility(driver, dlgManagePasswords, 5);
    }

    // *********
    // END CLASS
}
