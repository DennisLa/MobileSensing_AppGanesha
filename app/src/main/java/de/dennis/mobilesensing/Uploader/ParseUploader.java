package de.dennis.mobilesensing.Uploader;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.dennis.mobilesensing_module.mobilesensing.EventBus.UploadEvent;
import de.dennis.mobilesensing_module.mobilesensing.Module;
import de.dennis.mobilesensing_module.mobilesensing.Storage.DataAdapter;
import de.dennis.mobilesensing_module.mobilesensing.Storage.ObjectBox.SensorTimeseries;
import de.dennis.mobilesensing_module.mobilesensing.Storage.ObjectBox.SensorValue;

/**
 * Created by Dennis on 17.09.2017.
 */

public class ParseUploader {

    public ParseUploader()
    {
        EventBus.getDefault().register(this);
    }

    // This method will be called when a MessageEvent is posted (in the UI thread for Toast)
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(UploadEvent event) {
        uploadToParse(event.data);
    }

    public void uploadToParse(SensorTimeseries st){
        /*
        {
                  "timestamp_hour": "2016-05-23T16:00:00.000Z", // Date,
                  "type": "atomic_activity", //namespace
                  "sensor_id": "UUID", //Hash of UUID or suggestion; global unique
                  "info": { // Is something missing?
                      "sensor_name": "Activity Recognition",
                      "description": "Sensor to detect atomic movements using Smartphone and Wristband",
                      "value_info": { // using the same keys as the actual value keys
                          "1": { "name": "Standing", "description": "User is standing.", "unit": "probability (float)" },
                          "2": { "name": "Sitting", "description": "User is sitting.", "unit": "probability (float)" },
                          "3": { "name": "Lying", "description": "User is lying.", "unit": "probability (float)" }
                      }
                  },
                  "user": "user",
                  "values": [
                      {"timestamp":"1471352846000", "value": [0.85, 0.6, 0.5]}, //Number of Values depending on the dimesions of Sensor (e.g. 3 for Gyroscope, 2 for Bloddpressure, etc.)
                          // further value in hour
                          ]
              }
         */
        ParseObject po = new ParseObject(st.getSensor_info().getSensor_name());
        po.put("timestamp_day", st.getTimestamp_day());
        po.put("type",st.getType());
        po.put("sensor_id",st.getSensor_id());
        try {
            JSONObject jo = st.getSensor_info().toJSON();
            po.put("info",jo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        po.put("user",st.getUser());
        try {
            JSONArray ja = new JSONArray();
            for(SensorValue sv: st.getValues()){
                ja.put(sv.toJSON());
            }
            po.put("values",ja);
        } catch (JSONException e){

        }
        po.saveEventually(deleteSensorTimeseries(st));
    }

    public SaveCallback deleteSensorTimeseries(final SensorTimeseries st){
        return new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    DataAdapter da = new DataAdapter();
                    da.deleteTimeseries(st.getTimestamp_day(),st.getSensor_info().getSensor_name());
                }else{
                    //TODO
                }
            }
        };
    }
}
