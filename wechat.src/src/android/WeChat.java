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

import de.greenrobot.event.EventBus;

public class WeChat extends CordovaPlugin {
    final String TAG = "WeChat";
    static JSONObject defaultSettings = new JSONObject();
    static JSONObject updateSettings;
    NetworkInfo mNetworkInfo = null;

    @Override
    public void onPause(boolean multitasking) {
        JSONObject  configure = getSettings();
        if (!configure.has("serverip") ||!configure.has("port") || !configure.has("notifyTarget") ) return;
        try {
            configure.remove("hasRecieve");
            configure.put("hasRecieve", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Activity context = cordova.getActivity();
        /*
        Intent _intent = new Intent(context ,tw.com.bais.wechat.EBusService.class);
        _intent.putExtra("xaction", 0);
        _intent.putExtra("configure", configure.toString());
        cordova.getActivity().startService(_intent);
        */

        EBundle eb = new EBundle();
        eb.action = EBusService.INIT;
        eb.Settings = getSettings();
        callService(context,eb);

        Log.d(TAG, "WeChat onPause");
    }


    @Override
    public void onResume(boolean multitasking) {
        //super.onResume(multitasking);
        JSONObject configure = getSettings();
        if (!configure.has("serverip") ||!configure.has("port") || !configure.has("notifyTarget") ) return;
        try {
            configure.remove("hasRecieve");
            configure.put("hasRecieve",false);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Activity context = cordova.getActivity();
        /*
        Intent _intent = new Intent(context,tw.com.bais.wechat.EBusService.class);
        _intent.putExtra("xaction", 0);
        _intent.putExtra("configure", configure.toString());
        cordova.getActivity().startService(_intent);
        */
        EBundle eb = new EBundle();
        eb.action = EBusService.INIT;
        eb.Settings = getSettings();
        callService(context,eb);
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
            /*
            Intent _intent = new Intent(context,tw.com.bais.wechat.EBusService.class);
            String configure = getSettings().toString();
            _intent.putExtra("xaction", 0);
            _intent.putExtra("configure", configure);
            context.startService(_intent);
            */
            EBundle eb = new EBundle();
            eb.action = EBusService.INIT;
            eb.Settings = getSettings();
            callService(context,eb);
            return true;
        }

        if (action.equalsIgnoreCase("disConnect")) {
            /*
            Intent _intent = new Intent(context,tw.com.bais.wechat.EBusService.class);
            String configure = getSettings().toString();
            _intent.putExtra("xaction", 5);
            context.startService(_intent);
            */
            EBundle eb = new EBundle();
            eb.action = EBusService.DISCONN;
            callService(context,eb);
            return true;
        }

        if (action.equalsIgnoreCase("setUnreadRec")) {
            /*
            int num = args.getInt(0);
            Intent _intent = new Intent(context,tw.com.bais.wechat.EBusService.class);
            _intent.putExtra("xaction", 8);
            _intent.putExtra("num", num);
            context.startService(_intent);
            */
            EBundle eb = new EBundle();
            eb.action = EBusService.UNREADREC;
            eb.num = args.getInt(0);
            callService(context,eb);
            return true;
        }
        return false;
    }


    private void callService(final Activity context, final EBundle eb){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context, tw.com.bais.wechat.EBusService.class);
                //startService(intent);
                context.startService( intent);
                EventBus.getDefault().post( eb );
            }
        }).start();
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
