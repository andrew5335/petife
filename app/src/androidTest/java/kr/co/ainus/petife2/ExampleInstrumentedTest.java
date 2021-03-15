//package kr.co.ainus.petica;
//
//import android.content.Context;
//import android.util.Log;
//
//import androidx.lifecycle.ViewModelProviders;
//import androidx.test.InstrumentationRegistry;
//import androidx.test.runner.AndroidJUnit4;
//
//import com.nabto.api.RemoteTunnel;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import kr.co.ainus.petica.viewmodel.PeticaViewModel;
//
//import static org.junit.Assert.*;
//
///**
// * Instrumented test, which will execute on an Android device.
// *
// * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
// */
//@RunWith(AndroidJUnit4.class)
//public class ExampleInstrumentedTest {
//    @Test
//    public void useAppContext() {
//        // Context of the app under test.
//        Context appContext = InstrumentationRegistry.getTargetContext();
//
//        assertEquals("kr.co.ainus.petica", appContext.getPackageName());
//
//        Log.e("start", "test start");
//
//        RemoteTunnel remoteTunnel = new RemoteTunnel(appContext);
//        remoteTunnel.setOnResultListener(new RemoteTunnel.OnResultListener() {
//            @Override
//            public void onResult(int id, String result) {
//
//                Log.e("onResult", "result = " + result);
//
//
//            }
//        });
//        remoteTunnel.openTunnel(, 9999, 80, "lx520.cffa73.p2p.rakwireless.com");
//
//    }
//}
