package com.uid.common.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.uid.common.utils.CsvUtils;
import com.uid.common.utils.SelWindowUtil;
import org.apache.log4j.Logger;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.ProfilesIni;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.AbstractWebDriverEventListener;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.events.WebDriverEventListener;

public class WebdriverManager
{
    private static Logger log = Logger.getLogger(WebdriverManager.class.getName());
    private static final String CSV_CHROME_PROFILE_LOCATION = "Config/ChromeProfilesPath.csv";

    private WebdriverManager() {}

    // WebDriver as localthread
    private static final ThreadLocal<EventFiringWebDriver> webDriver = new ThreadLocal<>();

    // Verification Error Buffer as localthread
    // -To store failures in a buffer
    private static final ThreadLocal<StringBuffer> errorBuffer = new ThreadLocal<>();

    // Verification boolean as localthread
    // -Tells the verification framework that it is an expected error
    // -Allows test to continue
    private static final ThreadLocal<Boolean> errorExpected = new ThreadLocal<>();

    /**
     * ----------------------------------------------------------- /*
     * [INITDRIVER:] /* -Initialize driver as an error listener (for
     * verification purposes)
     * /*----------------------------------------------------------
     */
    private static EventFiringWebDriver initDriver(WebDriver driver)
    {
        // Set listener for uncaught EXCEPTIONS (will take screenshot and log
        // error to report):
        // - Exceptions that are *not expected* failure verifications points
        // (based on SelUtil.errorCaught global variable)
        WebDriverEventListener errorListener = new AbstractWebDriverEventListener()
        {
            @Override public void onException(Throwable throwable, WebDriver driver)
            {

                if (!WebdriverManager.getErrorExpected())
                {
                    Verification.exceptionError(WebdriverManager.get(), "Unexpected Error",
                            throwable.getMessage());
                }
            }

        };
        ((EventFiringWebDriver)driver).register(errorListener);

        return (EventFiringWebDriver)driver;
    }

    public static synchronized void setBrowserDriver(String browserType, boolean addExt) throws IOException
    {
        log.debug("Step in setBrowserDriver");
        Capabilities options = setBrowserOption(browserType, addExt);

        if (!Setup.getGridServer().equalsIgnoreCase("Local"))
        {
            RemoteWebDriver driver =
                    new RemoteWebDriver(new URL("http://" + Setup.getGridServer() + ":4444/wd/hub"),
                            options);
            SelWindowUtil.maximizeWindow(driver);

            String[] tmp = GridInfoExtractor
                    .getHostNameAndPort(Setup.getGridServer(), 4444, driver.getSessionId());
            log.info("Run test on machine: " + tmp[0]);
            Setup.setWebRuntime(tmp[0]);

            storeBrowserInfo(driver);
            driver.manage().timeouts().implicitlyWait(0L, TimeUnit.SECONDS);

            WebDriver eDriver = new EventFiringWebDriver(driver);
            webDriver.set(initDriver(eDriver));
        }
        else
        {
            WebDriver driver;
            if (Setup.getOS().equalsIgnoreCase("windows"))
            {
                switch (Setup.getBrowserType())
                {
                    case "edge":
                        System.setProperty("webdriver.edge.driver", Setup.getEdgeDriverPath());
                        driver = new EdgeDriver((EdgeOptions)options);

                        break;
                    case "firefox":
                        System.setProperty("webdriver.gecko.driver", Setup.getGeckoDriverPath());
                        driver = new FirefoxDriver((FirefoxOptions)options);

                        break;
                    case "ie":
                        System.setProperty("webdriver.ie.driver", Setup.getIeDriverPath());
                        driver = new InternetExplorerDriver((InternetExplorerOptions)options);

                        break;
                    default:
                        System.setProperty("webdriver.chrome.driver", Setup.getChromeDriverPath());
                        driver = new ChromeDriver((ChromeOptions)options);
                        break;
                }
            }
            else
            {
                String webDriversDir = "WebDrivers/";
                if (Setup.getBrowserType().equals("firefox"))
                {
                    System.setProperty("webdriver.gecko.driver", webDriversDir + "geckodriver");
                    driver = new FirefoxDriver((FirefoxOptions)options);
                }
                else
                {
                    System.setProperty("webdriver.chrome.driver",
                            webDriversDir + "chromedriver_mac");
                    driver = new ChromeDriver((ChromeOptions)options);
                }
            }

            storeBrowserInfo((RemoteWebDriver)driver);
            driver.manage().timeouts().implicitlyWait(0L, TimeUnit.SECONDS);

            WebDriver tmpDriver = new EventFiringWebDriver(driver);
            SelWindowUtil.maximizeWindow(driver);
            webDriver.set(initDriver(tmpDriver));
        }
        log.debug("Step out setBrowserDriver");
    }

    private static Capabilities setBrowserOption(String browserType, boolean addExt)
            throws IOException
    {
        switch (browserType)
        {
            case "chrome":
                return setChromeOption(addExt);

            case "edge":
                String localAppDataDir = System.getenv("LOCALAPPDATA");
                String edgeSideLoadPath = localAppDataDir
                        + "\\Packages\\Microsoft.MicrosoftEdge_8wekyb3d8bbwe\\LocalState\\BE";
                log.info("Path: " + edgeSideLoadPath);
                String[] extPaths = new String[] {edgeSideLoadPath};

                EdgeOptions edgeOpts = new EdgeOptions();
                edgeOpts.setCapability("extensionPaths", extPaths);
                edgeOpts.setPageLoadStrategy("normal");
                edgeOpts.setCapability(CapabilityType.PLATFORM_NAME, Platform.WINDOWS);
                edgeOpts.setCapability(CapabilityType.VERSION, Setup.getEnvironment());

                return edgeOpts;

            case "firefox":
                return setFirefoxOption();

            case "ie":
                InternetExplorerOptions ieOpts = new InternetExplorerOptions();
                ieOpts.ignoreZoomSettings();
                ieOpts.setPageLoadStrategy(PageLoadStrategy.NORMAL);
                ieOpts.setCapability(CapabilityType.PLATFORM_NAME, Platform.WINDOWS);
                ieOpts.setCapability(CapabilityType.VERSION, Setup.getEnvironment());

                return ieOpts;

            default:
                return null;
        }
    }

    private static ChromeOptions setChromeOption(boolean addExt)
            throws FileNotFoundException, MalformedURLException
    {
        ChromeOptions chromeOpts = new ChromeOptions();

        if (Setup.getEnvironment().equalsIgnoreCase("beta"))
        {
            chromeOpts.setBinary("C:\\Program Files (x86)\\Google\\Chrome Beta\\Application\\chrome.exe");
        }

        if (addExt && Setup.getTestType().equalsIgnoreCase("new"))
        {
            chromeOpts.addExtensions(new File(Setup.getExtensionDir() + Setup.getChromeExtName()));
        }

        if (Setup.getTestType().equalsIgnoreCase("upgrade"))
        {
            chromeOpts.addArguments(generateChromeUserDataDir());
        }

        chromeOpts.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        chromeOpts.addArguments("--start-maximized");
        chromeOpts.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        chromeOpts.setCapability(CapabilityType.VERSION, Setup.getEnvironment());

        return chromeOpts;
    }

    private static String generateChromeUserDataDir()
            throws FileNotFoundException, MalformedURLException
    {
        String userDataDirStr;

        // Load Chrome profiles
        Map<String, String> chromeProfileMap = new ConcurrentHashMap<>();
        List<String[]> csvImport = CsvUtils.importCsv(new FileInputStream(CSV_CHROME_PROFILE_LOCATION));
        for (String[] row : csvImport)
        {
            chromeProfileMap.put(row[0], row[1]);
        }

        if (Setup.getGridServer().equalsIgnoreCase("local"))
        {
            userDataDirStr = "user-data-dir=" + System.getenv("LOCALAPPDATA") + "\\Google\\Chrome\\User Data";
        }
        else
        {
            ChromeOptions tmpOpt = new ChromeOptions();
            tmpOpt.setCapability(CapabilityType.VERSION, Setup.getEnvironment());
            if (Setup.getEnvironment().equalsIgnoreCase("beta"))
            {
                tmpOpt.setBinary("C:\\Program Files (x86)\\Google\\Chrome Beta\\Application\\chrome.exe");
            }
            RemoteWebDriver driver = new RemoteWebDriver(new URL("http://" + Setup.getGridServer() + ":4444/wd/hub"), tmpOpt);
            String[] tmp = GridInfoExtractor.getHostNameAndPort(Setup.getGridServer(), 4444, driver.getSessionId());
            driver.quit();

            userDataDirStr = "user-data-dir=" + chromeProfileMap.get(tmp[0]);
            log.info(chromeProfileMap.get(tmp[0]));
        }

        return userDataDirStr;
    }

    private static FirefoxOptions setFirefoxOption()
    {
        FirefoxProfile firefoxProfile;

        if (Setup.getTestType().equals("new"))
        {
            File file = new File(Setup.getExtensionDir() + Setup.firefoxExtName);
            firefoxProfile = new FirefoxProfile();
            firefoxProfile.addExtension(file);
        }
        else
        {
            firefoxProfile = new  ProfilesIni().getProfile("AutomationProfile");
        }

        firefoxProfile.setAcceptUntrustedCertificates(true);
        firefoxProfile.setAssumeUntrustedCertificateIssuer(false);
        firefoxProfile.setPreference("dom.disable_open_during_load", false);

        FirefoxOptions ffOptions = new FirefoxOptions();
        ffOptions.setProfile(firefoxProfile);
        ffOptions.addPreference("marionette", true);
        ffOptions.addPreference("acceptInsecureCerts", true);
        ffOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);

        ffOptions.setCapability(CapabilityType.PLATFORM_NAME, Platform.WINDOWS);
        ffOptions.setCapability(CapabilityType.VERSION, Setup.getEnvironment());

        return ffOptions;
    }

    private static void storeBrowserInfo(RemoteWebDriver driver)
    {
        Capabilities cap = driver.getCapabilities();
        Setup.setBrowserName(cap.getBrowserName());
        log.info(Setup.getBrowserName());
        Setup.setBrowserVersion(cap.getVersion());
        log.info(Setup.getBrowserVersion());
    }

    public static WebDriver get()
    {
        return webDriver.get();
    }

    /**
     * ------------------------------------------------------ /* [SETVERBUFFER]
     * /*- Sets a new Error Buffer
     * /*------------------------------------------------------
     */
    public static synchronized void setErrorBuffer()
    {
        errorBuffer.set(new StringBuffer());
    }

    /**
     * ------------------------------------------------------ /* [GETVERBUFFER]
     * /*- Returns the Error Buffer
     * /*------------------------------------------------------
     */
    static synchronized StringBuilder getErrorBuffer()
    {
        return new StringBuilder(errorBuffer.get());
    }

    /**
     * ------------------------------------------------------ /*
     * [SETERROREXPECTEDTRUE] /*- Set Verification boolean to True
     * /*------------------------------------------------------
     */
    public static void setErrorExpectedTrue()
    {
        errorExpected.set(true);
    }

    /**
     * ------------------------------------------------------ /*
     * [SETERROREXPECTEDFALSE] /*- Set Verification boolean to False
     * /*------------------------------------------------------
     */
    public static void setErrorExpectedFalse()
    {
        errorExpected.set(false);
    }

    /**
     * ------------------------------------------------------ /*
     * [GETERROREXPECTED] /*- Returns the Verification boolean
     * /*------------------------------------------------------
     */
    private static Boolean getErrorExpected()
    {
        return errorExpected.get();
    }

}
