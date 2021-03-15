package kr.co.ainus.petife2.video;

public class PCMA2PCM {
    public static int SIGN_BIT = 128;
    public static int QUANT_MASK = 15;
    public static int NSEGS = 8;
    public static int SEG_SHIFT = 4;
    public static int SEG_MASK = 112;
    public static int[] seg_end = new int[]{255, 511, 1023, 2047, 4095, 8191, 16383, 32767};
    char[] a = new char[]{'\u0001', '\u0001', '\u0002', '\u0002', '\u0003', '\u0003', '\u0004', '\u0004', '\u0005', '\u0005', '\u0006', '\u0006', '\u0007', '\u0007', '\b', '\b', '\t', '\n', '\u000b', '\f', '\r', '\u000e', '\u000f', '\u0010', '\u0011', '\u0012', '\u0013', '\u0014', '\u0015', '\u0016', '\u0017', '\u0018', '\u0019', '\u001b', '\u001d', '\u001f', '!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '.', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[', '\\', ']', '^', '_', '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '|', '}', '~', '\u007f', '\u0080'};
    char[] b = new char[]{'\u0001', '\u0003', '\u0005', '\u0007', '\t', '\u000b', '\r', '\u000f', '\u0010', '\u0011', '\u0012', '\u0013', '\u0014', '\u0015', '\u0016', '\u0017', '\u0018', '\u0019', '\u001a', '\u001b', '\u001c', '\u001d', '\u001e', '\u001f', ' ', ' ', '!', '!', '"', '"', '#', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', '0', '0', '1', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?', '@', '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[', '\\', ']', '^', '_', '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '|', '}', '~', '\u007f'};
    static int c = 132;

    public PCMA2PCM() {
    }

    static int a(byte var0) {
        var0 = (byte)(var0 ^ 85);
        int var1 = var0 & QUANT_MASK;
        int var2 = (var0 & SEG_MASK) >> SEG_SHIFT;
        if (var2 != 0) {
            var1 = var1 + var1 + 1 + 32 << var2 + 2;
        } else {
            var1 = var1 + var1 + 1 << 3;
        }

        return (var0 & SIGN_BIT) == 0 ? -var1 : var1;
    }
}
