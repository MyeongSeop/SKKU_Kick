package edu.skku.map.map_pp;

import androidx.fragment.app.FragmentPagerAdapter;

import java.util.HashMap;
import java.util.Map;

public class FirebasePostTime {
    public String stID;
    public String time;
    public String minute;

    public FirebasePostTime(){

    }

    public FirebasePostTime(String stID, String time, String minute){
        this.stID = stID;
        this.time = time;
        this.minute = minute;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("stID", stID);
        result.put("time", time);
        result.put("minute", minute);
        return result;
    }
}
