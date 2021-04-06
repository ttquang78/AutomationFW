/***************************************************************************
 * Copyright (C) 2013 Ping Identity Corporation
 * All rights reserved.
 *
 * The contents of this file are the property of Ping Identity Corporation.
 * You may not copy or use this file, in either source code or executable
 * form, except in compliance with terms set by Ping Identity Corporation.
 * For further information please contact:
 *
 *     Ping Identity Corporation
 *     1001 17th Street Suite 100
 *     Denver, CO 80202
 *     303.468.2900
 *     http://www.pingidentity.com
 *
 **************************************************************************/

package com.uid.common.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class ApplicationData
{
    public static final String JSON_USERNAME_KEY = "learned_xpath_username";
    public static final String JSON_USERNAME_XPATH_KEY = "learned_xpath_username_xpath";
    public static final String JSON_DOC_ID_USERNAME = "learned_doc_id_username";
    public static final String JSON_PSWD_KEY = "learned_xpath_password";
    public static final String JSON_PSWD_XPATH_KEY = "learned_xpath_password_xpath";
    public static final String JSON_DOC_ID_PSW = "learned_doc_id_password";
    public static final String JSON_SUBMIT_KEY = "learned_xpath_click";
    public static final String JSON_SUBMIT_XPATH_KEY = "learned_xpath_click_xpath";
    public static final String JSON_DOC_ID_CLICK = "learned_doc_id_click";

    private String appName;
    private String loginUrl;
    private String newLoginUrl = "";
    private String trainingJson;
    private String learnedUsername = "";
    private String learnedUsernameXpath = "";
    private int learnedDocIdUserName = -1;
    private String learnedPsw = "";
    private String learnedPswXpath = "";
    private int learnedDocIdPsw = -1;
    private String learnedSubmit = "";
    private String learnedSubmitXpath = "";
    private int learnedDocIdSubmit = -1;

    public ApplicationData(String appName, String loginUrl, String trainingJson)
            throws JSONException
    {
        init(appName, loginUrl, "", trainingJson);
    }

    private void init(String appName, String loginUrl, String newURL, String trainingJson)
            throws JSONException
    {
        this.appName = appName;
        this.loginUrl = loginUrl;
        this.newLoginUrl = newURL;
        this.trainingJson = trainingJson;

        JSONObject json = new JSONObject(
                trainingJson.replaceAll("\\\\u0027", "'").replaceAll("\\\\u003d", "="));

        //Collect Username element
        if (json.has(JSON_USERNAME_KEY))
        {
            this.learnedUsername = json.getString(JSON_USERNAME_KEY);
        }
        if (json.has(JSON_USERNAME_XPATH_KEY))
        {
            this.learnedUsernameXpath = json.getString(JSON_USERNAME_XPATH_KEY);
        }
        if (json.has(JSON_DOC_ID_USERNAME))
        {
            this.learnedDocIdUserName = Integer.parseInt(json.getString(JSON_DOC_ID_USERNAME));
        }

        //Collect Password element
        if (json.has(JSON_PSWD_KEY))
        {
            this.learnedPsw = json.getString(JSON_PSWD_KEY);
        }
        if (json.has(JSON_PSWD_XPATH_KEY))
        {
            this.learnedPswXpath = json.getString(JSON_PSWD_XPATH_KEY);
        }
        if (json.has(JSON_DOC_ID_PSW))
        {
            this.learnedDocIdPsw = Integer.parseInt(json.getString(JSON_DOC_ID_PSW));
        }

        //Collect Submit element
        if (json.has(JSON_SUBMIT_KEY))
        {
            this.learnedSubmit = json.getString(JSON_SUBMIT_KEY);
        }
        if (json.has(JSON_SUBMIT_XPATH_KEY))
        {
            this.learnedSubmitXpath = json.getString(JSON_SUBMIT_XPATH_KEY);
        }
        if (json.has(JSON_DOC_ID_CLICK))
        {
            this.learnedDocIdSubmit = Integer.parseInt(json.getString(JSON_DOC_ID_CLICK));
        }
    }

    public ApplicationData(String appName, String loginUrl, String newUrl, String trainingJson)
            throws JSONException
    {
        init(appName, loginUrl, newUrl, trainingJson);
    }

    /*
     * Getter/Setter
     */
    public String getAppName()
    {
        return appName;
    }

    public String getLoginUrl()
    {
        return loginUrl;
    }

    public String getNewLoginUrl()
    {
        return newLoginUrl;
    }

    public String getTrainingJson()
    {
        return trainingJson;
    }

    public String getLearnedUserName()
    {
        return learnedUsername;
    }

    public String getLearnedUserNameXpath()
    {
        return learnedUsernameXpath;
    }

    public int getLearnedDocIdUserName()
    {
        return learnedDocIdUserName;
    }

    public String getLearnedPsw()
    {
        return learnedPsw;
    }

    public String getLearnedPswXpath()
    {
        return learnedPswXpath;
    }

    public int getLearnedDocIdPsw()
    {
        return learnedDocIdPsw;
    }

    public String getLearnedSubmit()
    {
        return learnedSubmit;
    }

    public String getLearnedSubmitXpath()
    {
        return learnedSubmitXpath;
    }

    public int getLearnedDocIdSubmit()
    {
        return learnedDocIdSubmit;
    }

    public String toString()
    {
        return appName;
    }
}
