package com.browserextension.selenium.testcases;

import com.uid.common.base.CommonMessage;
import com.uid.common.config.TestAccountManager;
import com.uid.common.config.Verification;
import org.openqa.selenium.*;
import org.openqa.selenium.support.*;
import com.uid.cloudportal.pages.CloudDesktopPage;

public class BEPrivacyKeyCat
{
    private BEPrivacyKeyCat() {}

    /**
     * ===============================================================
     * /* [PRIVACYKEY]
     * /* -This test combine tests which relate to privacy key. Intent to install BE just one time on IE
     * /* -Use test account with existed privacy key
     * /*===============================================================
     */
    public static void testPrivacyKey(WebDriver driver)
    {
        String privacyKey = "PingOne1";
        boolean checkPoint;
        //Initialize class needed:
        //------------------------------------------------------------
        Verification.startCollapseHtml();  //initialize steps logging formatting
        CloudDesktopPage cloudDesktopPage = PageFactory.initElements(driver, CloudDesktopPage.class);
        //------------------------------------------------------------

        cloudDesktopPage.login(TestAccountManager.getUserName(), TestAccountManager.getPassword());

        //OPTION STEP: Switch test account to Legacy mode
        cloudDesktopPage.switchToLegacyMode();

        Verification.logStep("Sign in with empty privacy key");
        cloudDesktopPage.beDialog.clickInputPKSave();
        checkPoint = cloudDesktopPage.beDialog.getDialogErrorMsg().contains("That is not your privacy key. Please try again.");
        Verification.verifyTrue(driver, checkPoint, CommonMessage.LEGACY_CORRECT_ERROR_MSG, false);

        Verification.logStep("Sign in with invalid privacy key");
        cloudDesktopPage.beDialog.signInKey("invalid");
        checkPoint = cloudDesktopPage.beDialog.getDialogErrorMsg().contains("That is not your privacy key. Please try again.");
        Verification.verifyTrue(driver, checkPoint, CommonMessage.LEGACY_CORRECT_ERROR_MSG, false);

        Verification.logStep("Select Delete and Start Over link");
        cloudDesktopPage.beDialog.clickClearPKeyLink();
        checkPoint = (cloudDesktopPage.beDialog.waitForConfirmClearPKDlgLoad() && cloudDesktopPage.beDialog.getAuthenticationHeader().contains("Confirm"));
        Verification.verifyTrue(driver, checkPoint, CommonMessage.CONFIRM_DLG_DISPLAYED_MSG, false);

        Verification.logStep("Cancel clearing privacy key");
        cloudDesktopPage.beDialog.clickCancelClear();
        checkPoint = cloudDesktopPage.beDialog.isClearPKDisplay();
        Verification.verifyTrue(driver, checkPoint, "Verification: Reset all password is displayed", false);

        Verification.logStep("Select Delete and Start Over link again");
        cloudDesktopPage.beDialog.clickClearPKeyLink();
        cloudDesktopPage.beDialog.waitConfirmDialog();
        checkPoint = cloudDesktopPage.beDialog.getAuthenticationHeader().contains("Confirm");
        Verification.verifyTrue(driver, checkPoint, CommonMessage.CONFIRM_DLG_DISPLAYED_MSG, false);

        Verification.logStep("Confirm clearing privacy key");
        cloudDesktopPage.beDialog.clickConfirmClear();
        checkPoint = cloudDesktopPage.beDialog.isCreatePKDisplayed();
        Verification.verifyTrue(driver, checkPoint, CommonMessage.CREATE_PK_DISPLAYED_MSG, false);

        //Step: TC_PK_02_Error message displays when leave blank for both PK and confirm PK
        Verification.logStep("Save empty privacy key");
        cloudDesktopPage.beDialog.clickNewPKSave();
        checkPoint = cloudDesktopPage.beDialog.getDialogErrorMsg().contains(CommonMessage.LEGACY_PK_REQ_MSG);
        Verification.verifyTrue(driver, checkPoint , CommonMessage.LEGACY_CORRECT_ERROR_MSG);

        //Step: TC_PK_03_Error message displays when leave blank for PK
        Verification.logStep("Save confirmation key only");
        cloudDesktopPage.beDialog.setConfirmPrivacyKey(privacyKey);
        cloudDesktopPage.beDialog.clickNewPKSave();
        checkPoint = cloudDesktopPage.beDialog.getDialogErrorMsg().contains(CommonMessage.LEGACY_PK_REQ_MSG);
        Verification.verifyTrue(driver, checkPoint, CommonMessage.LEGACY_CORRECT_ERROR_MSG);

        //Step: TC_PK_04_Error message displays when leave blank for Confirm PK
        Verification.logStep("Save with main key only");
        cloudDesktopPage.beDialog.setMainPrivacyKey(privacyKey);
        cloudDesktopPage.beDialog.clickNewPKSave();
        checkPoint =  cloudDesktopPage.beDialog.getDialogErrorMsg().contains("The privacy key and confirmation entered don't match.");
        Verification.verifyTrue(driver, checkPoint, CommonMessage.LEGACY_CORRECT_ERROR_MSG);

        //Step: TC_PK_05 -TC_PK_08
        String[] testNameList = {"Save with correct format but less than 8 characters",
                "Save with privacy key has no number",
                "Save with privacy key has no lower char",
                "Save with privacy key has no upper char"};
        String[] testDataList = {"PingOn1", "PingOnee", "PINGONE1", "pingone1"};
        for (int i = 0; i < testNameList.length; i++)
        {
            Verification.logStep(testNameList[i]);
            cloudDesktopPage.beDialog.createPrivacyKey(testDataList[i]);
            checkPoint = cloudDesktopPage.beDialog.getDialogErrorMsg().contains(CommonMessage.LEGACY_PK_REQ_MSG);
            Verification.verifyTrue(driver, checkPoint, CommonMessage.LEGACY_PK_REQ_MSG);
        }

        //Step: TC_PK_09_Error message "don't match" displays when privacy key confirms a different key
        Verification.logStep("Save with miss match privacy key");
        cloudDesktopPage.beDialog.setMainPrivacyKey(privacyKey);
        cloudDesktopPage.beDialog.setConfirmPrivacyKey("PingOne2");
        cloudDesktopPage.beDialog.clickNewPKSave();
        checkPoint = cloudDesktopPage.beDialog.getDialogErrorMsg().contains("The privacy key and confirmation entered don't match.");
        Verification.verifyTrue(driver, checkPoint, CommonMessage.LEGACY_CORRECT_ERROR_MSG);

        //Step: Create New privacy key
        Verification.logStep("Create new privacy key");
        cloudDesktopPage.beDialog.clearPrivacyKeyFields();
        cloudDesktopPage.beDialog.createPrivacyKey(TestAccountManager.getPrivacyKey());

        cloudDesktopPage.logout();
        Verification.endCollapseHtml(); //End steps logging formatting
        Verification.checkForVerificationErrors();
    }

}
