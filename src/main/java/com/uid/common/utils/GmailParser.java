package com.uid.common.utils;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.SearchTerm;

public class GmailParser
{
    private static Logger log = Logger.getLogger(GmailParser.class.getName());

    String username = "thanhtestkms";
    private String gmailPass = "!QAZxsw2";
    private static final String HOST = "pop.gmail.com";
    private static final String PORT = "993";
    private static final String PROTOCOL = "pop3s";
    protected static final Properties properties;

    static
    {
        properties = new Properties();
        properties.put("mail.pop3.HOST", HOST);
        properties.put("mail.pop3.port", PORT);
        properties.put("mail.pop3.starttls.enable", "true");
    }

    public GmailParser()
    {
        // Do nothing for now
    }

    public String getVerifyCodeFromGmail(String subject)
    {
        try
        {
            Session emailSession = Session.getDefaultInstance(properties);

            //create the POP3 store object and connect with the pop server
            Store store = emailSession.getStore(PROTOCOL);
            store.connect(HOST, username, gmailPass);

            //create the folder object and open it
            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_WRITE);
            SearchTerm term = new SearchTerm()
            {
                public boolean match(Message message)
                {
                    try
                    {
                        if (message.getSubject().contains(subject))
                        {
                            return true;
                        }
                    }
                    catch (MessagingException ex)
                    {
                        log.error(ex.getMessage());
                    }
                    return false;
                }
            };

            // Search message with subject
            //Finally delete mail
            Message[] messages = emailFolder.search(term);
            String res = getTextFromMessage(messages[0]);
            messages[0].setFlag(Flags.Flag.DELETED, true);

            //close the store and folder objects
            emailFolder.close(true);
            store.close();

            return res;

        }
        catch (Exception e)
        {
            log.error(e.getMessage());
            return "";
        }
    }

    private String getTextFromMessage(Message message) throws MessagingException, IOException
    {
        String result = "";
        if (message.isMimeType("text/plain"))
        {
            result = message.getContent().toString();
        }
        else if (message.isMimeType("multipart/*"))
        {
            MimeMultipart mimeMultipart = (MimeMultipart)message.getContent();
            result = getTextFromMimeMultipart(mimeMultipart);
        }
        return result;
    }

    private String getTextFromMimeMultipart(MimeMultipart mimeMultipart)
            throws MessagingException, IOException
    {
        StringBuilder result = new StringBuilder();
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++)
        {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain"))
            {
                result.append(bodyPart.getContent());
                break;
            }
            else if (bodyPart.isMimeType("text/html"))
            {
                String html = (String)bodyPart.getContent();
                Document document = Jsoup.parse(html);
                Elements elements = document.select(
                        "html > body > div > table.body-wrap > tbody > tr > td.container > div.content > table > tbody > tr > td > p:nth-child(3)");
                result = new StringBuilder(elements.get(0).text());
            }
            else if (bodyPart.getContent() instanceof MimeMultipart)
            {
                result.append(getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent()));
            }
        }
        return result.toString();
    }

    public void clearAllOldMail()
    {
        try
        {
            Session emailSession = Session.getDefaultInstance(properties);
            Store store = emailSession.getStore(PROTOCOL);
            store.connect(HOST, username, gmailPass);

            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_WRITE);

            // Search message with subject
            //Finally delete mail
            Message[] messages = emailFolder.getMessages();
            for (Message message : messages)
            {
                message.setFlag(Flags.Flag.DELETED, true);
            }

            emailFolder.close(true);
            store.close();

        }
        catch (MessagingException e)
        {
            log.error(e.getMessage());
        }
    }
}
