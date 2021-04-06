package com.uid.webportal.pages;

import com.uid.common.utils.SelCheckUtil;
import com.uid.common.utils.SelUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class UsersByServicePage
{

    final WebDriver driver;

    public UsersByServicePage(WebDriver driver)
    {
        this.driver = driver;
    }

    //*********************************************************************//
    //                    WEB ELEMENTS / METHODS                           //
    //*********************************************************************//

    @FindBy(xpath = "//input[@data-id='users-search-field-input']")
    WebElement txtUserSearch;

    @FindBy(xpath = "//a[@data-id='expand-btn']")
    WebElement btnExpand;

    @FindBy(xpath = "//div[@data-id='device-display']")
    WebElement lblDeviceName;

    @FindBy(xpath = "//div[@data-id='section-title']")
    WebElement lblSectionTitle;

    @FindBy(xpath = "//div[@data-id='user-toggle']")
    WebElement btnStatus;

    @FindBy(xpath = "bypass-service")
    WebElement btnByPass;

    @FindBy(xpath = "remove-service")
    WebElement btnRemove;

    @FindBy(xpath = "resume-service")
    WebElement btnResume;

    @FindBy(xpath = ".//*[@data-id='confirm-action']")
    WebElement btnConfirm;

    @FindBy(xpath = "label-button")
    List<WebElement> listLabelButtons;

    //*********************************************************************//
    //                             FUNCTIONS                               //
    //*********************************************************************//

    private String generateSectionXpath(String sectionName)
    {
        return "//div[@data-id='section-title' and contains(text(), '" + sectionName + "')]";
    }

    public Boolean isUserByServiceDisplayed()
    {
        return SelCheckUtil.isElementVisibility(driver, txtUserSearch);
    }

    public void fillSearchBox(String text)
    {
        SelUtil.sendKeyToElement(driver, txtUserSearch, text);
    }

    public void clickFirstRowDetail()
    {
        SelUtil.waitElementClickableAndClick(driver, btnExpand);
        SelUtil.waitForElementVisibility(driver, By.xpath(generateSectionXpath("PingID")));
    }

    public void expandServiceByName(String name)
    {
        String xpathStr = generateSectionXpath(name);
        SelUtil.waitElementClickableAndClick(driver, By.xpath(xpathStr));
        SelUtil.waitForElementVisibility(driver, lblDeviceName);
    }

    public void clickEditPingID()
    {
        for (WebElement item : listLabelButtons)
        {
            SelUtil.waitElementClickableAndClick(driver, item);
            List<WebElement> removeService = SelUtil.findElementsByDataId(driver, "remove-service");
            if (!removeService.isEmpty())
            {
                break;
            }
        }
    }

    public void removePingID()
    {
        SelUtil.waitElementClickableAndClick(driver, btnRemove);
        SelUtil.waitElementClickableAndClick(driver, btnConfirm);
    }

    public Boolean isExpandDetailUser()
    {
        return SelCheckUtil.isElementVisibility(driver, lblSectionTitle);
    }

    public void clickEditPingService()
    {
        for (WebElement item : listLabelButtons)
        {
            SelUtil.waitElementClickableAndClick(driver, item);
            List<WebElement> byPass = SelUtil.findElementsByDataId(driver, "bypass-service");
            if (!byPass.isEmpty())
            {
                break;
            }
        }
    }

    public void setByPass()
    {
        SelUtil.waitElementClickableAndClick(driver, btnByPass);
        SelUtil.waitElementClickableAndClick(driver, btnConfirm);
    }

    public void revertByPass()
    {
        SelUtil.waitElementClickableAndClick(driver, btnResume);
    }

    public void clickToggleButton()
    {
        SelUtil.waitElementClickableAndClick(driver, btnStatus);
    }

    //*********
    //END CLASS
}
