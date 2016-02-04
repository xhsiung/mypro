package tw.com.bais.wechat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WeChat extends CordovaPlugin {
    final String TAG = "WeChat";
    static JSONObject defaultSettings = new JSONObject();
    static JSONObject updateSettings;
    NetworkInfo mNetworkInfo = null;

    @Override
    public void onPause(boolean multitasking) {
        Intent _intent = new Intent(cordova.getActivity(),tw.com.bais.wechat.EBusService.class);
        JSONObject  configure = getSettings();
        if (!configure.has("serverip") ||!configure.has("port") || !configure.has("notifyTarget") ) return;
        try {
            configure.put("hasRecieve", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        _intent.putExtra("xaction", 0);
        _intent.putExtra("configure", configure.toString());
        cordova.getActivity().startService(_intent);
        Log.d(TAG , "WeChat onPause");
    }


    @Override
    public void onResume(boolean multitasking) {
        //super.onResume(multitasking);
        Intent _intent = new Intent(cordova.getActivity(),tw.com.bais.wechat.EBusService.class);
        JSONObject configure = getSettings();
        if (!configure.has("serverip") ||!configure.has("port") || !configure.has("notifyTarget") ) return;
        try {
            configure.put("hasRecieve",false);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        _intent.putExtra("xaction", 0);
        _intent.putExtra("configure", configure.toString());
        cordova.getActivity().startService(_intent);
        Log.d(TAG, "WeChat onResume");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "WeChat onDestroy");
    }


    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        initNetworkInfo();
        if (mNetworkInfo == null || !mNetworkInfo.isAvailable()) {
            Log.d(TAG , "WeChat execute mNetworkInfo error");
            return  false;
        }

        if (action.equalsIgnoreCase("configure")){
            JSONObject settings = args.getJSONObject(0);
            boolean update = args.getBoolean(1);

            if (update){
                setDefaultSettings(settings);
            }else{
                setUpdateSettings(settings);
            }
            return true;
        }


        Activity context = cordova.getActivity();
        if (action.equalsIgnoreCase("initConnect")) {
            Intent _intent = new Intent(context,tw.com.bais.wechat.EBusService.class);
            String configure = getSettings().toString();
            _intent.putExtra("xaction", 0);
            _intent.putExtra("configure", configure);

            context.startService(_intent);
            return true;
        }

        if (action.equalsIgnoreCase("disConnect")) {
            Intent _intent = new Intent(context,tw.com.bais.wechat.EBusService.class);
            String configure = getSettings().toString();
            _intent.putExtra("xaction", 5);
            context.startService(_intent);
            return true;
        }

        if (action.equalsIgnoreCase("setUnreadRec")) {
            int num = args.getInt(0);
            Intent _intent = new Intent(context,tw.com.bais.wechat.EBusService.class);
            _intent.putExtra("xaction", 8);
            _intent.putExtra("num" , num);
            context.startService(_intent);
            return true;
        }
        return false;
    }

    private void initNetworkInfo() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager)cordova.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
    }

    private void setDefaultSettings(JSONObject settings){
        defaultSettings = settings;
    }

    private void setUpdateSettings(JSONObject settings){
        updateSettings = settings ;
    }

    protected static JSONObject getSettings(){
        if (updateSettings != null) return updateSettings;
        return defaultSettings;
    }

    protected static void delUpdateSettings(){
        updateSettings = null;
    }

}
