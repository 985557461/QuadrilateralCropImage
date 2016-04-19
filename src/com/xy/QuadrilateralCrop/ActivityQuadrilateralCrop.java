package com.xy.QuadrilateralCrop;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.xy.QuadrilateralCrop.quadrilateral_crop.QuadrilateralCropImageView;

/**
 * Created by fc on 15-3-11.
 */
public class ActivityQuadrilateralCrop extends Activity implements View.OnClickListener {
    /**
     * views
     **/
    private View backView;
    private View cancelView;
    private TextView title;
    private View nextStepView;
    private View okView;

    //用来展示剪裁，变换结果的ImageView
    private ImageView showImageView;
    private int imageViewWidth = 0;
    private int imageViewHeight = 0;
    /**
     * 下方可以选择的处理图片的方式
     **/
    private View horScrollView;
    private View cropTV;
    private View liangDuTV;
    private View duiBiDuTV;
    private View baoHeDuTV;
    private View showContainer;
    private QuadrilateralCropImageView cropImageView;

    /**
     * seekBar
     **/
    private View seekBarContainer;
    private SeekBar seekBar;

    /**
     * 当前正在操作的bitmap
     **/
    private Bitmap currentBmp;

    /**
     * 当前操作的状态(正在进行什么操作)
     **/
    private static final int NOTHING = 0;
    private static final int CROP = 1;
    private static final int ROTATE = 2;
    private static final int LIANGDU = 3;
    private static final int DUIBIDU = 4;
    private static final int LENGNUAN = 5;
    private static final int BAOHEDU = 6;
    private static final int RUIHUA = 7;

    private int currentOperation = NOTHING;//没有操作

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quadrilateral_crop);

        getViews();
        initViews();
        setListeners();
    }

    private void getViews() {
        backView = findViewById(R.id.backView);
        cancelView = findViewById(R.id.cancelView);
        title = (TextView) findViewById(R.id.title);
        nextStepView = findViewById(R.id.nextStepView);
        okView = findViewById(R.id.okView);
        showImageView = (ImageView) findViewById(R.id.showImageView);
        horScrollView = findViewById(R.id.horScrollView);
        cropTV = findViewById(R.id.cropTV);
        liangDuTV = findViewById(R.id.liangDuTV);
        duiBiDuTV = findViewById(R.id.duiBiDuTV);
        baoHeDuTV = findViewById(R.id.baoHeDuTV);
        showContainer = findViewById(R.id.showContainer);
        cropImageView = (QuadrilateralCropImageView) findViewById(R.id.cropImageView);
        seekBarContainer = findViewById(R.id.seekBarContainer);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
    }

    private void initViews() {
        loadBitmap();
    }

    private void setListeners() {
        backView.setOnClickListener(this);
        cancelView.setOnClickListener(this);
        nextStepView.setOnClickListener(this);
        okView.setOnClickListener(this);
        cropTV.setOnClickListener(this);
        liangDuTV.setOnClickListener(this);
        duiBiDuTV.setOnClickListener(this);
        baoHeDuTV.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backView:
                finish();
                break;
            case R.id.cancelView:
                doCancelAction();
                break;
            case R.id.okView:
                doOkAction();
                break;
            case R.id.nextStepView:
                //todo
                break;
            case R.id.cropTV:
                toCropImage();
                break;
            case R.id.liangDuTV:
                toChangeLiangDu();
                break;
            case R.id.baoHeDuTV:
                toChangeBaoHeDu();
                break;
            case R.id.duiBiDuTV:
                toChangeDuiBiDu();
                break;
        }
    }

    private void doCancelAction(){
        switch (currentOperation){
            case NOTHING:
                break;
            case CROP:
                changeTitleBarState(true,"");
                showContainer.setVisibility(View.VISIBLE);
                cropImageView.setVisibility(View.GONE);
                break;
            case ROTATE:
                break;
            case LIANGDU:
                changeTitleBarState(true,"");
                changeBottomBarState(true);
                break;
            case DUIBIDU:
                break;
            case LENGNUAN:
                break;
            case BAOHEDU:
                break;
            case RUIHUA:
                break;
        }
        currentOperation = NOTHING;
    }

    private void doOkAction(){
        switch (currentOperation){
            case NOTHING:
                break;
            case CROP:
                getCropImageAndShow();
                break;
            case ROTATE:
                break;
            case LIANGDU:
                break;
            case DUIBIDU:
                break;
            case LENGNUAN:
                break;
            case BAOHEDU:
                break;
            case RUIHUA:
                break;
        }
        currentOperation = NOTHING;
    }

    private void changeTitleBarState(boolean normal, String titleStr) {
        if (normal) {
            backView.setVisibility(View.VISIBLE);
            cancelView.setVisibility(View.INVISIBLE);
            nextStepView.setVisibility(View.VISIBLE);
            okView.setVisibility(View.INVISIBLE);
            title.setText("");
        } else {
            backView.setVisibility(View.INVISIBLE);
            cancelView.setVisibility(View.VISIBLE);
            nextStepView.setVisibility(View.INVISIBLE);
            okView.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(titleStr)) {
                title.setText(titleStr);
            } else {
                title.setText("");
            }
        }
    }

    private void changeBottomBarState(boolean normal) {
        if (normal) {
            horScrollView.setVisibility(View.VISIBLE);
            seekBarContainer.setVisibility(View.GONE);
        } else {
            horScrollView.setVisibility(View.GONE);
            seekBarContainer.setVisibility(View.VISIBLE);
        }
    }

    private void toCropImage() {
        currentOperation = CROP;
        changeTitleBarState(false, "");
        showContainer.setVisibility(View.GONE);
        cropImageView.setVisibility(View.VISIBLE);
        cropImageView.setImageBitmap(currentBmp);
    }

    private void toChangeLiangDu() {
        currentOperation = LIANGDU;
        changeTitleBarState(false,"亮度");
        changeBottomBarState(false);
    }

    private void toChangeBaoHeDu() {

    }

    private void toChangeDuiBiDu() {

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            showBitmap();
        }
    }

    public void loadBitmap() {
        currentBmp = BitmapFactory.decodeResource(getResources(), R.drawable.meinv);
    }

    public void showBitmap() {
        if (currentBmp == null || currentBmp.isRecycled()) {
            return;
        }
        int bmpWidth = currentBmp.getWidth();
        int bmpHeight = currentBmp.getHeight();

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int screenWidth = wm.getDefaultDisplay().getWidth();
        int screenHeight = wm.getDefaultDisplay().getHeight();
        float scale = getResources().getDisplayMetrics().density;
        int otherHeight = (int) (163.0f * scale + 0.5f);

        int fullWidth = screenWidth;
        int fullHeight = screenHeight - otherHeight - getStatusHeight();

        float horScaleRadio = fullWidth * 1.0f / bmpWidth;
        float verScaleRadio = fullHeight * 1.0f / bmpHeight;

        if (horScaleRadio < verScaleRadio) {//图片竖直缩放
            imageViewWidth = fullWidth;
            imageViewHeight = (int) (bmpHeight * horScaleRadio);
        } else {//图片水平缩放
            imageViewHeight = fullHeight;
            imageViewWidth = (int) (bmpWidth * verScaleRadio);
        }

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) showImageView.getLayoutParams();
        if (params != null) {
            params.width = imageViewWidth;
            params.height = imageViewHeight;
        } else {
            params = new FrameLayout.LayoutParams(imageViewWidth, imageViewHeight);
            params.gravity = Gravity.CENTER;
        }
        showImageView.setLayoutParams(params);
        showImageView.setImageBitmap(currentBmp);
    }

    private void getCropImageAndShow() {
        changeTitleBarState(true, "");
        Bitmap resultBmp = cropImageView.getCroppedImage();
        cropImageView.setVisibility(View.GONE);
        showContainer.setVisibility(View.VISIBLE);
        Bitmap lastBmp = currentBmp;
        currentBmp = resultBmp;
        showBitmap();
        //释放上一个bitmap
        if (lastBmp != null && lastBmp != currentBmp && !lastBmp.isRecycled()) {
            lastBmp.recycle();
            lastBmp = null;
        }
    }

    /**
     * 获得状态栏的高度
     */
    public int getStatusHeight() {
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }
}
