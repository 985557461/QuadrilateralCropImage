package com.xy.QuadrilateralCrop;

import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import com.xy.QuadrilateralCrop.gpuimage.GPUImageFilter;
import com.xy.QuadrilateralCrop.gpuimage.GPUImageView;
import com.xy.QuadrilateralCrop.gpuimage.util.GPUImageFilterTools;
import com.xy.QuadrilateralCrop.loading_view.DialogUtil;
import com.xy.QuadrilateralCrop.quadrilateral_crop.QuadrilateralCropImageView;

/**
 * Created by fc on 15-3-11.
 */
public class ActivityQuadrilateralCrop extends Activity implements View.OnClickListener {
    /**
     * views
     */
    private View backView;
    private View cancelView;
    private TextView title;
    private View nextStepView;
    private View okView;

    /**
     * 用来展示剪裁，变换结果的ImageView*
     */
    private ImageView showImageView;
    private int imageViewWidth = 0;
    private int imageViewHeight = 0;
    private int bmpWidth = 0;
    private int bmpHeight = 0;
    /**
     * 下方可以选择的处理图片的方式
     */
    private View horScrollView;
    private View cropTV;
    private View liangDuTV;
    private View duiBiDuTV;
    private View baoHeDuTV;
    private View whiteBalanceTV;
    private View ruiHuaTV;
    private View showContainer;
    private QuadrilateralCropImageView cropImageView;

    /**
     * seekBar
     */
    private View seekBarContainer;
    private SeekBar seekBar;

    /**
     * 当前正在操作的bitmap
     */
    private Bitmap currentBmp;
    private float scaleRatio = 1.0f;
    private Bitmap copyBmp;//副本，在进行其他操作时避免破坏currentBmp

    /**
     * 当前操作的状态(正在进行什么操作)
     */
    private static final int NOTHING = 0;
    private static final int CROP = 1;
    private static final int ROTATE = 2;
    private static final int LIANGDU = 3;
    private static final int DUIBIDU = 4;
    private static final int WHITEBALANCE = 5;
    private static final int BAOHEDU = 6;
    private static final int RUIHUA = 7;

    private int currentOperation = NOTHING;//没有操作

    /**
     * gpu filter*
     */
    private GPUImageView gpuImageView;
    private GPUImageFilter mFilter;
    private GPUImageFilterTools.FilterAdjuster mFilterAdjuster;

    /**
     * 当前的操作的各种数值*
     */
    private static final int CENTER_VALUE = 50;
    private int lingDuInt = CENTER_VALUE;
    private int duiBiDuInt = CENTER_VALUE;
    private int whiteBalanceInt = CENTER_VALUE;
    private int baoHeDuInt = CENTER_VALUE;
    private int ruiHuaInt = CENTER_VALUE;

    /**
     * 上次的进度数值*
     */
    private int lastlingDuInt = lingDuInt;
    private int lastduiBiDuInt = duiBiDuInt;
    private int lastWhiteBalanceInt = whiteBalanceInt;
    private int lastbaoHeDuInt = baoHeDuInt;
    private int lastruiHuaInt = ruiHuaInt;

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
        gpuImageView = (GPUImageView) findViewById(R.id.gpuImageView);
        horScrollView = findViewById(R.id.horScrollView);
        cropTV = findViewById(R.id.cropTV);
        liangDuTV = findViewById(R.id.liangDuTV);
        duiBiDuTV = findViewById(R.id.duiBiDuTV);
        baoHeDuTV = findViewById(R.id.baoHeDuTV);
        whiteBalanceTV = findViewById(R.id.whiteBalanceTV);
        ruiHuaTV = findViewById(R.id.ruiHuaTV);
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
        whiteBalanceTV.setOnClickListener(this);
        ruiHuaTV.setOnClickListener(this);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                seekBarChangeBmp(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void seekBarChangeBmp(int progress) {
        switch (currentOperation) {
            case LIANGDU:
                changeBmpLiangDu(progress);
                break;
            case DUIBIDU:
                changeBmpDuiBiDu(progress);
                break;
            case WHITEBALANCE:
                changeBmpWhiteBalance(progress);
                break;
            case BAOHEDU:
                changeBmpBaoHeDu(progress);
                break;
            case RUIHUA:
                changeBmpRuiHua(progress);
                break;
        }
    }

    private void changeBmpLiangDu(int progress) {
        lingDuInt = progress;
        //渲染
        if (mFilterAdjuster != null) {
            mFilterAdjuster.adjust(lingDuInt);
        }
        gpuImageView.requestRender();
    }

    private void changeBmpBaoHeDu(int progress) {
        baoHeDuInt = progress;
        //渲染
        if (mFilterAdjuster != null) {
            mFilterAdjuster.adjust(baoHeDuInt);
        }
        gpuImageView.requestRender();

    }

    private void changeBmpDuiBiDu(int progress) {
        duiBiDuInt = progress;
        //渲染
        if (mFilterAdjuster != null) {
            mFilterAdjuster.adjust(duiBiDuInt);
        }
        gpuImageView.requestRender();
    }

    private void changeBmpWhiteBalance(int progress) {
        whiteBalanceInt = progress;
        //渲染
        if (mFilterAdjuster != null) {
            mFilterAdjuster.adjust(whiteBalanceInt);
        }
        gpuImageView.requestRender();
    }

    private void changeBmpRuiHua(int progress) {
        ruiHuaInt = progress;
        //渲染
        if (mFilterAdjuster != null) {
            mFilterAdjuster.adjust(ruiHuaInt);
        }
        gpuImageView.requestRender();
    }

    private void switchFilterTo(final GPUImageFilter filter) {
        mFilter = filter;
        gpuImageView.setFilter(mFilter);
        mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);

        findViewById(R.id.seekBar).setVisibility(mFilterAdjuster.canAdjust() ? View.VISIBLE : View.GONE);
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
            case R.id.whiteBalanceTV:
                toChangeWhiteBalance();
                break;
            case R.id.ruiHuaTV:
                toChangeRuiDu();
                break;
        }
    }

    private void doCancelAction() {
        switch (currentOperation) {
            case NOTHING:
                break;
            case CROP:
                changeTitleBarState(true, "");
                showContainer.setVisibility(View.VISIBLE);
                cropImageView.setVisibility(View.GONE);
                break;
            case ROTATE:
                break;
            case LIANGDU:
                lingDuInt = lastlingDuInt;
                break;
            case DUIBIDU:
                duiBiDuInt = lastduiBiDuInt;
                break;
            case WHITEBALANCE:
                whiteBalanceInt = lastWhiteBalanceInt;
                break;
            case BAOHEDU:
                baoHeDuInt = lastbaoHeDuInt;
                break;
            case RUIHUA:
                ruiHuaInt = lastruiHuaInt;
                break;
        }
        if (currentOperation != NOTHING && currentOperation != CROP && currentOperation != ROTATE) {
            changeTitleBarState(true, "");
            changeBottomBarState(true);
            //显示原图像，并且释放产生的临时的bitmap
            if (copyBmp != null && !copyBmp.isRecycled()) {
                copyBmp.recycle();
                copyBmp = null;
            }
            showBitmap();
        }
        currentOperation = NOTHING;
    }

    private void doOkAction() {
        switch (currentOperation) {
            case NOTHING:
                break;
            case CROP:
                new GenCropBmpTask().execute();
                break;
            case ROTATE:
                break;
            case LIANGDU:
            case DUIBIDU:
            case WHITEBALANCE:
            case BAOHEDU:
            case RUIHUA: {
                new GenFilterBmpTask().execute();
            }
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

    //改变图片亮度
    private void toChangeLiangDu() {
        currentOperation = LIANGDU;
        changeTitleBarState(false, "亮度");
        changeBottomBarState(false);
        showImageView.setVisibility(View.INVISIBLE);

        //对图像的副本进行操作
        genCopyBmpAndSetGpuBmp();
        GPUImageFilter filter = GPUImageFilterTools.createFilterForType(this, GPUImageFilterTools.FilterType.BRIGHTNESS);
        switchFilterTo(filter);
        gpuImageView.requestRender();
        lastlingDuInt = lingDuInt;

        seekBar.setProgress(lingDuInt);
    }

    //改变图片饱和度
    private void toChangeBaoHeDu() {
        currentOperation = BAOHEDU;
        changeTitleBarState(false, "饱和度");
        changeBottomBarState(false);
        showImageView.setVisibility(View.INVISIBLE);

        //对图像的副本进行操作
        genCopyBmpAndSetGpuBmp();
        GPUImageFilter filter = GPUImageFilterTools.createFilterForType(this, GPUImageFilterTools.FilterType.SATURATION);
        switchFilterTo(filter);
        gpuImageView.requestRender();
        lastbaoHeDuInt = baoHeDuInt;

        seekBar.setProgress(baoHeDuInt);
    }

    //改变图片对比度
    private void toChangeDuiBiDu() {
        currentOperation = DUIBIDU;
        changeTitleBarState(false, "对比度");
        changeBottomBarState(false);
        showImageView.setVisibility(View.INVISIBLE);

        //对图像的副本进行操作
        genCopyBmpAndSetGpuBmp();
        GPUImageFilter filter = GPUImageFilterTools.createFilterForType(this, GPUImageFilterTools.FilterType.CONTRAST);
        switchFilterTo(filter);
        gpuImageView.requestRender();
        lastduiBiDuInt = duiBiDuInt;

        seekBar.setProgress(duiBiDuInt);
    }

    //改变图片冷暖
    private void toChangeWhiteBalance() {
        currentOperation = WHITEBALANCE;
        changeTitleBarState(false, "白平衡");
        changeBottomBarState(false);
        showImageView.setVisibility(View.INVISIBLE);

        //对图像的副本进行操作
        genCopyBmpAndSetGpuBmp();
        GPUImageFilter filter = GPUImageFilterTools.createFilterForType(this, GPUImageFilterTools.FilterType.WHITE_BALANCE);
        switchFilterTo(filter);
        gpuImageView.requestRender();
        lastWhiteBalanceInt = whiteBalanceInt;

        seekBar.setProgress(whiteBalanceInt);
    }

    //改变图片锐化
    private void toChangeRuiDu() {
        currentOperation = RUIHUA;
        changeTitleBarState(false, "锐化");
        changeBottomBarState(false);
        showImageView.setVisibility(View.INVISIBLE);

        //对图像的副本进行操作
        genCopyBmpAndSetGpuBmp();
        GPUImageFilter filter = GPUImageFilterTools.createFilterForType(this, GPUImageFilterTools.FilterType.SHARPEN);
        switchFilterTo(filter);
        gpuImageView.requestRender();
        lastruiHuaInt = ruiHuaInt;

        seekBar.setProgress(ruiHuaInt);
    }

    private void genCopyBmpAndSetGpuBmp() {
        //对图像的副本进行操作
        Bitmap lastBmp = copyBmp;
        copyBmp = Bitmap.createBitmap(imageViewWidth, imageViewHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(copyBmp);
        Matrix matrix = new Matrix();
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        matrix.postScale(scaleRatio, scaleRatio);
        canvas.drawBitmap(currentBmp, matrix, paint);
        gpuImageView.getGPUImage().deleteImage();
        gpuImageView.setImage(copyBmp);
        if (lastBmp != null && lastBmp != copyBmp && !lastBmp.isRecycled()) {
            lastBmp.recycle();
        }
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
        bmpWidth = currentBmp.getWidth();
        bmpHeight = currentBmp.getHeight();

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
            scaleRatio = horScaleRadio;
        } else {//图片水平缩放
            imageViewHeight = fullHeight;
            imageViewWidth = (int) (bmpWidth * verScaleRadio);
            scaleRatio = verScaleRadio;
        }

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) gpuImageView.getLayoutParams();
        if (params != null) {
            params.width = imageViewWidth;
            params.height = imageViewHeight;
        } else {
            params = new FrameLayout.LayoutParams(imageViewWidth, imageViewHeight);
            params.gravity = Gravity.CENTER;
        }

        showImageView.setLayoutParams(params);
        gpuImageView.setLayoutParams(params);

        showImageView.setVisibility(View.VISIBLE);
        showImageView.setImageBitmap(currentBmp);
    }

    /**
     * 获得状态栏的高度
     */
    public int getStatusHeight() {
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }

    @Override
    public void onBackPressed() {
        if (DialogUtil.getInstance().isShowing(this)) {
            //保存图片的过程中不允许退出
            return;
        }
        super.onBackPressed();
    }

    /**
     * 生成滤镜图片的线程*
     */
    private class GenFilterBmpTask extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DialogUtil.getInstance().showLoading(ActivityQuadrilateralCrop.this);
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            //得到gpu绘制的bitmap
            Bitmap lastBmp = currentBmp;
            try {
                currentBmp = gpuImageView.capture();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //释放产生的临时的bitmap
            if (lastBmp != null && lastBmp != currentBmp && !lastBmp.isRecycled()) {
                lastBmp.recycle();
            }
            if (copyBmp != null && copyBmp != currentBmp && !copyBmp.isRecycled()) {
                copyBmp.recycle();
                copyBmp = null;
            }
            return currentBmp;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            changeTitleBarState(true, "");
            changeBottomBarState(true);
            showBitmap();
            DialogUtil.getInstance().dismissLoading(ActivityQuadrilateralCrop.this);
        }
    }

    /**
     * 生成变形剪裁图片的线程*
     */
    private class GenCropBmpTask extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DialogUtil.getInstance().showLoading(ActivityQuadrilateralCrop.this);
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            //得到剪裁的bitmap
            Bitmap resultBmp = cropImageView.getCroppedImage();
            Bitmap lastBmp = currentBmp;
            currentBmp = resultBmp;
            //释放上一个bitmap
            if (lastBmp != null && lastBmp != currentBmp && !lastBmp.isRecycled()) {
                lastBmp.recycle();
            }
            return currentBmp;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            changeTitleBarState(true, "");
            cropImageView.setVisibility(View.GONE);
            showContainer.setVisibility(View.VISIBLE);
            showBitmap();
            DialogUtil.getInstance().dismissLoading(ActivityQuadrilateralCrop.this);
        }
    }
}
