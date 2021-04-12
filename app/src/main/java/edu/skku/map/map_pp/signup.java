package edu.skku.map.map_pp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.Profile;
import com.kakao.usermgmt.response.model.UserAccount;
import com.kakao.util.OptionalBoolean;
import com.muddzdev.styleabletoast.StyleableToast;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class signup extends AppCompatActivity {

    private DatabaseReference mPostReference;
    String name="", StID="", payment="", phone="";
    String ID;
    String[] permission_list = {Manifest.permission.ACCESS_FINE_LOCATION};

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mPostReference = FirebaseDatabase.getInstance().getReference();

        Button login_s = (Button)findViewById(R.id.signupButton);

        for(String permission : permission_list){
            int flag = checkCallingOrSelfPermission(permission);
            if(flag == PackageManager.PERMISSION_DENIED){
                requestPermissions(permission_list,0);
            }
        }
        login_s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText name_et = (EditText)findViewById(R.id.signupFullname);
                EditText StID_et = (EditText)findViewById(R.id.signupStudentID);
                EditText payment_et = (EditText)findViewById(R.id.signup_payment);
                EditText phone_et = (EditText)findViewById(R.id.signupPhone);
                name = name_et.getText().toString();
                StID = StID_et.getText().toString();
                payment = payment_et.getText().toString();
                phone = phone_et.getText().toString();
                name_et.setBackgroundResource(R.drawable.init_while);
                StID_et.setBackgroundResource(R.drawable.init_while);
                payment_et.setBackgroundResource(R.drawable.init_while);
                phone_et.setBackgroundResource(R.drawable.init_while);

                if(name.getBytes().length<=0 || StID.getBytes().length!=10 || payment.getBytes().length!=16 || phone.getBytes().length!=11){
                    StyleableToast.makeText(getApplicationContext(), "Check red lined part", Toast.LENGTH_LONG, R.style.mytoast).show();
                    if(name.getBytes().length <=0) name_et.setBackgroundResource(R.drawable.white_edit);
                    if(StID.getBytes().length!=10) StID_et.setBackgroundResource(R.drawable.white_edit);
                    if(payment.getBytes().length !=16) payment_et.setBackgroundResource(R.drawable.white_edit);
                    if(phone.getBytes().length != 11) phone_et.setBackgroundResource(R.drawable.white_edit);
                    return;
                }

                UserManagement.getInstance()
                        .me(new MeV2ResponseCallback() {
                            @Override
                            public void onSessionClosed(ErrorResult errorResult) {
                            }

                            @Override
                            public void onSuccess(MeV2Response result) {
                                UserAccount myAccount = result.getKakaoAccount();
                                if(myAccount != null){
                                    Profile myprofile = myAccount.getProfile();
                                    if(myprofile != null){
                                        //Log.d("check", "start");
                                        ID = myprofile.getNickname();
                                        go_intent();
                                    }else if(myAccount.profileNeedsAgreement() == OptionalBoolean.TRUE){
                                    }else{
                                    }
                                }
                            }
                        });
            }
        });

    }

    public void go_intent(){
        final ValueEventListener postListner = new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                postFirebaseDatabase(true);
                Intent signupIntent = new Intent(signup.this, mainpage.class);
                startActivity(signupIntent);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mPostReference.child("user").addValueEventListener(postListner);
    }

    public void postFirebaseDatabase(boolean add){
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postvalues = null;
        if(add){
            String pay1, pay2, pay3, pay4;
            pay1=payment.substring(0, 4);
            pay2=payment.substring(4, 8);
            pay3=payment.substring(8, 12);
            pay4=payment.substring(12, 16);
            String cal_payment = pay1+"-"+pay2+"-"+pay3+"-"+pay4;

            FirebasePost post = new FirebasePost(ID, name, StID, cal_payment, phone);
            postvalues = post.toMap();
        }
        childUpdates.put("/user/" + ID, postvalues);
        mPostReference.updateChildren(childUpdates);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 0)
            for(int i=0;i<permissions.length;i++){
                if(grantResults[i] == PackageManager.PERMISSION_DENIED){
                    moveTaskToBack(true);
                    finishAndRemoveTask();
                    android.os.Process.killProcess(android.os.Process.myPid());
            }
        }
    }
}
