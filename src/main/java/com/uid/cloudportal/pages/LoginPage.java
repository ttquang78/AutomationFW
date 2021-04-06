package com.uid.cloudportal.pages;

import com.uid.common.utils.GmailParser;
import com.uid.common.utils.SelCheckUtil;
import com.uid.common.utils.SelUtil;
import com.uid.common.config.Verification;
import org.openqa.selenium.*;
import org.openqa.selenium.support.*;

public class LoginPage
{

    final WebDriver driver;

    public LoginPage(WebDriver driver)
    {
        this.driver = driver;
    }

    //*********************************************************************//
    //                    WEB ELEMENTS / METHODS                           //
    //*********************************************************************//

    //[Sign in]
    @FindBy(id = "btn-sign-in")
    WebElement btnSignIn;

    //[Sign On]
    @FindBy(xpath = "//*[@id='otp-form']/div[2]/input")
    WebElement btnSignON;

    //[Username]
    @FindBy(id = "ping-username")
    WebElement txtUsername;

    //[Password]
    @FindBy(id = "ping-password")
    WebElement txtPassword;

    //[InputCode]
    @FindBy(id = "otp")
    WebElement txtInputCode;

    @FindBy(xpath = "//a[contains(text(), 'I want to use a different authentication method.")
    WebElement lnkChangeAuthenMethod;

    @FindBy(xpath = "//div[contains(text(), 'Disabled')]")
    WebElement lblDisableDock;

    //*********************************************************************//
    //                             FUNCTIONS                               //
    //*********************************************************************//

    public Boolean isDisableDock()
    {
        return SelCheckUtil.isElementVisibility(driver, lblDisableDock);
    }

    public Boolean isDisplayPairDevice()
    {
        return SelCheckUtil.isElementVisibility(driver, lnkChangeAuthenMethod);
    }

    public Boolean isDisplayVerifyForm()
    {
        return true;
    }

    public NewCloudDesktopPage login(String userName, String password)
    {
        Verification.logSubStep("Login dock by: " + userName);

        SelUtil.sendKeyToElement(driver, txtUsername, userName);
        SelUtil.sendKeyToElement(driver, txtPassword, password);
        SelUtil.wait(3, "Wait to prevent Server Busy issue on Test and ORT environments");
        SelUtil.waitElementClickableAndClick(driver, btnSignIn);

        return PageFactory.initElements(driver, NewCloudDesktopPage.class);
    }

    public void verifyLoginStep2(String code)
    {
        SelUtil.sendKeyToElement(driver, txtInputCode, code);
        SelUtil.waitElementClickableAndClick(driver, btnSignON);
    }

    public void verifyPairDevice()
    {
        GmailParser gmailParser = new GmailParser();
        gmailParser.clearAllOldMail();
        SelUtil.waitElementClickableAndClick(driver, lnkChangeAuthenMethod);
        SelUtil.waitForElementVisibility(driver,
                By.xpath("//span[contains(text(), 'Alternative Authentication')]"));
        SelUtil.waitElementClickableAndClick(driver,
                driver.findElement(By.xpath("//input[@data-id='email']")));
        SelUtil.waitForElementVisibility(driver, By.id("emailInput"));
        SelUtil.sendKeyToElement(driver, driver.findElement(By.id("emailInput")),
                "thanhtestkms@gmail.com");
        SelUtil.waitElementClickableAndClick(driver,
                driver.findElement(By.xpath("//input[@data-id='next']")));

        //GetCode From Gmail
        String code = gmailParser.getVerifyCodeFromGmail("Welcome to PingID");

        SelUtil.waitForElementVisibility(driver, By.xpath("//input[@data-id='otp']"));
        SelUtil.sendKeyToElement(driver, driver.findElement(By.xpath("//input[@data-id='otp']")),
                code);
        SelUtil.waitElementClickableAndClick(driver,
                driver.findElement(By.xpath("//input[@data-id='submit']")));
    }

    public boolean waitForPageLoad()
    {
        return SelCheckUtil.isElementVisibility(driver, txtUsername);
    }

    //*********
    //END CLASS
}
