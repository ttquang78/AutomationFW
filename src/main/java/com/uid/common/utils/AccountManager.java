package com.uid.common.utils;

import com.uid.common.config.Setup;
import com.opencsv.CSVReader;
import org.apache.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author thanh.tran
 */
public class AccountManager
{
    private static Logger log = Logger.getLogger(AccountManager.class.getName());

    private Map<String, MyAccount> accountsTest = new HashMap<>();
    private static final AccountManager instance = new AccountManager();

    private AccountManager()
    {
        loadAccounts();
    }

    public static AccountManager getInstance()
    {
        return instance;
    }

    private void loadAccounts()
    {
        String csvFile = getPathFileByEvironment();

        try (CSVReader reader = new CSVReader(new FileReader(csvFile)))
        {
            String[] line;
            while ((line = reader.readNext()) != null)
            {
                accountsTest.put(line[0], new MyAccount(line[1], line[2], line[3]));
            }
        }
        catch (IOException e)
        {
            log.error(e.getMessage());
        }
    }

    public MyAccount getAccountByKey(String key)
    {
        return accountsTest.get(key);
    }

    private String getPathFileByEvironment()
    {
        switch (Setup.getEnvironment())
        {
            case "test":
                return "Config/CDP_TESTAccounts.csv";
            case "ort":
                return "Config/CDP_ORTAccounts.csv";
            case "prod":
                return "Config/CDP_PRODAccounts.csv";
            default:
                return "";
        }
    }

    public class MyAccount
    {
        private String userName;
        private String password;
        private String cloudUrl;

        MyAccount(String userName, String passWord, String url)
        {
            this.userName = userName;
            this.password = passWord;
            this.cloudUrl = url;
        }

        public String getUserName()
        {
            return userName;
        }

        public void setUserName(String userName)
        {
            this.userName = userName;
        }

        public String getPassword()
        {
            return password;
        }

        public String getCloudUrl()
        {
            return cloudUrl;
        }

    }
}
