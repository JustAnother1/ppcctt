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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Lars P&ouml;tter
 * (<a href=mailto:Lars_Poetter@gmx.de>Lars_Poetter@gmx.de</a>)
 *
 */
public class CommandLayerTest
{
    public static final String COMMENT_START = "#";
    public static final char   SEPERATOR_CHAR = '=';
    private final TransportLayerTest tlt;
    private final ClientInformation dutInfo;
    private boolean success = true;
    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private final String FirmwareConfigurationFileName;

    public CommandLayerTest(TransportLayerTest tlt, ClientInformation dutInfo, String FirmwareConfigurationFileName)
    {
        this.tlt = tlt;
        this.dutInfo = dutInfo;
        this.FirmwareConfigurationFileName = FirmwareConfigurationFileName;
    }

    private void prepare()
    {
        // make sure we did not receive something already
        byte[] response = tlt.getFrame();
        if(null != response)
        {
            log.error("Did receive something unexpectedly !");
            success = false;
            return;
        }
    }

    public boolean doAllTests()
    {
        prepare();
        if(true == success) testStoppedMode();
        if(true == success) applyFirmwareConfiguration();
        if(true == success) testRequestInformation();
        if(true == success) testResuestDeviceCount();
        if(true == success) testConfigureAxisMovementRates();
        if(true == success) testConfigureMovementUnderrunAvoidanceParameters();
        if(true == success) testActivateStepperControl();
        return success;
    }

    private void applyFirmwareConfiguration()
    {
        log.debug("Startiing Test: Applying Firmware Configuration");
        if(null != FirmwareConfigurationFileName)
        {
            if(false == applyFirmwareConfigurationFrom(FirmwareConfigurationFileName, tlt))
            {
                log.error("Failed to apply Firmware configuration ! ");
                return;
            }
        }
    }

    private String removeCommentsFrom(String aLine)
    {
        if(true == aLine.contains(COMMENT_START))
        {
            final String res = aLine.substring(0, aLine.indexOf(COMMENT_START));
            return res.trim();
        }
        else
        {
            return aLine.trim();
        }
    }

    private String getKeyFrom(final String line)
    {
        if(-1 == line.indexOf(SEPERATOR_CHAR))
        {
            return line;
        }
        else
        {
            return (line.substring(0, line.indexOf(SEPERATOR_CHAR))).trim();
        }
    }

    private String getValueFrom(final String line)
    {
        if(-1 == line.indexOf(SEPERATOR_CHAR))
        {
            return "";
        }
        else
        {
            return (line.substring(line.indexOf(SEPERATOR_CHAR) + 1)).trim();
        }
    }

    private boolean applyFirmwareConfigurationFrom(String fileName, TransportLayerTest tlt)
    {
        try
        {
            final BufferedReader br = new BufferedReader(
                                          new InputStreamReader(
                                              new FileInputStream(fileName),
                                              Charset.forName("UTF-8") ) );
            String curLine = br.readLine();
            while(null != curLine)
            {
                curLine = removeCommentsFrom(curLine);
                if(0 < curLine.length())
                {
                    String setting = getKeyFrom(curLine);
                    String value = getValueFrom(curLine);
                    log.trace("Writing to Client : -{}- = -{}- !", setting, value);
                    if(false == writeFirmwareConfigurationValue(setting, value, tlt))
                    {
                        br.close();
                        return false;
                    }
                }
                // We are done with this line -> Read the next line.
                curLine = br.readLine();
            }
            br.close();
            return true;
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public boolean writeFirmwareConfigurationValue(String name, String value, TransportLayerTest tlt)
    {
        final byte[] nameBuf = name.getBytes(Charset.forName("UTF-8"));
        final byte[] valueBuf = value.getBytes(Charset.forName("UTF-8"));
        final byte[] parameter = new byte[nameBuf.length + valueBuf.length + 1];
        parameter[0] = (byte)nameBuf.length;
        for(int i = 0; i < nameBuf.length; i++)
        {
            parameter[i+1] = nameBuf[i];
        }
        for(int i = 0; i < valueBuf.length; i++)
        {
            parameter[i+nameBuf.length + 1] = valueBuf[i];
        }
        if(false == tlt.send(Protocol.ORDER_WRITE_FIRMWARE_CONFIGURATION, parameter))
        {
            log.error("Failed to write Firmware Setting {} = {} !", name, value);
            return false;
        }
        byte[] response = tlt.getFrame();
        if(false == tlt.checkReply(response, Protocol.RESPONSE_OK, 0))
        {
            log.error("Failed to apply Firmware Setting {} = {} !", name, value);
            return false;
        }
        tlt.IncrementSequenceCounter();
        return true;
    }

    private void testConfigureAxisMovementRates()
    {
        log.debug("Startiing Test: Configure Axis Movement Rates");
        int numSteppers = dutInfo.getNumberOfDevices(Protocol.DEVICE_TYPE_STEPPER);
        // with all the steppers
        byte[]  response;
        for(int i = 0; i < numSteppers; i++)
        {
            // 0 is not allowed
            tlt.send_byte_U32(Protocol.ORDER_CONFIGURE_AXIS_MOVEMENT_RATES, i +1, 0);
            response = tlt.getFrame();
            if(false == tlt.checkReply(response, Protocol.RESPONSE_GENERIC_APPLICATION_ERROR, 1))
            {
                success = false;
                return;
            }
            tlt.IncrementSequenceCounter();

            // 17 millions is probably too much
            tlt.send_byte_U32(Protocol.ORDER_CONFIGURE_AXIS_MOVEMENT_RATES, i +1, 17000000);
            response = tlt.getFrame();
            if(false == tlt.checkReply(response, Protocol.RESPONSE_GENERIC_APPLICATION_ERROR, 1))
            {
                success = false;
                return;
            }
            tlt.IncrementSequenceCounter();

            // 39000 should be ok.
            tlt.send_byte_U32(Protocol.ORDER_CONFIGURE_AXIS_MOVEMENT_RATES, i +1, 39000);
            response = tlt.getFrame();
            if(false == tlt.checkReply(response, Protocol.RESPONSE_OK, 0))
            {
                success = false;
                return;
            }
            tlt.IncrementSequenceCounter();
        }
    }

    private byte[] getParameterForUnderrunAvoidance(int StepperIndex, long maxMoveRate, long maxAccelleration)
    {
        byte[] res = new byte[9];
        res[0] = (byte)(0xff &  (StepperIndex + 1));
        res[1] = (byte)(0xff & (maxMoveRate>>24));
        res[2] = (byte)(0xff & (maxMoveRate>>16));
        res[3] = (byte)(0xff & (maxMoveRate>>8));
        res[4] = (byte)(0xff & (maxMoveRate));
        res[5] = (byte)(0xff & (maxAccelleration>>24));
        res[6] = (byte)(0xff & (maxAccelleration>>16));
        res[7] = (byte)(0xff & (maxAccelleration>>8));
        res[8] = (byte)(0xff & (maxAccelleration));
        return res;
    }

    private void testConfigureMovementUnderrunAvoidanceParameters()
    {
        log.debug("Startiing Test: Configure Movement Underrun avoidance Paramaters");
        int numSteppers = dutInfo.getNumberOfDevices(Protocol.DEVICE_TYPE_STEPPER);
        // with all the steppers
        for(int i = 0; i < numSteppers; i++)
        {
            // 0 is not allowed
            tlt.send(Protocol.ORDER_CONFIGURE_MOVEMENT_UNDERRUN_AVOIDANCE_PARAMETERS,
                     getParameterForUnderrunAvoidance(i, 0, 0));
            byte[] response = tlt.getFrame();
            if(false == tlt.checkReply(response, Protocol.RESPONSE_GENERIC_APPLICATION_ERROR, 1)) // TODO
            {
                success = false;
                return;
            }
            if(Protocol.RESPONSE_BAD_PARAMETER_VALUE != response[1])
            {
                success = false;
                return;
            }
            tlt.IncrementSequenceCounter();

            // 17 millions is probably too much
            tlt.send(Protocol.ORDER_CONFIGURE_MOVEMENT_UNDERRUN_AVOIDANCE_PARAMETERS,
                    getParameterForUnderrunAvoidance(i, 170000000, 0));
            response = tlt.getFrame();
            if(false == tlt.checkReply(response, Protocol.RESPONSE_GENERIC_APPLICATION_ERROR, 1))
            {
                success = false;
                return;
            }
            if(Protocol.RESPONSE_BAD_PARAMETER_VALUE != response[1])
            {
                success = false;
                return;
            }
            tlt.IncrementSequenceCounter();

            // 39000 should be ok.
            tlt.send(Protocol.ORDER_CONFIGURE_MOVEMENT_UNDERRUN_AVOIDANCE_PARAMETERS,
                    getParameterForUnderrunAvoidance(i, 39000, 10000));
            response = tlt.getFrame();
            if(false == tlt.checkReply(response, Protocol.RESPONSE_OK, 0))
            {
                success = false;
                return;
            }
            tlt.IncrementSequenceCounter();
        }
        // TODO do more combinations
    }

    private void testActivateStepperControl()
    {
        log.debug("Startiing Test: Activate Stepper Control");
        // Deactivate
        tlt.send(Protocol.ORDER_ACTIVATE_STEPPER_CONTROL, 0);
        byte[] response = tlt.getFrame();
        if(false == tlt.checkReply(response, Protocol.RESPONSE_OK, 0))
        {
            if(false == tlt.checkReply(response, Protocol.RESPONSE_GENERIC_APPLICATION_ERROR, 1))
            {
                success = false;
                return;
            }
            dutInfo.setCanDeactivateStepperControl(false);
        }
        tlt.IncrementSequenceCounter();
        // Activate
        tlt.send(Protocol.ORDER_ACTIVATE_STEPPER_CONTROL, 1);
        response = tlt.getFrame();
        if(false == tlt.checkReply(response, Protocol.RESPONSE_OK, 0))
        {
            success = false;
            return;
        }
        tlt.IncrementSequenceCounter();
    }

    private void testResuestDeviceCount()
    {
        log.debug("Startiing Test: Device Count");
        for(int i = Protocol.DEVICE_TYPE_FIRST; i < Protocol.DEVICE_TYPE_LAST + 1; i++)
        {
            tlt.send(Protocol.ORDER_REQUEST_DEVICE_COUNT, i);
            byte[] response = tlt.getFrame();
            if(false == tlt.checkReply(response, Protocol.RESPONSE_OK, 1))
            {
                success = false;
                return;
            }
            dutInfo.setDeviceCount(i, tlt.getIntegerFrom(response, 1, response.length -1));
            tlt.IncrementSequenceCounter();
        }
    }

    private void testRequestInformation()
    {
        log.debug("Startiing Test: Request Information");
        // firmware name string
        tlt.send(Protocol.ORDER_REQ_INFORMATION, 0);
        byte[] response = tlt.getFrame();
        if(false == tlt.checkReply(response, Protocol.RESPONSE_OK, 0))
        {
            success = false;
            return;
        }
        dutInfo.setFirmwareName(tlt.getStringFrom(response, 1, response.length -1));
        tlt.IncrementSequenceCounter();

        // serial Number String
        tlt.send(Protocol.ORDER_REQ_INFORMATION, 1);
        response = tlt.getFrame();
        if(false == tlt.checkReply(response, Protocol.RESPONSE_OK, 0))
        {
            success = false;
            return;
        }
        dutInfo.setSerialNumber(tlt.getStringFrom(response, 1, response.length -1));
        tlt.IncrementSequenceCounter();

        // hardware name String
        tlt.send(Protocol.ORDER_REQ_INFORMATION, 2);
        response = tlt.getFrame();
        if(false == tlt.checkReply(response, Protocol.RESPONSE_OK, 0))
        {
            success = false;
            return;
        }
        dutInfo.setHardwareName(tlt.getStringFrom(response, 1, response.length -1));
        tlt.IncrementSequenceCounter();

        // given name String or identity string
        tlt.send(Protocol.ORDER_REQ_INFORMATION, 3);
        response = tlt.getFrame();
        if(false == tlt.checkReply(response, Protocol.RESPONSE_OK, 0))
        {
            success = false;
            return;
        }
        dutInfo.setGivenIdentity(tlt.getStringFrom(response, 1, response.length -1));
        tlt.IncrementSequenceCounter();

        // major Protocol Version
        tlt.send(Protocol.ORDER_REQ_INFORMATION, 4);
        response = tlt.getFrame();
        if(false == tlt.checkReply(response, Protocol.RESPONSE_OK, 1))
        {
            success = false;
            return;
        }
        dutInfo.setProtocolVersionMajor(tlt.getIntegerFrom(response, 1, response.length -1));
        tlt.IncrementSequenceCounter();

        // minor Protocol Version
        tlt.send(Protocol.ORDER_REQ_INFORMATION, 5);
        response = tlt.getFrame();
        if(false == tlt.checkReply(response, Protocol.RESPONSE_OK, 1))
        {
            success = false;
            return;
        }
        dutInfo.setProtocolVersionMinor(tlt.getIntegerFrom(response, 1, response.length -1));
        tlt.IncrementSequenceCounter();

        // list of supported protocol extensions
        tlt.send(Protocol.ORDER_REQ_INFORMATION, 6);
        response = tlt.getFrame();
        if(false == tlt.checkReply(response, Protocol.RESPONSE_OK, 0))
        {
            success = false;
            return;
        }
        int i = 1;
        while(i < response.length)
        {
            dutInfo.addSupportedExtension(response[i]);
            i++;
        }
        tlt.IncrementSequenceCounter();

        // Firmware Type
        tlt.send(Protocol.ORDER_REQ_INFORMATION, 7);
        response = tlt.getFrame();
        if(false == tlt.checkReply(response, Protocol.RESPONSE_OK, 1))
        {
            success = false;
            return;
        }
        dutInfo.setFirmwareType(tlt.getIntegerFrom(response, 1, response.length -1));
        tlt.IncrementSequenceCounter();

        // Major Firmware Version
        tlt.send(Protocol.ORDER_REQ_INFORMATION, 8);
        response = tlt.getFrame();
        if(false == tlt.checkReply(response, Protocol.RESPONSE_OK, 1))
        {
            success = false;
            return;
        }
        dutInfo.setFirmwareVersionMajor(tlt.getIntegerFrom(response, 1, response.length -1));
        tlt.IncrementSequenceCounter();

        // Minor Firmware Version
        tlt.send(Protocol.ORDER_REQ_INFORMATION, 9);
        response = tlt.getFrame();
        if(false == tlt.checkReply(response, Protocol.RESPONSE_OK, 1))
        {
            success = false;
            return;
        }
        dutInfo.setFirmwareVersionMinor(tlt.getIntegerFrom(response, 1, response.length -1));
        tlt.IncrementSequenceCounter();

        // Hardware Type
        tlt.send(Protocol.ORDER_REQ_INFORMATION, 10);
        response = tlt.getFrame();
        if(false == tlt.checkReply(response, Protocol.RESPONSE_OK, 1))
        {
            success = false;
            return;
        }
        dutInfo.setHardwareType(tlt.getIntegerFrom(response, 1, response.length -1));
        tlt.IncrementSequenceCounter();

        // Hardware Revision
        tlt.send(Protocol.ORDER_REQ_INFORMATION, 11);
        response = tlt.getFrame();
        if(false == tlt.checkReply(response, Protocol.RESPONSE_OK, 0))
        {
            success = false;
            return;
        }
        dutInfo.setHardwareRevision(tlt.getIntegerFrom(response, 1, response.length -1));
        tlt.IncrementSequenceCounter();

        // max supported Step Rate
        tlt.send(Protocol.ORDER_REQ_INFORMATION, 12);
        response = tlt.getFrame();
        if(false == tlt.checkReply(response, Protocol.RESPONSE_OK, 1))
        {
            success = false;
            return;
        }
        dutInfo.setMaxStepRate(tlt.getIntegerFrom(response, 1, response.length -1));
        tlt.IncrementSequenceCounter();

        // Host Timeout
        tlt.send(Protocol.ORDER_REQ_INFORMATION, 13);
        response = tlt.getFrame();
        if(false == tlt.checkReply(response, Protocol.RESPONSE_OK, 1))
        {
            success = false;
            return;
        }
        dutInfo.setHostTimeout(tlt.getIntegerFrom(response, 1, response.length -1));
        tlt.IncrementSequenceCounter();
    }

    private void testStoppedMode()
    {
        log.debug("Startiing Test: Stopped Mode");
        // Send request expect Stopped (As we are after a reset)
        tlt.send(Protocol.ORDER_REQ_INFORMATION, 0);
        byte[] response = tlt.getFrame();
        if(false == tlt.checkReply(response, Protocol.RESPONSE_STOPPED, 2))
        {
            success = false;
            return;
        }
        if(response[1] != Protocol.RECOVERY_CLEARED)
        {
            log.error("Reply has wrong Recovery Option ({})!", response[1]);
            success = false;
            return;
        }
        if(response[2] != Protocol.CAUSE_RESET)
        {
            log.error("Reply has wrong Stopped Cause ({})!", response[2]);
            success = false;
            return;
        }
        tlt.IncrementSequenceCounter();
        // send a resume ack expect still stopped mode
        tlt.send(Protocol.ORDER_RESUME, 0);
        response = tlt.getFrame();
        if(false == tlt.checkReply(response, Protocol.RESPONSE_STOPPED, 2))
        {
            success = false;
            return;
        }
        if(response[1] != Protocol.RECOVERY_CLEARED)
        {
            log.error("Reply has wrong Recovery Option ({})!", response[1]);
            success = false;
            return;
        }
        if(response[2] != Protocol.CAUSE_RESET)
        {
            log.error("Reply has wrong Stopped Cause ({})!", response[2]);
            success = false;
            return;
        }
        tlt.IncrementSequenceCounter();
        // Send resume expect ok and no stopped mode anymore
        tlt.send(Protocol.ORDER_RESUME, 1);
        response = tlt.getFrame();
        if(false == tlt.checkReply(response, Protocol.RESPONSE_OK, 0))
        {
            success = false;
            return;
        }
        tlt.IncrementSequenceCounter();
    }

}
