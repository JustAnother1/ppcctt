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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import purejavacomm.CommPort;
import purejavacomm.CommPortIdentifier;
import purejavacomm.NoSuchPortException;
import purejavacomm.PortInUseException;
import purejavacomm.SerialPort;
import purejavacomm.UnsupportedCommOperationException;

/**
 * @author Lars P&ouml;tter
 * (<a href=mailto:Lars_Poetter@gmx.de>Lars_Poetter@gmx.de</a>)
 *
 */
public class UartConnection
{
    public static final String OPTION_SEPERATOR = ":";

    public static final int TIMEOUT_PORT_OPEN_MS = 1000;
    // databits: 5,6,7,8
    public static final String[] bits = {"5", "6", "7", "8"};
    // parity: None, even, odd mark, space
    public static final String[] parityOptions = {"None", "Even", "Odd", "Mark", "Space"};
    // stop bits: 1, 1.5, 2
    public static final String[] stop = {"1", "1 1/2", "2"};

    private static byte[] crc_array =
    {
        //       0           1           2           3           4           5           6           7           8           9           A           B           C           D           E           F
    /* 0*/ (byte)0x00, (byte)0xa6, (byte)0xea, (byte)0x4c, (byte)0x72, (byte)0xd4, (byte)0x98, (byte)0x3e, (byte)0xe4, (byte)0x42, (byte)0x0e, (byte)0xa8, (byte)0x96, (byte)0x30, (byte)0x7c, (byte)0xda,
    /* 1*/ (byte)0x6e, (byte)0xc8, (byte)0x84, (byte)0x22, (byte)0x1c, (byte)0xba, (byte)0xf6, (byte)0x50, (byte)0x8a, (byte)0x2c, (byte)0x60, (byte)0xc6, (byte)0xf8, (byte)0x5e, (byte)0x12, (byte)0xb4,
    /* 2*/ (byte)0xdc, (byte)0x7a, (byte)0x36, (byte)0x90, (byte)0xae, (byte)0x08, (byte)0x44, (byte)0xe2, (byte)0x38, (byte)0x9e, (byte)0xd2, (byte)0x74, (byte)0x4a, (byte)0xec, (byte)0xa0, (byte)0x06,
    /* 3*/ (byte)0xb2, (byte)0x14, (byte)0x58, (byte)0xfe, (byte)0xc0, (byte)0x66, (byte)0x2a, (byte)0x8c, (byte)0x56, (byte)0xf0, (byte)0xbc, (byte)0x1a, (byte)0x24, (byte)0x82, (byte)0xce, (byte)0x68,
    /* 4*/ (byte)0x1e, (byte)0xb8, (byte)0xf4, (byte)0x52, (byte)0x6c, (byte)0xca, (byte)0x86, (byte)0x20, (byte)0xfa, (byte)0x5c, (byte)0x10, (byte)0xb6, (byte)0x88, (byte)0x2e, (byte)0x62, (byte)0xc4,
    /* 5*/ (byte)0x70, (byte)0xd6, (byte)0x9a, (byte)0x3c, (byte)0x02, (byte)0xa4, (byte)0xe8, (byte)0x4e, (byte)0x94, (byte)0x32, (byte)0x7e, (byte)0xd8, (byte)0xe6, (byte)0x40, (byte)0x0c, (byte)0xaa,
    /* 6*/ (byte)0xc2, (byte)0x64, (byte)0x28, (byte)0x8e, (byte)0xb0, (byte)0x16, (byte)0x5a, (byte)0xfc, (byte)0x26, (byte)0x80, (byte)0xcc, (byte)0x6a, (byte)0x54, (byte)0xf2, (byte)0xbe, (byte)0x18,
    /* 7*/ (byte)0xac, (byte)0x0a, (byte)0x46, (byte)0xe0, (byte)0xde, (byte)0x78, (byte)0x34, (byte)0x92, (byte)0x48, (byte)0xee, (byte)0xa2, (byte)0x04, (byte)0x3a, (byte)0x9c, (byte)0xd0, (byte)0x76,
    /* 8*/ (byte)0x3c, (byte)0x9a, (byte)0xd6, (byte)0x70, (byte)0x4e, (byte)0xe8, (byte)0xa4, (byte)0x02, (byte)0xd8, (byte)0x7e, (byte)0x32, (byte)0x94, (byte)0xaa, (byte)0x0c, (byte)0x40, (byte)0xe6,
    /* 9*/ (byte)0x52, (byte)0xf4, (byte)0xb8, (byte)0x1e, (byte)0x20, (byte)0x86, (byte)0xca, (byte)0x6c, (byte)0xb6, (byte)0x10, (byte)0x5c, (byte)0xfa, (byte)0xc4, (byte)0x62, (byte)0x2e, (byte)0x88,
    /* A*/ (byte)0xe0, (byte)0x46, (byte)0x0a, (byte)0xac, (byte)0x92, (byte)0x34, (byte)0x78, (byte)0xde, (byte)0x04, (byte)0xa2, (byte)0xee, (byte)0x48, (byte)0x76, (byte)0xd0, (byte)0x9c, (byte)0x3a,
    /* B*/ (byte)0x8e, (byte)0x28, (byte)0x64, (byte)0xc2, (byte)0xfc, (byte)0x5a, (byte)0x16, (byte)0xb0, (byte)0x6a, (byte)0xcc, (byte)0x80, (byte)0x26, (byte)0x18, (byte)0xbe, (byte)0xf2, (byte)0x54,
    /* C*/ (byte)0x22, (byte)0x84, (byte)0xc8, (byte)0x6e, (byte)0x50, (byte)0xf6, (byte)0xba, (byte)0x1c, (byte)0xc6, (byte)0x60, (byte)0x2c, (byte)0x8a, (byte)0xb4, (byte)0x12, (byte)0x5e, (byte)0xf8,
    /* D*/ (byte)0x4c, (byte)0xea, (byte)0xa6, (byte)0x00, (byte)0x3e, (byte)0x98, (byte)0xd4, (byte)0x72, (byte)0xa8, (byte)0x0e, (byte)0x42, (byte)0xe4, (byte)0xda, (byte)0x7c, (byte)0x30, (byte)0x96,
    /* E*/ (byte)0xfe, (byte)0x58, (byte)0x14, (byte)0xb2, (byte)0x8c, (byte)0x2a, (byte)0x66, (byte)0xc0, (byte)0x1a, (byte)0xbc, (byte)0xf0, (byte)0x56, (byte)0x68, (byte)0xce, (byte)0x82, (byte)0x24,
    /* F*/ (byte)0x90, (byte)0x36, (byte)0x7a, (byte)0xdc, (byte)0xe2, (byte)0x44, (byte)0x08, (byte)0xae, (byte)0x74, (byte)0xd2, (byte)0x9e, (byte)0x38, (byte)0x06, (byte)0xa0, (byte)0xec, (byte)0x4a
    };

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private SerialPort port;
    private boolean connected = false;
    protected InputStream in;
    protected OutputStream out;
    private byte[] frame = new byte[512];

    public UartConnection()
    {
        final Properties systemProperties = System.getProperties();
        systemProperties.setProperty("jna.nosys", "true");
    }


    public byte getCRCfor(final byte[] buf, int length, final int offset)
    {
        byte crc = 0;
        int pos = offset;
        while (length > 0)
        {
            crc = crc_array[0xff & (buf[pos] ^ crc)];
            pos = pos + 1;
            length = length - 1;
        }
        return crc;
    }

    private static String getPortNameFromDescriptor(String data)
    {
        final Scanner sc = new Scanner(data);
        sc.useDelimiter(OPTION_SEPERATOR);
        final String res = sc.next();
        sc.close();
        return res;
    }

    private static int getBaudrateFromDescriptor(String data)
    {
        final Scanner sc = new Scanner(data);
        sc.useDelimiter(OPTION_SEPERATOR);
        sc.next(); // skip Port Name
        final int res = Integer.parseInt(sc.next());
        sc.close();
        return res;
    }

    private static int getDataBitIdxFromDescriptor(String data)
    {
        final Scanner sc = new Scanner(data);
        sc.useDelimiter(OPTION_SEPERATOR);
        sc.next(); // skip Port Name
        sc.next(); // skip Baudrate
        final String help = sc.next();
        sc.close();
        int i;
        for(i = 0; i < bits.length; i++)
        {
            if(true == bits[i].equals(help))
            {
                break;
            }
        }
        return i; // Default -> 8 bits
    }

    private int getSerialPortDataBitFromDescriptor(String data)
    {
        int spDataBits;
        final int dataBitIdx = getDataBitIdxFromDescriptor(data);
        switch(dataBitIdx)
        {
        case 0: spDataBits = SerialPort.DATABITS_5;break;
        case 1: spDataBits = SerialPort.DATABITS_6;break;
        case 2: spDataBits = SerialPort.DATABITS_7;break;
        case 3: spDataBits = SerialPort.DATABITS_8;break;
        default: spDataBits = SerialPort.DATABITS_8;break;
        }
        return spDataBits;
    }

    private static int getParityIdxFromDescriptor(String data)
    {
        final Scanner sc = new Scanner(data);
        sc.useDelimiter(OPTION_SEPERATOR);
        sc.next(); // skip Port Name
        sc.next(); // skip Baudrate
        sc.next(); // skip data bits
        final String help = sc.next();
        sc.close();
        for(int i = 0; i < parityOptions.length; i++)
        {
            if(true == parityOptions[i].equals(help))
            {
                return i;
            }
        }
        return 0; // default -> No Parity
    }

    private int getSerialPortParityFromDescriptor(String data)
    {
        int spParity;
        final int parityIdx = getParityIdxFromDescriptor(data);
        switch(parityIdx)
        {
        case 0: spParity = SerialPort.PARITY_NONE; break;
        case 1: spParity = SerialPort.PARITY_EVEN; break;
        case 2: spParity = SerialPort.PARITY_ODD; break;
        case 3: spParity = SerialPort.PARITY_MARK; break;
        case 4: spParity = SerialPort.PARITY_SPACE; break;
        default: spParity = SerialPort.PARITY_NONE; break;
        }
        return spParity;
    }

    private static int getStopBitIdxFromDescriptor(String data)
    {
        final Scanner sc = new Scanner(data);
        sc.useDelimiter(OPTION_SEPERATOR);
        sc.next(); // skip Port Name
        sc.next(); // skip Baudrate
        sc.next(); // skip data bits
        sc.next(); // skip parity
        final String help = sc.next();
        sc.close();
        for(int i = 0; i < stop.length; i++)
        {
            if(true == stop[i].equals(help))
            {
                return i;
            }
        }
        return 0; // default -> 1 Stop Bit
    }

    private int getSerialPortStopBitFromDescriptor(String data)
    {
        int spStopBits;
        final int stopBitIdx = getStopBitIdxFromDescriptor(data);
        switch(stopBitIdx)
        {
        case 0:spStopBits = SerialPort.STOPBITS_1;break;
        case 1:spStopBits = SerialPort.STOPBITS_1_5;break;
        case 2:spStopBits = SerialPort.STOPBITS_2;break;
        default: spStopBits = SerialPort.STOPBITS_1;break;
        }
        return spStopBits;
    }

    private static boolean getRtsCtsInFromDescriptor(String data)
    {
        final Scanner sc = new Scanner(data);
        sc.useDelimiter(OPTION_SEPERATOR);
        sc.next(); // skip Port Name
        sc.next(); // skip Baudrate
        sc.next(); // skip data bits
        sc.next(); // skip parity
        sc.next(); // skip stop bits
        final String help = sc.next();
        final boolean res = Boolean.getBoolean(help);
        sc.close();
        return res;
    }

    private int getFlowControlFromDescriptor(String data)
    {
        int flowControl = SerialPort.FLOWCONTROL_NONE;
        if(true == getRtsCtsInFromDescriptor(data))
        {
            flowControl = flowControl | SerialPort.FLOWCONTROL_RTSCTS_IN;
        }
        if(true == getRtsCtsOutFromDescriptor(data))
        {
            flowControl = flowControl | SerialPort.FLOWCONTROL_RTSCTS_OUT;
        }
        if(true == getXonXoffInFromDescriptor(data))
        {
            flowControl = flowControl | SerialPort.FLOWCONTROL_XONXOFF_IN;
        }
        if(true == getXonXoffOutFromDescriptor(data))
        {
            flowControl = flowControl | SerialPort.FLOWCONTROL_XONXOFF_OUT;
        }
        return flowControl;
    }

    private static boolean getRtsCtsOutFromDescriptor(String data)
    {
        final Scanner sc = new Scanner(data);
        sc.useDelimiter(OPTION_SEPERATOR);
        sc.next(); // skip Port Name
        sc.next(); // skip Baudrate
        sc.next(); // skip data bits
        sc.next(); // skip parity
        sc.next(); // skip stop bits
        sc.next(); // skip RTS / CTS In
        final String help = sc.next();
        final boolean res = Boolean.getBoolean(help);
        sc.close();
        return res;
    }

    private static boolean getXonXoffInFromDescriptor(String data)
    {
        final Scanner sc = new Scanner(data);
        sc.useDelimiter(OPTION_SEPERATOR);
        sc.next(); // skip Port Name
        sc.next(); // skip Baudrate
        sc.next(); // skip data bits
        sc.next(); // skip parity
        sc.next(); // skip stop bits
        sc.next(); // skip RTS / CTS In
        sc.next(); // skip RTS / CTS Out
        final String help = sc.next();
        final boolean res = Boolean.getBoolean(help);
        sc.close();
        return res;
    }

    private static boolean getXonXoffOutFromDescriptor(String data)
    {
        final Scanner sc = new Scanner(data);
        sc.useDelimiter(OPTION_SEPERATOR);
        sc.next(); // skip Port Name
        sc.next(); // skip Baudrate
        sc.next(); // skip data bits
        sc.next(); // skip parity
        sc.next(); // skip stop bits
        sc.next(); // skip RTS / CTS In
        sc.next(); // skip RTS / CTS Out
        sc.next(); // skip Xon / Xoff In
        final String help = sc.next();
        final boolean res = Boolean.getBoolean(help);
        sc.close();
        return res;
    }

    public boolean establishConnectionTo(String data)
    {
        final String PortName = getPortNameFromDescriptor(data);
        try
        {
            final CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(PortName);
            if(CommPortIdentifier.PORT_SERIAL != portId.getPortType())
            {
                log.error("Specified Port {} is not a Serial Port ({})!", PortName, portId.getPortType());
                return false;
            }
            CommPort basePort = null;
            try
            {
                basePort = portId.open("Pacemaker Host", TIMEOUT_PORT_OPEN_MS);
            }
            catch(PortInUseException e)
            {
                log.error("Specified Port {} is in use by another Application !", PortName);
                return false;
            }
            if(false ==(basePort instanceof SerialPort))
            {
                log.error("Specified Port {} is not a Serial Port Object!", PortName);
                return false;
            }
            port = (SerialPort)basePort;
            port.setFlowControlMode(getFlowControlFromDescriptor(data));

            port.setSerialPortParams(getBaudrateFromDescriptor(data),
                                     getSerialPortDataBitFromDescriptor(data),
                                     getSerialPortStopBitFromDescriptor(data),
                                     getSerialPortParityFromDescriptor(data));

            in = port.getInputStream();
            out = port.getOutputStream();
            connected = true;
            log.info("Serial Port is open");
            return true;
        }
        catch(NoSuchPortException e)
        {
            log.error("There is no port named {} !", PortName);
            e.printStackTrace();
        }
        catch(UnsupportedCommOperationException e)
        {
            log.error("The Interface {} does not support the requested parameters !", PortName);
            e.printStackTrace();
        }
        catch(IOException e)
        {
            log.error("The Interface {} caused an IO Exception !", PortName);
            e.printStackTrace();
        }
        close(); // In case that we had a problem after the open
        return false;
    }

    public boolean isConnected()
    {
        return connected;
    }

    public boolean close()
    {
        connected = false;
        if(null != port)
        {
            port.close();
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean sendRequest(final byte[] data)
    {
        return sendRequest(data, 0, data.length);
    }
    /** sends a request frame to the client.
    *
    * @param data the bytes of the frame
    * @param offset frame bytes starts at this offset in the buffer.
    * @param length send only this many bytes. May be 0 !
    * @return true= success false = no reply received - timeout
    */
   public boolean sendRequest(final byte[] data, int offset, int length)
   {
       try
       {
           log.trace("Sending Frame  : " + Tool.fromByteBufferToHexString(data, length, offset));
           out.write(data);
           return true;
       }
       catch (final IOException e)
       {
           e.printStackTrace();
           log.error("Failed to send Request - Exception !");
           return false;
       }
   }

    public byte[] getFrame()
    {
        int pos = 0;
        int length = 300;
        int res = getABNonlockingByte(100);
        if(res == -1)
        {
            return null;
        }
        if(Protocol.START_OF_CLIENT_FRAME != res)
        {
            log.error("Received Invalid Data !");
            return null;
        }
        frame[pos] = (byte)res;
        pos ++;
        do
        {
            res = getABNonlockingByte(20);
            if(-1 != res)
            {
                frame[pos] = (byte)res;
                pos ++;
                if(2 == pos)
                {
                    length = res;
                }
                else
                {
                    if(pos == length + 3)
                    {
                        break;
                    }
                }
            }
        }while(res != -1);
        byte[] recFrame = new byte[pos];
        for(int i = 0; i < pos; i++)
        {
            recFrame[i] = (byte)(0xff & frame[i]);
        }
        log.trace("Received Frame : " + Tool.fromByteBufferToHexString(recFrame) + " parsed : " + Protocol.parse(recFrame));
        return recFrame;
    }

    private int getABNonlockingByte(int timeout)
    {
        int numAvail;
        try
        {
            numAvail = in.available();
            if(1 > numAvail)
            {
                int timeoutCounter = 0;
                do
                {
                    Thread.sleep(1);
                    timeoutCounter++;
                    if(timeout < timeoutCounter)
                    {
                        return -1;
                    }
                    numAvail = in.available();
                }while(1 > numAvail);
            }
            // else a byte is already available
            return in.read();
        }
        catch(IOException e)
        {
            return -1;
        }
        catch(InterruptedException e)
        {
            return -1;
        }
    }

}
