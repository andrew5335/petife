package kr.co.ainus.petife2.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonHelper {
    private static final GsonHelper INSTANCE = new GsonHelper();

    private GsonHelper() {}

    public static Gson getGson() {
        return new GsonBuilder().setPrettyPrinting().create();
    }
}
