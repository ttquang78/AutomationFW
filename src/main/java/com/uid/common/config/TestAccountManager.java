package com.uid.common.config;

import com.uid.common.utils.CsvUtils;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TestAccountManager
{

    private static Logger log = Logger.getLogger(TestAccountManager.class.getName());

    private TestAccountManager() {}

    //Test Account (String Array) as localThread
    private static final ThreadLocal<String[]> accountThreadLocal = new ThreadLocal();

    //CDP Url as localthread
    private static final ThreadLocal<String> CDPUrlThreadLocal = new ThreadLocal();

    //admin Url as localthread
    private static final ThreadLocal<String> adminUrlThreadLocal = new ThreadLocal();

    // PRE-MADE TEST-ACCOUNTS variables
    protected static final Map<String, String[]> testAccountsMap = new ConcurrentHashMap<>();
    private static final String CSV_TESTACCOUNTS_LOCATION = "Config/TestAccounts.csv";
    private static final String CSV_ORTACCOUNTS_LOCATION = "Config/ORTAccounts.csv";
    private static final String CSV_PRODACCOUNTS_LOCATION = "Config/ProdAccounts.csv";

    public static int getNumberOfLoadedAcc()
    {
        return testAccountsMap.size();
    }

    private static String getAccFilePath()
    {
        String filePath;

        switch (Setup.getEnvironment().toLowerCase())
        {
            case "test":
                filePath = CSV_TESTACCOUNTS_LOCATION;
                break;

            case "ort":
                filePath = CSV_ORTACCOUNTS_LOCATION;
                break;

            default:
                filePath = CSV_PRODACCOUNTS_LOCATION;
                break;
        }

        Verification.logSubStep("Load account from " + filePath);
        return filePath;
    }

    private static void copyAccount(String[] account, int key)
    {
        String[] tempAccountInfo = new String[account.length - 2];
        System.arraycopy(account, 2, tempAccountInfo, 0, account.length - 2);
        testAccountsMap.put(String.valueOf(key), tempAccountInfo);
    }

    private static int findByUser(List<String[]> accountItems)
    {
        int index = -1;
        for (int i = 0; i < accountItems.size(); i++)
        {
            if (accountItems.get(i)[2].equalsIgnoreCase(Setup.getBeUser()))
            {
                index = i;
                break;
            }
        }

        return index;
    }

    private static int findByDockMode(List<String[]> accountItems)
    {
        int index = -1;
        for (int i = 0; i < accountItems.size(); i++)
        {
            if (accountItems.get(i)[1].equalsIgnoreCase(Setup.getMode()))
            {
                index = i;
                break;
            }
        }

        return index;
    }

    private static int findByDockModeAndMachine(List<String[]> accountItems)
    {
        int index = -1;
        for (int i = 0; i < accountItems.size(); i++)
        {
            if (accountItems.get(i)[0].equalsIgnoreCase(Setup.getWebRuntime())
                    && accountItems.get(i)[1].equalsIgnoreCase(Setup.getMode()))
            {
                index = i;
                break;
            }
        }

        return index;
    }

    private static void loadAccountsByDockMode(List<String[]> accountItems, int startIndex)
    {
        int key = 0;
        for (int i = startIndex; i < accountItems.size(); i++)
        {
            if (accountItems.get(i)[1].equalsIgnoreCase(Setup.getMode()))
            {
                copyAccount(accountItems.get(i), key);
                key++;
            }
        }
    }

    private static void loadAccountsByDockModeAndMachine(List<String[]> accountItems,
            int startIndex)
    {
        int key = 0;
        for (int i = startIndex; i < accountItems.size(); i++)
        {
            if (accountItems.get(i)[0].equalsIgnoreCase(Setup.getWebRuntime())
                    && accountItems.get(i)[1].equalsIgnoreCase(Setup.getMode()))
            {
                copyAccount(accountItems.get(i), key);
                key++;
            }
        }
    }

    /**
     * ---------------------------------------------------------- /* [
     * LOADTESTACCOUNTS:] /* -Will read all test accounts info from spreadsheet
     * /*----------------------------------------------------------
     */
    public static void loadTestAccounts()
    {
        List<String[]> accountItems = CsvUtils.importCsv(getAccFilePath());

        Verification.logSubStep("Loaded from csv: " + accountItems.size() + " data rows");
        Verification.logSubStep("BE User: " + Setup.getBeUser());
        Verification.logSubStep("Mode: " + Setup.getMode());
        Verification.logSubStep("Grid info: " + Setup.getGridServer());
        Verification.logSubStep("Machine info: " + Setup.getWebRuntime());

        int index;
        if (Setup.getBeUser().isEmpty())
        {
            index = findByDockModeAndMachine(accountItems);
            if (index == -1)
            {
                index = findByDockMode(accountItems);
                if (index != -1)
                {
                    loadAccountsByDockMode(accountItems, index);
                }
            }
            else
            {
                loadAccountsByDockModeAndMachine(accountItems, index);
            }
        }
        else
        {
            index = findByUser(accountItems);
            if (index != -1)
            {
                copyAccount(accountItems.get(index), 0);
            }
        }

        Verification.logSubStep("Loaded: " + testAccountsMap.size() + " accounts");
    }


    /**
     * ------------------------------------
     * /* [SET]
     * /* - Find a free test account and set it as ThreadLocal
     * /*------------------------------------
     */
    public static synchronized void set()
    {
        //Get the size of the current account map list
        int mapSize = testAccountsMap.size();

        //Temp String array to hold test account
        String[] tempAccount = new String[5];
        //Set flag when a free test account is found
        boolean gotFreeAccount = false;
        //Index holder
        int key = 0;

        //Loop through the map and find a free test accounts
        while (key < mapSize && !gotFreeAccount)
        {
            tempAccount = testAccountsMap.get(String.valueOf(key));

            //Check if account is "free"
            if (!tempAccount[4].equalsIgnoreCase("Free"))
            {
                key++;
            }
            else
            {
                gotFreeAccount = true;
            }
        }

        //Mark current test account status to "used"
        String[] usedAccount =
                {tempAccount[0], tempAccount[1], tempAccount[2], tempAccount[3], "Used"};
        //Find and Save it back to the map
        testAccountsMap.put(String.valueOf(key), usedAccount);

        //Save test account with respective index(key)
        String[] accountInfo = {tempAccount[0], tempAccount[1], tempAccount[2], tempAccount[3],
                String.valueOf(key)};
        accountThreadLocal.set(accountInfo);

        if (Setup.getEnvironment().toLowerCase().contains("test") || Setup.getEnvironment().toLowerCase()
                .contains("ort"))
        {
            setCDPUrl("https://" + Setup.getEnvironment() + "-desktop.pingone.com/" + tempAccount[3]);

            setAdminUrl(
                    "https://" + Setup.getEnvironment() + "-admin.pingone.com/web-portal/dashboard#");
        }
        else
        {
            setCDPUrl("https://desktop.pingone.com/" + tempAccount[3]);

            setAdminUrl("https://admin.pingone.com/web-portal/dashboard#");
        }

        //***DEBUG
        tempAccount = testAccountsMap.get(String.valueOf(key));
        log.info("Take Test-Account: " + tempAccount[0]);
        //*******

        //Set global boolean to true (to reset in aftermethod)
        Setup.usedTestAccounts = true;
    }

    /**
     * ---------------------------
     * /* [SETCDPURL]
     * /* -Set the CDP Url for the current test account
     * /* -Retrieved from Setup
     * /*---------------------------
     */
    private static void setCDPUrl(String url)
    {
        CDPUrlThreadLocal.set(url);
    }

    /**
     * ---------------------------
     * /* [SETADMINURL]
     * /* -Set the Admin Url for the current test account
     * /* -Retrieved from Setup
     * /*---------------------------
     */
    private static void setAdminUrl(String url)
    {
        adminUrlThreadLocal.set(url);
    }

    /**
     * ---------------------------
     * /* [RESET]
     * /* -Sets the current test account back to free in the map
     * /*---------------------------
     */
    public static void reset()
    {

        //Mark current test account status to "free"
        String[] resetAccountInfo = {get()[0], get()[1], get()[2], get()[3], "Free"};
        //Find index(key) and Save it back to the map
        testAccountsMap.put(get()[4], resetAccountInfo);

        //***DEBUG
        String[] tempAccount = testAccountsMap.get(get()[4]);
        log.info("Reset Test-Account: " + tempAccount[0]);
        //*******
    }

    /**
     * ---------------------------
     * /* [GET]
     * /* -Returns the current test account's full information
     * /*---------------------------
     */
    public static String[] get()
    {
        return accountThreadLocal.get();
    }

    /**
     * ---------------------------
     * /* [GETUSERNAME]
     * /* -Returns the username of the current test account
     * /*---------------------------
     */
    public static String getUserName()
    {
        return get()[0];
    }

    /**
     * ---------------------------
     * /* [GETPASSWORD]
     * /* -Returns the password of the current test account
     * /*---------------------------
     */
    public static String getPassword()
    {
        return get()[1];
    }

    /**
     * ---------------------------
     * /* [GETPRIVACYKEY]
     * /* -Returns the privacykey of the current test account
     * /*---------------------------
     */
    public static String getPrivacyKey()
    {
        return get()[2];
    }

    /**
     * ---------------------------
     * /* [GETCDPURL]
     * /* -Returns the current test CDP Url
     * /*---------------------------
     */
    public static String getCDPUrl()
    {
        return CDPUrlThreadLocal.get();
    }

    /**
     * ---------------------------
     * /* [GETADMINURL]
     * /* -Returns the current test CDP Url
     * /*---------------------------
     */
    public static String getAdminUrl()
    {
        return adminUrlThreadLocal.get();
    }

    //#ENDCLASS    
}
