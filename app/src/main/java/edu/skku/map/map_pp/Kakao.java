package edu.skku.map.map_pp;

import android.app.Application;
import android.content.Context;

import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;

public class Kakao extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        KakaoSDK.init(new KakaoAdapter() {
            @Override
            public IApplicationConfig getApplicationConfig() {
                return new IApplicationConfig() {
                    @Override
                    public Context getApplicationContext() {
                        return Kakao.this;
                    }
                };
            }
        });
    }
}
