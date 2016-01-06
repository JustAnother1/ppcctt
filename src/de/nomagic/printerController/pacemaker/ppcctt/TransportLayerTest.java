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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Lars P&ouml;tter
 * (<a href=mailto:Lars_Poetter@gmx.de>Lars_Poetter@gmx.de</a>)
 *
 */
public class TransportLayerTest
{
    private final UartConnection client;
    private int curSequenceCounter = 0;
    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    public TransportLayerTest(UartConnection client)
    {
        this.client = client;
    }

    public boolean send(byte order, byte[] parameter)
    {
        byte[] frame = new byte[5 + parameter.length];
        frame[0] = Protocol.START_OF_HOST_FRAME;
        frame[1] = (byte) (0xff & (2 + parameter.length)); // Length
        frame[2] = getSequenceCounterValue(); // Control
        frame[3] = order;
        for(int i = 0; i < parameter.length; i++)
        {
            frame[4 + i] = parameter[i];
        }
        frame[4 + parameter.length] = client.getCRCfor(frame, 3 +  parameter.length, 1); //Sync is not part of CRC
        return client.sendRequest(frame);
    }


    public boolean send(byte order, int parameter)
    {
        byte[] frame = new byte[6];
        frame[0] = Protocol.START_OF_HOST_FRAME;
        frame[1] = 3; // Length
        frame[2] = getSequenceCounterValue(); // Control
        frame[3] = order;
        frame[4] = (byte)parameter;
        frame[5] = client.getCRCfor(frame, 4, 1); //Sync is not part of CRC
        return client.sendRequest(frame);
    }

    public boolean send_byte_U32(byte order, int parameter, long para2)
    {
        byte[] frame = new byte[10];
        frame[0] = Protocol.START_OF_HOST_FRAME;
        frame[1] = 7; // Length
        frame[2] = getSequenceCounterValue(); // Control
        frame[3] = order;
        frame[4] = (byte)parameter;
        frame[5] = (byte)((para2 >>24) & 0xff);
        frame[6] = (byte)((para2 >>16) & 0xff);
        frame[7] = (byte)((para2 >>8 ) & 0xff);
        frame[8] = (byte)( para2       & 0xff);
        frame[9] = client.getCRCfor(frame, 8, 1); //Sync is not part of CRC
        return client.sendRequest(frame);
    }

    public byte[] getFrame()
    {
        byte[] frame = client.getFrame();
        if(frame == null)
        {
            return null;
        }
        if(frame.length < 5)
        {
            log.error("Received some Bytes that have not been a valid Frame !");
            return null;
        }
        if(frame[0] != Protocol.START_OF_CLIENT_FRAME)
        {
            log.error("Frame did not start with a Sync Byte !");
            return null;
        }
        if(frame[frame.length -1] != client.getCRCfor(frame, frame.length -2, 1))
        {
            log.error("Frame had an invalid CRC !");
            return null;
        }
        if(   (frame[1] == 0)
           || (frame[1] == 1)
           || (frame[1] != frame.length - 3) )
        {
            log.error("Frame did have an invalid Length Byte !");
            return null;
        }
        if((frame[2] & 0x80) == 0x80)
        {
            log.debug("Is a Debug Frame -> ignore");
            return getFrame();
        }
        if((frame[2] & 0x0f) != getSequenceCounterValue())
        {
            log.error("Frame had an invalid Sequence Counter !");
            return null;
        }
        byte[] res = new byte[frame.length - 4];
        res[0] = frame[3];
        for(int i = 1; i < frame.length -4; i++)
        {
            res[i] = frame[i + 3];
        }
        return res;
    }


    private byte getSequenceCounterValue()
    {
        return (byte)curSequenceCounter;
    }

    public void IncrementSequenceCounter()
    {
        curSequenceCounter ++;
        if(Protocol.MAX_SEQUENCE_NUMBER < curSequenceCounter)
        {
            curSequenceCounter = 0;
        }
    }

    public String getStringFrom(byte[] response, int startPosition, int length)
    {
        StringBuffer sb = new StringBuffer();
        if(response.length < startPosition + length)
        {
            log.error("Invalid Length for String !");
            length = response.length - startPosition;
        }
        for(int i = 0; i < length; i++)
        {
            sb.append((char)response[i + startPosition]);
        }
        return sb.toString();
    }

    public int getIntegerFrom(byte[] response, int startPosition, int length)
    {
        if(response.length < startPosition + length)
        {
            log.error("Invalid Length for Integer !");
            length = response.length - startPosition;
        }
        int res = 0;
        for(int i = 0; i < length; i++)
        {
            res = res * 256;
            res = res + (0xff & response[i + startPosition]);
        }
        return res;
    }

    public boolean checkReply(byte[] response, byte expectedReplyCode, int expectedMinParameterLength)
    {
        if(response == null)
        {
            log.error("Did not get a Reply !");
            return false;
        }
        if(response.length < expectedMinParameterLength + 1)
        {
            log.error("Reply too short !");
            return false;
        }
        if(response[0] != expectedReplyCode)
        {
        	String repDescr;
        	switch(response[0])
        	{
        	case 0x10: repDescr = "OK"; break;
        	case 0x11: repDescr = "Generic Application Error"; break;
        	case 0x12: repDescr = "Stopped"; break;
        	case 0x13: repDescr = "Order Specific Error"; break;
        	default:
        		repDescr = "" +  response[0];
        		break;
        	}
        	log.error("Reply has wrong Reply Code ({})!", repDescr);
            return false;
        }
        return true;
    }
}
