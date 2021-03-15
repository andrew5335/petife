package kr.co.ainus.peticaexcutor.type;

public enum CommandType {
    FEEDER((byte)1),
    WATER((byte)2),
    LAMP((byte)3),
    MODE((byte)4),
    STATUS_REQUEST((byte)5),
    CLOCK_SET((byte)6),
    FEEDER_TIME_SET((byte)7),
    WATER_TIME_SET((byte)8),
    INITIALIZATION((byte)9),
    VERSION_REQUEST((byte)10),
    KEY_LOCK((byte)11);

    private byte value;

    CommandType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}
