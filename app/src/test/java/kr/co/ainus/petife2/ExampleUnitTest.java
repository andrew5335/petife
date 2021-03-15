package kr.co.ainus.petife2;

import android.content.Intent;

import com.demo.sdk.Lx520;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void test() {
        Lx520 lx520 = new Lx520("192.168.100.1" + ":" + 80, "admin");
        lx520.setOnResultListener(new Lx520.OnResultListener() {
            @Override
            public void onResult(Lx520.Response result) {
                if (result.statusCode == 200) {
                    Intent intent = new Intent();
                }
            }
        });
        lx520.joinWifi("jcom", "jcom1961^");
    }
}