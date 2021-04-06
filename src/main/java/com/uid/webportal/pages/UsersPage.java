package com.uid.webportal.pages;

import com.uid.common.utils.SelCheckUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.uid.common.utils.SelUtil;

public class UsersPage
{

    final WebDriver driver;

    public UsersPage(WebDriver driver)
    {
        this.driver = driver;
    }

    // *********************************************************************//
    // WEB ELEMENTS                                                         //
    // *********************************************************************//    

    // [Edit button]
    @FindBy(xpath = "//table[@id='GroupManagementPanel']//a[@title='Edit Group']")
    WebElement btnEditGroup;

    // [Save button]
    @FindBy(xpath = "//input[@value='Save']")
    WebElement btnSave;

    // *********************************************************************//
    // ACTIONS                                                              //
    // *********************************************************************//

    void selectAppInUserGroup(String appName)
    {
        SelUtil.waitElementClickableAndClick(driver, btnEditGroup);

        String xpathStr = "//div[@class='groupApplication'][label[text()='" + appName + " (SSO)']]/input";

        WebElement element = SelUtil.waitForElementVisibility(driver, By.xpath(xpathStr));

        if (!element.isSelected())
        {
            SelUtil.waitElementClickableAndClick(driver, element);
        }

        SelUtil.waitElementClickableAndClick(driver, btnSave);
        SelCheckUtil.waitForElementChangeToInvisible(driver, btnSave, 20);
    }

}
