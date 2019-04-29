package com.jyx.myimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * 自定义ImageView，可选择相册里的图片或视频，并展示，视频展示第一帧.
 *
 * @author Jingyongxuan
 * @date 2019/4/29
 */
public class MultiChosenImageView extends RelativeLayout {

    /**
     * 宽高模式
     */
    public static final String SHAPE_MODE_NORMAL = "0";//指定宽高
    public static final String SHAPE_MODE_EQUALS_WIDTH = "1";//边长为宽的正方形
    public static final String SHAPE_MODE_EQUALS_HEIGHT = "2";//边长为高的正方形

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

    private static final int paddingSizeDp = 10;


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
     * 是否显示删除图标，默认显示
     */
    private boolean deletable;

    /**
     * 默认图片
     */
    private Drawable defaultImage;

    /**
     * 宽高模式，
     * normal指定宽高
     * equalsWidth 等宽
     * equalsHeight 等高
     */
    private String shapeMode;

    /**
     * 上传限制
     */
    private int limitedSize;


    public MultiChosenImageView(Context context) {
        super(context);
    }

    public MultiChosenImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultiChosenImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //绑定view
        LayoutInflater.from(context).inflate(R.layout.muti_chosen_image_view, this, true);
        ivMain = findViewById(R.id.iv_main);
        ivPlay = findViewById(R.id.iv_play);
        ivDelete = findViewById(R.id.iv_delete);
        rlBack = findViewById(R.id.rl_back);

        DpPxUtils.init(context);
        //获取属性
        TypedArray attribute = context.obtainStyledAttributes(attrs, R.styleable.MultiChosenImageView);

        shapeMode = attribute.getString(R.styleable.MultiChosenImageView_shapeMode);

        choseType = attribute.getString(R.styleable.MultiChosenImageView_choseType);

        choseFrom = attribute.getString(R.styleable.MultiChosenImageView_choseFrom);

        deletable = attribute.getBoolean(R.styleable.MultiChosenImageView_deletable, true);

        defaultImage = attribute.getDrawable(R.styleable.MultiChosenImageView_defaultImage);

        limitedSize = attribute.getInt(R.styleable.MultiChosenImageView_limitedSize, 2048);

        setDefaultValue();

        resize(shapeMode);


    }

    /**
     * 设置各属性默认值
     */
    private void setDefaultValue() {
        if (shapeMode == null)//默认指定宽高
            shapeMode = SHAPE_MODE_NORMAL;

        if (choseType == null)//默认选择图片
            choseType = CHOSE_TYPE_IMAGE;

        if (choseFrom == null)//默认从相册选择
            choseFrom = CHOSE_FROM_GALLERY;

    }

    /**
     * 重新调整大小
     *
     * @param shapeMode
     */
    private void resize(final String shapeMode) {


        final int paddingSizePx = DpPxUtils.dipToPx(paddingSizeDp);
        post(new Runnable() {
            @Override
            public void run() {
                Class<? extends ViewGroup.LayoutParams> viewClass = getLayoutParams().getClass();
                int width;
                int height;
                switch (shapeMode) {
                    case SHAPE_MODE_EQUALS_WIDTH:
                        height = width = getWidth();
                        break;
                    case SHAPE_MODE_EQUALS_HEIGHT:
                        width = height = getHeight();
                        break;
                    default:
                        width = getWidth() + paddingSizePx;
                        height = getHeight() + paddingSizePx;
                        break;
                }

                try {
                    setLayoutParams(viewClass.getDeclaredConstructor(int.class,int.class).newInstance(width,height));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

}
