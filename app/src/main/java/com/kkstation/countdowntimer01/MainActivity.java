package com.kkstation.countdowntimer01;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener {
    private static final String TAG = "artxxx" ;
    private CountDownTimer countDownTimer;
    private EditText time;
    Editable x;
    private TextView tv01;
    MediaPlayer play01,playEnd,playCancel;
    Button btnStart,btnCancel;
    NumberPicker numberPicker;
    ToggleButton tgBtnSound,tgBtnFlash;
    private Boolean swSoundisChecked = false;
    private Boolean swFlashisChecked = false;
    private Boolean swVibratorisChecked = false;
    private Boolean swBlink = false;
    Vibrator vibrator;
    private LinearLayout linearLayout;
    private Switch swSound, swFlash, swVibrate;
    public String strNP="12";
    private AdView mAdView;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        time = findViewById(R.id.time);
        time.setText(strNP);
        tv01 = findViewById(R.id.tv01);
        play01 = MediaPlayer.create(this,R.raw.button01a);
        playEnd = MediaPlayer.create(this,R.raw.button01b);
        playCancel = MediaPlayer.create(this,R.raw.button05);
        btnStart = findViewById(R.id.btnStart);
        btnCancel = findViewById(R.id.btnCancel);
        //btnCancel.setEnabled(false);

        swSound = findViewById(R.id.swSound);
        swFlash = findViewById(R.id.swFlash);
        swVibrate = findViewById(R.id.swVibrate);

        numberPicker = findViewById(R.id.numberPicker);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(300);
        numberPicker.setValue(Integer.valueOf(strNP));
        numberPicker.setOnValueChangedListener(this);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        linearLayout = findViewById(R.id.blanking);

        /*
        add by artchou 201203
        Log.d the versioncode
         */
        Log.d(TAG, "the version code is " + getVersionCode(this));

        /**
         * add by artchou 201202
         * add admob
         */

       // MobileAds.initialize(this, String.valueOf(R.string.banner_ad_unit_id));
        MobileAds.initialize(this);
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //@artchou 201206 debug admob status
        mAdView.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                Log.d(TAG, "onAdLoaded: ");
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                Log.d(TAG, "onAdFailedToLoad: " + loadAdError);
            }

            @Override
            public void onAdOpened() {
                Log.d(TAG, "onAdOpened: ");
            }

            @Override
            public void onAdClicked() {
                Log.d(TAG, "onAdClicked: ");
            }

            @Override
            public void onAdLeftApplication() {
                Log.d(TAG, "onAdLeftApplication: ");
            }

            @Override
            public void onAdClosed() {
                Log.d(TAG, "onAdClosed: ");
            }
        });



//        Log.d(TAG, "AppID is : " + getResources().getString(R.string.AppID));
//        Log.d(TAG, "AD Banner id is " + getResources().getString(R.string.banner_ad_unit_id));
//        Log.d(TAG, "AdUnitID is : " + mAdView.getAdUnitId());


        /*
        add by artchou 201129
        check if Flash available in the devices
         */
        if(getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)){
            Log.d(TAG, "onCreate: has camera flash ~~~");
        }else{
            swFlash.setEnabled(false);
            swFlash.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            Log.d(TAG, "swFlash check : NO Flash");
            Toast.makeText(this, "Has no FLASH, disable FLASH SWITCH !", Toast.LENGTH_SHORT).show();
        }

        /*
        add by artchou 201203
        Check if vibrator is available in the device
         */
        if(!vibrator.hasVibrator()){
            swVibrate.setEnabled(false);
            swVibrate.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            Log.d(TAG, "swVibrator check : No vibrator. disable vibrator switch ");
            Toast.makeText(this, "Devics has no vibrator, disable switch", Toast.LENGTH_SHORT).show();
        }else{
            Log.d(TAG, "swVibrator is enable ");
        }

        swSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b==true){
                    swSoundisChecked = true;
                }else{
                    swSoundisChecked = false;
                }
            }
        });

        swFlash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b==true){
                    swFlashisChecked = true;
                }else{
                    swFlashisChecked = false;
                }
            }
        });

        swVibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b==true){
                    swVibratorisChecked = true;
                }else{
                    swVibratorisChecked =false;
                }
            }
        });
        btnCancel.setEnabled(false);

    }

    public void start(View view) {

        //time.setText("15");

//        Log.d(TAG, "start: tgBtnSound is " + tgBtnSoundisChecked + " --- and tgBtnFlas is " +tgBtnFlashisChecked);

        /* Check tgBtnSound and tgBtnFlash
         *
         */

        int x = Integer.parseInt(time.getText().toString());
       // if ()

        countDownTimer = new CountDownTimer(x *1000,1000){
            @Override
            public void onTick(long l) {
                time.setText("" + l/1000);
                tv01.setText(R.string.tv01Text01);
                Log.d(TAG, "onTick: " + l);



               if(swSoundisChecked){ play01.start();}
               if(swFlashisChecked){blinkFlash(50);}
               if(swVibratorisChecked){vibrator.vibrate(100);}
                Log.d(TAG, "onTick: swSound swFlash swVibrate " + swSoundisChecked+ swFlashisChecked+ swVibratorisChecked);
//                if(tgVibrator){vibrator.vibrate(VibrationEffect.createOneShot(100, EFFECT_CLICK));}

//                try {
//                    linearlayoutBlink(1000);
//                    Log.d(TAG, "onTick: linearlayoutBlink");
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }


            }

            @Override
            public void onFinish() {
                Log.d(TAG, "onFinish: FINSH enable btnStart");
                btnStart.setEnabled(true);
                btnCancel.setEnabled(false);
                time.setText("0");
                tv01.setText(R.string.tv01Text);
                if(swSoundisChecked)playEnd.start();
                if(swVibratorisChecked){vibrator.vibrate(500);}
                if(swFlashisChecked)blinkFlash(300);
                //linearLayout.setBackgroundColor(Color.GREEN);
                time.setText(strNP);
            }
        };
        btnStart.setEnabled(false);
        btnCancel.setEnabled(true);
        countDownTimer.start();
    }

//    private void linearlayoutBlink(int i) throws InterruptedException {
//
//        int x = i;
//
//        Log.d(TAG, "onTick: linearlayoutBlink in function");
//        linearLayout.setBackgroundColor(Color.BLUE);
//        Thread.sleep(x);
//        linearLayout.setBackgroundColor(Color.RED);
//
//
//    }

    public void cancel(View view) {
        if (countDownTimer != null){
            countDownTimer.cancel();
            countDownTimer = null;
        }
        tv01.setText(R.string.tv01Text02);
        playCancel.start();
        btnStart.setEnabled(true);
        btnCancel.setEnabled(false);

    }

//    public void flash(View view) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            CameraManager camManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
//            String cameraId = null;
//            try {
//                cameraId = camManager.getCameraIdList()[0];
//                //if CameraManager.TorchCallback.
//                camManager.setTorchMode(cameraId, true);   //Turn ON
//            } catch (CameraAccessException e) {
//                e.printStackTrace();
//            }
//        }
//
//    }

    private void blinkFlash(int x)
    {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        String myString = "01";
        //x = 1000;
        long blinkDelay = x; //Delay in ms
        for (int i = 0; i < myString.length(); i++) {
            if (myString.charAt(i) == '0') {
                try {
                    String cameraId = cameraManager.getCameraIdList()[0];
                    cameraManager.setTorchMode(cameraId, true);
                } catch (CameraAccessException e) {
                }
            } else {
                try {
                    String cameraId = cameraManager.getCameraIdList()[0];
                    cameraManager.setTorchMode(cameraId, false);
                } catch (CameraAccessException e) {
                }
            }
            try {
                Thread.sleep(blinkDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
        Log.d(TAG, "onValueChange: i = " +i + "   i1 = " +i1);
        strNP =String.valueOf(i1);
        time.setText(strNP);

    }
    public String getVersionCode(Context context){
        PackageManager packageManager=context.getPackageManager();
        PackageInfo packageInfo;
        String versionCode="";
        try {
            packageInfo=packageManager.getPackageInfo(context.getPackageName(),0);
            versionCode=packageInfo.versionCode+"";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;

    }
}