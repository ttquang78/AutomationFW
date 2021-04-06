package com.uid.common.utils;

import java.util.ArrayList;
import java.util.List;

public class Reporter
{

    //--------------------------------------
    public static final String REPORT_LOCATION = "reports/";

    static List<String[]> csvAppDataTestResult = new ArrayList<>();

    public static final String CSV_PROBLEM_APP_EXPORT_LOCATION =
            REPORT_LOCATION + "AppScanning_InvalidAppData.csv";
    private static List<String[]> csvBETestResult = new ArrayList<>();

    private Reporter() {}

    //*********************************************************************//
    //                          FUNCTIONS                                  //
    //*********************************************************************//

    /**
     * [APPENDCVSINFO]:
     * /* -Appends formatted information to a targeted CVS file
     * /*------------------------------------------------------------------------
     */
    static void appendCSVInfo(List<String[]> csvName, String[] appendInfo)
    {
        csvName.add(appendInfo);
    }

    public static void addReport(String[] report)
    {
        csvBETestResult.add(report);
    }

    public static List<String[]> getCsvBETestResult()
    {
        return csvBETestResult;
    }

    public static void setCsvBETestResult(List<String[]> srcItems)
    {
        for (String[] item : srcItems)
        {
            addReport(item);
        }
    }

    //#END CLASS 
}