package edu.skku.map.map_pp;

import java.util.HashMap;
import java.util.Map;

public class FirebasePostData {
    public String ID;
    public String x;
    public String y;

    public FirebasePostData(){

    }

    public FirebasePostData(String ID, String x, String y){
        this.ID = ID;
        this.x = x;
        this.y = y;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("ID", ID);
        result.put("x", x);
        result.put("y", y);
        return result;
    }
}
