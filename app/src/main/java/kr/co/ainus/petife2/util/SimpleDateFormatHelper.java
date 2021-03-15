package kr.co.ainus.petife2.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SimpleDateFormatHelper {
    private static final SimpleDateFormatHelper INSTANCE = new SimpleDateFormatHelper();

    private SimpleDateFormatHelper() {}

    public static String toString(Date date) {
        if (date != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
            return simpleDateFormat.format(date);
        } else return null;

    }
}
