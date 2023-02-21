/*====================================================================================*/
/*                                                                                    */
/*                        Copyright 2021 NXP                                          */
/*                                                                                    */
/*   All rights are reserved. Reproduction in whole or in part is prohibited          */
/*   without the written consent of the copyright owner.                              */
/*                                                                                    */
/*   NXP reserves the right to make changes without notice at any time. NXP makes     */
/*   no warranty, expressed, implied or statutory, including but not limited to any   */
/*   implied warranty of merchantability or fitness for any particular purpose,       */
/*   or that the use will not infringe any third party patent, copyright or trademark.*/
/*   NXP must not be liable for any loss or damage arising from its use.              */
/*                                                                                    */
/*====================================================================================*/
package fr.patronuscontrol.androiduwb.uwb;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;


@SuppressWarnings("unused")
public class UwbConfig {
    private static final String TAG = UwbConfig.class.getSimpleName();

    public int SessionStatus = 0xFF;

    public int SessionId = new Random().nextInt();
    public byte SessionType = 0x00;
    public byte RangingDataSamplingRate = 1;

    // byte  (1 octet)   8-bit signed two's complement integer [-128..127]. For unsigned, use int toUnsignedInt(byte x)
    // short (2 octets) 16-bit signed two's complement integer [-32768..32757]. For unsigned, use int toUnsignedInt(short x)
    // int   (4 octets) 32-bit signed two's complement integer or unsigned with parseUnsignedInt(String s)
    // long  (8 octets) 64-bit two's complement integer or unsigned with parseUnsignedLong(String s)

    // UCI application parameters with default value
    public byte DeviceType = 0x00;
    public byte RangingRoundUsage = 0x02;
    public byte StsConfig = 0x00;
    public byte MultiNodeMode = 0x00;
    public byte ChannelNumber = 9;
    public byte NumberOfControlees = 1;
    public long DeviceMacAddress = 0x3344;
    public long[] DstMacAddressList = new long[] {0x1122};
    public short SlotDuration = 2400;
    public int RangingInterval = 240;
    public int StsIndex = 0x00000000;
    public byte MacFcsType = 0x00;
    public byte RangingRoundControl = 0x03;
    public byte AoaResultReq = 0x01;
    public byte RangeDataNtfConfig = 0x01;
    public short RangeDataNtfProximityNear = 0;
    public short RangeDataNtfProximityFar = 20000;
    public byte DeviceRole = 0x00;
    public byte RframeConfig = 0x03;
    public byte RxMode = 0x00;
    public byte PreambleCodeIndex = 10;
    public byte SfdId = 0x02;
    public byte PsduDataRate = 0x00;
    public byte PreambleDuration = 0x01;
    public byte RxAntennaSelection = 1;
    public byte MacCfg = 0x03;
    public byte RangingTimeStruct = 0x01;
    public byte SlotPerRR = 6;
    public byte TxAdaptivePayloadPower = 0x00;
    public byte TxAntennaSelection = 0x01;
    public byte ResponderSlotIndex = 1;
    public byte PrfMode = 0x00;
    public byte MaxContentionPhaseLength = 50;
    public byte ContentionPhaseUpdateLength = 5;
    public byte ScheduledMode = 0x01;
    public byte KeyRotation = 0x00;
    public byte KeyRotationRate = 0;
    public byte SessionPriority = 50;
    public byte MacAddressMode = 0x00;
    public short VendorId = 0x0708;
    public byte[] StaticStsIv = new byte[]{0x01,0x02,0x03,0x04,0x05,0x06};
    public byte NumberOfStsSegments = 1;
    public short MaxRrRetry = 0;
    public int UwbInitiationTime = 0;
    public byte HoppingMode = 0x00;
    public byte BlockStriding = 0x00;
    public byte ResultReportConfig = 0x01;
    public byte InBandTerminationAttemptCount = 1;
    public int SubSessionId = 0x00000000;
    public short TdoaReportFrequency = 0x0100;
    public short BlinkRandomInterval = 0;
    public byte AuthenticityTag = 0x00;
    public short MaxNumberOfBlocks = 0;
    public short MaxNumberOfMeasurements = 0;
    public byte StsLength = 1;

    // Ext application parameters with default values
    public byte ToaMode = 0x02;
    public byte CirCaptureMode = 0x76;
    public byte MacPayloadEncryption = 0x01;
    public byte RxAntennaPolarizationOption = 0x00;
    public byte SessionSyncAttempts = 3;
    public byte SessionSchedAttempts = 3;
    public byte SessionInbandDataTxBlocks = 1;
    public byte SessionInbandDataRxBlocks = 1;
    public byte DataTransfertMode = 1;
    public byte SchedStatusNtf = 0x00;
    public byte TxPowerDeltaFcc = 0;
    public byte TestKdfFeature = 0x00;
    public byte DualAoaPreambleSts = 0x00;
    public byte TxPowerTempCompensation = 0x00;
    public byte WifiCoexMaxToleranceCount = 3;

    // Debug parameters
    public short ThreadSecure = 0x0000;
    public short ThreadSecureIsr = 0x0000;
    public short ThreadNonSecureIsr = 0x0000;
    public short ThreadShell = 0x0000;
    public short ThreadPhy = 0x0000;
    public short ThreadRanging = 0x0000;
    public short ThreadSecureElement = 0x0000;
    public short ThreadUwbWlanCoex = 0x0000;
    public byte DataLoggerNtf = 0x00;
    public byte CirLogNtf = 0x00;
    public byte PsduLogNtf = 0x00;
    public byte RframeLogNtf = 0x00;
    public byte TestContentionRangingFeature = 0x00;
    public short CirCaptureWindowStartIndex = 0;
    public short CirCaptureWindowEndIndex = 1023;


    public static final String ACTION_SET_CONFIG = "com.nxp.cascaen.action.SET_CONFIG";
    public static final String ACTION_SET_FILE_NAME = "com.nxp.cascaen.action.SET_FILE_NAME";
    public static final String ACTION_SET_PARAMETER = "com.nxp.cascaen.action.SET_PARAMETER";
    public static final String ACTION_START_RANGING = "com.nxp.cascaen.action.START_RANGING";
    public static final String ACTION_STOP_RANGING = "com.nxp.cascaen.action.STOP_RANGING";


    public void setDstMacAddressList(long[] dstMacAddressList) {
        DstMacAddressList = dstMacAddressList;
    }

    public byte[] getDstMacAddressList() {
        ByteBuffer buffer;

        if (MacAddressMode == 0){
            // 2-bytes MAC address
            buffer = ByteBuffer.allocate(2 * DstMacAddressList.length);
            buffer.order(ByteOrder.BIG_ENDIAN);

            for (long l : DstMacAddressList) {
                buffer.putShort((short) l);
            }
        } else {
            // 8-bytes MAC address
            buffer = ByteBuffer.allocate(8 * DstMacAddressList.length);
            buffer.order(ByteOrder.BIG_ENDIAN);

            for (long l : DstMacAddressList) {
                buffer.putLong(l);
            }
        }

        return buffer.array();
    }

    public void setNumberOfControlees(byte numberOfControlees) {
        NumberOfControlees = numberOfControlees;
    }

    // Method to convert data
    public long byteArrayToAddress(byte[] array) {
        ByteBuffer buffer = ByteBuffer.wrap(array);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        if (MacAddressMode == 0) {
            // 2-bytes MAC address
            return buffer.getShort();
        } else {
            // 8-bytes MAC address
            return buffer.getLong();
        }
    }

    public byte[] addressToByteArray(long value) {
        ByteBuffer buffer;

        if (MacAddressMode == 0){
            // 2-bytes MAC address
            buffer = ByteBuffer.allocate(2);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.putShort((short) value);
        } else {
            // 8-bytes MAC address
            buffer = ByteBuffer.allocate(8);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.putLong(value);
        }

        return buffer.array();
    }

    public byte[] addressListToByteArray(long[] addressArray) {
        ByteBuffer buffer;

        if (MacAddressMode == 0){
            // 2-bytes MAC address
            buffer = ByteBuffer.allocate(2 * addressArray.length);
            buffer.order(ByteOrder.LITTLE_ENDIAN);

            for (long l : addressArray) {
                buffer.putShort((short) l);
            }
        } else {
            // 8-bytes MAC address
            buffer = ByteBuffer.allocate(8 * addressArray.length);
            buffer.order(ByteOrder.LITTLE_ENDIAN);

            for (long l : addressArray) {
                buffer.putLong(l);
            }
        }

        return buffer.array();
    }

    public byte byteToAntenna(byte value) {
        byte mask;

        // Return index 8 to 1 depending on the first bit set to one
        for (byte i = 8 ; i > 0 ; i--) {
            mask = (byte) (1 << (i - 1));
            if ((value & mask) == mask) return i;
        }

        return 0;
    }

    public byte antennaToByte(byte value) {
        // Invalid value
        if ((value < 1) || (value > 8)) return 0;

        return (byte) (1 << (value - 1));
    }

    public byte[] staticStsIvToByteArray(long value) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putLong(value);

        byte[] array = {0, 0, 0, 0, 0, 0};

        // Keep the 6 lower bytes
        for (byte idx = 0 ; idx < 6 ; idx++) {
            array[idx] = buffer.array()[idx];
        }

        return array;
    }
}
