package com.uid.cloudportal.pages;

import com.uid.common.config.Setup;
import com.uid.common.config.Verification;
import com.uid.common.utils.ApplicationData;
import com.uid.common.utils.SelCheckUtil;
import com.uid.common.utils.SelUtil;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;

public class PVLegacy extends NewPVExtension
{
    public PVLegacy(WebDriver driver)
    {
        super(driver);
    }

    //*********************************************************************//
    //                    WEB ELEMENTS                                     //
    //*********************************************************************//

    // [Training Help popup]
    @FindBy(id = "pingone-submit-overlay-div")
    private WebElement submitOverlay;

    //*********************************************************************//
    //                    METHODS                                          //
    //*********************************************************************//

    public void appLoginLegacy(ApplicationData appData)
    {
        String userNameCSS = appData.getLearnedUserName();
        String passwordCSS = appData.getLearnedPsw();
        String submitCSS = appData.getLearnedSubmit();

        String userNameXpath = appData.getLearnedUserNameXpath();
        String passwordXpath = appData.getLearnedPswXpath();
        String submitXpath = appData.getLearnedSubmitXpath();

        int frameIndexUserName = appData.getLearnedDocIdUserName();
        int frameIndexPassword = appData.getLearnedDocIdPsw();
        int frameIndexSubmit = appData.getLearnedDocIdSubmit();

        String userNameValue = appData.getAppName() + "@pingidentity.com";
        String passwordValue = "Password123" + appData.getAppName();

        // Get username element and enter data
        if (frameIndexUserName > -1)
        {
            driver.switchTo().frame(frameIndexUserName);
        }
        SelUtil.sendKeyToElement(driver, getLearnedElement(userNameCSS, userNameXpath), userNameValue);
        driver.switchTo().defaultContent();

        // Get password element and enter data
        if (frameIndexPassword > -1)
        {
            driver.switchTo().frame(frameIndexPassword);
        }
        SelUtil.sendKeyToElement(driver, getLearnedElement(passwordCSS, passwordXpath), passwordValue);
        driver.switchTo().defaultContent();

        // Submit credentials using default overlay
        if (frameIndexSubmit > -1)
        {
            driver.switchTo().frame(frameIndexUserName);
        }
        if (Setup.getBrowserVersion().equalsIgnoreCase("9")
                || Setup.getBrowserVersion().equalsIgnoreCase("10"))
        {
            String tagName = getLearnedElement(submitCSS, submitXpath).getTagName();
            String cssStr = submitCSS + " ~ " + tagName.toUpperCase();

            SelUtil.waitElementClickableAndClick(driver, By.cssSelector(cssStr));
        }
        else
        {
            SelUtil.waitElementClickableAndClick(driver, submitOverlay);
        }
        driver.switchTo().defaultContent();
    }

    public void appTrainingLegacy(ApplicationData appData, boolean... verifyDragDialog)
    {
        Verification.logSubStep("Learn credential");
        String curURL = driver.getCurrentUrl();
        log.info(curURL);

        Verification.verifyTrue(driver, isPVPopupLoad("Sign on like you normally do."),
                "Verification: Overlay is displayed", false);
        Verification.verifyTrue(driver, getContentOfPVPopup().contains("Sign on"),
                "Verification: Overlay content contains 'Sign on...'");

        Point currentLocation = getPVPopupLocation();
        Point newLocation = new Point(100, 200);
        if (verifyDragDialog.length > 0)
        {
            SelUtil.dragElement(driver, dlgPV, newLocation.x - currentLocation.x,
                    newLocation.y - currentLocation.y);
            Verification.verifyTrue(driver, isPVPopupDisplayAtPosition(newLocation),
                    "Verification: Learning dialog is dragged and dropped to new location.", false);
        }

        // Step: Fill in credentials and submit
        Verification.logSubStep("Fill-in credentials and Submit");
        appLoginLegacy(appData);

        // Verify Save Overlay displays as expected
        verifyPVPopupButtons(Setup.ButtonName.SAVE);
        Verification.verifyTrue(driver, getContentOfPVPopup().contains("Save password"),
                "Verification: Overlay content contains 'Save password...'");

        if (verifyDragDialog.length > 0)
        {
            Verification.verifyTrue(driver, isPVPopupDisplayAtPosition(newLocation),
                    "Verification: Learning dialog is still in the new location.", false);
        }

        // Step: Click save and wait for login process to finish
        Verification.logSubStep("Click Save on extension overlay");
        clickButtonOnPVPopup(Setup.ButtonName.SAVE);
        Verification.verifyTrue(driver, SelCheckUtil.waitForElementChangeToInvisible(driver, dlgPV),
                "Verification: Overlay dissappears", false);
    }

}
