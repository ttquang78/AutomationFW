package com.uid.common.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.remote.SessionId;

class GridInfoExtractor
{
    private static Logger log = Logger.getLogger(GridInfoExtractor.class.getName());

    GridInfoExtractor() {}

    static String[] getHostNameAndPort(String hostName, int port, SessionId session)
    {
        String[] hostAndPort = new String[2];

        try
        {
            HttpHost host = new HttpHost(hostName, port);
            HttpClient client = HttpClientBuilder.create().build();
            URL sessionURL = new URL("http://" + hostName + ":" + port + "/grid/api/testsession?session="
                            + session);
            BasicHttpEntityEnclosingRequest r = new BasicHttpEntityEnclosingRequest("GET", sessionURL.toExternalForm());
            HttpResponse response = client.execute(host, r);
            JSONObject object = extractObject(response);
            URL myURL = new URL(object.getString("proxyId"));
            if ((myURL.getHost() != null) && (myURL.getPort() != -1))
            {
                hostAndPort[0] = myURL.getHost();
                hostAndPort[1] = Integer.toString(myURL.getPort());
            }
        }
        catch (Exception e)
        {
            log.error(e.getMessage());
        }
        return hostAndPort;
    }

    private static JSONObject extractObject(HttpResponse resp) throws IOException, JSONException
    {
        try (BufferedReader rd =
                new BufferedReader(new InputStreamReader(resp.getEntity().getContent())))
        {
            StringBuilder s = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null)
            {
                s.append(line);
            }
            return new JSONObject(s.toString());
        }
    }
}
