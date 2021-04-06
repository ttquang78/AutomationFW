package com.uid.webportal.pages;

import com.uid.cloudportal.pages.NewCloudDesktopPage;
import com.uid.common.utils.ApplicationData;
import com.uid.common.utils.SelCheckUtil;
import com.uid.common.utils.SelUtil;
import com.uid.common.utils.SelWindowUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.uid.common.config.Setup;
import com.uid.common.config.Verification;

public class MyApplicationsSAMLPage
{

    final WebDriver driver;

    public MyApplicationsSAMLPage(WebDriver driver)
    {
        this.driver = driver;
    }

    // *********************************************************************//
    // WEB ELEMENTS                                                         //
    // *********************************************************************//

    // [App Edit button]
    @FindBy(xpath = "//form[@wicketpath='panel_wizardContainer_setupWizard_appDetailsForm']//input[@name='setupLink']")
    WebElement btnEditApp;

    // [Install BE label]
    @FindBy(id = "pluginNotYetInstalledContainer")
    WebElement txtInstallBE;

    // [Add Application]
    @FindBy(id = "addApplicationLabel")
    WebElement btnAddApplication;

    // [New Basic SSO Application]
    @FindBy(xpath = "//*[contains(text(), 'New Basic SSO Application')]")
    WebElement lnkNewBasicSSOApplication;

    // [Begin]
    @FindBy(xpath = "//span[contains(text(), 'Begin')]")
    WebElement btnBasic;

    // [Category]
    @FindBy(id = "category")
    WebElement lstCategory;

    // [Application Name]
    @FindBy(xpath = "//div[@id='pluginInstalledContainer']//div[label[text()='Application Name']]/input")
    WebElement txtAppName;

    // [Login URL]
    @FindBy(id = "loginUrl")
    WebElement txtLoginUrl;

    // [Wizard Save]
    @FindBy(id = "wizardSaveButton")
    WebElement btnWizardSaveButton;

    // [Delete]
    @FindBy(id = "confirmationDialogAction")
    WebElement btnConfirmationDialogAction;

    // [App Filter]
    @FindBy(id = "filterField")
    WebElement txtAppFilter;

    // [Show Advanced Configuration]
    @FindBy(xpath = "//*[contains(text(), 'Show Advanced Configuration')]")
    WebElement lnkShowAdvancedConfiguration;

    // [Json]
    @FindBy(name = "view:form:trainingJsonContainer:trainingJson")
    WebElement txtTrainingJsonContainer;

    // *********************************************************************//
    // ACTIONS                                                              //
    // *********************************************************************//

    private String getJsonTraining()
    {
        if (!SelCheckUtil.isElementVisibility(driver, txtTrainingJsonContainer, 5))
        {
            SelUtil.waitElementClickableAndClick(driver, lnkShowAdvancedConfiguration);
            SelCheckUtil.isElementVisibility(driver, txtTrainingJsonContainer);
        }

        if (Setup.getBrowserType().equalsIgnoreCase("edge"))
        {
            return SelUtil.getAttribute(txtTrainingJsonContainer, "value");
        }
        else
        {
            return SelUtil.getText(txtTrainingJsonContainer);
        }
    }

    private String getLoginUrl()
    {
        SelCheckUtil.isElementVisibility(driver, txtLoginUrl);

        if (Setup.getBrowserType().equalsIgnoreCase("edge"))
        {
            return SelUtil.getAttribute(txtLoginUrl, "value");
        }
        else
        {
            return SelUtil.getText(txtLoginUrl);
        }
    }

    private String generateAppRowXpath(String appName)
    {
        return "//table[@id='IdpMyApplicationsPanel']//tr[td[node()='" + appName + "']][td[node()='Basic SSO']]";
    }

    private String generateAppRemoveXpath(String appName)
    {
        return generateAppRowXpath(appName) + "//a[text()='Remove']";
    }

    private String generateAppShowDetailXpath(String appName)
    {
        return generateAppRowXpath(appName) + "//a[@title='Show application details']";
    }

    public boolean checkTrainedData(String originLoginURL)
    {
        boolean isCorrect = true;
        try
        {
            Verification.verifyTrue(driver, originLoginURL.contains(getLoginUrl()),
                    "Verification: Login URL is " + originLoginURL);

            String trainedJson = getJsonTraining();
            JSONObject trainedJSONObject = new JSONObject(
                    trainedJson.replaceAll("\\\\u0027", "'").replaceAll("\\\\u003d", "="));

            Verification.logSubStep("Verify format of trained JSON");
            Verification.verifyTrue(driver, trainedJSONObject.has(ApplicationData.JSON_USERNAME_KEY),
                    "--- Verification: Trained JSON contains 'learned_xpath_username'");
            Verification.verifyTrue(driver, (!trainedJSONObject.isNull(ApplicationData.JSON_USERNAME_KEY)),
                    "--- Verification: Trained JSON field 'learned_xpath_username' is not null");
            Verification.verifyTrue(driver,
                    (!trainedJSONObject.getString(ApplicationData.JSON_USERNAME_KEY).isEmpty()),
                    "--- Verification: Trained JSON field 'learned_xpath_username' is not empty");

            Verification.verifyTrue(driver, trainedJSONObject.has(ApplicationData.JSON_USERNAME_XPATH_KEY),
                    "--- Verification: Trained JSON contains 'learned_xpath_username_xpath'");
            Verification.verifyTrue(driver,
                    (!trainedJSONObject.isNull(ApplicationData.JSON_USERNAME_XPATH_KEY)),
                    "--- Verification: Trained JSON field 'learned_xpath_username_xpath' is not null");
            Verification.verifyTrue(driver,
                    (!trainedJSONObject.getString(ApplicationData.JSON_USERNAME_XPATH_KEY).isEmpty()),
                    "--- Verification: Trained JSON field 'learned_xpath_username_xpath' is not empty");

            Verification.verifyTrue(driver, trainedJSONObject.has(ApplicationData.JSON_DOC_ID_USERNAME),
                    "--- Verification: Trained JSON contains 'learned_doc_id_username'");
            Verification.verifyTrue(driver,
                    (!trainedJSONObject.isNull(ApplicationData.JSON_DOC_ID_USERNAME)),
                    "--- Verification: Trained JSON field 'learned_doc_id_username' is not null");
            Verification.verifyTrue(driver,
                    (!trainedJSONObject.getString(ApplicationData.JSON_DOC_ID_USERNAME).isEmpty()),
                    "--- Verification: Trained JSON field 'learned_doc_id_username' is not empty");

            Verification.verifyTrue(driver, trainedJSONObject.has(ApplicationData.JSON_PSWD_KEY),
                    "--- Verification: Trained JSON contains 'learned_xpath_password'");
            Verification.verifyTrue(driver, (!trainedJSONObject.isNull(ApplicationData.JSON_PSWD_KEY)),
                    "--- Verification: Trained JSON field 'learned_xpath_password' is not null");
            Verification.verifyTrue(driver,
                    (!trainedJSONObject.getString(ApplicationData.JSON_PSWD_KEY).isEmpty()),
                    "--- Verification: Trained JSON field 'learned_xpath_password' is not empty");

            Verification.verifyTrue(driver, trainedJSONObject.has(ApplicationData.JSON_PSWD_XPATH_KEY),
                    "--- Verification: Trained JSON contains 'learned_xpath_password_xpath'");
            Verification.verifyTrue(driver,
                    (!trainedJSONObject.isNull(ApplicationData.JSON_PSWD_XPATH_KEY)),
                    "--- Verification: Trained JSON field 'learned_xpath_password_xpath' is not null");
            Verification.verifyTrue(driver,
                    (!trainedJSONObject.getString(ApplicationData.JSON_PSWD_XPATH_KEY).isEmpty()),
                    "--- Verification: Trained JSON field 'learned_xpath_password_xpath' is not empty");

            Verification.verifyTrue(driver, trainedJSONObject.has(ApplicationData.JSON_DOC_ID_PSW),
                    "--- Verification: Trained JSON contains 'learned_doc_id_password'");
            Verification.verifyTrue(driver,
                    (!trainedJSONObject.isNull(ApplicationData.JSON_DOC_ID_PSW)),
                    "--- Verification: Trained JSON field 'learned_doc_id_password' is not null");
            Verification.verifyTrue(driver,
                    (!trainedJSONObject.getString(ApplicationData.JSON_DOC_ID_PSW).isEmpty()),
                    "--- Verification: Trained JSON field 'learned_doc_id_password' is not empty");

            Verification.verifyTrue(driver, trainedJSONObject.has(ApplicationData.JSON_SUBMIT_KEY),
                    "--- Verification: Trained JSON contains 'learned_xpath_click'");
            Verification.verifyTrue(driver, (!trainedJSONObject.isNull(ApplicationData.JSON_SUBMIT_KEY)),
                    "--- Verification: Trained JSON field 'learned_xpath_click' is not null");
            Verification.verifyTrue(driver,
                    (!trainedJSONObject.getString(ApplicationData.JSON_SUBMIT_KEY).isEmpty()),
                    "--- Verification: Trained JSON field 'learned_xpath_click' is not empty");

            Verification.verifyTrue(driver, trainedJSONObject.has(ApplicationData.JSON_SUBMIT_XPATH_KEY),
                    "--- Verification: Trained JSON contains 'learned_xpath_click_xpath'");
            Verification.verifyTrue(driver,
                    (!trainedJSONObject.isNull(ApplicationData.JSON_SUBMIT_XPATH_KEY)),
                    "--- Verification: Trained JSON field 'learned_xpath_click_xpath' is not null");
            Verification.verifyTrue(driver,
                    (!trainedJSONObject.getString(ApplicationData.JSON_SUBMIT_XPATH_KEY).isEmpty()),
                    "--- Verification: Trained JSON field 'learned_xpath_click_xpath' is not empty");

            Verification.verifyTrue(driver, trainedJSONObject.has(ApplicationData.JSON_DOC_ID_CLICK),
                    "--- Verification: Trained JSON contains 'learned_doc_id_click'");
            Verification.verifyTrue(driver, (!trainedJSONObject.isNull(ApplicationData.JSON_DOC_ID_CLICK)),
                    "--- Verification: Trained JSON field 'learned_doc_id_click' is not null");
            Verification.verifyTrue(driver,
                    (!trainedJSONObject.getString(ApplicationData.JSON_DOC_ID_CLICK).isEmpty()),
                    "--- Verification: Trained JSON field 'learned_doc_id_click' is not empty");
        }
        catch (JSONException e)
        {
            isCorrect = false;
        }

        return isCorrect;
    }

    private void clickWizardSave()
    {
        SelWindowUtil.scrollToElement(driver, btnWizardSaveButton);
        SelUtil.waitElementClickableAndClick(driver, btnWizardSaveButton);
    }

    public void clickAddApplication()
    {
        SelUtil.waitElementClickableAndClick(driver, btnAddApplication);
    }

    public void clickNewBasicSSOApp()
    {
        SelUtil.waitElementClickableAndClick(driver, lnkNewBasicSSOApplication);
    }

    private void modifyApp(String appName)
    {
        filterApp(appName);

        String appStr = generateAppShowDetailXpath(appName);

        if (SelCheckUtil.isElementVisibility(driver, By.xpath(appStr)))
        {
            SelUtil.waitElementClickableAndClick(driver, By.xpath(appStr));
            SelUtil.waitElementClickableAndClick(driver, btnEditApp);
        }
    }

    public boolean isAppExistingInTrainingList(String appName)
    {
        String appStr = generateAppRowXpath(appName);
        return SelCheckUtil.isElementVisibility(driver, By.xpath(appStr));
    }

    public boolean isRequestInstallBEMsgDisplay()
    {
        return SelCheckUtil.isElementVisibility(driver, txtInstallBE);
    }

    void removeApplication(String appName)
    {
        String appStr = generateAppRemoveXpath(appName);

        if (!SelCheckUtil.isElementVisibility(driver, By.xpath(appStr)))
        {
            filterApp(appName);
        }

        if (SelCheckUtil.isElementVisibility(driver, By.xpath(appStr)))
        {
            Verification.logSubStep("Remove app: " + appName);
            SelUtil.waitElementClickableAndClick(driver, By.xpath(appStr));
            SelUtil.waitElementClickableAndClick(driver, btnConfirmationDialogAction);
            SelCheckUtil.waitForElementChangeToInvisible(driver, btnConfirmationDialogAction);
            SelCheckUtil.waitForElementChangeToInvisible(driver, By.xpath(appStr));
            SelUtil.wait(3, "Test");
        }
    }

    public void saveTrainedData(NewCloudDesktopPage.Category appCat, String applicationName, String appLoginURL)
    {
        SelUtil.selectCategory(driver, lstCategory, appCat.getCat());
        SelUtil.sendKeyToElement(driver, txtAppName, applicationName);
        SelUtil.sendKeyToElement(driver, txtLoginUrl, appLoginURL);
        clickWizardSave();
        SelCheckUtil.waitForElementChangeToInvisible(driver, btnWizardSaveButton);
    }

    private void filterApp(String value)
    {
        if (SelCheckUtil.isElementVisibility(driver, txtAppFilter))
        {
            SelUtil.sendKeyToElement(driver, txtAppFilter, value);
        }
    }

    public void startBasicSSOTrainingWizard()
    {
        Verification.logSubStep("Start Basic SSO Training wizard");
        SelUtil.waitElementClickableAndClick(driver, btnAddApplication, 20);
        SelUtil.waitElementClickableAndClick(driver, lnkNewBasicSSOApplication);
        SelUtil.waitElementClickableAndClick(driver, btnBasic, 20);
    }

    public boolean waitForPageLoad()
    {
        return SelCheckUtil.isElementVisibility(driver, btnAddApplication, 20);
    }

    public void removeAppAndStartBasicSSOTraining(String appName)
    {
        removeApplication(appName);
        driver.navigate().refresh();
        startBasicSSOTrainingWizard();
    }

    private void setJsonTraining(String data)
    {
        Verification.logSubStep("Set configuration: " + data);
        SelUtil.sendKeyToElement(driver, txtTrainingJsonContainer, data);
    }

    void updateConfigurationAndLoginURL(String appName, String configuration, String loginURL)
    {
        if (!SelCheckUtil.isElementVisibility(driver, txtLoginUrl, 5))
        {
            modifyApp(appName);
        }

        if (!SelCheckUtil.isElementVisibility(driver, txtTrainingJsonContainer, 5))
        {
            SelUtil.waitElementClickableAndClick(driver, lnkShowAdvancedConfiguration);
            SelCheckUtil.isElementVisibility(driver, txtTrainingJsonContainer);
        }

        if (!loginURL.isEmpty())
        {
            SelUtil.sendKeyToElement(driver, txtLoginUrl, loginURL);
        }

        if (!configuration.isEmpty())
        {
            setJsonTraining(configuration);
        }
        clickWizardSave();
        SelCheckUtil.waitForElementChangeToInvisible(driver, txtLoginUrl, 20);
    }

}