package fr.patronuscontrol.androiduwb.uwb;

import java.io.Serializable;

import fr.patronuscontrol.androiduwb.utils.Utils;

public class UwbPhoneConfigData implements Serializable {
    private short specVerMajor;
    private short specVerMinor;
    private int sessionId;
    private byte preambleId;
    private byte channel;
    private byte profileId;
    private byte deviceRangingRole;
    private byte[] phoneMacAddress;

    public UwbPhoneConfigData() {
        specVerMajor = 0;
        specVerMinor = 0;
        sessionId = 0;
        preambleId = 0;
        channel = 0;
        profileId = 0;
        deviceRangingRole = 0;
        phoneMacAddress = new byte[2];
    }

    public UwbPhoneConfigData(short specVerMajor, short specVerMinor, int sessionId
            , byte preambleId, byte channel, byte profileId, byte deviceRangingRole
            , byte[] phoneMacAddress) {
        this.specVerMajor = specVerMajor;
        this.specVerMinor = specVerMinor;
        this.sessionId = sessionId;
        this.preambleId = preambleId;
        this.channel = channel;
        this.profileId = profileId;
        this.deviceRangingRole = deviceRangingRole;
        this.phoneMacAddress = phoneMacAddress;
    }

    public short getSpecVerMajor() {
        return specVerMajor;
    }

    public void setSpecVerMajor(short specVerMajor) {
        this.specVerMajor = specVerMajor;
    }

    public short getSpecVerMinor() {
        return specVerMinor;
    }

    public void setSpecVerMinor(short specVerMinor) {
        this.specVerMinor = specVerMinor;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public byte getPreambleId() {
        return preambleId;
    }

    public void setPreambleId(byte preambleId) {
        this.preambleId = preambleId;
    }

    public byte getChannel() {
        return channel;
    }

    public void setChannel(byte channel) {
        this.channel = channel;
    }

    public byte getProfileId() {
        return profileId;
    }

    public void setProfileId(byte profileId) {
        this.profileId = profileId;
    }

    public byte getDeviceRangingRole() {
        return deviceRangingRole;
    }

    public void setDeviceRangingRole(byte deviceRangingRole) {
        this.deviceRangingRole = deviceRangingRole;
    }

    public byte[] getPhoneMacAddress() {
        return phoneMacAddress;
    }

    public void setPhoneMacAddress(byte[] phoneMacAddress) {
        this.phoneMacAddress = phoneMacAddress;
    }

    public byte[] toByteArray() {
        byte[] response;
        response = Utils.concat(null, Utils.shortToByteArray(this.specVerMajor));
        response = Utils.concat(response, Utils.shortToByteArray(this.specVerMinor));
        response = Utils.concat(response, Utils.intToByteArray(this.sessionId));
        response = Utils.concat(response, Utils.byteToByteArray(this.preambleId));
        response = Utils.concat(response, Utils.byteToByteArray(this.channel));
        response = Utils.concat(response, Utils.byteToByteArray(this.profileId));
        response = Utils.concat(response, Utils.byteToByteArray(this.deviceRangingRole));
        response = Utils.concat(response, this.phoneMacAddress);

        return response;
    }

    public static UwbPhoneConfigData fromByteArray(byte[] data) {
        UwbPhoneConfigData uwbPhoneConfigData = new UwbPhoneConfigData();
        uwbPhoneConfigData.setSpecVerMajor(Utils.byteArrayToShort(Utils.extract(data, 2, 0)));
        uwbPhoneConfigData.setSpecVerMinor(Utils.byteArrayToShort(Utils.extract(data, 2, 2)));
        uwbPhoneConfigData.setSessionId(Utils.byteArrayToShort(Utils.extract(data, 4, 4)));
        uwbPhoneConfigData.setPreambleId(Utils.byteArrayToByte(Utils.extract(data, 1, 8)));
        uwbPhoneConfigData.setChannel(Utils.byteArrayToByte(Utils.extract(data, 1, 9)));
        uwbPhoneConfigData.setProfileId(Utils.byteArrayToByte(Utils.extract(data, 1, 10)));
        uwbPhoneConfigData.setDeviceRangingRole(Utils.byteArrayToByte(Utils.extract(data, 1, 11)));
        uwbPhoneConfigData.setPhoneMacAddress(Utils.extract(data, 2, 12));

        return uwbPhoneConfigData;
    }
}
