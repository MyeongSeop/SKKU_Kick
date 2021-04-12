package edu.skku.map.map_pp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.Profile;
import com.kakao.usermgmt.response.model.UserAccount;
import com.kakao.util.OptionalBoolean;
import com.kakao.util.exception.KakaoException;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    String ID;
    DatabaseReference mPostReference;
    int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ID="";
        Session.getCurrentSession().addCallback(sessionCallback);
        getHash();
    }

    @Override
    protected void onResume() {
        ID="";
        super.onResume();
    }

    public void go_intent(){
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
                                Log.d("check", "start");
                                ID = myprofile.getNickname();
                                go_intent_2();
                            }else if(myAccount.profileNeedsAgreement() == OptionalBoolean.TRUE){
                            }else{
                            }
                        }
                    }
                });
    }

    private void go_intent_2(){
        mPostReference = FirebaseDatabase.getInstance().getReference();
        final ValueEventListener postListner = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                flag=0;
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    if(ID.equals(key)){
                        mPostReference.removeEventListener(this);
                        flag=1;
                        Intent signupIntent = new Intent(MainActivity.this, mainpage.class);
                        startActivity(signupIntent);
                        return;
                    }
                }
                if(flag==0){
                    Intent loginIntent = new Intent(MainActivity.this, signup.class);
                    startActivity(loginIntent);
                }
                //Intent signupIntent = new Intent(signup.this, mainpage.class);
                //startActivity(signupIntent);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mPostReference.child("user").addValueEventListener(postListner);
    }

    private void getHash(){
        PackageInfo packageInfo =null;
        try{
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
        if(packageInfo == null) Log.e("KeyHash", "null");

        for(Signature signature : packageInfo.signatures){
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private ISessionCallback sessionCallback = new ISessionCallback() {
        @Override
        public void onSessionOpened() {
            Log.i("Kakao", "success");
            go_intent();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Log.i("Kakao", "fail");
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(sessionCallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)){
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}