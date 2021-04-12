package edu.skku.map.map_pp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.health.TimerStat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.message.template.LocationTemplate;
import com.kakao.message.template.TemplateParams;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.Profile;
import com.kakao.usermgmt.response.model.UserAccount;
import com.kakao.util.OptionalBoolean;
import com.muddzdev.styleabletoast.StyleableToast;
import com.squareup.picasso.Picasso;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.Inflater;

import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class mainpage extends AppCompatActivity implements MapView.POIItemEventListener, MapView.CurrentLocationEventListener, MapView.MapViewEventListener{

    Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private DatabaseReference mPostReference;
    MenuItem payment, time, phone;
    int menu_time, menu_minute;
    TextView nav_ID, nav_studentID;
    ImageView nav_img;
    String ID, x, y, get_time, student_ID, get_minute;
    MapView mapView;
    FloatingActionButton btn, cancel_btn;
    MapPoint.GeoCoordinate my_point;
    FloatingTextButton text_btn;
    public static int mark_num=20;
    int cur_mark;
    long s_time=0, e_time=0;
    int mode, count_time=0; // mode:0->normal, 1-> reservation, 2->riding
    TimerTask tt;
    Timer timer;
    boolean quit_state=false;
    int blablabla=0;
    int sharemode=0;
    String URL = "https://www.google.con.kr/maps/@"; //x,y,18z

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);

        Intent intent = getIntent();
        ID =intent.getStringExtra("ID");
        mPostReference = FirebaseDatabase.getInstance().getReference();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        text_btn = (FloatingTextButton)findViewById(R.id.riding_button);
        cur_mark=0;
        mode=0; //0: nothing , 1: reservation, 2: riding
        mapView = new MapView(this);
        ViewGroup mapViewContainer = (ViewGroup)findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        map_init();

        mapView.setCurrentLocationEventListener(this);
        mapView.setPOIItemEventListener(this);
        mapView.setMapViewEventListener(this);
        set_marker();
        //add marker by reading file -> don't erase
        /*String result = "";
        InputStream in = context.getResources().openRawResource(R.raw.kick_data);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int i;
        try{
            i = in.read();
            while( i!= -1){
                byteArrayOutputStream.write(i);
                i = in.read();
            }
            result =  new String(byteArrayOutputStream.toByteArray(), "UTF-8");
            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] result_parse = result.split("\n");
        int count=1;
        for(i=0;i<result_parse.length;i++){
            String[] adder = result_parse[i].split(" ");
            x = adder[0];
            y = adder[1];
            postFirebaseDatabase(true, String.valueOf(count), x, y);
            kick_marker();
            count++;
        }
        */
        //Don't erase!

        btn = (FloatingActionButton)findViewById(R.id.location_btn);
        btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(mapView.getCurrentLocationTrackingMode() == MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading){
                   mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
               }
               else {
                   mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
                   mapView.setZoomLevel(2, true);
               }
           }
        });
        cancel_btn = (FloatingActionButton)findViewById(R.id.cancel_button);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                double x_fin, y_fin;
                x_fin = my_point.latitude;
                y_fin = my_point.longitude;
                if(mode==2) {
                    postFirebaseDatabase(true, String.valueOf(cur_mark), String.valueOf(x_fin), String.valueOf(y_fin));
                    text_btn.setVisibility(View.GONE);
                    cancel_btn.setVisibility(View.GONE);
                    btn.setVisibility(View.VISIBLE);
                    set_marker();
                    e_time=System.currentTimeMillis();
                    upload_time();
                    mode=0;
                    NotificationManagerCompat.from(mainpage.this).cancel(1);
                    StyleableToast.makeText(mainpage.this, "Riding finished! Thank you for using!", Toast.LENGTH_LONG, R.style.mytoast2).show();
                }
                else if(mode==1){
                    text_btn.setVisibility(View.GONE);
                    cancel_btn.setVisibility(View.GONE);
                    btn.setVisibility(View.VISIBLE);
                    end_reservation();
                    StyleableToast.makeText(mainpage.this, "Reservation Canceled!", Toast.LENGTH_LONG, R.style.mytoast).show();
                }
            }
        });

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_Payment:
                    case R.id.navigation_Time:
                    case R.id.navigation_phone:
                        break;
                    case R.id.navigation_share:
                        //share
                        share();
                    case R.id.navigation_logout:
                        onBackPressed();
                }
                return false;
            }
        });
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerToggle.syncState();

        //navigation bar
        View header = navigationView.getHeaderView(0);
        final Menu menu = navigationView.getMenu();
        nav_ID = header.findViewById(R.id.drawer_ID);
        nav_img = header.findViewById(R.id.profile_img);
        nav_studentID = header.findViewById(R.id.drawer_studentID);
        payment = menu.findItem(R.id.navigation_Payment);
        time = menu.findItem(R.id.navigation_Time);
        phone = menu.findItem(R.id.navigation_phone);

        UserManagement.getInstance()
                .me(new MeV2ResponseCallback() {
                    @Override
                    public void onSessionClosed(ErrorResult errorResult) {
                        get_data();
                    }

                    @Override
                    public void onSuccess(MeV2Response result) {
                        UserAccount myAccount = result.getKakaoAccount();
                        if(myAccount != null){
                            Profile myprofile = myAccount.getProfile();
                            if(myprofile != null){
                                ID = myprofile.getNickname();
                                String get_img = "";
                                get_img = myprofile.getProfileImageUrl();
                                nav_ID.setText(ID);
                                get_data();
                                if(get_img != null)Picasso.get().load(get_img).into(nav_img);
                            }else if(myAccount.profileNeedsAgreement() == OptionalBoolean.TRUE){
                            }else{
                            }
                        }
                    }
                });
        menu_time =0;
        menu_minute=0;
        final ValueEventListener postListner = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    FirebasePostTime get = postSnapshot.getValue(FirebasePostTime.class);
                    if(key.equals(student_ID)){
                        menu_time = Integer.parseInt(get.time);
                        menu_minute = Integer.parseInt(get.minute);
                        break;
                    }
                }
                time.setTitle(menu_minute + " minutes" + menu_time + " seconds");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        //mPostReference.child("time").addListenerForSingleValueEvent(postListner);
        mPostReference.child("time").addValueEventListener(postListner);

    }

    public void get_data(){
        final ValueEventListener postListner = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    FirebasePost get = postSnapshot.getValue(FirebasePost.class);
                    if(ID.equals(key)) {
                        String info[] = {get.ID, get.StID, get.name, get.payment, get.phone};
                        student_ID = info[1];
                        nav_studentID.setText(info[1]);
                        payment.setTitle( info[3]);
                        phone.setTitle(info[4]);
                        read_location_Marker();
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mPostReference.child("user").addValueEventListener(postListner);
    }

    public void map_init(){
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
        mapView.setZoomLevel(2, true);
    }

    public void kick_marker(String x_s, String y_s, int cnt){
        double x = Double.parseDouble(x_s);
        double y = Double.parseDouble(y_s);
        MapPOIItem kick = new MapPOIItem();
        kick.setItemName("Kickboard"+cnt);
        kick.setTag(cnt);
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(x, y);
        kick.setMapPoint(mapPoint);
        //kick.setMarkerType(MapPOIItem.MarkerType.BluePin);
        kick.setMarkerType(MapPOIItem.MarkerType.CustomImage);
        kick.setCustomImageResourceId(R.mipmap.ic_kick_marker);
        kick.setCustomImageAutoscale(false);
        kick.setCustomImageAnchor(0.5f, 1.0f);
        kick.setRightSideButtonResourceIdOnCalloutBalloon(R.mipmap.ic_go);
        kick.setLeftSideButtonResourceIdOnCalloutBalloon(R.mipmap.ic_reservation);
        mapView.addPOIItem(kick);
    }

    public void postFirebaseDatabase(boolean add, String cnt, String x_get, String y_get){
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postvalues = null;
        if(add){
            FirebasePostData post = new FirebasePostData(cnt, x_get, y_get);
            postvalues = post.toMap();
            cur_mark=0;
        }
        childUpdates.put("/data/" + cnt, postvalues);
        mPostReference.updateChildren(childUpdates);

    }

    public void reservation(String num, MapPoint item_point){
        mode =1;
        int tag_num = Integer.parseInt(num);
        text_btn.setTitle("Reservation");
        text_btn.setVisibility(View.VISIBLE);
        cancel_btn.setVisibility(View.VISIBLE);
        btn.setVisibility(View.GONE);
        mapView.setZoomLevel(1, true);
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
        //make reservationed mark
        double x = item_point.getMapPointGeoCoord().latitude;
        double y = item_point.getMapPointGeoCoord().longitude;
        mapView.removeAllPOIItems();
        read_location_Marker();
        MapPOIItem reservation_item = new MapPOIItem();
        reservation_item.setItemName("Kickboard"+tag_num);
        reservation_item.setTag(tag_num);
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(x, y);
        reservation_item.setMapPoint(mapPoint);
        reservation_item.setMarkerType(MapPOIItem.MarkerType.CustomImage);
        reservation_item.setCustomImageResourceId(R.mipmap.ic_kick_marker);
        reservation_item.setCustomImageAutoscale(false);
        reservation_item.setCustomImageAnchor(0.5f, 1.0f);
        reservation_item.setRightSideButtonResourceIdOnCalloutBalloon(R.mipmap.ic_go);
        mapView.addPOIItem(reservation_item);

        //set timer
        count_time=0;
        timer = new Timer();
        tt = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(blablabla==0){
                            blablabla++;
                            count_time=0;
                            return;
                        }
                        else if(count_time!=0) {
                            text_btn.setVisibility(View.GONE);
                            cancel_btn.setVisibility(View.GONE);
                            btn.setVisibility(View.VISIBLE);
                            end_reservation();
                            //Toast.makeText(mainpage.this, "Times up!", Toast.LENGTH_LONG).show();
                            stopTimer();
                        }
                        count_time++;
                    }
                });
            }
        };
        timer.schedule(tt, 0, 5*60*1000); // To TA.... => initial value: 5*60*1000 || for easier test: 10*1000(ten second)
        count_time=0;
        StyleableToast.makeText(mainpage.this, "Reservation made! Get to the point in 5 minutes!", Toast.LENGTH_LONG, R.style.mytoast2).show();

    }

    public void stopTimer(){
        if(tt != null){
            tt.cancel();
            tt=null;
        }
        if(timer != null){
            timer.cancel();
            timer=null;
        }
    }

    public void end_reservation(){
        stopTimer();
        NotificationManagerCompat.from(mainpage.this).cancel(2);
        if(mode!=2) mode=0;
        mapView.removeAllPOIItems();
        read_location_Marker();
        set_marker();
    }

    //num = tag_num
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void go_ride(String num, MapPoint item_point){
        int tag_num = Integer.parseInt(num);
        cur_mark = tag_num;
        double item_x = item_point.getMapPointGeoCoord().latitude;
        double item_y = item_point.getMapPointGeoCoord().longitude;
        double my_x = my_point.latitude;
        double my_y = my_point.longitude;
        if(Math.abs(item_x-my_x) <= 0.0001 && Math.abs(item_y-my_y) <= 0.0001){ // To TA..... => initial value: 0.0001, 0.0001 || for easier test: 0.1, 0.1
            ride();
        }
        else{
            StyleableToast.makeText(mainpage.this, "You should get more closer to kickboard", Toast.LENGTH_LONG, R.style.mytoast).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("WrongConstant")
    public void ride(){
        text_btn.setTitle("Riding");
        mode=2;
        text_btn.setVisibility(View.VISIBLE);
        cancel_btn.setVisibility(View.VISIBLE);
        btn.setVisibility(View.GONE);
        mapView.setZoomLevel(1, true);
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
        mapView.removeAllPOIItems();
        read_location_Marker();
        s_time=System.currentTimeMillis();
        push_alarm();
        StyleableToast.makeText(mainpage.this, "Riding start! Have fun!", Toast.LENGTH_LONG, R.style.mytoast2).show();
    }

    public void set_marker(){
        final ValueEventListener postListner = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    FirebasePostData get = postSnapshot.getValue(FirebasePostData.class);
                    String info[] = {get.ID, get.x, get.y};
                    kick_marker(info[1], info[2], Integer.parseInt(info[0]));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mPostReference.child("data").addValueEventListener(postListner);
    }
    double cal_time=0;
    public void upload_time(){
        cal_time = e_time - s_time;
        cal_time = cal_time/1000;

        final ValueEventListener postListner2 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double middle=cal_time;
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    FirebasePostTime get = postSnapshot.getValue(FirebasePostTime.class);
                    if(key.equals(student_ID)){
                        get_time = get.time;
                        get_minute = get.minute;
                        middle = middle+Double.parseDouble(get_time) + 60*Double.parseDouble(get_minute);
                        int change =(int)Math.round(middle);
                        int minute = change/60;
                        int second = change%60;
                        String result = String.valueOf(second);
                        String result_min = String.valueOf(minute);
                        postFirebaseTime(true, student_ID, result, result_min);
                        return;
                    }
                }
                int change =(int)Math.round(middle);
                int minute = change/60;
                int second = change%60;
                String result = String.valueOf(second);
                String result_min = String.valueOf(minute);
                postFirebaseTime(true, student_ID, result, result_min);
                return;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mPostReference.child("time").addListenerForSingleValueEvent(postListner2);
        //mPostReference.child("time").addValueEventListener(postListner2);
    }

    public void postFirebaseTime(boolean add, String stID, String result_time, String result_minute){
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postvalues = null;
        if(add){
            FirebasePostTime post = new FirebasePostTime(stID, result_time, result_minute);
            postvalues = post.toMap();
            cur_mark=0;
        }
        childUpdates.put("/time/" + stID, postvalues);
        mPostReference.updateChildren(childUpdates);
    }

    public void postFirebaseMarker(boolean add, String stID, String location_name, String x_val, String y_val){
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postvalues = null;
        if(add){
            FirebasePostMarker post = new FirebasePostMarker(stID, location_name, x_val, y_val);
            postvalues = post.toMap();
        }
        childUpdates.put("/location_marker/" + stID + "/"+ location_name, postvalues);
        mPostReference.updateChildren(childUpdates);
    }

    public void read_location_Marker(){
        final ValueEventListener postListner = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    FirebasePostMarker get = postSnapshot.getValue(FirebasePostMarker.class);
                    String[] info = {get.stID, get.location_name, get.x_val, get.y_val};
                    MapPOIItem location_marker = new MapPOIItem();
                    location_marker.setItemName(info[1]);
                    location_marker.setTag(-1);
                    MapPoint location = MapPoint.mapPointWithGeoCoord(Double.parseDouble(info[2]), Double.parseDouble(info[3]));
                    location_marker.setMapPoint(location);
                    location_marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
                    location_marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
                    location_marker.setLeftSideButtonResourceIdOnCalloutBalloon(R.mipmap.ic_delete);
                    mapView.addPOIItem(location_marker);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mPostReference.child("location_marker").child(student_ID).addValueEventListener(postListner);
    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
        if(mapPOIItem.getMarkerType() == MapPOIItem.MarkerType.CustomImage) {
            if (calloutBalloonButtonType == MapPOIItem.CalloutBalloonButtonType.RightSideButton) { // ride
                stopTimer();
                NotificationManagerCompat.from(mainpage.this).cancel(2);
                String num_get = mapPOIItem.getItemName();
                MapPoint item_point = mapPOIItem.getMapPoint();
                String[] num = num_get.split("d");
                go_ride(num[1], item_point);
                mapView.deselectPOIItem(mapPOIItem);
                //push_alarm();
            } else if (calloutBalloonButtonType == MapPOIItem.CalloutBalloonButtonType.LeftSideButton) { //reservation
                //if(mode==1) stopTimer();
                String num_get = mapPOIItem.getItemName();
                MapPoint item_point = mapPOIItem.getMapPoint();
                String[] num = num_get.split("d");
                reservation(num[1], item_point);
                mapView.deselectPOIItem(mapPOIItem);
                push_alarm_reservation();
            }
        }
        else{
            if(calloutBalloonButtonType == MapPOIItem.CalloutBalloonButtonType.LeftSideButton){ //delete location
                //delete from firebase
                String rmname = mapPOIItem.getItemName();
                mPostReference.child("location_marker").child(student_ID).child(rmname).removeValue();
                mapView.removePOIItem(mapPOIItem);
            }
        }
    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
        my_point = mapPoint.getMapPointGeoCoord();
    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if(sharemode==1) return;
        quit_state=true;
        if(mode==1){
            StyleableToast.makeText(mainpage.this, "You have to cancel your reservation first!!", Toast.LENGTH_SHORT, R.style.mytoast).show();
            quit_state=false;
            return;
        }
        if(mode==2){
            StyleableToast.makeText(mainpage.this, "You have to finish your riding first!", Toast.LENGTH_SHORT, R.style.mytoast).show();
            quit_state=false;
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure to logout?").setMessage("Touch YES to logout");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                moveTaskToBack(true);
                finishAndRemoveTask();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                quit_state=false;
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onMapViewInitialized(MapView mapView) {
        //Toast.makeText(mainpage.this, "Ready to use!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(final MapView mapView, final MapPoint mapPoint) {
        if(quit_state) return;
        if(mode==1 || mode==2) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ADD Location Marker?").setMessage("Enter name");
        final EditText dialog_marker = new EditText(mainpage.this);
        builder.setView(dialog_marker);
        builder.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String marker_name = dialog_marker.getText().toString();
                if (marker_name.getBytes().length > 0) {
                    MapPOIItem location_marker = new MapPOIItem();
                    location_marker.setItemName(marker_name);
                    location_marker.setTag(-1);
                    location_marker.setMapPoint(mapPoint);
                    location_marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
                    location_marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
                    location_marker.setLeftSideButtonResourceIdOnCalloutBalloon(R.mipmap.ic_delete);
                    //mapView.addPOIItem(location_marker);
                    String x_val = String.valueOf(mapPoint.getMapPointGeoCoord().latitude);
                    String y_val = String.valueOf(mapPoint.getMapPointGeoCoord().longitude);
                    postFirebaseMarker(true, student_ID, marker_name, x_val, y_val);
                }
                else{
                    StyleableToast.makeText(mainpage.this, "Please enter name of marker", Toast.LENGTH_SHORT, R.style.mytoast).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        ///
    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void push_alarm(){
        Bitmap push_img = BitmapFactory.decodeResource(getResources(), R.drawable.ic_kick_scooter);

        NotificationManager notificationManager = (NotificationManager)mainpage.this.getSystemService(mainpage.this.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("default", "defalut", NotificationManager.IMPORTANCE_DEFAULT));
        }
        Notification.Builder builder = new Notification.Builder(mainpage.this, "default");
        builder.setSmallIcon(R.drawable.ic_kick_scooter)
                .setContentTitle("Riding Start!")
                .setContentText("Boong Boong Boong")
                .setDefaults(Notification.DEFAULT_SOUND&Notification.DEFAULT_VIBRATE)
                .setLargeIcon(push_img)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setNumber(1)
                .setAutoCancel(true);
        notificationManager.notify(1, builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void push_alarm_reservation(){
        Bitmap push_img = BitmapFactory.decodeResource(getResources(), R.drawable.ic_kick_scooter);

        NotificationManager notificationManager = (NotificationManager)mainpage.this.getSystemService(mainpage.this.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("default", "defalut", NotificationManager.IMPORTANCE_DEFAULT));
        }
        Notification.Builder builder = new Notification.Builder(mainpage.this, "default");
        builder.setSmallIcon(R.drawable.ic_kick_scooter)
                .setContentTitle("Reservation Made!")
                .setContentText("Get to point in time")
                .setDefaults(Notification.DEFAULT_SOUND&Notification.DEFAULT_VIBRATE)
                .setLargeIcon(push_img)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setNumber(1)
                .setAutoCancel(true);
        notificationManager.notify(2, builder.build());
    }

    public void share(){
        sharemode=1;
        String share_x = String.valueOf(my_point.latitude );
        String share_y = String.valueOf(my_point.longitude);
        String share_url = share_x+","+share_y+",18z";
        Map<String, String> templateArgs = new HashMap<>();
        templateArgs.put("KEY1", share_x);
        templateArgs.put("KEY2", share_y);
        TemplateParams params = LocationTemplate.newBuilder(
                "Current Location",
                ContentObject.newBuilder(
                        "SKKICK Location Share",
                        "https://firebasestorage.googleapis.com/v0/b/map-pp-390bc.appspot.com/o/ic_kick_marker.png?alt=media&token=3d320be6-9bc8-4853-8af0-512ad86ccd07",
                        LinkObject.newBuilder()
                                .setWebUrl(share_url)
                                .setMobileWebUrl(share_url)
                                .build())
                        .setDescrption("Current Location of user")
                        .build())
                .setAddressTitle("SKKICK")
                .build();
        KakaoLinkService.getInstance()
                .sendCustom(this, "29934",templateArgs, new ResponseCallback<KakaoLinkResponse>() {
                    @Override
                    public void onFailure(ErrorResult errorResult) {
                        sharemode=0;
                    }

                    @Override
                    public void onSuccess(KakaoLinkResponse result) {
                        StyleableToast.makeText(mainpage.this, "Share Start!", Toast.LENGTH_SHORT, R.style.mytoast2).show();
                        sharemode=0;
                    }
                });
    }
}
