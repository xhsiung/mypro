package tw.com.bais.wechat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;

public class BootReceiver extends BroadcastReceiver {
    final String TAG = "WeChat";

    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String SPSetting = "SPSETTING";
        Log.d(TAG, "BootReceiver onReceive");

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED") ||
                intent.getAction().equals("android.intent.action.USER_PRESENT") ||
                intent.getAction().equals("tw.com.bais.wechat.BootReceiver") ){
                Log.d(TAG , "BootReceiver on Boot_COMPLETED ");

            try {

                if ( EBusService.mSocket !=null && EBusService.mSocket.connected() ){
                    Log.d(TAG , "BootReceiver EBusService.mSocket.connected()");
                    return;
                }

                SharedPreferences pre = context.getSharedPreferences(SPSetting, Context.MODE_WORLD_READABLE);
                String configure = pre.getString("wechatSetting", "");
                if (configure.isEmpty()) {
                    Log.d(TAG , "BootReceiver configure.isEmpty");
                    return;
                }

                JSONObject obj = new JSONObject(configure);
                if (obj.toString() == null || !obj.has("serverip") || !obj.has("port") || !obj.has("notifyTarget")) {
                    Log.d(TAG , "BootReceiver obj.toString maybe null");
                    return;
                }
                /*
                    obj.put("hasRecieve", true);
                    Intent _intent = new Intent(context, tw.com.bais.wechat.EBusService.class);
                    _intent.putExtra("xaction", 0);
                    _intent.putExtra("configure", obj.toString());
                    context.startService(_intent);
                */

                EBundle eb = new EBundle();
                eb.action = EBusService.INIT;
                eb.Settings = obj;
                callService(context, eb);
                Log.d(TAG, "BootReceive END");

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void callService(final Context context, final EBundle eb){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context, tw.com.bais.wechat.EBusService.class);
                //startService(intent);
                context.startService(intent);
                EventBus.getDefault().post( eb );
            }
        }).start();
    }
}
