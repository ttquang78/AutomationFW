package com.uid.common.config;

import java.io.*;
// Get file user dir
// Get properties from file
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import com.uid.common.utils.CsvUtils;

public class Setup
{
    public enum View
    {
        ORGANIZATION, PERSONAL
    }

    public enum ButtonName
    {
        ACCEPT("Accept"), LEARN("Learn"), NEXT("Next"), TRY_AGAIN("Try Again"), SAVE("Save"),
        SELECT_MANUALLY("Select Manually"), SKIP_THIS_STEP("Skip this step");

        private String button;

        ButtonName(String name)
        {
            this.button = name;
        }

        public String getBtnName()
        {
            return this.button;
        }
    }

    public static final String SAMPLE_USERNAME = "test@test.com";
    public static final String SAMPLE_PSW = "testpassword";

    // Default Value
    private static String defaultApp = "Evernote";
    private static String defaultAppUrl = "";
    private static int maxRetryCount = 1;

    // Execute variables
    private static boolean isForceRun = false;

    // TEST variables
    private static String testName; // Hold curent test name

    // SETUP variables
    private static String archivedBEDir = "";

    // WEB environment variables (from setup.properties)
    private static String os;
    private static String gridServer;
    private static String environment = "prod"; // "test-" or "prod-"
    private static String webRuntime;
    private static String browserType;
    private static String beVersion = "";
    private static String beUser;
    private static String testType = "new"; // Upgrade or New
    private static String dataFilesPath; // path to the DataFiles folder, when using datadriven test
    private static String chromeExtURL; // extension download url for chrome
    private static String chromeExtName = "PingOne-Extension.crx";
    static String firefoxExtName = "PingOne-Extension.xpi";
    private static String firefoxExtURL; // extension download url for firefox
    private static String iexpExtUrl; // extension download url for IE (x64)
    private static String extensionDir = "Extension/";
    private static String edgeExtURL; // extension download url for Edge
    private static String browserName = "";
    private static String browserVersion = "";
    private static String trainingDataForTrainingManually = "DataFiles/ManualTrainingData.csv";
    private static String trainingDataForTrainingFD = "DataFiles/FDTrainingData.csv";
    private static String personalAppData = "DataFiles/personalAppData.csv";
    private static String chromeDriverPath;
    private static String ieDriverPath;
    private static String geckoDriverPath;
    private static String edgeDriverPath;
    private static String reportFilePath;

    static Map<String, Integer> dataMap = new ConcurrentHashMap<>();
    static Map<String, String> result = new ConcurrentHashMap<>();

    static Boolean usedTestAccounts = false;
    public static final int DEFAULT_TIME_OUT = 10;

    // Mode Test
    private static String mode = "production";

    public static String getArchivedBEDir()
    {
        return archivedBEDir;
    }

    public static String getBeUser()
    {
        return beUser;
    }

    public static String getBEVersion()
    {
        return beVersion;
    }

    public static String getBrowserName()
    {
        return browserName;
    }

    public static String getBrowserType()
    {
        return browserType;
    }

    public static String getBrowserVersion()
    {
        return browserVersion;
    }

    public static String getChromeDriverPath()
    {
        return chromeDriverPath;
    }

    public static String getChromeExtName()
    {
        return chromeExtName;
    }

    public static String getChromeExtURL()
    {
        return chromeExtURL;
    }

    public static String getDataFilesPath()
    {
        return dataFilesPath;
    }

    public static String getDefaultApp()
    {
        return defaultApp;
    }

    public static String getDefaultAppUrl()
    {
        return defaultAppUrl;
    }

    public static String getEdgeDriverPath()
    {
        return edgeDriverPath;
    }

    public static String getEdgeExtURL()
    {
        return edgeExtURL;
    }

    public static String getEnvironment()
    {
        return environment;
    }

    public static String getExtensionDir()
    {
        return extensionDir;
    }

    public static String getFirefoxExtName()
    {
        return firefoxExtName;
    }

    public static String getFirefoxExtURL()
    {
        return firefoxExtURL;
    }

    public static String getGeckoDriverPath()
    {
        return geckoDriverPath;
    }

    public static String getGridServer()
    {
        return gridServer;
    }

    public static String getIeDriverPath()
    {
        return ieDriverPath;
    }

    public static String getIExpExtUrl()
    {
        return iexpExtUrl;
    }

    public static int getMaxRetryCount()
    {
        return maxRetryCount;
    }

    public static String getMode()
    {
        return mode;
    }

    public static String getOS()
    {
        return os;
    }

    public static String getPersonalAppData()
    {
        return personalAppData;
    }

    public static String getReportFilePath()
    {
        return reportFilePath;
    }

    public static String getTestName()
    {
        return testName;
    }

    public static String getTestType()
    {
        return testType;
    }

    public static String getTrainingDataForTrainingFD()
    {
        return trainingDataForTrainingFD;
    }

    public static String getTrainingDataForTrainingManually()
    {
        return trainingDataForTrainingManually;
    }

    public static String getWebRuntime()
    {
        return webRuntime;
    }

    public static void setBrowserName(String value)
    {
        browserName = value;
    }

    public static void setBrowserType(String value)
    {
        browserType = value;
    }

    public static void setBEVersion(String version)
    {
        beVersion = version;
    }

    public static void setBrowserVersion(String value)
    {
        browserVersion = value;
    }

    public static void setMaxRetryCount(int value)
    {
        maxRetryCount = value;
    }

    public static void setMode(String value)
    {
        mode = value;
    }

    public static void setTestName(String value)
    {
        testName = value;
    }

    public static void setWebRuntime(String value)
    {
        webRuntime = value;
    }

    public static boolean isForceRun()
    {
        return isForceRun;
    }

    // *********************************************************************//
    // FUNCTIONS //
    // *********************************************************************//

    /**
     * ---------------------------------------------------------- /* [
     * INITTESTENV:] /* -Initialize the test environment properties, reportng
     * properties /* -For Web testing
     * /*----------------------------------------------------------
     */
    public static void initTestEnv() throws IOException
    {
        //Read setup Properties
        Properties properties = new Properties();
        properties.load(Setup.class.getResourceAsStream("/setup.properties"));

        reportFilePath = properties.getProperty("ReportFilePath");
        isForceRun = Boolean.parseBoolean(properties.getProperty("ForceRun"));
        chromeDriverPath = properties.getProperty("ChromeDriverPath");
        ieDriverPath = properties.getProperty("IEDriverPath");
        geckoDriverPath = properties.getProperty("GeckoDriverPath");
        edgeDriverPath = properties.getProperty("EdgeDriverPath");
        os = properties.getProperty("OS");
        beUser = properties.getProperty("BEUser");
        defaultApp = properties.getProperty("DefaultAppTest");
        defaultAppUrl = properties.getProperty("DefaultAppURL");
        browserType = properties.getProperty("BrowserType");
        gridServer = properties.getProperty("RunTime");
        if (gridServer.equalsIgnoreCase("local"))
        {
            webRuntime = gridServer;
        }

        if (!properties.getProperty("ArchiveBEPath").toLowerCase().contains("${archivepath}"))
        {
            archivedBEDir = properties.getProperty("ArchiveBEPath");
            if (!archivedBEDir.endsWith("\\"))
            {
                archivedBEDir += "\\";
            }
        }

        dataFilesPath = properties.getProperty("DataFilesPath");

        // Store environment and build paths
        environment = properties.getProperty("Environment").toLowerCase();
        if (environment.toLowerCase().startsWith("dev"))
        {
            chromeExtURL = properties.getProperty("DevChromeExtURL");
            firefoxExtURL = properties.getProperty("DevFirefoxExtURL");
            iexpExtUrl = properties.getProperty("DevIExpExtURL");
            edgeExtURL = properties.getProperty("DevEdgeExtURL");
        }
        else
        {
            // Test environment (Webportal+extension)
            if (environment.toLowerCase().contains("test"))
            {
                chromeExtURL = properties.getProperty("StagChromeExtURL");
                firefoxExtURL = properties.getProperty("StagFirefoxExtURL");
                iexpExtUrl = properties.getProperty("StagIExpExtURL");
                edgeExtURL = properties.getProperty("StagEdgeExtURL");
            }
            // ORT environment (Webportal+extension)
            else if (environment.toLowerCase().contains("ort"))
            {
                chromeExtURL = properties.getProperty("OrtChromeExtURL");
                firefoxExtURL = properties.getProperty("OrtFirefoxExtURL");
                iexpExtUrl = properties.getProperty("OrtIExpExtURL");
                edgeExtURL = properties.getProperty("OrtEdgeExtURL");
            }
            // Default Production environment (Webportal+extension)
            else
            {
                chromeExtURL = properties.getProperty("ProdChromeExtURL");
                firefoxExtURL = properties.getProperty("ProdFirefoxExtURL");
                iexpExtUrl = properties.getProperty("ProdIExpExtURL");
                edgeExtURL = properties.getProperty("ProdEdgeExtURL");
            }
        }

        // Store BE Version
        String beInfoFile;
        if (browserType.equals("ie"))
        {
            beInfoFile =
                    "DataFiles/internet explorer_" + environment.toLowerCase() + "_version.csv";
        }
        else
        {
            beInfoFile =
                    "DataFiles/" + browserType + "_" + environment.toLowerCase() + "_version.csv";
        }
        beInfoFile = beInfoFile.replace("beta", "prod");
        File filePath = new File(beInfoFile);
        if (filePath.exists())
        {
            List<String[]> info = CsvUtils.importCsv(new FileInputStream(beInfoFile));
            beVersion = info.get(1)[0];
        }

        testType = properties.getProperty("TestType").toLowerCase();

        // Set SCREENSHOTS folder path (take/save capture when failure occurs):
        Verification.screenshotPath = "target/Screenshots/";

        // Set SYSTEM properties:
        // -TestNG properties
        System.setProperty("org.uncommons.reportng.stylesheet", "Config/reportng.css");
        System.setProperty("org.uncommons.reportng.escape-output", "false");

    }

    public static String getFinalResult()
    {
        String finalResult = "PASSED";
        for (Map.Entry<String, String> entry : result.entrySet()) {
            if (entry.getValue().equalsIgnoreCase("failed"))
            {
                finalResult = "FAILED";
                break;
            }
        }

        return finalResult;
    }

    public static boolean isResultExisting()
    {
        return result.size() > 0;
    }

    // #END SETUP CLASS
}
