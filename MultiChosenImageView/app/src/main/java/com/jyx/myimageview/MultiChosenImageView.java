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
public class MultiChosenImageView extends RelativeLayout {

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

    //四面padding的尺寸
    private static final int PADDING_SIZE_DP = 10;

    private ImageView ivMain;
    private ImageView ivPlay;
    private ImageView ivDelete;
    private RelativeLayout rlBack;


    /**
     * 要选择的类型
     * 图片或视频
     */
    private String choseType;
    /**
     * 文件来源
     */
    private String choseFrom;

    /**
     * 是否显示删除图标，默认不显示
     */
    private boolean deletable;

    /**
     * 默认图片
     */
    private Drawable defaultImage;

    /**
     * 上传限制
     */
    private int limitedSize;


    private String FILE_PATH;

    private int REQUEST_CODE;

    private String REAL_FROM;

    private File FILE;


    public MultiChosenImageView(Context context) {
        this(context,null);
    }

    public MultiChosenImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultiChosenImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        //绑定view
        LayoutInflater.from(context).inflate(R.layout.muti_chosen_image_view, this, true);
        ivMain = findViewById(R.id.iv_main);
        ivDelete = findViewById(R.id.iv_delete);
        rlBack = findViewById(R.id.rl_back);

        DpPxUtils.init(context);
        //获取属性
        TypedArray attribute = context.obtainStyledAttributes(attrs, R.styleable.MultiChosenImageView);

        choseType = attribute.getString(R.styleable.MultiChosenImageView_choseType);

        choseFrom = attribute.getString(R.styleable.MultiChosenImageView_choseFrom);

        deletable = attribute.getBoolean(R.styleable.MultiChosenImageView_deletable, false);

        defaultImage = attribute.getDrawable(R.styleable.MultiChosenImageView_defaultImage);

        limitedSize = attribute.getInt(R.styleable.MultiChosenImageView_limitedSize, 2048);

        setDefaultValue();

        ivDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnDeleteListener != null){
                    mOnDeleteListener.deleteCallback(MultiChosenImageView.this);
                }
                resetView();
            }
        });


    }

    public MultiChosenImageView setOnImageClickListener(OnClickListener onClickListener){
        ivMain.setOnClickListener(onClickListener);
        return this;
    }

    private void resetView() {
        FILE = null;
        FILE_PATH = null;
        ivMain.setImageDrawable(defaultImage);
        ivDelete.setVisibility(GONE);
        rlBack.setVisibility(GONE);
    }

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
        if (choseType == null)//默认选择图片
            choseType = CHOSE_TYPE_IMAGE;

        if (choseType.equals(CHOSE_TYPE_VIDEO))
            ivMain.setImageResource(R.drawable.video_upload);

        if (choseFrom == null){//默认从相册选择
            choseFrom = CHOSE_FROM_GALLERY;
            REAL_FROM = CHOSE_FROM_GALLERY;
        }

        if (defaultImage != null){//设置默认图片
            ivMain.setImageDrawable(defaultImage);
        }else {
            int imageId = choseType.equals(CHOSE_TYPE_VIDEO)?R.drawable.video_upload:R.drawable.image_upload;
            defaultImage = mContext.getResources().getDrawable(imageId);
        }

    }

    public MultiChosenImageView setChoseType(String choseType){
        this.choseType = choseType;
        return this;
    }

    public MultiChosenImageView setChoseFrom(String where){
        this.choseFrom = where;
        REAL_FROM = where;
        return this;
    }

    public MultiChosenImageView setDefaultImage(@DrawableRes int resourceId){
        this.defaultImage = mContext.getResources().getDrawable(resourceId);
        return this;
    }

    public MultiChosenImageView setDeletable(boolean canDelete){
        this.deletable = canDelete;
        return this;
    }

    public MultiChosenImageView setLimitedSize(int limitedSize){
        this.limitedSize = limitedSize;
        return this;
    }



    /**
     *
     * 主图片点击，选择图片或视频
     * @param
     * @param
     */
    public void choseFile(int requestCode) {
        REQUEST_CODE = requestCode;
        if (choseFrom.equals(CHOSE_FROM_CAMERA)){//打开相机
            openCamera();
        }else if (choseFrom.equals(CHOSE_FROM_GALLERY)){//打开图库
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
        REAL_FROM = CHOSE_FROM_GALLERY;
        Uri type;
        if (choseType.equals(CHOSE_TYPE_VIDEO)){
            type = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        }else {
            type = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }
        Intent cIntent = new Intent(Intent.ACTION_PICK,type);
        ((Activity)mContext).startActivityForResult(cIntent,REQUEST_CODE);
    }

    /**
     * 打开相机
     */
    private void openCamera() {
        REAL_FROM = CHOSE_FROM_CAMERA;
        String path = mContext.getFilesDir() + File.separator + "media" + File.separator;
        String type;
        File file;
        long time = System.currentTimeMillis();
        if (choseType.equals(CHOSE_TYPE_VIDEO)){
            type = MediaStore.ACTION_VIDEO_CAPTURE;
            file = new File(path,time+".mp4");
            FILE_PATH = path + time + ".mp4";
        }else {
            type = MediaStore.ACTION_IMAGE_CAPTURE;
            file = new File(path,time+".jpg");
            FILE_PATH = path + time + ".jpg";
        }

        if (!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }

        Uri DATA_URI = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".fileprovider", file);


        Intent cIntent = new Intent(type);
        cIntent.putExtra(MediaStore.EXTRA_OUTPUT, DATA_URI);
        ((Activity)mContext).startActivityForResult(cIntent,REQUEST_CODE);
    }

    public void handleData(Intent data){

        FILE = null;
        String path;
        Bitmap thumbnail;

        if (REAL_FROM.equals(CHOSE_FROM_CAMERA)){
            //相机拍照返回的data有可能是null，所以使用全局的FILE_PATH
            path = FILE_PATH;
        }else {
            //通过uri获取文件路径
            path = GalleryUtils.getRealPathFromURI((Activity) mContext,data.getData());
        }
        FILE = new File(path);
        double fileSize = FILE.length()/(1024*1024);
        if (fileSize > limitedSize){
            String type = choseType.equals(CHOSE_TYPE_VIDEO)?"所选视频":"所选图片";
            Toast.makeText(mContext,type+"不能大于"+limitedSize+"m",Toast.LENGTH_SHORT).show();
            return;
        }


        if (choseType.equals(CHOSE_TYPE_VIDEO)){
            thumbnail = ImageUtils.getVideoThumbnail(path,200,200, MediaStore.Images.Thumbnails.MICRO_KIND);
        }else {
            thumbnail = ImageUtils.returnRotatePhoto(path,mContext);
        }

        ivMain.setImageBitmap(thumbnail);

        //如果是视频，显示蒙版和播放键，否则隐藏
        if (choseType.equals(CHOSE_TYPE_VIDEO)){
            rlBack.setVisibility(VISIBLE);
        }else {
            rlBack.setVisibility(GONE);
        }
        //是否显示删除icon
        if (deletable){
            ivDelete.setVisibility(VISIBLE);
        }else {
            ivDelete.setVisibility(GONE);
        }

    }

    /**
     * @return 获取图片或视频文件
     */
    public File getFile(){
        return FILE;
    }
}
