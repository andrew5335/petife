package kr.co.ainus.peticaexcutor;

public class Packet {
    private static final byte HEADER_1 = 0x01;
    private static final byte HEADER_2 = 0x55;
    private static final byte STX = 0x02;
    private static final byte ETX = 0x03;

    private byte[] packet;

    public void onMake(Protocol protocol) {
        byte[] command = protocol.getValue();

        byte length = (byte) (command.length + 4);
        packet = new byte[length + 2];

        packet[0] = HEADER_1;
        packet[1] = HEADER_2;
        packet[2] = STX;
        packet[3] = length;
        packet[packet.length - 1] = ETX;
        packet[packet.length - 2] = getCheckSum(command);

        onMerge(command, packet);

    }

    private void onMerge(byte[] src, byte[] dst) {

        for (int i = 0; i < src.length; i++) {
            dst[i + 4] = src[i];
        }
    }

    private byte getCheckSum(byte[] command) {
        byte result = (byte) (command.length + 4);

        for (int i = 0; i < command.length; i++) {
            result = (byte) (result ^ command[i]);
        }

        return result;
    }

    public byte[] getBytes() {
        return packet;
    }

    public static class Builder {
        private static Packet packet;

        public static Packet build() {

            return new Packet();
        }
    }
}

