package administrator.example.com.javaScriptHTML;

import android.util.Log;
import android.webkit.JavascriptInterface;

import administrator.example.com.e_seal.JsBridge;

public class ImoocJsInterface {

    private static final String TAG = "ImoocIsInterface";

    private JsBridge jsBridge;

    public ImoocJsInterface(JsBridge jsBridge){
        this.jsBridge = jsBridge;
    }

    /*
    **非主线程
     */
    @JavascriptInterface
    public void setValue(String value){
        Log.d(TAG ,"value = " + value);
        String msg = "";
//        for (int i = 0;i < value.length;i++){
//            msg += value[i];
//        }
        jsBridge.setTextViewValue(value);
    }
}
