package com.uid.webportal.pages;

import com.uid.common.utils.SelCheckUtil;
import com.uid.common.utils.SelUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ApplicationCatalogPage
{
    final WebDriver driver;

    public ApplicationCatalogPage(WebDriver driver)
    {
        this.driver = driver;
    }

    //*********************************************************************//
    //                    WEB ELEMENTS                                     //
    //*********************************************************************//

    // [App Add button]
    @FindBy(xpath = "//form[@wicketpath='appCatalogPanel_wizardContainer_setupWizard_appDetailsForm']//input[@name='setupLink']")
    WebElement btnAddApp;

    // [Finish button]
    @FindBy(id = "wizardFinishButton")
    WebElement btnFinish;

    // [App Filter]
    @FindBy(id = "filterField")
    WebElement txtAppFilter;

    private String generateAppRowXpath(String appName)
    {
        return "//table[@id='IdpApplicationCatalogPanel']//tr[td[node()='" + appName + "']][td[node()='Basic SSO']]";
    }

    private String generateAppShowDetailXpath(String appName)
    {
        return generateAppRowXpath(appName) + "//a[@title='Show application details']";
    }

    //*********************************************************************//
    //                    ACTIONS                                          //
    //*********************************************************************//

    public void addAppFromCatalog(String appName)
    {
        filterApp(appName);

        String appStr = generateAppShowDetailXpath(appName);

        if (SelCheckUtil.isElementVisibility(driver, By.xpath(appStr)))
        {
            SelUtil.waitElementClickableAndClick(driver, By.xpath(appStr));
            SelUtil.waitElementClickableAndClick(driver, btnAddApp);
            SelUtil.waitElementClickableAndClick(driver, btnFinish);
        }
    }

    void filterApp(String value)
    {
        if (SelCheckUtil.isElementVisibility(driver, txtAppFilter))
        {
            SelUtil.sendKeyToElement(driver, txtAppFilter, value);
        }
    }
}
