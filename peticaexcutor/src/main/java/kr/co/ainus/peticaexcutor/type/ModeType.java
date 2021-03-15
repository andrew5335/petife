package kr.co.ainus.peticaexcutor.type;

public enum ModeType {
    MANUAL((byte)0), AUTO((byte)1), FREE((byte)2);

    private byte value;

    ModeType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}
