package com.uid.cloudportal.pages;

import com.uid.common.config.Verification;
import com.uid.common.utils.SelCheckUtil;
import com.uid.common.utils.SelUtil;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class NewBEInstallDlg
{
    final WebDriver driver;

    public NewBEInstallDlg(WebDriver driver)
    {
        this.driver = driver;
    }

    //*********************************************************************//
    //                    WEB ELEMENTS                                     //
    //*********************************************************************//

    //[Install button]
    @FindBy(css = "div.install-be button[data-id='installBrowserExtension']")
    WebElement btnAdd;

    //[Cancel link]
    @FindBy(css = "div.install-be a[data-id='cancel-link']")
    WebElement lnkCancel;

    //*********************************************************************//
    //                    METHODS                                          //
    //*********************************************************************//

    /**
     * [CLOSEDLG]:
     * /*-------------------------------------------------------------------------------
     */
    public void closeDlg()
    {
        Verification.logSubStep("Cancel BE Install");
        SelUtil.waitElementClickableAndClick(driver, lnkCancel);
    }

    /**
     * [ADDBE]:
     * /*-------------------------------------------------------------------------------
     */
    public void addBE()
    {
        Verification.logSubStep("Click Add BE");
        SelUtil.waitElementClickableAndClick(driver, btnAdd);
    }

    /**
     * [WAITINSTALLDIALOGLOAD]:
     * /* -Wait for dialog to load by monitoring visibility of certain elements
     * /*-------------------------------------------------------------------------------
     */
    public boolean waitDlgLoad()
    {
        return SelCheckUtil.isElementVisibility(driver, lnkCancel);
    }

}
