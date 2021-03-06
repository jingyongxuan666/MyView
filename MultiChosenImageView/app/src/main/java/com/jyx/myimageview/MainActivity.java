package com.jyx.myimageview;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSION_CODE = 100;

    private static final int REQUEST_CODE_IMAGE = 100;
    private static final int REQUEST_CODE_VIDEO = 200;

    private Context mContext;

    private MultiTypeImageView ivImg;
    private MultiTypeImageView ivVideo;
    private LinearLayout llImgWrapper;

    private LinearLayout.LayoutParams params;

    private Button button;

    private List<File> imageFiles;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        imageFiles = new ArrayList<>();

        ivImg = findViewById(R.id.iv_img);
        ivVideo = findViewById(R.id.iv_video);
        llImgWrapper = findViewById(R.id.ll_img_wrapper);
        button = findViewById(R.id.button);

        ivImg.post(new Runnable() {
            @Override
            public void run() {
                params = new LinearLayout.LayoutParams(ivImg.getWidth(),ivImg.getWidth());
                ivImg.setLayoutParams(params);
                ivVideo.setLayoutParams(params);
            }
        });



        if (!checkPermission()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions();
            }
        }
        ivImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivImg.choseFile(REQUEST_CODE_IMAGE);
            }
        });

        ivVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivVideo.choseFile(REQUEST_CODE_VIDEO);
            }
        });



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file1 = ivImg.getFile();
            }
        });

    }

    public static List<String> getSystemPhotoList(Context context) {
        List<String> result = new ArrayList<String>();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor == null || cursor.getCount() <= 0) return null; // 没有图片
        while (cursor.moveToNext()) {
            int index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String path = cursor.getString(index); // 文件地址
            File file = new File(path);
            if (file.exists()){
                result.add(path);
            }
        }

        return result ;
    }
    //检查权限
    private boolean checkPermission() {
        //是否有权限
        boolean haveCameraPermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;

        boolean haveWritePermission = ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        return haveCameraPermission && haveWritePermission;

    }

    // 请求所需权限
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermissions() {
        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
    }

    // 请求权限后会在这里回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE:

                boolean allowAllPermission = false;

                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {//被拒绝授权
                        allowAllPermission = false;
                        break;
                    }
                    allowAllPermission = true;
                }

                if (allowAllPermission) {
                    List<String> list = getSystemPhotoList(this);
                    Toast.makeText(this,"hahaha",Toast.LENGTH_LONG).show();
//                    takePhotoOrPickPhoto();//开始拍照或从相册选取照片
                } else {
                    Toast.makeText(mContext, "该功能需要授权方可使用", Toast.LENGTH_SHORT).show();
                }

                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case REQUEST_CODE_VIDEO:
                    ivVideo.handleData(data);
                    break;
                case REQUEST_CODE_IMAGE:
                    //创建新的view
                    final MultiTypeImageView newImageView = new MultiTypeImageView(mContext);
                    newImageView.setChoseType(MultiTypeImageView.CHOSE_TYPE_IMAGE)
                            .setChoseFrom(MultiTypeImageView.CHOSE_FROM_GALLERY)
                            .setDeletable(true)
                            .setLimitedSize(10)
                            .setOnImageClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            }).setOnDeleteClickListener(new MultiTypeImageView.OnDeleteClickListener() {
                                @Override
                                public void deleteCallback(View view) {
                                    llImgWrapper.removeView(newImageView);
                                    if (newImageView.getFile() != null){
                                        imageFiles.remove(newImageView.getFile());
                                    }
                                    if (imageFiles.size()<3){
                                        ivImg.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                    newImageView.setLayoutParams(params);
                    llImgWrapper.addView(newImageView);
                    newImageView.handleData(data);
                    imageFiles.add(newImageView.getFile());
                    if (imageFiles.size() == 3) {
                        ivImg.setVisibility(View.GONE);
                    }
                    break;
            }
        }

    }


}
