package edu.skku.map.map_pp;

import java.util.HashMap;
import java.util.Map;

public class FirebasePostMarker {
    public String stID;
    public String location_name;
    public String x_val;
    public String y_val;

    public FirebasePostMarker(){

    }

    public FirebasePostMarker(String stID, String location_name, String x_val, String y_val){
        this.stID = stID;
        this.location_name = location_name;
        this.x_val = x_val;
        this.y_val = y_val;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("stID", stID);
        result.put("location_name", location_name);
        result.put("x_val", x_val);
        result.put("y_val", y_val);
        return result;
    }
}
