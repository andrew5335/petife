package kr.co.ainus.peticaexcutor.type;

public enum CommandType {
    FEEDER((byte)1),
    WATER((byte)2),
    LAMP((byte)3),
    MODE((byte)4),    // 변경된 자율 급식 프로토콜에서 0x04 사용하고 있어 기존 MODE 값을 자율 급식에 사용 2021-05-13 by Andrew Kim
    //FEEDER_FREE((byte)4),    // 변경된 급식 예약 프로토콜 규격 적용 2021-05-13 by Andrew Kim
    STATUS_REQUEST((byte)5),
    CLOCK_SET((byte)6),
    FEEDER_TIME_SET((byte)7),
    WATER_TIME_SET((byte)8),
    INITIALIZATION((byte)9),
    VERSION_REQUEST((byte)10),
    KEY_LOCK((byte)11),
    WATER_FREE((byte)12);    // 변경된 자율 급수 프로토콜 규격 적용 2021-05-13 by Andrew Kim

    private byte value;

    CommandType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}
