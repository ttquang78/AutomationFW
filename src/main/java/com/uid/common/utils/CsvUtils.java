package com.uid.common.utils;

import static org.testng.AssertJUnit.fail;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONException;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class CsvUtils
{
    private static final int APP_NAME_COLUMN = 0;
    private static final int APP_LOGIN_URL_COLUMN = 1;
    private static final int APP_TRAINING_JSON_COLUMN = 2;
    private static final int APP_NEW_LOGIN_URL_COLUMN = 3;
    private static String failReason = null;
    // logger
    private static final Logger log = Logger.getLogger(CsvUtils.class);

    private CsvUtils() {}

    public static List<String[]> importCsv(String accFilePath)
    {
        List<String[]> csvImport = new ArrayList<>();

        try (InputStream csvFilePath = new FileInputStream(accFilePath))
        {
            CSVReader reader;
            reader = new CSVReader(new InputStreamReader(csvFilePath));
            csvImport = reader.readAll();
            reader.close();
        }
        catch (IOException e)
        {
            log.error(e.getMessage());
        }

        return csvImport;
    }

    public static List<String[]> importCsv(InputStream csvFilePath)
    {
        CSVReader reader;
        List<String[]> csvImport = new ArrayList<>();
        reader = new CSVReader(new InputStreamReader(csvFilePath));
        try
        {
            csvImport = reader.readAll();
            reader.close();
        }
        catch (IOException e)
        {
            log.error(e.getMessage());
        }

        return csvImport;
    }

    /**
     * Return the problem apps in a list. remove the problem apps from origin
     * list
     *
     * @param csvImport               the original list to be filtered
     * @param problemAppCsvExportPath the file path to save problem apps
     */
    public static ApplicationData[][] validateCsvAndOutputInvalidRows(List<String[]> csvImport,
            String problemAppCsvExportPath) throws IOException, JSONException
    {
        List<String[]> validAppList = new ArrayList<>();
        List<String[]> problemAppList = new ArrayList<>();
        for (String[] row : csvImport)
        {
            try
            {
                assertAppContainsEnoughData(row);
                assertAppNameValid(row);
                assertAppLoginUrlValid(row);
                assertAppJsonValid(row);
                validAppList.add(row);
            }
            catch (AssertionError ae)
            {
                String[] appResult = {row[0], "FAILED - INVALID DATA"};
                Reporter.appendCSVInfo(Reporter.csvAppDataTestResult, appResult);

                problemAppList.add(appendTextColumn(row, failReason));
            }
        }
        if (!problemAppList.isEmpty())
        {
            exportCsv(problemAppList, problemAppCsvExportPath);
        }
        log.info("[INFO] - There were " + problemAppList.size() + " applications "
                + "removed due to invalid data");

        ApplicationData[][] appData = new ApplicationData[validAppList.size()][1];
        int j;
        for (j = 0; j < validAppList.size(); j++)
        {
            appData[j][0] = CsvUtils.defineApplicationData(validAppList.get(j), "true");
        }

        return appData;
    }

    public static void exportCsv(List<String[]> csvData, String exportLocation) throws IOException
    {
        File file = new File(exportLocation);
        file.getParentFile().mkdirs();
        CSVWriter writer = new CSVWriter(new FileWriter(exportLocation), ',');
        writer.writeAll(csvData);
        writer.close();
    }

    private static ApplicationData defineApplicationData(String[] row, String isTestable)
            throws JSONException
    {
        String appName = row[APP_NAME_COLUMN];
        String newURL = "";
        if (row.length == 4)
        {
            newURL = row[APP_NEW_LOGIN_URL_COLUMN];
        }
        if (isTestable != null && Boolean.valueOf(isTestable))
        {
            String loginUrl = row[APP_LOGIN_URL_COLUMN];
            String trainingJson = row[APP_TRAINING_JSON_COLUMN];
            return new ApplicationData(appName, loginUrl, newURL, trainingJson);
        }
        return new ApplicationData(appName, " ", newURL,
                "{'learned_xpath_username':'#login-form-username','learned_xpath_password':'#login-form-password','learned_xpath_click':'#login-form-submit'}");
    }

    /*
     * HELPER Methods
     */
    private static String[] appendTextColumn(String[] row, String newColumnText)
    {
        final int N = row.length;
        row = Arrays.copyOf(row, N + 1);
        row[N] = newColumnText;
        return row;
    }

    /**
     * Verify that the correct number of values in each row
     */
    private static void assertAppContainsEnoughData(String[] row)
    {
        if (row.length < 3)  //additional template can have 4 columns
        {
            String failReason =
                    "[FAIL] - There were not the expected number of columns of data for this application";
            CsvUtils.failReason = failReason;
            fail(failReason);
        }
    }

    private static void assertAppNameValid(String[] row)
    {
        if ((row[APP_NAME_COLUMN].length() <= 1))
        {
            String failReason = "[FAIL] - The Application Name is blank";
            CsvUtils.failReason = failReason;
            fail(failReason);
        }
    }

    private static void assertAppLoginUrlValid(String[] row)
    {
        if (!(row[APP_LOGIN_URL_COLUMN].startsWith("http")))
        {
            String failReason = "[FAIL] - The application login URL is not a valid url";
            CsvUtils.failReason = failReason;
            fail(failReason);
        }
    }

    private static void assertAppJsonValid(String[] row)
    {
        String trainingJson = row[APP_TRAINING_JSON_COLUMN];

        if (!trainingJson.contains(ApplicationData.JSON_USERNAME_KEY) && !trainingJson
                .contains(ApplicationData.JSON_USERNAME_XPATH_KEY))
        {
            String failReason = "[FAIL] - The training json does not contain username element";
            CsvUtils.failReason = failReason;
            fail(failReason);
        }
        if (!trainingJson.contains(ApplicationData.JSON_PSWD_KEY) && !trainingJson
                .contains(ApplicationData.JSON_PSWD_XPATH_KEY))
        {
            String failReason = "[FAIL] - The training json does not contain passWord element";
            CsvUtils.failReason = failReason;
            fail(failReason);
        }
        if (!trainingJson.contains(ApplicationData.JSON_SUBMIT_KEY) && !trainingJson
                .contains(ApplicationData.JSON_SUBMIT_XPATH_KEY))
        {
            String failReason = "[FAIL] - The training json does not contain SUBMIT element";
            CsvUtils.failReason = failReason;
            fail(failReason);
        }
    }
}
