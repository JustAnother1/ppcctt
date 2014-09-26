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

import java.nio.charset.Charset;


/**
 * @author Lars P&ouml;tter
 * (<a href=mailto:Lars_Poetter@gmx.de>Lars_Poetter@gmx.de</a>)
 *
 */
public class Protocol
{
    public static final int ORDER_POS_OF_SYNC               = 0;
    public static final int ORDER_POS_OF_LENGTH             = 1;
    public static final int ORDER_POS_OF_CONTROL            = 2;
    public static final int ORDER_POS_OF_ORDER_CODE         = 3;
    public static final int ORDER_POS_OF_START_OF_PARAMETER = 4;

    public static final int REPLY_POS_OF_SYNC               = 0;
    public static final int REPLY_POS_OF_LENGTH             = 1;
    public static final int REPLY_POS_OF_CONTROL            = 2;
    public static final int REPLY_POS_OF_REPLY_CODE         = 3;
    public static final int REPLY_POS_OF_START_OF_PARAMETER = 4;

    public static final int START_OF_HOST_FRAME = 0x23;

    public static final byte ORDER_RESUME                                           = 0;
    public static final byte ORDER_REQ_INFORMATION                                  = 1;
    public static final byte ORDER_REQ_DEVICE_NAME                                  = 2;
    public static final byte ORDER_REQ_TEMPERATURE                                  = 3;
    public static final byte ORDER_GET_HEATER_CONFIGURATION                         = 4;
    public static final byte ORDER_CONFIGURE_HEATER                                 = 5;
    public static final byte ORDER_SET_HEATER_TARGET_TEMPERATURE                    = 6;
    public static final byte ORDER_REQ_INPUT                                        = 7;
    public static final byte ORDER_SET_OUTPUT                                       = 8;
    public static final byte ORDER_SET_PWM                                          = 9;
    public static final byte ORDER_WRITE_FIRMWARE_CONFIGURATION                     = 0x0A; // 10
    public static final byte ORDER_READ_FIRMWARE_CONFIGURATION                      = 0x0B; // 11
    public static final byte ORDER_STOP_PRINT                                       = 0x0C; // 12
    public static final byte ORDER_ACTIVATE_STEPPER_CONTROL                         = 0x0D; // 13
    public static final byte ORDER_ENABLE_DISABLE_STEPPER_MOTORS                    = 0x0E; // 14
    public static final byte ORDER_CONFIGURE_END_STOPS                              = 0x0F; // 15
    public static final byte ORDER_ENABLE_DISABLE_END_STOPS                         = 0x10; // 16
    public static final byte ORDER_REQUEST_DEVICE_COUNT                             = 0x11; // 17
    public static final byte ORDER_QUEUE_COMMAND_BLOCKS                             = 0x12; // 18
    public static final byte ORDER_CONFIGURE_AXIS_MOVEMENT_RATES                    = 0x13; // 19
    public static final byte ORDER_RETRIEVE_EVENTS                                  = 0x14; // 20
    public static final byte ORDER_GET_NUMBER_EVENT_FORMAT_IDS                      = 0x15; // 21
    public static final byte ORDER_GET_EVENT_STRING_FORMAT_ID                       = 0x16; // 22
    public static final byte ORDER_CLEAR_COMMAND_BLOCK_QUEUE                        = 0x17; // 23
    public static final byte ORDER_REQUEST_DEVICE_STATUS                            = 0x18; // 24
    public static final byte ORDER_CONFIGURE_MOVEMENT_UNDERRUN_AVOIDANCE_PARAMETERS = 0x19; // 25
    public static final byte ORDER_GET_FIRMWARE_CONFIGURATION_VALUE_PROPERTIES      = 0x1a; // 26
    public static final byte ORDER_TRAVERSE_FIRMWARE_CONFIGURATION_VALUES           = 0x1b; // 27
    public static final byte ORDER_RESET                                            = (byte)0x7f; // 127

    public static final int MAX_SEQUENCE_NUMBER = 15;

    public static final byte QUERY_STOPPED_STATE = 0;
    public static final byte CLEAR_STOPPED_STATE = 1;
    public static final int INFO_FIRMWARE_NAME_STRING = 0;
    public static final int INFO_SERIAL_NUMBER_STRING = 1;
    public static final int INFO_BOARD_NAME_STRING = 2;
    public static final int INFO_GIVEN_NAME_STRING = 3;
    public static final int INFO_SUPPORTED_PROTOCOL_VERSION_MAJOR = 4;
    public static final int INFO_SUPPORTED_PROTOCOL_VERSION_MINOR = 5;
    public static final int INFO_LIST_OF_SUPPORTED_PROTOCOL_EXTENSIONS = 6;

    public static final int INFO_PROTOCOL_EXTENSION_STEPPER_CONTROL = 0;
    public static final int INFO_PROTOCOL_EXTENSION_QUEUED_COMMAND = 1;
    public static final int INFO_PROTOCOL_EXTENSION_BASIC_MOVE = 2;
    public static final int INFO_PROTOCOL_EXTENSION_EVENT_REPORTING = 3;

    public static final int INFO_FIRMWARE_TYPE = 7;
    public static final int INFO_FIRMWARE_REVISION_MAJOR = 8;
    public static final int INFO_FIRMWARE_REVISION_MINOR = 9;
    public static final int INFO_HARDWARE_TYPE = 10;
    public static final int INFO_HARDWARE_REVISION = 11;
    public static final int INFO_MAX_STEP_RATE= 12;
    public static final int INFO_HOST_TIMEOUT = 13;

    public static final byte INPUT_HIGH = 1;
    public static final byte INPUT_LOW = 0;
    public static final int OUTPUT_STATE_LOW = 0;
    public static final int OUTPUT_STATE_HIGH = 0;
    public static final int OUTPUT_STATE_DISABLED = 0;
    public static final byte ORDERED_STOP = 0;
    public static final byte EMERGENCY_STOP = 1;
    public static final int DIRECTION_INCREASING = 1;
    public static final int DIRECTION_DECREASING = 0;

    public static final byte MOVEMENT_BLOCK_TYPE_COMMAND_WRAPPER     = 0x01;
    public static final byte MOVEMENT_BLOCK_TYPE_DELAY               = 0x02;
    public static final byte MOVEMENT_BLOCK_TYPE_BASIC_LINEAR_MOVE   = 0x03;
    public static final byte MOVEMENT_BLOCK_TYPE_SET_ACTIVE_TOOLHEAD = 0x04;

// Client
    public static final int START_OF_CLIENT_FRAME = 0x42;
    public static final int DEBUG_FLAG = 0x80;

    public static final byte RESPONSE_FRAME_RECEIPT_ERROR = 0;
    public static final int RESPONSE_BAD_FRAME = 0;
    public static final int RESPONSE_BAD_ERROR_CHECK_CODE = 1;
    public static final int RESPONSE_UNABLE_TO_ACCEPT_FRAME = 2;

    public static final byte RESPONSE_OK = 0x10;
    public static final byte RESPONSE_GENERIC_APPLICATION_ERROR = 0x11;

    public static final int RESPONSE_UNKNOWN_ORDER = 1;
    public static final int RESPONSE_BAD_PARAMETER_FORMAT = 2;
    public static final int RESPONSE_BAD_PARAMETER_VALUE = 3;
    public static final int RESPONSE_INVALID_DEVICE_TYPE = 4;
    public static final int RESPONSE_INVALID_DEVICE_NUMBER = 5;
    public static final int RESPONSE_INCORRECT_MODE = 6;
    public static final int RESPONSE_BUSY = 7;
    public static final int RESPONSE_FAILED = 8;
    public static final int RESPONSE_FIRMWARE_ERROR = 9;
    public static final int RESPONSE_CANNOT_ACTIVATE_DEVICE = 10;

    public static final byte RESPONSE_STOPPED = 0x12;
    public static final byte STOPPED_UNACKNOWLEADGED = 0;
    public static final byte STOPPED_ACKNOWLEADGED = 1;
    public static final byte RECOVERY_CLEARED = 1;
    public static final byte RECOVERY_PERSISTS = 2;
    public static final byte RECOVERY_UNRECOVERABLE = 3;
    public static final byte CAUSE_RESET = 0;
    public static final byte CAUSE_END_STOP_HIT = 1;
    public static final byte CAUSE_MOVEMENT_ERROR = 2;
    public static final byte CAUSE_TEMPERATURE_ERROR = 3;
    public static final byte CAUSE_DEVICE_FAULT = 4;
    public static final byte CAUSE_ELECTRICAL_FAULT = 5;
    public static final byte CAUSE_FIRMWARE_FAULT = 6;
    public static final byte CAUSE_USER_REQUESTED = 7;
    public static final byte CAUSE_HOST_TIMEOUT = 8;
    public static final byte CAUSE_OTHER_FAULT = 9;

    public static final byte RESPONSE_ORDER_SPECIFIC_ERROR = 0x13;
    public static final int SENSOR_PROBLEM = 0x7fff;

    public static final byte RESPONSE_DEBUG_FRAME_DEBUG_MESSAGE = 0x50;
    public static final byte RESPONSE_DEBUG_FRAME_NEW_EVENT = 0x51;

    public static final byte DEVICE_TYPE_FIRST = 1;
    public static final byte DEVICE_TYPE_UNUSED = 0;
    public static final byte DEVICE_TYPE_INPUT = 1;
    public static final byte DEVICE_TYPE_OUTPUT = 2;
    public static final byte DEVICE_TYPE_PWM_OUTPUT = 3;
    public static final byte DEVICE_TYPE_STEPPER = 4;
    public static final byte DEVICE_TYPE_HEATER = 5;
    public static final byte DEVICE_TYPE_TEMPERATURE_SENSOR = 6;
    public static final byte DEVICE_TYPE_BUZZER = 7;
    public static final byte DEVICE_TYPE_LAST = 7;

    public static final int FIRMWARE_SETTING_TYPE_VOLATILE_CONFIGURATION = 0;
    public static final int FIRMWARE_SETTING_TYPE_NON_VOLATILE_CONFIGURATION = 1;
    public static final int FIRMWARE_SETTING_TYPE_STATISTIC = 2;
    public static final int FIRMWARE_SETTING_TYPE_SWITCH = 3;
    public static final int FIRMWARE_SETTING_TYPE_DEBUG = 4;


    public Protocol()
    {
        // TODO Auto-generated constructor stub
    }

    public static String parse(byte[] buf)
    {
        if(null == buf)
        {
            return "no data";
        }
        if(1 > buf.length)
        {
            return "no data";
        }
        final StringBuffer res = new StringBuffer();
        if(ORDER_POS_OF_SYNC < buf.length)
        {
            if(START_OF_HOST_FRAME == buf[ORDER_POS_OF_SYNC])
            {
                res.append("Order:");
                if(ORDER_POS_OF_ORDER_CODE < buf.length)
                {
                    res.append(orderCodeToString(buf[ORDER_POS_OF_ORDER_CODE]));
                    if(ORDER_POS_OF_START_OF_PARAMETER < buf.length)
                    {
                        final int length = (0xff & buf[ORDER_POS_OF_LENGTH]) -2;
                        final int offset = ORDER_POS_OF_START_OF_PARAMETER;
                        if(0 < length)
                        {
                            if(ORDER_QUEUE_COMMAND_BLOCKS == buf[ORDER_POS_OF_ORDER_CODE])
                            {
                                res.append(parseQueueBlock(buf, length, offset));
                            }
                            else
                            {
                                res.append(Tool.fromByteBufferToHexString(buf, length, offset));
                            }
                        }
                    }
                }
            }
        }
        if(REPLY_POS_OF_SYNC < buf.length)
        {
            if(START_OF_CLIENT_FRAME == buf[REPLY_POS_OF_SYNC])
            {
                res.append("Response:");
                if(REPLY_POS_OF_REPLY_CODE < buf.length)
                {
                    res.append(replyCodeToString(buf[REPLY_POS_OF_REPLY_CODE]));
                    if(REPLY_POS_OF_START_OF_PARAMETER < buf.length)
                    {
                        final int length = (0xff & buf[REPLY_POS_OF_LENGTH]) -2;
                        final int offset = REPLY_POS_OF_START_OF_PARAMETER;
                        if(0 < length)
                        {
                            res.append(parseReplyParameter(buf[REPLY_POS_OF_REPLY_CODE], buf, length, offset));
                        }
                    }
                }
            }
        }
        return res.toString();
    }

    private static String parseReplyParameter(byte replyCode, byte[] buf, int length, int offset)
    {
        switch(replyCode)
        {
        case RESPONSE_FRAME_RECEIPT_ERROR:
            switch(buf[offset])
            {
            case RESPONSE_BAD_FRAME: return "(bad frame)";
            case RESPONSE_BAD_ERROR_CHECK_CODE: return "(bad error check code)";
            case RESPONSE_UNABLE_TO_ACCEPT_FRAME: return "(unable to accept frame)";
            default: return "(Invalid : " + Tool.fromByteBufferToHexString(buf, length, offset) + ")";
            }

        case RESPONSE_GENERIC_APPLICATION_ERROR:
            switch(buf[offset])
            {
            case RESPONSE_UNKNOWN_ORDER: return "(unknown order)";
            case RESPONSE_BAD_PARAMETER_FORMAT: return "(bad parameter format)";
            case RESPONSE_BAD_PARAMETER_VALUE: return "(bad parameter value)";
            case RESPONSE_INVALID_DEVICE_TYPE: return "(invalid device type)";
            case RESPONSE_INVALID_DEVICE_NUMBER: return "(invalid device number)";
            case RESPONSE_INCORRECT_MODE: return "(wrong mode)";
            case RESPONSE_BUSY: return "(busy)";
            case RESPONSE_FAILED: return "(failed)";
            case RESPONSE_FIRMWARE_ERROR: return "(firmware error)";
            case RESPONSE_CANNOT_ACTIVATE_DEVICE: return "(device unavailable)";
            default: return "(Invalid : " + Tool.fromByteBufferToHexString(buf, length, offset) + ")";
            }

        case RESPONSE_DEBUG_FRAME_DEBUG_MESSAGE:
            return Tool.fromByteBufferToUtf8String(buf, length, offset);

        default:
            return Tool.fromByteBufferToHexString(buf, length, offset);
        }
    }

    private static String parseQueueBlock(byte[] buf, int length, int offset)
    {
        final StringBuffer res = new StringBuffer();
        int bytesToGo = length;
        do
        {
            final int blockLength = buf[offset];
            final int BlockType = buf[offset + 1];
            switch(BlockType)
            {
            case MOVEMENT_BLOCK_TYPE_COMMAND_WRAPPER:
                res.append("[order:" + orderCodeToString(buf[offset + 2]));
                res.append(Tool.fromByteBufferToHexString(buf, blockLength -3, offset + 3));
                break;

            case MOVEMENT_BLOCK_TYPE_DELAY:
                res.append("[delay " + (((0xff & buf[offset + 2])*256 + (0xff & buf[offset + 3]))*10) + "us]");
                break;

            case MOVEMENT_BLOCK_TYPE_BASIC_LINEAR_MOVE:
                res.append(parseBasicLinearMove(buf, blockLength -2, offset +2));
                break;

            case MOVEMENT_BLOCK_TYPE_SET_ACTIVE_TOOLHEAD:
                res.append("[use Toolhead " + (0xff & buf[offset + 2]) + "]");
                break;

            default:
                res.append(Tool.fromByteBufferToHexString(buf, blockLength, offset));
                break;
            }
            offset = offset + blockLength;
            bytesToGo = bytesToGo - blockLength;
        } while(1 >bytesToGo);
        return res.toString();
    }

    private static String parseBasicLinearMove(byte[] data, int length, int offset)
    {
        final StringBuffer res = new StringBuffer();
        res.append("[");
        boolean twoByteAxisFormat;
        if(0 == (0x80 & data[offset]))
        {
            twoByteAxisFormat = false;
        }
        else
        {
            twoByteAxisFormat = true;
        }
        int AxisSelection;
        int nextByte;
        if(false == twoByteAxisFormat)
        {
            AxisSelection = (0x7f & data[offset]);
            nextByte = offset + 1;
        }
        else
        {
            AxisSelection = (0x7f & data[offset])<<8 + (0xff & data[offset + 1]);
            nextByte = offset + 2;
        }
        boolean twoByteStepCount;
        if(0 == (0x80 & data[nextByte]))
        {
            twoByteStepCount = false;
        }
        else
        {
            twoByteStepCount = true;
        }
        int AxisDirection;
        if(false == twoByteAxisFormat)
        {
            AxisDirection = (0x7f & data[nextByte]);
            nextByte = nextByte + 1;
        }
        else
        {
            AxisDirection = (0x7f & data[nextByte])<<8 + (0xff & data[nextByte + 1]);
            nextByte = nextByte + 2;
        }
        res.append("AxisDirections=" + AxisDirection);
        final int primaryAxis = (0x0f & data[nextByte]);
        res.append(" primaryAxis=" + primaryAxis);
        if(0 == (0x10 & data[nextByte]))
        {
            // normal move
        }
        else
        {
            // homing move
            res.append(" homing");
        }
        nextByte++;
        final int nominalSpeed = (0xff & data[nextByte]);
        res.append(" nominalSpeed=" + nominalSpeed);
        nextByte++;
        final int endSpeed = (0xff & data[nextByte]);
        res.append(" endSpeed=" + endSpeed);
        nextByte++;
        int accelerationSteps;
        if(true == twoByteStepCount)
        {
            accelerationSteps = (0xff & data[nextByte])*256 + (0xff & data[nextByte + 1]);
            nextByte = nextByte + 2;
        }
        else
        {
            accelerationSteps = (0xff & data[nextByte]);
            nextByte ++;
        }
        res.append(" accelSteps=" + accelerationSteps);
        int decelerationSteps;
        if(true == twoByteStepCount)
        {
            decelerationSteps = (0xff & data[nextByte])*256 + (0xff & data[nextByte + 1]);
            nextByte = nextByte + 2;
        }
        else
        {
            decelerationSteps = (0xff & data[nextByte]);
            nextByte ++;
        }
        res.append(" decelSteps=" + decelerationSteps);
        for(int i = 0; i < 16; i++)
        {
            final int pattern = 0x1<<i;
            if(pattern == (AxisSelection & pattern))
            {
                int StepsOnAxis;
                if(true == twoByteStepCount)
                {
                    StepsOnAxis = (0xff & data[nextByte])*256 + (0xff & data[nextByte + 1]);
                    nextByte = nextByte + 2;
                }
                else
                {
                    StepsOnAxis = (0xff & data[nextByte]);
                    nextByte ++;
                }
                res.append("(" + StepsOnAxis + " Steps on Axis " + i);
                if(pattern == (AxisDirection & pattern))
                {
                    res.append(" direction increasing)");
                }
                else
                {
                    res.append(" direction decreasing)");
                }
            }
            // else this axis is not selected
        }
        res.append("]");
        return res.toString();
    }

    private static String replyCodeToString(byte b)
    {
        switch(b)
        {
        case RESPONSE_FRAME_RECEIPT_ERROR: return "Frame Receipt Error";
        case RESPONSE_OK: return "ok";
        case RESPONSE_GENERIC_APPLICATION_ERROR: return "generic application Error";
        case RESPONSE_STOPPED: return "stopped";
        case RESPONSE_ORDER_SPECIFIC_ERROR: return "order specific error";
        case RESPONSE_DEBUG_FRAME_DEBUG_MESSAGE: return "debug";
        default: return "Invalid Reply Code";
        }
    }

    public static String orderCodeToString(byte b)
    {
        switch(b)
        {
        case ORDER_RESUME: return "resume";
        case ORDER_REQ_INFORMATION: return "req Information";
        case ORDER_REQ_DEVICE_NAME: return "req Device name";
        case ORDER_REQ_TEMPERATURE: return "req Temperature";
        case ORDER_GET_HEATER_CONFIGURATION: return "get Heater cfg";
        case ORDER_CONFIGURE_HEATER: return "cfg Heater";
        case ORDER_SET_HEATER_TARGET_TEMPERATURE: return "set Heater Temperature";
        case ORDER_REQ_INPUT: return "req Input";
        case ORDER_SET_OUTPUT: return "set Output";
        case ORDER_SET_PWM: return "set PWM";
        case ORDER_WRITE_FIRMWARE_CONFIGURATION: return "write FWcfg";
        case ORDER_READ_FIRMWARE_CONFIGURATION: return "read FWcfg";
        case ORDER_STOP_PRINT: return "stop print";
        case ORDER_ACTIVATE_STEPPER_CONTROL: return "activate stepper control";
        case ORDER_ENABLE_DISABLE_STEPPER_MOTORS: return "en/disable stepper";
        case ORDER_CONFIGURE_END_STOPS: return "cfg end stop";
        case ORDER_ENABLE_DISABLE_END_STOPS: return "en/disable end stops";
        case ORDER_REQUEST_DEVICE_COUNT: return "req. Device count";
        case ORDER_QUEUE_COMMAND_BLOCKS: return "add to Queue";
        case ORDER_CONFIGURE_AXIS_MOVEMENT_RATES: return "cfg Axis movement rate";
        case ORDER_RETRIEVE_EVENTS: return "retrieve Events";
        case ORDER_GET_NUMBER_EVENT_FORMAT_IDS: return "get Num. Event Format IDs";
        case ORDER_GET_EVENT_STRING_FORMAT_ID: return "get Event String Format ID";
        case ORDER_CLEAR_COMMAND_BLOCK_QUEUE: return "clear Queue";
        case ORDER_REQUEST_DEVICE_STATUS: return "req Device status";
        case ORDER_CONFIGURE_MOVEMENT_UNDERRUN_AVOIDANCE_PARAMETERS: return "cfg under run avoidance";
        case ORDER_GET_FIRMWARE_CONFIGURATION_VALUE_PROPERTIES: return "get FWcfg value Props";
        case ORDER_TRAVERSE_FIRMWARE_CONFIGURATION_VALUES: return "traverse FWcfg Values";
        case ORDER_RESET: return "reset";
        default: return "Invalid Order Code";
        }
    }

    public String getDescriptionOfStopped(byte[] stoppedMessage)
    {
        if(2 > stoppedMessage.length)
        {
            return "invalid Message";
        }
        String res = "";
        // recovery Options
        switch(stoppedMessage[0])
        {
        case RECOVERY_CLEARED:
            res = res + "(one time event- cleared)";
            break;

        case RECOVERY_PERSISTS:
            res = res + "(persisting problem)";
            break;

        case RECOVERY_UNRECOVERABLE:
            res = res + "(unrecoverable)";
            break;

        default:
            res = res + "(invalid recovery)";
            break;
        }
        // Cause
        switch(stoppedMessage[1])
        {
        case CAUSE_RESET:
            res = res + "reset";
            break;

        case CAUSE_END_STOP_HIT:
            res = res + "end stop has triggered";
            break;

        case CAUSE_MOVEMENT_ERROR:
            res = res + "movement error";
            break;

        case CAUSE_TEMPERATURE_ERROR:
            res = res + "temperature error";
            break;

        case CAUSE_DEVICE_FAULT:
            res = res + "device fault";
            break;

        case CAUSE_ELECTRICAL_FAULT:
            res = res + "electrical fault";
            break;

        case CAUSE_FIRMWARE_FAULT:
            res = res + "firmware fault";
            break;

        case CAUSE_USER_REQUESTED:
            res = res + "user request";
            break;

        case CAUSE_HOST_TIMEOUT:
            res = res + "host timeout";
            break;

        case CAUSE_OTHER_FAULT:
            res = res + "other cause";
            break;

        default:
            res = res + "invalid cause !";
            break;
        }
        // Reason
        if(2 < stoppedMessage.length)
        {
             res = res + " " + new String(stoppedMessage,
                                    2, (stoppedMessage.length - 2),
                                    Charset.forName("UTF-8"));
        }
        return res;
    }

}
