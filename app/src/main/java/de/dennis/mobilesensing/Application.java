package de.dennis.mobilesensing;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.parse.Parse;
import com.parse.ParseUser;

import org.greenrobot.eventbus.EventBus;

import de.dennis.mobilesensing.Uploader.ParseUploader;
import de.dennis.mobilesensing_module.mobilesensing.Module;
import de.dennis.mobilesensing_module.mobilesensing.SensingManager.SensingManager;
import de.dennis.mobilesensing_module.mobilesensing.SensingManager.SensorNames;
import de.dennis.mobilesensing_module.mobilesensing.Upload.UploadManager;

/**
 * Created by Dennis on 28.02.2017.
 */
public class Application extends MultiDexApplication {
    private static Context context;
    private static SensingManager sensMang;
    private static UploadManager uplMang;
    private static ParseUploader uploader;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        //TODO
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("SensingApp")
                .server("http://141.99.12.45:1337/parse/")
                .build()
        );
        Module.init(context, "USERNAME");
        sensMang = Module.getSensingManager();
        sensMang.setSensingSetting(SensorNames.Activity,false);
        sensMang.setSensingSetting(SensorNames.GPS,false);
        sensMang.setSensingSetting(SensorNames.WLANUpload,false);
        sensMang.setSensingSetting(SensorNames.ScreenOn,false);
        sensMang.setSensingSetting(SensorNames.Apps,false);
        sensMang.setSensingSetting(SensorNames.Call,false);
        sensMang.setSensingSetting(SensorNames.Network,false);

        uplMang = Module.getUploadManager();


    }
    public static Context getContext() {
        return context;
    }
    public static SensingManager getSensingManager(){
        return sensMang;
    }
    public static void startSensing(){
        if(ParseUser.getCurrentUser().isAuthenticated()){
            sensMang.startSensing();
            uplMang.setDailyUpload(context);
            uploader = new ParseUploader();
        }
    }
}
