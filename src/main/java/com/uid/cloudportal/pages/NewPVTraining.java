package com.uid.cloudportal.pages;

import com.uid.common.base.CommonMessage;
import com.uid.common.config.Setup;
import com.uid.common.config.Verification;
import com.uid.common.utils.ApplicationData;
import com.uid.common.utils.SelCheckUtil;
import com.uid.common.utils.SelUtil;
import com.uid.common.utils.SelWindowUtil;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class NewPVTraining extends NewPVExtension
{
    private static final String USERNAME_COVER = "username";
    private static final String PASS_COVER = "password";
    private static final String THIRDFIELD_COVER = "thirdfield";
    private static final String SUBMIT_COVER = "submit";
    
    public NewPVTraining(WebDriver driver)
    {
        super(driver);
    }

    //*********************************************************************//
    //                    WEB ELEMENTS                                     //
    //*********************************************************************//

    // [PV popup title]
    @FindBy(css = "div#pingone-dialog-overlay div.pingone-header div.pingone-header-middle div.pingone-header-text")
    WebElement lblTitle;

    // [Username cover]
    @FindBy(xpath = "//div[@class='pingone-field-marker'][text()='Username Field']")
    WebElement lblUsernameCover;

    // [Password cover]
    @FindBy(xpath = "//div[@class='pingone-field-marker'][text()='Password Field']")
    WebElement lblPasswordCover;

    // [Submit cover]
    @FindBy(xpath = "//div[@class='pingone-field-marker'][text()='Submit Button']")
    WebElement lblSubmitCover;

    // [URL textbox of PV popup]
    @FindBy(id = "pingone-dialog-text")
    WebElement txtURL;

    // [Training Help popup]
    @FindBy(id = "pingone-dialog-help")
    WebElement dlgTrainingHelp;

    // [title of Training Help popup]
    @FindBy(css = "div#pingone-dialog-help div.pingone-header-text")
    WebElement lblTrainingHelpTitle;

    // [Close icon of Training Help popup]
    @FindBy(css = "div#pingone-dialog-help span.pingone-icon-close")
    WebElement icoCloseTrainingHelp;

    // [Content of Training Help popup]
    @FindBy(css = "div#pingone-dialog-help div.pingone-body")
    WebElement lblTrainingHelpContent;

    // [Okay button]
    @FindBy(css = "div#pingone-dialog-help button.pingone-button-close")
    WebElement btnOkay;

    //*********************************************************************//
    //                    METHODS                                          //
    //*********************************************************************//

    public void checkGoToApplicationStep()
    {
        checkPopup(CommonMessage.TRAINING_STEP1_TITLE_MSG, CommonMessage.TRAINING_STEP1_BODY_MSG);
        verifyPVPopupButtons(Setup.ButtonName.NEXT);
        Verification.verifyTrue(driver, SelCheckUtil.isElementVisibility(driver, icoHelp),
                CommonMessage.HELP_ICON_IS_DISPLAYED_MSG, true);
        Verification.verifyTrue(driver, SelCheckUtil.isElementVisibility(driver, icoClose),
                CommonMessage.CLOSE_ICON_IS_DISPLAYED_MSG, true);
    }

    public void checkGoToSignOnStep()
    {
        checkPopup(CommonMessage.TRAINING_STEP2_TITLE_MSG, CommonMessage.TRAINING_STEP2_BODY_MSG);
        verifyPVPopupButtons(Setup.ButtonName.NEXT);
        Verification.verifyTrue(driver, SelCheckUtil.isElementVisibility(driver, icoHelp),
                CommonMessage.HELP_ICON_IS_DISPLAYED_MSG, true);
        Verification.verifyTrue(driver, SelCheckUtil.isElementVisibility(driver, icoClose),
                CommonMessage.CLOSE_ICON_IS_DISPLAYED_MSG, true);
    }

    public void checkVerifySignOnControls1Step(ApplicationData data)
    {
        checkPopup(CommonMessage.TRAINING_STEP3_TITLE_MSG, CommonMessage.TRAINING_STEP3_BODY1_MSG);
        verifyPVPopupButtons(Setup.ButtonName.ACCEPT, Setup.ButtonName.SELECT_MANUALLY);
        Verification.verifyTrue(driver, is3CoverPointToCorrectFields(data),
                "Verification: Covers (Username, Password, Submit) are displayed to correct fields.");
        Verification.verifyTrue(driver, SelCheckUtil.isElementVisibility(driver, icoHelp),
                CommonMessage.HELP_ICON_IS_DISPLAYED_MSG, true);
        Verification.verifyTrue(driver, SelCheckUtil.isElementVisibility(driver, icoClose),
                CommonMessage.CLOSE_ICON_IS_DISPLAYED_MSG, true);
    }

    public void checkVerifyUsernameFieldStep(ApplicationData data)
    {
        checkPopup(CommonMessage.TRAINING_STEP4_TITLE_MSG, CommonMessage.TRAINING_STEP4_BODY_MSG);
        verifyPVPopupButtons(Setup.ButtonName.LEARN);
        Verification.verifyFalse(driver, is3CoverDisplay(data),
                "Verification: Covers (Username, Password, Submit) are not displayed.", false);
        Verification.verifyTrue(driver, SelCheckUtil.isElementVisibility(driver, icoHelp),
                CommonMessage.HELP_ICON_IS_DISPLAYED_MSG, true);
        Verification.verifyTrue(driver, SelCheckUtil.isElementVisibility(driver, icoClose),
                CommonMessage.CLOSE_ICON_IS_DISPLAYED_MSG, true);
    }

    public void checkVerifyPasswordFieldStep(ApplicationData data)
    {
        checkPopup(CommonMessage.TRAINING_STEP5_TITLE_MSG, CommonMessage.TRAINING_STEP5_BODY_MSG);
        verifyPVPopupButtons(Setup.ButtonName.LEARN);
        Verification.verifyTrue(driver, isCoverDisplay(data, USERNAME_COVER),
                "Verification: Covers (Username) is displayed.", false);
        SelWindowUtil.moveToFrameByIndex(driver, data.getLearnedDocIdUserName());
        String userValue = getLearnedElement(data.getLearnedUserName(), data.getLearnedUserNameXpath()).getAttribute("value");
        Verification.verifyTrue(driver, (!userValue.isEmpty()), "--- Verification: Email is filled " + userValue);
        driver.switchTo().defaultContent();
        Verification.verifyTrue(driver, SelCheckUtil.isElementVisibility(driver, icoHelp),
                CommonMessage.HELP_ICON_IS_DISPLAYED_MSG, true);
        Verification.verifyTrue(driver, SelCheckUtil.isElementVisibility(driver, icoClose),
                CommonMessage.CLOSE_ICON_IS_DISPLAYED_MSG, true);
    }

    public void checkVerifyThirdFieldStep(ApplicationData data)
    {
        checkPopup(CommonMessage.TRAINING_STEP6_TITLE_MSG, CommonMessage.TRAINING_STEP6_BODY_MSG);
        verifyPVPopupButtons(Setup.ButtonName.SKIP_THIS_STEP, Setup.ButtonName.LEARN);
        Verification.verifyTrue(driver, isCoverDisplay(data, USERNAME_COVER),
                "Verification: Covers (Username) is displayed.", false);
        Verification.verifyTrue(driver, isCoverDisplay(data, PASS_COVER),
                "Verification: Covers (Password) is displayed.", false);
        SelWindowUtil.moveToFrameByIndex(driver, data.getLearnedDocIdPsw());
        Verification.verifyTrue(driver, (getLearnedElement(data.getLearnedPsw(), data.getLearnedPswXpath()).getAttribute("value").equals("P@ssword1!")),
                "--- Verification: P@ssword1 is added to Password");
        driver.switchTo().defaultContent();
        Verification.verifyTrue(driver, SelCheckUtil.isElementVisibility(driver, icoHelp),
                CommonMessage.HELP_ICON_IS_DISPLAYED_MSG, true);
        Verification.verifyTrue(driver, SelCheckUtil.isElementVisibility(driver, icoClose),
                CommonMessage.CLOSE_ICON_IS_DISPLAYED_MSG, true);
    }

    public void checkVerifySubmitButtonStep(ApplicationData data)
    {
        checkPopup(CommonMessage.TRAINING_STEP7_TITLE_MSG, CommonMessage.TRAINING_STEP7_BODY_MSG);
        verifyPVPopupButtons(Setup.ButtonName.LEARN);
        Verification.verifyTrue(driver, isCoverDisplay(data, USERNAME_COVER),
                "Verification: Covers (Username) is displayed.", false);
        Verification.verifyTrue(driver, isCoverDisplay(data, PASS_COVER),
                "Verification: Covers (Password) is displayed.", false);
        SelWindowUtil.moveToFrameByIndex(driver, data.getLearnedDocIdPsw());
        Verification.verifyTrue(driver, (getLearnedElement(data.getLearnedPsw(), data.getLearnedPswXpath()).getAttribute("value").equals("P@ssword1!")),
                "--- Verification: P@ssword1 is added to Password");
        driver.switchTo().defaultContent();
        Verification.verifyTrue(driver, SelCheckUtil.isElementVisibility(driver, icoHelp),
                CommonMessage.HELP_ICON_IS_DISPLAYED_MSG, true);
        Verification.verifyTrue(driver, SelCheckUtil.isElementVisibility(driver, icoClose),
                CommonMessage.CLOSE_ICON_IS_DISPLAYED_MSG, true);
    }

    public void checkVerifySignOnControls3Step(ApplicationData data)
    {
        checkPopup(CommonMessage.TRAINING_STEP3_TITLE_MSG, CommonMessage.TRAINING_STEP3_BODY3_MSG);
        verifyPVPopupButtons(Setup.ButtonName.TRY_AGAIN, Setup.ButtonName.ACCEPT);
        Verification.verifyTrue(driver, is3CoverPointToCorrectFields(data),
                "Verification: Covers (Username, Password, Submit) are displayed to correct fields.");
        Verification.verifyTrue(driver, SelCheckUtil.isElementVisibility(driver, icoHelp),
                CommonMessage.HELP_ICON_IS_DISPLAYED_MSG, true);
        Verification.verifyTrue(driver, SelCheckUtil.isElementVisibility(driver, icoClose),
                CommonMessage.CLOSE_ICON_IS_DISPLAYED_MSG, true);
    }

    public void checkVerifySignOnControls2Step(ApplicationData data)
    {
        checkPopup(CommonMessage.TRAINING_STEP3_TITLE_MSG, CommonMessage.TRAINING_STEP3_BODY2_MSG);
        verifyPVPopupButtons(Setup.ButtonName.SELECT_MANUALLY);
        Verification.verifyFalse(driver, is3CoverDisplay(data),
                "Verification: Covers (Username, Password, Submit) are not displayed.", false);
        Verification.verifyTrue(driver, SelCheckUtil.isElementVisibility(driver, icoHelp),
                CommonMessage.HELP_ICON_IS_DISPLAYED_MSG, true);
        Verification.verifyTrue(driver, SelCheckUtil.isElementVisibility(driver, icoClose),
                CommonMessage.CLOSE_ICON_IS_DISPLAYED_MSG, true);
    }

    public void checkCancelStep()
    {
        checkPopup(CommonMessage.TRAINING_CANCEL_MSG, CommonMessage.TRAINING_CANCEL_MSG);
        verifyPVPopupButtons(Setup.ButtonName.NEXT);
        Verification.verifyFalse(driver, SelCheckUtil.isElementVisibility(driver, icoHelp),
                "--- Verification: Help icon not is displayed.", true);
        Verification.verifyFalse(driver, SelCheckUtil.isElementVisibility(driver, icoClose),
                "--- Verification: Close icon is not displayed.", true);
    }

    public void checkExistingAppStep()
    {
        checkPopup(CommonMessage.TRAINING_EXISTING_APP_MSG, CommonMessage.TRAINING_EXISTING_APP_MSG);
        verifyPVPopupButtons();
        Verification.verifyFalse(driver, SelCheckUtil.isElementVisibility(driver, icoHelp),
                "--- Verification: Help icon is not displayed.", true);
        Verification.verifyTrue(driver, SelCheckUtil.isElementVisibility(driver, icoClose),
                CommonMessage.CLOSE_ICON_IS_DISPLAYED_MSG, true);
    }

    public void trainingHelpCloseIconTest()
    {
        Verification.logSubStep("Open Help and verify content");
        SelUtil.waitElementClickableAndClick(driver, icoHelp);
        verifyTrainingHelp();
        SelUtil.waitElementClickableAndClick(driver, icoCloseTrainingHelp);
    }

    public void trainingHelpOKButtonTest()
    {
        Verification.logSubStep("Open Help and verify content");
        SelUtil.waitElementClickableAndClick(driver, icoHelp);
        SelUtil.waitElementClickableAndClick(driver, btnOkay);
    }

    private void verifyTrainingHelp()
    {
        Verification.verifyTrue(driver, SelCheckUtil.isElementVisibility(driver, dlgTrainingHelp),
                "Verification: Training Help popup is displayed", false);
        Verification.verifyTrue(driver, SelCheckUtil
                        .isElementVisibility(driver, lblTrainingHelpTitle),
                "Verification: title is 'Application Training Help'");
        Verification.verifyTrue(driver, SelCheckUtil
                        .isElementVisibility(driver, icoCloseTrainingHelp),
                "Verification: Close button", false);

        List<WebElement> elements = lblTrainingHelpContent.findElements(By.tagName("p"));
        if (Setup.getBrowserType().equalsIgnoreCase("edge"))
        {
            Verification.verifyTrue(driver,
                    elements.get(0).getText().equalsIgnoreCase("BASIC SSO TRAINING STEPS"),
                    "Verification: BASIC SSO TRAINING STEPS");
        }
        else
        {
            Verification.verifyTrue(driver,
                    elements.get(0).getText().contentEquals("BASIC SSO TRAINING STEPS"),
                    "Verification: BASIC SSO TRAINING STEPS");
        }
        Verification.verifyTrue(driver, elements.get(1).getText().contentEquals(
                "To add a new basic SSO application, a wizard will guide you through identifying the essential sign-on controls."),
                "Verification: To add a new basic SSO application, a wizard will guide you through identifying the essential sign-on controls.");
        Verification.verifyTrue(driver, elements.get(2).getText().contentEquals(
                "Step 1: Enter the application URL in this entry field (or copy/paste the URL from a separate tab), and click Next."),
                "Verification: Step 1: Enter the application URL in this entry field (or copy/paste the URL from a separate tab), and click Next.");
        Verification.verifyTrue(driver, elements.get(3).getText().contentEquals(
                "Step 2: If the system identifies the correct sign-on controls, click \"Accept\" and ignore the remaining steps. If not, click \"Select Manually\" and continue to next steps."),
                "Verification: Step 2: If the system identifies the correct sign-on controls, click \"Accept\" and ignore the remaining steps. If not, click \"Select Manually\" and continue to next steps.");
        Verification.verifyTrue(driver,
                elements.get(4).getText().contentEquals("Step 3: Identify the Username field."),
                "Verification: Step 3: Identify the Username field.");
        Verification.verifyTrue(driver,
                elements.get(5).getText().contentEquals("Step 4: Identify the Password field."),
                "Verification: Step 4: Identify the Password field.");
        Verification.verifyTrue(driver,
                elements.get(6).getText().contentEquals("Step 5: Identify the Sign-On button."),
                "Verification: Step 5: Identify the Sign-On button.");
        Verification.verifyTrue(driver,
                elements.get(7).getText().contentEquals("Step 6: Accept the results or try again."),
                "Verification: Step 6:  Accept the results or try again.");
        Verification.verifyTrue(driver, elements.get(8).getText().contentEquals(
                "Once you've completed these steps, we'll bring you back to the admin portal to complete the site configuration, and you can upload a logo for the application."),
                "Verification: Once you've completed these steps, we'll bring you back to the admin portal to complete the site configuration, and you can upload a logo for the application.");

        Verification.verifyTrue(driver, SelCheckUtil.isElementVisibility(driver, btnOkay),
                "Verification: OK button", false);

        Verification.takeScreenshot(driver, "HELP Popup");
    }

    public void cancelTrainingSession()
    {
        closePVPopup();
        processTrainingWizard("You have cancelled Learn Mode.", Setup.ButtonName.NEXT);
    }

    private boolean is3CoverDisplay(ApplicationData data)
    {
        boolean flag = true;
        if (!isCoverDisplay(data, USERNAME_COVER))
        {
            flag = false;
        }

        if (!isCoverDisplay(data, PASS_COVER))
        {
            flag = false;
        }

        if (!isCoverDisplay(data, SUBMIT_COVER))
        {
            flag = false;
        }

        return flag;
    }

    private boolean is3CoverPointToCorrectFields(ApplicationData appData)
    {
        boolean result = false;

        if (isCoverDisplayCorrect(appData, USERNAME_COVER) && isCoverDisplayCorrect(appData, PASS_COVER)
                && isCoverDisplayCorrect(appData, SUBMIT_COVER))
        {
            result = true;
        }

        return result;
    }

    private boolean isCoverDisplay(ApplicationData data, String field)
    {
        boolean result = false;

        int frameIndex;
        if (field.equalsIgnoreCase(USERNAME_COVER))
        {
            frameIndex = data.getLearnedDocIdUserName();
            if (frameIndex > -1)
            {
                driver.switchTo().frame(frameIndex);
            }
            if (SelCheckUtil.isElementVisibility(driver, lblUsernameCover))
            {
                driver.switchTo().defaultContent();
                result = true;
            }
        }
        else if (field.equalsIgnoreCase(PASS_COVER))
        {
            frameIndex = data.getLearnedDocIdPsw();
            if (frameIndex > -1)
            {
                driver.switchTo().frame(frameIndex);
            }
            if (SelCheckUtil.isElementVisibility(driver, lblPasswordCover))
            {
                driver.switchTo().defaultContent();
                result = true;
            }
        }
        else if (field.equalsIgnoreCase(SUBMIT_COVER))
        {
            frameIndex = data.getLearnedDocIdSubmit();
            if (frameIndex > -1)
            {
                driver.switchTo().frame(frameIndex);
            }
            if (SelCheckUtil.isElementVisibility(driver, lblSubmitCover))
            {
                driver.switchTo().defaultContent();
                result = true;
            }
        }
        driver.switchTo().defaultContent();

        return result;
    }

    private boolean isCoverDisplayCorrect(ApplicationData appData, String field)
    {
        boolean result = false;
        int frameIndex;

        WebElement element = SelUtil.getWebElementFromApplicationData(driver, appData, field);

        if (element != null)
        {
            switch (field)
            {
                case USERNAME_COVER:
                    frameIndex = appData.getLearnedDocIdUserName();
                    SelWindowUtil.moveToFrameByIndex(driver, frameIndex);
                    if (SelCheckUtil.isElementVisibility(driver, lblUsernameCover, 3))
                    {
                        result = SelCheckUtil.isElementCoveredByElement(lblUsernameCover, element);
                    }
                    break;

                case PASS_COVER:
                    frameIndex = appData.getLearnedDocIdPsw();
                    SelWindowUtil.moveToFrameByIndex(driver, frameIndex);
                    if (SelCheckUtil.isElementVisibility(driver, lblPasswordCover, 3))
                    {
                        result = SelCheckUtil.isElementCoveredByElement(lblPasswordCover, element);
                    }
                    break;

                case SUBMIT_COVER:
                    frameIndex = appData.getLearnedDocIdSubmit();
                    SelWindowUtil.moveToFrameByIndex(driver, frameIndex);
                    if (SelCheckUtil.isElementVisibility(driver, lblSubmitCover, 3))
                    {
                        result = SelCheckUtil.isElementCoveredByElement(lblSubmitCover, element);
                    }
                    break;

                default:
                    break;
            }
        }
        driver.switchTo().defaultContent();
        return result;
    }

    public void processTrainingWizard(String title, Setup.ButtonName button, String... url)
    {
        Verification.logSubStep("STEP: " + title);
        isPVPopupLoad(title);
        if (url.length > 0)
        {
            SelUtil.sendKeyToElement(driver, txtURL, url[0]);
        }
        clickButtonOnPVPopup(button);
        isPVPopupUnload(title);
    }

    public void trainingApp(ApplicationData appData)
    {
        processTrainingWizard("GO TO APPLICATION", Setup.ButtonName.NEXT, appData.getLoginUrl());
        processTrainingWizard("GO TO SIGN-ON", Setup.ButtonName.NEXT);

        isPVPopupLoad("VERIFY SIGN-ON CONTROLS");
        if (!is3CoverDisplay(appData))
        {
            clickButtonOnPVPopup(Setup.ButtonName.SELECT_MANUALLY);
            trainElement(appData, USERNAME_COVER);
            trainElement(appData, PASS_COVER);
            clickButtonOnPVPopup(Setup.ButtonName.SKIP_THIS_STEP);
            trainElement(appData, SUBMIT_COVER);
        }

        clickButtonOnPVPopup(Setup.ButtonName.ACCEPT);
    }

    public void trainElement(ApplicationData appData, String elementName)
    {
        Verification.logSubStep("Learn " + elementName);

        String title;
        int frameIndex;
        String learnedCSS;
        String learnedXPath;

        switch (elementName)
        {
            case USERNAME_COVER:
                title = "VERIFY USERNAME FIELD";
                frameIndex = appData.getLearnedDocIdUserName();
                learnedCSS = appData.getLearnedUserName();
                learnedXPath = appData.getLearnedUserNameXpath();
                break;
            case PASS_COVER:
                title = "VERIFY PASSWORD FIELD";
                frameIndex = appData.getLearnedDocIdPsw();
                learnedCSS = appData.getLearnedPsw();
                learnedXPath = appData.getLearnedPswXpath();
                break;
            default:
                title = "VERIFY SUBMIT BUTTON";
                frameIndex = appData.getLearnedDocIdSubmit();
                learnedCSS = appData.getLearnedSubmit();
                learnedXPath = appData.getLearnedSubmitXpath();
        }

        isPVPopupLoad(title);
        SelWindowUtil.moveToFrameByIndex(driver, frameIndex);

        WebElement mainElement = getLearnedElement(learnedCSS, learnedXPath);
        if (elementName.equalsIgnoreCase(SUBMIT_COVER) || !Setup.getBrowserType().equalsIgnoreCase("ie"))
        {
            SelUtil.waitElementClickableAndClick(driver, mainElement);
        }
        else if (elementName.equalsIgnoreCase(PASS_COVER))
        {
            SelUtil.sendKeyToElement(driver, mainElement, "");
        }
        else
        {
            SelUtil.sendKeyToElement(driver, getLearnedElement(appData.getLearnedPsw(),
                    appData.getLearnedPswXpath()), "");
            SelUtil.sendKeyToElement(driver, mainElement, "");
        }

        driver.switchTo().defaultContent();
        clickButtonOnPVPopup(Setup.ButtonName.LEARN);
        isPVPopupUnload(title);
    }

}