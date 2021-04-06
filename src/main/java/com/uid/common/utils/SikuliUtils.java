package com.uid.common.utils;

import com.uid.common.config.Setup;
import com.uid.common.config.Verification;
import com.uid.common.config.WebdriverManager;
import org.sikuli.script.Key;
import org.sikuli.script.Pattern;
import org.sikuli.script.Screen;

public class SikuliUtils
{
    private SikuliUtils(){}

    public static boolean waitElementDisplay(String imgPath)
    {
        return waitElementDisplay(imgPath, Setup.DEFAULT_TIME_OUT);
    }

    public static boolean waitElementDisplay(String imgPath, int seconds)
    {
        boolean isDisplay = true;

        WebdriverManager.setErrorExpectedTrue();

        try
        {
            Screen screen = new Screen();
            Pattern img = new Pattern(imgPath);
            screen.wait(img, seconds);
        }
        catch (Exception e)
        {
            isDisplay = false;
        }

        WebdriverManager.setErrorExpectedFalse();

        return isDisplay;
    }

    public static boolean waitClickElement(String imgPath)
    {
        return waitClickElement(imgPath, Setup.DEFAULT_TIME_OUT);
    }

    public static boolean waitClickElement(String imgPath, int seconds)
    {
        Verification.logSubStep("CLICK " + imgPath);

        boolean isDisplay = true;

        WebdriverManager.setErrorExpectedTrue();

        if (waitElementDisplay(imgPath, seconds))
        {
            try
            {
                Screen screen = new Screen();
                Pattern img = new Pattern(imgPath);
                screen.click(img);
            }
            catch (Exception e)
            {
                isDisplay = false;
            }
        }
        else
        {
            isDisplay = false;
        }

        WebdriverManager.setErrorExpectedFalse();

        return isDisplay;
    }

    public static boolean rightClickElement(String imgPath)
    {
        return rightClickElement(imgPath, Setup.DEFAULT_TIME_OUT);
    }

    public static boolean rightClickElement(String imgPath, int seconds)
    {
        boolean isDisplay = true;

        WebdriverManager.setErrorExpectedTrue();

        if (waitElementDisplay(imgPath, seconds))
        {
            try
            {
                Screen screen = new Screen();
                Pattern img = new Pattern(imgPath);
                screen.find(img);
                screen.rightClick();
            }
            catch (Exception e)
            {
                isDisplay = false;
            }
        }
        else
        {
            isDisplay = false;
        }

        WebdriverManager.setErrorExpectedFalse();

        return isDisplay;
    }

    public static boolean type(String imgPath, int seconds, String value)
    {
        boolean isDisplay = true;

        WebdriverManager.setErrorExpectedTrue();

        if (waitElementDisplay(imgPath, seconds))
        {
            try
            {
                Screen screen = new Screen();
                Pattern img = new Pattern(imgPath);
                screen.type(img, value + Key.ENTER);
            }
            catch (Exception e)
            {
                isDisplay = false;
            }
        }
        else
        {
            isDisplay = false;
        }

        WebdriverManager.setErrorExpectedFalse();

        return isDisplay;

    }

}
