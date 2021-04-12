package edu.skku.map.map_pp;

import java.util.HashMap;
import java.util.Map;

public class FirebasePost {
    public String ID;
    public String name;
    public String StID;
    public String payment;
    public String phone;

    public FirebasePost(){

    }

    public FirebasePost(String ID, String name, String StID, String payment, String phone){
        this.ID = ID;
        this.name = name;
        this.StID = StID;
        this.payment = payment;
        this.phone = phone;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("ID", ID);
        result.put("name", name);
        result.put("StID", StID);
        result.put("payment", payment);
        result.put("phone", phone);
        return result;
    }
}
