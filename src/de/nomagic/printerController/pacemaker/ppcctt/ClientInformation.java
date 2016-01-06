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

import java.util.HashMap;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Lars P&ouml;tter
 * (<a href=mailto:Lars_Poetter@gmx.de>Lars_Poetter@gmx.de</a>)
 *
 */
public class ClientInformation
{
    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private String ConnectionDefinition;
    private String FirmwareName;
    private String SerialNumber;
    private String HardwareName;
    private String GivenIdentity;
    private Vector<Integer> supportedExtensions = new Vector<Integer>();
    private int FirmwareType = -1;
    private int FirmwareVersionMajor = -1;
    private int FirmwareVersionMinor = -1;
    private int HardwareType = -1;
    private int HardwareRevision = -1;
    private int maxStepRate = -1;
    private int hostTimeout = -1;
    private int ProtocolVersionMajor = -1;
    private int ProtocolVersionMinor = -1;
    private HashMap<Integer, Integer> NumberDevices = new HashMap<Integer, Integer>();
    private boolean canDeactivateStepperControl;

    @Override
    public String toString()
    {
        StringBuffer res = new StringBuffer();
        res.append("Client at " + ConnectionDefinition
                 + "\nis " + FirmwareName + "\n");
        res.append("Serial Number        : " + SerialNumber + "\n");
        res.append("Hardware             : " + HardwareName + "\n");
        res.append("ID                   : " + GivenIdentity + "\n");
        res.append("supported Extensions : \n");
        for(int i = 0; i < supportedExtensions.size(); i++)
        {
            Integer ExtensionType = supportedExtensions.get(i);
            switch(ExtensionType)
            {
            case Protocol.INFO_PROTOCOL_EXTENSION_STEPPER_CONTROL:
                res.append(" - stepper control \n");
                break;

            case Protocol.INFO_PROTOCOL_EXTENSION_QUEUED_COMMAND:
                res.append(" - queued commands \n");
                break;

            case Protocol.INFO_PROTOCOL_EXTENSION_BASIC_MOVE:
                res.append(" - basic move \n");
                break;

            case Protocol.INFO_PROTOCOL_EXTENSION_EVENT_REPORTING:
                res.append(" - event reporting \n");
                break;

            default:
                res.append("ERROR: Client supports unknown Extension of Type" + ExtensionType + " !\n");
                break;
            }
        }
        switch(FirmwareType)
        {
        case 0:
        	res.append("Firmware Type        : pmc\n");
        	break;

        case 1:
        	res.append("Firmware Type        : Minnow\n");
        	break;

        default:
        	res.append("Firmware Type        : " + FirmwareType + "\n");
        	break;
        }
        res.append("Firmware Version     : " + FirmwareVersionMajor + "." + FirmwareVersionMinor + "\n");
        res.append("Protocol Version     : " + ProtocolVersionMajor + "." + ProtocolVersionMinor + "\n");
        switch(HardwareType)
        {
        case 0:
        	res.append("Hardware Type        : pipy\n");
        	break;

        case 1:
        	res.append("Hardware Type        : AVR (Arduino,..)\n");
        	break;

        default:
            res.append("Hardware Type        : " + HardwareType + "\n");
        }
        res.append("Hardware Revision    : " + HardwareRevision + "\n");
        res.append("Step Rate max.       : " + maxStepRate + " Steps/sec\n");
        res.append("Host Timeout         : " + hostTimeout + " sec\n");
        res.append("has these Devices    :\n");
        res.append(NumberDevices.get(1) + " Switch Inputs\n");
        res.append(NumberDevices.get(2) + " Switch Outputs\n");
        res.append(NumberDevices.get(3) + " PWM Controlled Outputs\n");
        res.append(NumberDevices.get(4) + " Stepper\n");
        res.append(NumberDevices.get(5) + " Heater\n");
        res.append(NumberDevices.get(6) + " Temperature Sensors\n");
        res.append(NumberDevices.get(7) + " Buzzer\n");
        if(true ==canDeactivateStepperControl)
        {
            res.append("can");
        }
        else
        {
            res.append("can not");
        }
        res.append(" deactivate the Stepper control.");
        return res.toString();
    }

    public ClientInformation()
    {
    }

    public void setConnectionDefinition(String dutConnect)
    {
        ConnectionDefinition = dutConnect;
    }

    public void setFirmwareName(String name)
    {
        FirmwareName = name;
    }

    public void setSerialNumber(String number)
    {
        SerialNumber = number;
    }

    public void setHardwareName(String name)
    {
        HardwareName = name;
    }

    public void setGivenIdentity(String name)
    {
        GivenIdentity = name;
    }

    public void addSupportedExtension(byte b)
    {
        Integer i = new Integer(b);
        supportedExtensions.add(i);
    }

    public void setFirmwareType(int b)
    {
        FirmwareType = b;
    }

    public void setFirmwareVersionMajor(int number)
    {
        FirmwareVersionMajor = number;
    }

    public void setFirmwareVersionMinor(int number)
    {
        FirmwareVersionMinor = number;
    }

    public void setHardwareType(int number)
    {
        HardwareType = number;
    }

    public void setHardwareRevision(int number)
    {
        HardwareRevision = number;
    }

    public void setMaxStepRate(int number)
    {
        maxStepRate = number;
    }

    public void setHostTimeout(int number)
    {
        hostTimeout = number;
    }

    public void setProtocolVersionMajor(int number)
    {
        ProtocolVersionMajor = number;
    }

    public void setProtocolVersionMinor(int number)
    {
        ProtocolVersionMinor = number;
    }

    public void setDeviceCount(int deviceType, int number)
    {
        // log.trace("Device Number {} has {} instances", deviceType, number);
        NumberDevices.put(deviceType, number);
    }

    public void setCanDeactivateStepperControl(boolean b)
    {
        canDeactivateStepperControl = b;
    }

    public int getNumberOfDevices(int deviceType)
    {
        Integer res = NumberDevices.get(deviceType);  // to avoid null Pointer Exception
        if(null == res)
        {
            log.error("No Information about available Number of Devices for device type {} !", deviceType);
            return 0;
        }
        else
        {
            // log.trace("Device Number {} has {} instances", deviceType, res);
            return res;
        }
    }

}
