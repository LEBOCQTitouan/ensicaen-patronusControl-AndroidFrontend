package fr.patronuscontrol.androiduwb.uwb;

import java.io.Serializable;

import fr.patronuscontrol.androiduwb.utils.Utils;

public class UwbDeviceConfigData implements Serializable {
    private short specVerMajor;
    private short specVerMinor;
    private byte[] chipId;
    private byte[] chipFwVersion;
    private byte[] mwVersion;
    private int supportedUwbProfileIds;
    private byte supportedDeviceRangingRoles;
    public byte[] deviceMacAddress;

    public UwbDeviceConfigData() {
        specVerMajor = 0;
        specVerMinor = 0;
        chipId = new byte[2];
        chipFwVersion = new byte[2];
        mwVersion = new byte[3];
        supportedUwbProfileIds = 0;
        supportedDeviceRangingRoles = 0;
        deviceMacAddress = new byte[2];
    }

    public UwbDeviceConfigData(short specVerMajor, short specVerMinor, byte[] chipId, byte[] chipFwVersion, byte[] mwVersion, int supportedUwbProfileIds, byte supportedDeviceRangingRoles, byte[] deviceMacAddress) {
        this.specVerMajor = specVerMajor;
        this.specVerMinor = specVerMinor;
        this.chipId = chipId;
        this.chipFwVersion = chipFwVersion;
        this.mwVersion = mwVersion;
        this.supportedUwbProfileIds = supportedUwbProfileIds;
        this.supportedDeviceRangingRoles = supportedDeviceRangingRoles;
        this.deviceMacAddress = deviceMacAddress;
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

    public byte[] getChipId() {
        return chipId;
    }

    public void setChipId(byte[] chipId) {
        this.chipId = chipId;
    }

    public byte[] getChipFwVersion() {
        return chipFwVersion;
    }

    public void setChipFwVersion(byte[] chipFwVersion) {
        this.chipFwVersion = chipFwVersion;
    }

    public byte[] getMwVersion() {
        return mwVersion;
    }

    public void setMwVersion(byte[] mwVersion) {
        this.mwVersion = mwVersion;
    }

    public int getSupportedUwbProfileIds() {
        return supportedUwbProfileIds;
    }

    public void setSupportedUwbProfileIds(int supportedUwbProfileIds) {
        this.supportedUwbProfileIds = supportedUwbProfileIds;
    }

    public byte getSupportedDeviceRangingRoles() {
        return supportedDeviceRangingRoles;
    }

    public void setSupportedDeviceRangingRoles(byte supportedDeviceRangingRoles) {
        this.supportedDeviceRangingRoles = supportedDeviceRangingRoles;
    }

    public byte[] getDeviceMacAddress() {
        return deviceMacAddress;
    }

    public void setDeviceMacAddress(byte[] deviceMacAddress) {
        this.deviceMacAddress = deviceMacAddress;
    }

    public byte[] toByteArray() {
        byte[] response;
        response = Utils.shortToByteArray(this.specVerMajor);
        response = Utils.concat(response, Utils.shortToByteArray(this.specVerMinor));
        response = Utils.concat(response, this.chipId);
        response = Utils.concat(response, this.chipFwVersion);
        response = Utils.concat(response, this.mwVersion);
        response = Utils.concat(response, Utils.intToByteArray(this.supportedUwbProfileIds));
        response = Utils.concat(response, Utils.byteToByteArray(this.supportedDeviceRangingRoles));
        response = Utils.concat(response, this.deviceMacAddress);

        return response;
    }

    public static UwbDeviceConfigData fromByteArray(byte[] data) {
        UwbDeviceConfigData uwbDeviceConfigData = new UwbDeviceConfigData();
        uwbDeviceConfigData.setSpecVerMajor(Utils.byteArrayToShort(Utils.extract(data, 2, 0)));
        uwbDeviceConfigData.setSpecVerMinor(Utils.byteArrayToShort(Utils.extract(data, 2, 2)));
        uwbDeviceConfigData.setChipId(Utils.extract(data, 2, 4));
        uwbDeviceConfigData.setChipFwVersion(Utils.extract(data, 2, 6));
        uwbDeviceConfigData.setMwVersion(Utils.extract(data, 3, 8));
        uwbDeviceConfigData.setSupportedUwbProfileIds(Utils.byteArrayToInt(Utils.extract(data, 4, 11)));
        uwbDeviceConfigData.setSupportedDeviceRangingRoles(Utils.byteArrayToByte(Utils.extract(data, 1, 15)));
        uwbDeviceConfigData.setDeviceMacAddress(Utils.extract(data, 2, 16));

        return uwbDeviceConfigData;
    }
}
