package kr.co.ainus.petife2.util;

public class RandomPortHelper {
    public static int make() {
        return (int) (Math.random() * 55000) + 10000;
    }
}
