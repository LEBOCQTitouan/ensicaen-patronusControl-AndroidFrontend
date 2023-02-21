package fr.patronuscontrol.androiduwb.uwb;

public interface Protocol {

    byte RANGING_ROLE_CONTROLEE = 0x00;
    byte RANGING_ROLE_CONTROLLER = 0x01;

    // Android UWB OoB protocol
    enum MessageId {
        // Messages from the Uwb device
        uwbDeviceConfigurationData  ((byte) 0x01),
        uwbDidStart                 ((byte) 0x02),
        uwbDidStop                  ((byte) 0x03),

        // Messages from the Uwb phone
        initialize                  ((byte) 0xA5),
        uwbPhoneConfigurationData   ((byte) 0x0B),
        stop                        ((byte) 0x0C);

        private final byte value;

        MessageId(final byte newValue) {
            value = newValue;
        }

        public byte getValue() {
            return value;
        }
    }
}
