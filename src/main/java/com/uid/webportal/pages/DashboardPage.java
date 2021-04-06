package com.uid.webportal.pages;

import com.uid.common.utils.SelCheckUtil;
import com.uid.common.utils.SelUtil;
import org.openqa.selenium.*;
import org.openqa.selenium.support.*;

public class DashboardPage
{

    final WebDriver driver;

    public DashboardPage(WebDriver driver)
    {
        this.driver = driver;
    }

    //*********************************************************************//
    //                    WEB ELEMENTS / METHODS                           //
    //*********************************************************************//

    //[CloudDesktop Url]
    @FindBy(id = "cloudDesktopUrl") WebElement lnkCLoudDesktopURL;

    //*********************************************************************//
    //                    ACTIONS                                          //
    //*********************************************************************//

    //-------------------------------------------------------------------------------
    //[CLICKCLOUDDESKTOPURL]:
    //-Navigate to Cloud Desktop url
    //-------------------------------------------------------------------------------
    void clickCloudDesktopURL()
    {
        SelUtil.waitElementClickableAndClick(driver, lnkCLoudDesktopURL);
    }

    //-------------------------------------------------------------------------------
    //[GETCLOUDDESKTOPURL]:
    //-Get Cloud Destop page url
    //-------------------------------------------------------------------------------
    public String getCloudDesktopURL()
    {
        return SelUtil.getText(lnkCLoudDesktopURL);
    }

    //-------------------------------------------------------------------------------
    //[WAITPAGELOAD]:
    //-Wait for page to load by monitoring visibility of certain elements
    //-------------------------------------------------------------------------------
    public boolean waitPageLoad()
    {
        return SelCheckUtil.isElementVisibility(driver, lnkCLoudDesktopURL, 5);
    }

    //*********
    //END CLASS
}
