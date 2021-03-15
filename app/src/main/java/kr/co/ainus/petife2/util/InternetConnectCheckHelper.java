package kr.co.ainus.petife2.util;

import android.content.Context;

public class InternetConnectCheckHelper {
    /*
Get_Internet
: 인터넷 연결환경에 대해 체크한다.
0을 리턴할 경우, 인터넷 연결끊김
1을 리턴할 경우, 와이파이 연결상태
2를 연결할 경우, 인터넷 연결상태
 */
    public static int checkInternet(Context context) {
//        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//        if (activeNetwork != null) {
//            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
//                return 1;
//            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
//                return 2;
//            }
//        }
        return 1;
    }
//    출처: https://itun.tistory.com/355 [Bino]
}
