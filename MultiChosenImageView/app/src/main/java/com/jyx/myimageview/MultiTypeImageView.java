package com.jyx.myimageview;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.DrawableRes;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;

/**
 * 自定义ImageView，可选择相册里的图片或视频，并展示，视频展示第一帧.
 *
 * @author Jingyongxuan
 * @date 2019/4/29
 */
public class MultiTypeImageView extends RelativeLayout {

    private Context mContext;
    private OnDeleteClickListener mOnDeleteListener;

    /**
     * 选择文件的类型
     */
    public static final String CHOSE_TYPE_IMAGE = "0";//图片
    public static final String CHOSE_TYPE_VIDEO = "1";//视频

    /**
     * 选择的位置
     */
    public static final String CHOSE_FROM_CAMERA = "0";//拍照
    public static final String CHOSE_FROM_GALLERY = "1";//相册
    public static final String CHOSE_FROM_BOTH = "2";//皆可


    private ImageView ivMain;
    private ImageView ivDelete;
    private RelativeLayout rlBack;


    /**
     * 要选择的类型
     * 图片或视频
     */
    private String mChoseType;
    /**
     * 文件来源
     */
    private String mChoseFrom;

    /**
     * 是否显示删除图标，默认不显示
     */
    private boolean mDeletable;

    /**
     * 默认图片
     */
    private Drawable mDefaultImage;

    /**
     * 上传限制
     */
    private int mLimitedSize;


    private String mFilePath;

    private int mRequestCode;

    private String mRealFrom;

    private File mFile;


    public MultiTypeImageView(Context context) {
        this(context,null);
    }

    public MultiTypeImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultiTypeImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        //绑定view
        LayoutInflater.from(context).inflate(R.layout.multi_type_imageview, this, true);
        ivMain = findViewById(R.id.iv_main);
        ivDelete = findViewById(R.id.iv_delete);
        rlBack = findViewById(R.id.rl_back);

        //获取属性
        TypedArray attribute = context.obtainStyledAttributes(attrs, R.styleable.MultiTypeImageView);

        mChoseType = attribute.getString(R.styleable.MultiTypeImageView_choseType);

        mChoseFrom = attribute.getString(R.styleable.MultiTypeImageView_choseFrom);

        mDeletable = attribute.getBoolean(R.styleable.MultiTypeImageView_deletable, false);

        mDefaultImage = attribute.getDrawable(R.styleable.MultiTypeImageView_defaultImage);

        mLimitedSize = attribute.getInt(R.styleable.MultiTypeImageView_limitedSize, 2048);

        setDefaultValue();

        ivDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnDeleteListener != null){
                    mOnDeleteListener.deleteCallback(MultiTypeImageView.this);
                }
                resetView();
            }
        });


    }

    public MultiTypeImageView setOnImageClickListener(OnClickListener onClickListener){
        ivMain.setOnClickListener(onClickListener);
        return this;
    }

    /**
     * 重置view
     */
    private void resetView() {
        mFile = null;
        mFilePath = null;
        ivMain.setImageDrawable(mDefaultImage);
        ivDelete.setVisibility(GONE);
        rlBack.setVisibility(GONE);
    }

    /**
     * 删除后的监听
     * @param listener
     */
    public void setOnDeleteClickListener(OnDeleteClickListener listener){
        this.mOnDeleteListener = listener;
    }

    public interface OnDeleteClickListener{
        void deleteCallback(View view);
    }
    /**
     * 设置各属性默认值
     */
    private void setDefaultValue() {
        if (mChoseType == null)//默认选择图片
            mChoseType = CHOSE_TYPE_IMAGE;

        if (mChoseType.equals(CHOSE_TYPE_VIDEO))
            ivMain.setImageResource(R.drawable.video_upload);

        if (mChoseFrom == null){//默认从相册选择
            mChoseFrom = CHOSE_FROM_GALLERY;
            mRealFrom = CHOSE_FROM_GALLERY;
        }

        if (mDefaultImage != null){//设置默认图片
            ivMain.setImageDrawable(mDefaultImage);
        }else {
            int imageId = mChoseType.equals(CHOSE_TYPE_VIDEO)?R.drawable.video_upload:R.drawable.image_upload;
            mDefaultImage = mContext.getResources().getDrawable(imageId);
        }

    }

    public MultiTypeImageView setChoseType(String choseType){
        this.mChoseType = choseType;
        return this;
    }

    public MultiTypeImageView setChoseFrom(String where){
        this.mChoseFrom = where;
        mRealFrom = where;
        return this;
    }

    public MultiTypeImageView setDefaultImage(@DrawableRes int resourceId){
        this.mDefaultImage = mContext.getResources().getDrawable(resourceId);
        return this;
    }

    public MultiTypeImageView setDeletable(boolean canDelete){
        this.mDeletable = canDelete;
        return this;
    }

    public MultiTypeImageView setLimitedSize(int limitedSize){
        this.mLimitedSize = limitedSize;
        return this;
    }

    /**
     *
     * 主图片点击，选择图片或视频
     * @param
     * @param
     */
    public void choseFile(int requestCode) {
        mRequestCode = requestCode;
        if (mChoseFrom.equals(CHOSE_FROM_CAMERA)){//打开相机
            openCamera();
        }else if (mChoseFrom.equals(CHOSE_FROM_GALLERY)){//打开图库
            openGallery();
        }else{//显示dialog，来选择图库或相机
            new AlertDialog.Builder(mContext)
                    .setTitle("请选择")
                    .setItems(new String[]{"相机","图库"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case 0:
                                    openCamera();
                                    break;
                                case 1:
                                    openGallery();
                                    break;
                            }
                        }
                    }).create().show();
        }

    }
    /**
     * 从相册获取
     */
    private void openGallery() {
        mRealFrom = CHOSE_FROM_GALLERY;
        Uri type;
        if (mChoseType.equals(CHOSE_TYPE_VIDEO)){
            type = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        }else {
            type = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }
        Intent cIntent = new Intent(Intent.ACTION_PICK,type);
        ((Activity)mContext).startActivityForResult(cIntent,mRequestCode);
    }

    /**
     * 打开相机
     */
    private void openCamera() {
        mRealFrom = CHOSE_FROM_CAMERA;
        String path = mContext.getFilesDir() + File.separator + "media" + File.separator;
        String type;
        File file;
        long time = System.currentTimeMillis();
        if (mChoseType.equals(CHOSE_TYPE_VIDEO)){
            type = MediaStore.ACTION_VIDEO_CAPTURE;
            file = new File(path,time+".mp4");
            mFilePath = path + time + ".mp4";
        }else {
            type = MediaStore.ACTION_IMAGE_CAPTURE;
            file = new File(path,time+".jpg");
            mFilePath = path + time + ".jpg";
        }

        if (!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }

        Uri DATA_URI = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".fileprovider", file);


        Intent cIntent = new Intent(type);
        cIntent.putExtra(MediaStore.EXTRA_OUTPUT, DATA_URI);
        ((Activity)mContext).startActivityForResult(cIntent,mRequestCode);
    }

    public void handleData(Intent data){

        mFile = null;
        String path;
        Bitmap thumbnail;

        if (mRealFrom.equals(CHOSE_FROM_CAMERA)){
            //相机拍照返回的data有可能是null，所以使用全局的mFilePath
            path = mFilePath;
        }else {
            //通过uri获取文件路径
            path = GalleryUtils.getRealPathFromURI((Activity) mContext,data.getData());
        }
        mFile = new File(path);
        double fileSize = mFile.length()/(1024*1024);
        if (fileSize > mLimitedSize){
            String type = mChoseType.equals(CHOSE_TYPE_VIDEO)?"所选视频":"所选图片";
            Toast.makeText(mContext,type+"不能大于"+mLimitedSize+"m",Toast.LENGTH_SHORT).show();
            return;
        }


        if (mChoseType.equals(CHOSE_TYPE_VIDEO)){
            thumbnail = ImageUtils.getVideoThumbnail(path,200,200, MediaStore.Images.Thumbnails.MICRO_KIND);
        }else {
            thumbnail = ImageUtils.returnRotatePhoto(path,mContext);
        }

        ivMain.setImageBitmap(thumbnail);

        //如果是视频，显示蒙版和播放键，否则隐藏
        if (mChoseType.equals(CHOSE_TYPE_VIDEO)){
            rlBack.setVisibility(VISIBLE);
        }else {
            rlBack.setVisibility(GONE);
        }
        //是否显示删除icon
        if (mDeletable){
            ivDelete.setVisibility(VISIBLE);
        }else {
            ivDelete.setVisibility(GONE);
        }

    }

    /**
     * @return 获取图片或视频文件
     */
    public File getFile(){
        return mFile;
    }
}
