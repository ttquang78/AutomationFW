package com.uid.webportal.pages;

import com.uid.cloudportal.pages.NewCloudDesktopPage;
import com.uid.cloudportal.pages.NewPVTraining;
import com.uid.common.utils.SelCheckUtil;
import com.uid.common.utils.SelUtil;
import com.uid.common.utils.SelWindowUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.uid.common.utils.ApplicationData;
import com.uid.common.config.Setup;
import com.uid.common.config.Verification;

public class WebPortalPage
{

    final WebDriver driver;

    public final LoginPage loginPage;
    public final DashboardPage dashboardPage;
    public final MyApplicationsSAMLPage myApplicationsSAMLPage;
    public final ApplicationCatalogPage applicationCatalogPage;
    public final SetupDockPage setupDockPage;
    public final UsersPage usersPage;
    public final UsersByServicePage usersByServicePage;
    public final CustomersPage customersPage;

    public enum WPMenu
    {
        DASHBOARD("Dashboard"),
        APPLICATIONS("Applications"), MY_APPLICATIONS("My Applications"), APPLICATION_CATALOG("Application Catalog"),
        USERS("Users"), USER_GROUPS("User Groups"), USER_BY_SERVICE("Users by Service"),
        SETUP("Setup"), ACCOUNT("Account"), DOCK("Dock"), CUSTOMERS("Customers");

        private String menu;

        WPMenu(String name)
        {
            this.menu = name;
        }

        public String getWPMenu()
        {
            return this.menu;
        }
    }

    // Initialize:
    public WebPortalPage(WebDriver driver)
    {
        this.driver = driver;

        loginPage = PageFactory.initElements(driver, LoginPage.class);
        dashboardPage = PageFactory.initElements(driver, DashboardPage.class);
        myApplicationsSAMLPage = PageFactory.initElements(driver, MyApplicationsSAMLPage.class);
        applicationCatalogPage = PageFactory.initElements(driver, ApplicationCatalogPage.class);
        setupDockPage = PageFactory.initElements(driver, SetupDockPage.class);
        usersPage = PageFactory.initElements(driver, UsersPage.class);
        usersByServicePage = PageFactory.initElements(driver, UsersByServicePage.class);
        customersPage = PageFactory.initElements(driver, CustomersPage.class);
    }

    // LOGOUT PAGE-----------------------------------------------------------------

    // [LOGOUT]
    @FindBy(xpath = "//a[contains(@title,'Sign off')]")
    WebElement btnLogout;

    // [APPLICATIONS]
    @FindBy(id = "headerLinkApplications")
    WebElement mnuApplications;

    // [Application Name]
    @FindBy(id = "headerLinkIdpMyApplications")
    WebElement mnuMyApplications;

    // *********************************************************************//
    // FUNCTIONS                                                            //
    // *********************************************************************//

    public void logout()
    {
        SelUtil.waitElementClickableAndClick(driver, btnLogout, 20);
    }

    public void goToCDP()
    {
        Verification.logSubStep("Go to CDP");
        goToMenu(WPMenu.DASHBOARD);
        dashboardPage.clickCloudDesktopURL();
    }

    public void goToMenu(WPMenu... menus)
    {
        //TODO: Remove this when new drivers is fixed
        if (Setup.getBrowserType().equalsIgnoreCase("edge"))
        {
            SelWindowUtil.scrollToTop(driver);
        }

        for (WPMenu menu : menus)
        {
            String xpathStr = "//a[span[text()='" + menu.getWPMenu() + "']]";
            String parentXpathStr = xpathStr + "/..";
            WebElement parent =
                    SelUtil.waitForElementVisibility(driver, By.xpath(parentXpathStr), 20);
            if (!parent.getAttribute("class").equalsIgnoreCase("active"))
            {
                Verification.logSubStep("Go to menu: " + menu);
                SelUtil.waitElementClickableAndClick(driver, By.xpath(xpathStr));
            }
        }
    }

    public void enableAppInUserGroup(String appName)
    {
        goToMenu(WPMenu.USERS, WPMenu.USER_GROUPS);
        usersPage.selectAppInUserGroup(appName);
    }

    public void updateTrainingData(ApplicationData appData)
    {
        String appName = appData.getAppName();
        String trainingData = appData.getTrainingJson();
        if (!trainingData.contains("\\u003d"))
        {
            trainingData = trainingData.replace("u003d", "\\u003d");
            trainingData = trainingData.replace("u0027", "\\u0027");
        }
        String newLoginURL = appData.getNewLoginUrl();

        goToMenu(WPMenu.APPLICATIONS, WPMenu.MY_APPLICATIONS);

        myApplicationsSAMLPage.updateConfigurationAndLoginURL(appName, trainingData, newLoginURL);
    }

    public void retrainAppAndAddToCDP(ApplicationData appData)
    {
        Verification.logSubStep("Training " + appData.getAppName());

        NewPVTraining newPVTraining = PageFactory.initElements(driver, NewPVTraining.class);

        goToMenu(WPMenu.APPLICATIONS, WPMenu.MY_APPLICATIONS);
        waitForMyAppMenuLoad();
        myApplicationsSAMLPage.removeAppAndStartBasicSSOTraining(appData.getAppName());
        newPVTraining.trainingApp(appData);
        myApplicationsSAMLPage.waitForPageLoad();
        myApplicationsSAMLPage.saveTrainedData(NewCloudDesktopPage.Category.OTHER, appData.getAppName(), appData.getLoginUrl());
        enableAppInUserGroup(appData.getAppName());
    }

    public void trainAppAndAddToCDP(ApplicationData appData)
    {
        goToMenu(WPMenu.APPLICATIONS, WPMenu.MY_APPLICATIONS);
        waitForMyAppMenuLoad();
        if (!myApplicationsSAMLPage.isAppExistingInTrainingList(appData.getAppName()))
        {
            Verification.logSubStep("Training " + appData.getAppName());

            NewPVTraining newPVTraining = PageFactory.initElements(driver, NewPVTraining.class);

            myApplicationsSAMLPage.startBasicSSOTrainingWizard();
            newPVTraining.trainingApp(appData);
            myApplicationsSAMLPage.waitForPageLoad();
            myApplicationsSAMLPage.saveTrainedData(NewCloudDesktopPage.Category.OTHER, appData.getAppName(), appData.getLoginUrl());
        }
        enableAppInUserGroup(appData.getAppName());
    }

    public void removeApp(String appName)
    {
        Verification.logStep("Remove app");
        goToMenu(WPMenu.APPLICATIONS, WPMenu.MY_APPLICATIONS);
        myApplicationsSAMLPage.removeApplication(appName);
    }

    public void startBasicSSOTraining()
    {
        goToMenu(WPMenu.APPLICATIONS, WPMenu.MY_APPLICATIONS);
        myApplicationsSAMLPage.startBasicSSOTrainingWizard();
    }

    public boolean waitForMyAppMenuLoad()
    {
        return SelCheckUtil.isElementVisibility(driver, mnuMyApplications, 20);
    }

    // -------------------------------------------------------------------------------
    // [WAITFORWEBPORTALLOAD]:
    // -Wait for page to load by monitoring visibility of certain elements
    // -------------------------------------------------------------------------------
    public boolean waitForPageLoad()
    {
        return SelCheckUtil.isElementVisibility(driver, mnuApplications, 30);
    }

    // *********
    // END CLASS
}
