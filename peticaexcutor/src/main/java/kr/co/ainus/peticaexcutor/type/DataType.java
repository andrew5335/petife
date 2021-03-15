package kr.co.ainus.peticaexcutor.type;

public enum DataType {
    OFF((byte)0),
    ON((byte)1);

    private byte value;

    DataType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}
