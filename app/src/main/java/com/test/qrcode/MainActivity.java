package com.test.qrcode;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.test.qrcode.camera.utils.FileUtil;
import com.test.qrcode.camera.utils.ZXingUtils;
import com.test.qrcode.model.CheckInRequestVo;
import com.test.qrcode.model.QRStr;
import com.test.qrcode.model.ResponseVo;
import com.test.qrcode.utils.HashUtil;
import com.test.qrcode.utils.JSONPostUtil;
import com.test.qrcode.utils.JsonUtil;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private final int REQUEST_CODE_ADDRESS = 100;
    private final String[] permissions = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //创建两个Bitmap,一个放二维码，一个放logo
    private Bitmap codeBmp, logoBmp;
    private String url = "hello world!";
    private String checkInUrl = "http://checkin.sg-stay-safe.org/";
//    private TextView tvSaveCode;
    private TextView tvCheckinReqult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        tvSaveCode = findViewById(R.id.tv_save_code);
        tvCheckinReqult = findViewById(R.id.tv_checkin_result);

//        findViewById(R.id.tv_create_code).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //如果需要logo圆角的话可以对bitmap进行圆角处理或者图片用圆角图片
//                logoBmp = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
//                codeBmp = ZXingUtils.createQRImage(url, logoBmp);
//                ((ImageView) findViewById(R.id.image)).setImageBitmap(codeBmp);
//                tvSaveCode.setVisibility(codeBmp != null ? View.VISIBLE : View.GONE);
//            }
//        });
        findViewById(R.id.tv_code).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < permissions.length; i++) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, permissions[0]) != PackageManager.PERMISSION_GRANTED
                            || ContextCompat.checkSelfPermission(MainActivity.this, permissions[1]) != PackageManager.PERMISSION_GRANTED
                            || ContextCompat.checkSelfPermission(MainActivity.this, permissions[2]) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, permissions, REQUEST_CODE_ADDRESS);
                    } else {
                        Intent intent = new Intent(MainActivity.this, ScanningQRCodeActivity.class);
                        startActivityForResult(intent, 1000);
                        break;
                    }
                }
            }
        });
//        tvSaveCode.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FileUtil.saveImageToGallery(MainActivity.this, codeBmp);
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String resultText = data.getStringExtra("resultText");
//                Toast.makeText(this, resultText, Toast.LENGTH_LONG).show();
                //Call check in api
                new MyTask().execute(resultText);
//                checkIn(resultText);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_ADDRESS) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(this.permissions)) {
                    Intent intent = new Intent(MainActivity.this, ScanningQRCodeActivity.class);
                    startActivity(intent);
                    break;
                }
            }
        }
    }

    private class MyTask extends AsyncTask<String, Void, Void> {
        String resultMsg = "";

        @Override
        protected Void doInBackground(String... strings) {
            String rawStr = strings[0];

            Log.i("checkin rawStr", rawStr);
            String jsonStr = HashUtil.deCrypt(rawStr);
            Log.i("checkin jsonStr", jsonStr);
            QRStr qrObj = (QRStr) JsonUtil.convertJsonToObj(jsonStr, QRStr.class);
            if (qrObj == null || TextUtils.isEmpty(qrObj.getSiteId()) || TextUtils.isEmpty(qrObj.getAnonymousId())) {
                resultMsg = "QR Code is invalid...";
            }
            CheckInRequestVo requestVo = new CheckInRequestVo(qrObj.getAnonymousId(), qrObj.getSiteId());
            String requestStr = JsonUtil.convertObjToStr(requestVo);
            Log.i("checkin requestStr", requestStr);
            String respStr = null;
            try {
                respStr = JSONPostUtil.jsonPost(checkInUrl, requestStr);
                Log.i("checkin respStr", respStr);
            } catch (IOException e) {
                e.printStackTrace();
                resultMsg = "Check in unsuccessfully...";
            }
            if (TextUtils.isEmpty(respStr)) {
                resultMsg = "Check in unsuccessfully...";
            } else {
                ResponseVo response = (ResponseVo) JsonUtil.convertJsonToObj(respStr, ResponseVo.class);
                resultMsg = response.getMsg();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            tvCheckinReqult.setText(resultMsg);
            tvCheckinReqult.getPaint().setFakeBoldText(true);
        }
    }

    private void checkIn(String rawStr) {
        Log.i("checkin rawStr", rawStr);
        String jsonStr = HashUtil.deCrypt(rawStr);
        Log.i("checkin jsonStr", jsonStr);
        QRStr qrObj = (QRStr) JsonUtil.convertJsonToObj(jsonStr, QRStr.class);
        if (qrObj == null || TextUtils.isEmpty(qrObj.getSiteId()) || TextUtils.isEmpty(qrObj.getAnonymousId())) {
            tvCheckinReqult.setText("QR Code is invalid...");
        }
        CheckInRequestVo requestVo = new CheckInRequestVo(qrObj.getAnonymousId(), qrObj.getSiteId());
        String requestStr = JsonUtil.convertObjToStr(requestVo);
        Log.i("checkin requestStr", requestStr);
        String respStr = null;
        try {
            respStr = JSONPostUtil.jsonPost(checkInUrl, requestStr);
            Log.i("checkin respStr", respStr);
        } catch (IOException e) {
            e.printStackTrace();
            tvCheckinReqult.setText("Check in unsuccessfully...");
        }
        if (TextUtils.isEmpty(respStr)) {
            tvCheckinReqult.setText("Check in unsuccessfully...");
        } else {
            ResponseVo response = (ResponseVo) JsonUtil.convertJsonToObj(respStr, ResponseVo.class);
            tvCheckinReqult.setText(response.getMsg());
        }
    }
}