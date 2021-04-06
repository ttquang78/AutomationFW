package com.uid.common.utils;

import com.uid.common.config.Setup;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class FileUtil
{
    private static Logger log = Logger.getLogger(FileUtil.class.getName());

    private FileUtil() {}

    private static void archiveExtension()
    {
        String archivePath = Setup.getArchivedBEDir();
        File archiveFolder = new File(archivePath);

        if (archivePath.isEmpty() || Setup.getBEVersion().isEmpty() || !archiveFolder.exists())
        {
            log.info(
                    "!!! WARNING: Archive path is invalid. Please set a valid path in setup.properties. Download process is stopped without any files.");
        }
        else
        {
            String path;
            if (Setup.getEnvironment().toLowerCase().startsWith("dev"))
            {
                path = Setup.getArchivedBEDir() + "\\DEV";
            }
            else
            {
                path = Setup.getArchivedBEDir() + Setup.getEnvironment();
            }
            path = path.replace("beta", "prod");

            //Add a sub folder with name is beVersion
            path += "\\" + Setup.getBEVersion();
            File reportFolder = new File(path);

            //Just download if sub folder is not exist
            if (!reportFolder.exists())
            {
                reportFolder.mkdir();
                // Download extension to archive
                downloadUrlFile(Setup.getChromeExtURL(), Setup.getChromeExtName(), path);
                String firefoxExtName = "PingOne-Extension.xpi";
                downloadUrlFile(Setup.getFirefoxExtURL(), firefoxExtName, path);
                String ieExtName = "PingOne-Extension.msi";
                downloadUrlFile(Setup.getIExpExtUrl(), ieExtName, path);
                String edgeExtName = "PingOne-Extension.appx";
                downloadUrlFile(Setup.getEdgeExtURL(), edgeExtName, path);
            }
        }
    }

    private static void copyBE(String srcPath, String destPath)
    {
        int count = 0;
        while (count < 3)
        {
            try
            {
                File source = new File(srcPath);
                File dest = new File(destPath);

                FileUtils.copyFileToDirectory(source, dest);
                log.info("Copied successfully from " + srcPath);
                break;
            }
            catch (IOException e)
            {
                log.error("Failed to copy from " + srcPath + " times: " + count);
            }
            count++;
        }
    }

    /**
     * ---------------------------------------------------------- /* [
     * DOWNLOADEXTENSION:] /* -Download extension based on browser type
     * /*----------------------------------------------------------
     */
    public static void downloadExtension()
    {
        archiveExtension();
        String path;
        if (Setup.getEnvironment().toLowerCase().startsWith("dev"))
        {
            path = Setup.getArchivedBEDir() + "\\DEV";
        }
        else
        {
            path = Setup.getArchivedBEDir() + Setup.getEnvironment();
        }
        path = path.replace("beta", "prod");

        //Add a sub folder with name is beVersion
        path += "\\" + Setup.getBEVersion();
        File reportFolder = new File(path);

        if (reportFolder.exists() && !Setup.getBEVersion().isEmpty())
        {
            // Copy Chrome from archive folder to working folder
            if (Setup.getBrowserType().equalsIgnoreCase("chrome"))
            {
                copyBE(path + "\\" + Setup.getChromeExtName(), Setup.getExtensionDir());
            }
            else if (Setup.getBrowserType().equalsIgnoreCase("firefox"))
            {
                copyBE(path + "\\" + Setup.getFirefoxExtName(), Setup.getExtensionDir());
            }
        }
        else
        {
            if (Setup.getBrowserType().equalsIgnoreCase("chrome"))
            {
                downloadUrlFile(Setup.getChromeExtURL(), Setup.getChromeExtName(), Setup.getExtensionDir());
            }
            else if (Setup.getBrowserType().equalsIgnoreCase("firefox"))
            {
                downloadUrlFile(Setup.getFirefoxExtURL(), Setup.getFirefoxExtName(), Setup.getExtensionDir());
            }
        }
    }

    private static void downloadUrlFile(String fileAddress, String localFileName,
            String destinationDir)
    {
        int size = 1024;
        URLConnection uCon;

        InputStream is;
        try (OutputStream outStream = new BufferedOutputStream(
                new FileOutputStream(destinationDir + "/" + localFileName)))
        {
            URL url;
            byte[] buf;
            int byteRead;
            int byteWritten = 0;
            url = new URL(fileAddress);

            File folder = new File(destinationDir);
            folder.mkdirs();// will create a folder to hold the downloaded extension

            uCon = url.openConnection();
            is = uCon.getInputStream();
            buf = new byte[size];
            while ((byteRead = is.read(buf)) != -1)
            {
                outStream.write(buf, 0, byteRead);
                byteWritten += byteRead;
            }
            log.info("===========================================================");
            log.info("Downloaded Successfully.");
            log.info("File name:\"" + localFileName + "\"\nNo of bytes :" + byteWritten);
            log.info("===========================================================");
            is.close();
        }
        catch (Exception e)
        {
            log.error(e.getMessage());
        }
    }

}
