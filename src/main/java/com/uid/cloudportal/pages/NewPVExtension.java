package com.uid.cloudportal.pages;

import java.util.List;

import com.uid.common.utils.SelCheckUtil;
import com.uid.common.utils.SelUtil;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.uid.common.config.Setup;
import com.uid.common.config.Verification;

public class NewPVExtension
{
    static Logger log = Logger.getLogger(NewPVExtension.class.getName());

    final WebDriver driver;

    public NewPVExtension(WebDriver driver)
    {
        this.driver = driver;
    }

    //*********************************************************************//
    //                    WEB ELEMENTS                                     //
    //*********************************************************************//

    // [Button list]
    @FindBy(css = "div#pingone-dialog-overlay div.pingone-footer button")
    List<WebElement> buttonList;

    // [PV popup]
    @FindBy(id = "pingone-dialog-overlay")
    WebElement dlgPV;

    // [Close button]
    @FindBy(css = "div#pingone-dialog-overlay div.pingone-header div.pingone-header-right span.pingone-icon-close")
    WebElement icoClose;

    // [Ping Logo]
    @FindBy(css = "div#pingone-dialog-overlay div.pingone-header div.pingone-header-left div.pingone-logo-ping")
    WebElement icoPing;

    // [Help button]
    @FindBy(css = "div#pingone-dialog-overlay div.pingone-header div.pingone-header-right span.pingone-icon-help")
    WebElement icoHelp;

    // [PV popup content]
    @FindBy(css = "div#pingone-dialog-overlay div.pingone-body")
    WebElement lblContent;

    //*********************************************************************//
    //                    METHODS                                          //
    //*********************************************************************//

    void checkPopup(String title, String body)
    {
        Verification.verifyTrue(driver, isPVPopupLoad(title),
                title + " popup is displayed.", false);
        verifyPVPopupContent(title, body);
        Verification.takeScreenshot(driver, title);
    }

    public void closePVPopup()
    {
        SelUtil.waitElementClickableAndClick(driver, icoClose);
        SelCheckUtil.waitForElementChangeToInvisible(driver, dlgPV);
    }

    public void clickButtonOnPVPopup(Setup.ButtonName buttonName)
    {
        Verification.logSubStep("Click " + buttonName + " button");
        String xpathStr = generateXPathForButton(buttonName);
        SelUtil.waitElementClickableAndClick(driver, By.xpath(xpathStr));
    }

    public void dragPVPopup(int xOffset, int yOffset)
    {
        Verification.logSubStep(
                "Drag and drop Training popup with distance: " + xOffset + "," + yOffset);
        SelUtil.dragElement(driver, dlgPV, xOffset, yOffset);
    }

    public boolean isCloseBtnDisplayOnPVPopup()
    {
        return SelCheckUtil.isElementVisibility(driver, icoClose, 5);
    }

    public boolean isHelpBtnDisplayOnPVPopup()
    {
        return SelCheckUtil.isElementVisibility(driver, icoHelp, 5);
    }

    public boolean isPingLogoDisplayOnPVPopup()
    {
        return SelCheckUtil.isElementVisibility(driver, icoPing, 5);
    }

    public boolean isPVPopupDisplayAtPosition(Point location)
    {
        Point currentLocation = dlgPV.getLocation();
        return currentLocation.x == location.x && currentLocation.y == location.y;
    }

    public boolean isPVPopupLoad()
    {
        return SelCheckUtil.isElementVisibility(driver, dlgPV);
    }

    public boolean isPVPopupLoad(String title)
    {
        return SelCheckUtil.waitForCondition(driver,
                driver1 -> SelUtil.getText(lblContent).toLowerCase().startsWith(title.toLowerCase()));
    }

    void isPVPopupUnload(String name)
    {
        if (SelCheckUtil.isElementVisibility(driver, dlgPV))
        {
            for (int i = 0; i < Setup.DEFAULT_TIME_OUT; i++)
            {
                String value = getContentOfPVPopup();
                if (value == null || !value.startsWith(name))
                {
                    break;
                }
                SelUtil.wait(1, "wait for Training popup disappear");
            }
        }
    }

    private void verifyPVPopupContent(String... trainingContent)
    {
        Verification.verifyTrue(driver, SelCheckUtil.isElementVisibility(driver, lblContent),
                "Content of popup is loaded");

        if (trainingContent.length > 1)
        {
            List<WebElement> elements = lblContent.findElements(By.tagName("p"));

            int i = 0;
            for (WebElement webElement : elements)
            {
                if (Setup.getBrowserType().equalsIgnoreCase("edge"))
                {
                    Verification.verifyTrue(driver, webElement.getText().equalsIgnoreCase(trainingContent[i]),
                            trainingContent[i], false);
                }
                else
                {
                    Verification.verifyTrue(driver, webElement.getText().contentEquals(trainingContent[i]),
                            trainingContent[i], false);
                }
                i++;
            }
        }
        else
        {
            Verification.verifyTrue(driver, lblContent.getText().equalsIgnoreCase(trainingContent[0]),
                    "Verification: " + trainingContent[0], false);
        }
    }

    public void verifyPVPopupButtons(Setup.ButtonName... buttonNames)
    {
        for (Setup.ButtonName button : buttonNames)
        {
            String xpathStr = generateXPathForButton(button);
            Verification.verifyTrue(driver, (SelCheckUtil.isElementVisibility(driver, By.xpath(xpathStr))),
                            "Verification: Button name is " + button.getBtnName());
        }

        int visibleElementNumber;
        int count = 0;
        do
        {
            visibleElementNumber = SelUtil.countVisibleElements(buttonList);
            count++;
        }
        while (visibleElementNumber != buttonNames.length && count < 3);

        Verification.verifyTrue(driver, (visibleElementNumber == buttonNames.length),
                "Verification: There are " + buttonNames.length + " buttons");
    }

    private String generateXPathForButton(Setup.ButtonName buttonName)
    {
        return "//div[@id='pingone-dialog-overlay']//div[@class='pingone-footer']//button[text()='"
                + buttonName.getBtnName() + "']";
    }

    WebElement getLearnedElement(String learnedCSSValue, String learnedXpathValue)
    {
        WebElement webElement = null;

        if (SelCheckUtil.isElementPresence(driver, By.cssSelector(learnedCSSValue)))
        {
            webElement = SelUtil.waitForElementPresence(driver, By.cssSelector(learnedCSSValue));
        }

        if (webElement == null && SelCheckUtil.isElementPresence(driver, By.xpath(learnedXpathValue)))
        {
            webElement = SelUtil.waitForElementPresence(driver, By.xpath(learnedXpathValue));
        }

        return webElement;
    }

    public String getContentOfPVPopup()
    {
        return SelUtil.getText(lblContent);
    }

    public Point getPVPopupLocation()
    {
        return dlgPV.getLocation();
    }

}
