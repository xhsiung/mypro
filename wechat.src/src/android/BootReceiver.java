package tw.com.bais.wechat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.net.NetworkInfo;
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
        Log.d(TAG, "BootReceiver onReceive");

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED") ||
                intent.getAction().equals("android.intent.action.USER_PRESENT") ||
                intent.getAction().equals("tw.com.bais.wechat.BootReceiver") ){
                Log.d(TAG , "BootReceiver on Boot_COMPLETED  ");

            try {
                SharedPreferences pre = context.getSharedPreferences(SPSetting, Context.MODE_WORLD_READABLE);
                String configure = pre.getString("wechatSetting", "");
                if (configure.isEmpty()) return ;

                JSONObject obj  = new JSONObject(configure);
                if (!obj.has("serverip") ||!obj.has("port") || !obj.has("notifyTarget") ) return;
                obj.put("hasRecieve", true);

                Intent _intent = new Intent(context, tw.com.bais.wechat.EBusService.class);
                _intent.putExtra("xaction", 0);

                _intent.putExtra("configure", obj.toString());
                context.startService(_intent);
                Log.d(TAG , "BootReceive END");

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}
