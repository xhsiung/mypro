package tw.com.bais.wechat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class BootReceiver extends BroadcastReceiver {
    final String TAG = "WeChat";

    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String SPSetting = "SPSETTING";
        Log.d(TAG,"onReceive");
        Log.d(TAG,intent.getAction());

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED") ||
                intent.getAction().equals("android.intent.action.USER_PRESENT") ||
                intent.getAction().equals("tw.com.bais.wechat.BootReceiver") ){
            Log.d(TAG , "BootReceiver on Boot_COMPLETED  ");

            SharedPreferences pre = context.getSharedPreferences(SPSetting, Context.MODE_WORLD_READABLE);
            String configure = pre.getString("wechatSetting", "");

            if (configure.isEmpty()) return ;
            JSONObject obj = null;
            try {
                obj = new JSONObject(configure);
                obj.put("hasRecieve", true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Intent _intent = new Intent(context, tw.com.bais.wechat.EBusService.class);
            _intent.putExtra("xaction", 0);
            _intent.putExtra("configure", obj.toString());
            context.startService(_intent);
        }
    }
}
