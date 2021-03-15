package kr.co.ainus.petife2.util;

public class IpAddressHelper {
    public static String intToString(int ipAddress) {
        int addr1 = (ipAddress & 0xff);
        int addr2 = (ipAddress >> 8 & 0xff);
        int addr3 = (ipAddress >> 16 & 0xff);
        int addr4 = (ipAddress >> 24 & 0xff);
        return String.format("%d.%d.%d.%d", addr1, addr2, addr3, addr4);
    }
}
