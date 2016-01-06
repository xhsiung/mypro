package tw.com.bais.wechat;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.content.SharedPreferences;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URISyntaxException;

import me.leolin.shortcutbadger.ShortcutBadger;


public class EBusService extends Service {
    final String TAG = "WeChat";
    String KEY;
    String  deviceId;
    static boolean hasGetMessage = true ;
    NotificationManager notificationManager;
    PowerManager.WakeLock wakeLock = null;
    Socket mSocket = null;
    JSONObject mSettings;
    static final String SPSetting = "SPSETTING";

    int connErrTimesStop = 0 ;
    int connErrTimes = 0 ;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle _bundle = msg.getData();
            if (msg.what == 0){
                String tmpMsg = _bundle.getString("xdata");
				int unread = _bundle.getInt("xunread");
				
                Intent intent = null;
                try {
                    String target = mSettings.getString("notifyTarget");
                    intent = new Intent( tw.com.bais.wechat.EBusService.this , Class.forName(target));

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                PendingIntent pIntent = PendingIntent.getActivity(tw.com.bais.wechat.EBusService.this, 0, intent, 0);
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(tw.com.bais.wechat.EBusService.this).setSmallIcon(getResources().getIdentifier("icon", "drawable", getPackageName()))
                                         .setContentText(tmpMsg);

                mBuilder.setAutoCancel(true);
                try {
                    if (mSettings.has("notifyTitle")){
                        String notifyTitle = mSettings.getString("notifyTitle");
                        mBuilder.setContentTitle(notifyTitle);
                    }else{
                        mBuilder.setTicker("news");
                    }

                    if (mSettings.has("notifyTicker")){
                        String notifyTicker = mSettings.getString("notifyTicker");
                        mBuilder.setTicker(notifyTicker);
                    }else{
                        mBuilder.setTicker("message");
                    }

                    if ( mSettings.getBoolean("hasSound")){
                        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
                    }
                    if ( mSettings.getBoolean("hasVibrate")){
                        long[] vibtimes = {0 ,100 ,200 ,300};
                        mBuilder.setVibrate(vibtimes);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mBuilder.setContentIntent(pIntent);
                notificationManager =(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(0, mBuilder.build());

                try {
                    ShortcutBadger.with(getApplicationContext()).count(unread);
                }catch (Exception e) {
                    Log.d(TAG,"ShortcutBadger error");
                }

            }
            super.handleMessage(msg);
        }
    };


    public EBusService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        //super.onTaskRemoved(rootIntent);
        this.stopSelf();

        //send broadcast
        Intent intent = new Intent();
        intent.setAction("tw.com.bais.wechat.BootReceiver");
        sendBroadcast(intent);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        setDeviceID();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle _bundle = intent.getExtras();
        if (_bundle != null) {
            if (_bundle.getString("configure") != null) {
                try {
                    mSettings = new JSONObject(_bundle.getString("configure"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            int xaction = _bundle.getInt("xaction");
            String channel = _bundle.getString("xchannel");

            switch (xaction) {
                case 0:
                    try {
                        Init( mSettings );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 1:
                    disConnect();
                    Connect();
                    break;

                case 2:
                    try {
                        Subscribe(channel , KEY);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case 3:
                    try {
                        UnSubscribe(channel);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case 4:
                    String msg = _bundle.getString("xmsg");
                    try {
                        Send(channel, msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case 5:
                    disConnect();
                    break;

                case 6:
                    hasGetMessage =  true;
                    break;
                case 7:
                    hasGetMessage = false;
                    break;
                case 8:
                    int num = _bundle.getInt("num");
                    setUnreadRec(num);
		    break;
            }
        }

        return START_STICKY;
        //return super.onStartCommand(intent, flags, startId);
    }


    private void setUnreadRec(int num) {
        try {
            ShortcutBadger.with(getApplicationContext()).count(num);
        }catch (Exception e) {
            Log.d(TAG,"ShortcutBadger error");
        }
    }

    private void Init(JSONObject obj) throws JSONException {
        connErrTimes = 0;
        connErrTimesStop = obj.has("connErrTimesStop") ? obj.getInt("connErrTimesStop") : 5 ;

        hasGetMessage =obj.has("hasRecieve") ? obj.getBoolean("hasRecieve") : true;
        String serverip = obj.has("serverip") ? obj.getString("serverip") : "localhost";
        Boolean hasSaveEl = obj.has("hasSaveEl") ? obj.getBoolean("hasSaveEl") : false ;

        KEY = obj.has("key") ? obj.getString("key") : "1234567890qwertyuiopasdfghjklzxcvbnm";
        int port = obj.getInt("port");
        String url = "http://" + serverip + ":" + port ;

        //wakeLock
        if (hasSaveEl){
            releaseWakeLock();
        }else {
            acquireWakeLock();
        }

        //socket
        try {
            mSocket = IO.socket(url);
            if (! mSocket.connected()){
                Connect();
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        UnSubscribe(deviceId);
        Subscribe( deviceId , KEY );

        SharedPreferences.Editor editor = getSharedPreferences(SPSetting , Context.MODE_WORLD_WRITEABLE).edit();
        editor.putString("wechatSetting", obj.toString());
        editor.commit();

        Log.d(TAG , "Init Configure && Connect");
    }

    private void Subscribe(String channel , String key) throws JSONException {
        JSONObject root = new JSONObject(String.format("{'channel': '%s' , 'user': '%s' , 'key': '%s'}" ,channel , getGAccount() , key));
        mSocket.emit("subscribe" ,root.toString());
        Log.d(TAG , "Subscribe:" + channel);
    }

    private void UnSubscribe(String channel) throws JSONException {
        JSONObject root = new JSONObject(String.format("{'channel': '%s'}" ,channel));
        mSocket.emit("unsubscribe" , root.toString());
        Log.d(TAG , "UnSubscribe:" + channel);
    }

    private void Send(String channel , String msg) throws JSONException {
        JSONObject root = new JSONObject(String.format("{'channel': '%s' ,'data': '%s' ,'device': 'mobile', 'user': '%s' }" ,channel , msg ,getGAccount()));
        mSocket.emit("send" , root.toString());
        Log.d(TAG , "Send:" + channel + " msg:" + msg);
    }

    private void Connect(){
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on(Socket.EVENT_RECONNECT , onReconnect);
        mSocket.on("mqmsg",onMqMsg);
        mSocket.connect();
        Log.d(TAG , "Connect");
    }

    public void disConnect(){
        if ( mSocket.connected()) {
            mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            mSocket.off("mqmsg", onMqMsg);
            mSocket.disconnect();
            Log.d(TAG, "disConnect");
        }
    }

    private Emitter.Listener onMqMsg = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "onMessage");

            if (hasGetMessage) {
                Message msg = handler.obtainMessage();
                msg.what = 0;
                Bundle _bundle = msg.getData();

                try {
                    JSONObject obj = new JSONObject(args[0].toString());

                    if (obj.has("device") && obj.getString("device").equalsIgnoreCase("mobile")){
						if (obj.has("unread")){
							_bundle.putInt("xunread", obj.getInt("unread"));
							_bundle.putString("xdata", obj.getString("data"));
							handler.sendMessage(msg);
						}
						
                        if (obj.has("contacts") && obj.getBoolean("contacts")){
                            mSocket.emit("send" , readContactsJson());
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

    };


    private Emitter.Listener onReconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG , "onReconnect");
            try {
                Subscribe( deviceId , KEY);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "onConnectError");
            if ( connErrTimes >= connErrTimesStop ) mSocket.disconnect();
            connErrTimes++;
        }
    };

    private String getGAccount(){
        try {
            AccountManager accountManager = (AccountManager)getSystemService(ACCOUNT_SERVICE);
            Account[] accounts = accountManager.getAccountsByType("com.google");
            return  accounts[0].name ;
        }catch (Exception e) {
            return "null@gmail.com";
        }
    }

    private  String readContactsJson() throws JSONException {
        JSONObject root = new JSONObject();
        JSONArray jarr = new JSONArray();

        String[] wantedData = { ContactsContract.Contacts._ID ,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME };
        ContentResolver cr  = getContentResolver();
        Cursor cursor = cr.query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI , wantedData ,null , null , null);

        while (cursor.moveToNext()){
            int indexID = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            int ID = cursor.getInt(indexID);

            int indexName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            String name = cursor.getString(indexName);

            int indexNumber = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            String number = cursor.getString(indexNumber);

            JSONObject obj = new JSONObject(String.format("{'name':'%s' , 'mobile':'%s'}", name , number));
            jarr.put(obj);
        }

        root.put("channel", deviceId);
        root.put("uid",getGAccount());
        root.put("data" , "");
        root.put( "contacts", jarr);
        return root.toString();
    }

    @Override
    public void onDestroy() {
        //super.onDestroy();
        releaseWakeLock();
    }

    private void setDeviceID(){
        try {
            deviceId = Settings.Secure.getString(this.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        }catch (Exception e){
            deviceId = "0987654321";
        }
    }

    private void acquireWakeLock()
    {
        if (null == wakeLock)
        {
            PowerManager pm = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK|PowerManager.ON_AFTER_RELEASE, "PostLocationService");
            if (null != wakeLock)
            {
                wakeLock.acquire();
            }
        }
    }


    private void releaseWakeLock()
    {
        if (null != wakeLock)
        {
            wakeLock.release();
            wakeLock = null;
        }
    }
}
