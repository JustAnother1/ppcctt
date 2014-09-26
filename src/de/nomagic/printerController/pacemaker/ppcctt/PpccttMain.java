/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses/>
 *
 */

package de.nomagic.printerController.pacemaker.ppcctt;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

/**
 * @author Lars P&ouml;tter
 * (<a href=mailto:Lars_Poetter@gmx.de>Lars_Poetter@gmx.de</a>)
 *
 */
public class PpccttMain
{
    private final Logger log = (Logger) LoggerFactory.getLogger(this.getClass().getName());
    private String dutConnect;
    private UartConnection client;
    private int bootLoaderDelayMs = 0;
    private String FirmwareConfigurationFileName = null;

    public PpccttMain()
    {
    }

    public void startLogging(final String[] args)
    {
        int numOfV = 0;
        for(int i = 0; i < args.length; i++)
        {
            if(true == "-v".equals(args[i]))
            {
                numOfV ++;
            }
        }

        // configure Logging
        switch(numOfV)
        {
        case 0: setLogLevel("warn"); break;
        case 1: setLogLevel("debug");break;
        case 2:
        default:
            setLogLevel("trace");
            break;
        }
        System.out.println("Build from " + getCommitID());
    }

    private void setLogLevel(String LogLevel)
    {
        final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        try
        {
            final JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            context.reset();
            final String logCfg;
            if("warn".equals(LogLevel))
            {
                logCfg =
                        "<configuration>" +
                          "<appender name='STDOUT' class='ch.qos.logback.core.ConsoleAppender'>" +
                            "<encoder>" +
                              "<pattern>%d{HH:mm:ss.SSS} %-5level - %msg%n</pattern>" +
                            "</encoder>" +
                          "</appender>" +
                          "<root level='" + LogLevel + "'>" +
                            "<appender-ref ref='STDOUT' />" +
                          "</root>" +
                        "</configuration>";
            }
            else
            {
                logCfg =
                        "<configuration>" +
                          "<appender name='STDOUT' class='ch.qos.logback.core.ConsoleAppender'>" +
                            "<encoder>" +
                              "<pattern>%d{HH:mm:ss.SSS} %-5level %logger{36} - %msg%n</pattern>" +
                            "</encoder>" +
                          "</appender>" +
                          "<root level='" + LogLevel + "'>" +
                            "<appender-ref ref='STDOUT' />" +
                          "</root>" +
                        "</configuration>";
            }

            ByteArrayInputStream bin;
            try
            {
                bin = new ByteArrayInputStream(logCfg.getBytes("UTF-8"));
                configurator.doConfigure(bin);
            }
            catch(UnsupportedEncodingException e)
            {
                // A system without UTF-8 ? - No chance to do anything !
                e.printStackTrace();
                System.exit(1);
            }
        }
        catch (JoranException je)
        {
          // StatusPrinter will handle this
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(context);
    }

    public void printHelp()
    {
        System.out.println("Pacemaker Protocol Client Complience Test Tool - PPCCTT");
        System.out.println("Usage: java -jar ClientComplienceTest.jar "
                + "COM3:115200:8:None:1:false:false:false:false");
        System.out.println("Parameters:");
        System.out.println("-h                         : print this message.");
        System.out.println("-b <ms>                    : After connect wait for the defined time.\n"
                         + "                             For Arduino Auto reset use -b 1000.");
        System.out.println("-c <fileName.cfg>          : applies the Firmware configuration in the File before testing.");
        System.out.println("-v                         : verbose output for even more messages use -v -v");
    }

    public boolean parseCommandLineParameters(final String[] args)
    {
        for(int i = 0; i < args.length; i++)
        {
            if(true == args[i].startsWith("-"))
            {
                if(true == "-h".equals(args[i]))
                {
                    return false;
                }
                else if(true == "-b".equals(args[i]))
                {
                    i++;
                    bootLoaderDelayMs = Integer.parseInt(args[i]);
                }
                else if(true == "-c".equals(args[i]))
                {
                    i++;
                    FirmwareConfigurationFileName = args[i];
                }
                else if(true == "-v".equals(args[i]))
                {
                    // already handled -> ignore
                }
                else
                {
                    System.err.println("Invalid Parameter : " + args[i]);
                    return false;
                }
            }
            else
            {
                dutConnect = args[i];
            }
        }
        if(null == dutConnect)
        {
            return false;
        }
        if(1 > dutConnect.length())
        {
            return false;
        }
        return true;
    }

    public static String getCommitID()
    {
        try
        {
            final InputStream s = PpccttMain.class.getResourceAsStream("/commit-id");
            final BufferedReader in = new BufferedReader(new InputStreamReader(s));
            final String commitId = in.readLine();
            final String changes = in.readLine();
            if(null != changes)
            {
                if(0 < changes.length())
                {
                    return commitId + "-(" + changes + ")";
                }
                else
                {
                    return commitId;
                }
            }
            else
            {
                return commitId;
            }
        }
        catch( Exception e )
        {
            return e.toString();
        }
    }


    private boolean connectToDut()
    {
        log.trace("Connecting to {} !",dutConnect );
        client = new UartConnection();
        if(false == client.establishConnectionTo(dutConnect))
        {
            return false;
        }
        // Arduino Clients with Automatic Reset need a pause of one second.(Bootloader)
        if(0 < bootLoaderDelayMs)
        {
            System.out.println("Delay of " + bootLoaderDelayMs + "ms !");
            try
            {
                Thread.sleep(bootLoaderDelayMs);
            }
            catch(InterruptedException e)
            {
                // I don't care
            }
        }
        return true;
    }

    private boolean closeConnectionToDut()
    {
        boolean ret = client.close();
        if(true == ret)
        {
            log.trace("Connecting to {} closed !",dutConnect );
        }
        return ret;
    }


    private void doAllTests()
    {
        System.out.println("Please make sure that the Client has been reset before starting this test !");
        ClientInformation dutInfo = new ClientInformation();
        dutInfo.setConnectionDefinition(dutConnect);
        boolean success;
        success = connectToDut();
        if(false == success)
        {
            log.error("Connecting failed !");
            return;
        }
        TransportLayerTest tlt = new TransportLayerTest(client);
        CommandLayerTest clt = new CommandLayerTest(tlt, dutInfo, FirmwareConfigurationFileName);
        success = clt.doAllTests();
        if(false == success)
        {
            log.error("Command Layer Tests failed !");
            return;
        }
        success = closeConnectionToDut();
        if(false == success)
        {
            log.error("closing the connection failed !");
            return;
        }
        System.out.println("===============================================================================");
        System.out.println("Test finished for :");
        System.out.println(dutInfo.toString());
        System.out.println("===============================================================================");
        System.out.println("All tests Successfull !");
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        final PpccttMain ct = new PpccttMain();
        ct.startLogging(args);
        if(false == ct.parseCommandLineParameters(args))
        {
            ct.printHelp();
            return;
        }
        ct.doAllTests();
        System.exit(0);
    }

}
