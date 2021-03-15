package kr.co.ainus.peticaexcutor.type;

public enum StatusType {
    FEEDER_RUN((byte) 0x01),
    WATER_RUN((byte) 0x02),
    LAMP_ON((byte) 0x04),
    CLOCK_SET((byte) 0x08),
    FEEDER_FULL((byte) 0x10),
    WATER_FULL((byte) 0x20),
    KEY_LOCK((byte) 0x40),
    POWER_ON((byte) 0x80);

    private byte value;

    StatusType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}
