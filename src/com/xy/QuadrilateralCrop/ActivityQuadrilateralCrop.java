package com.xy.QuadrilateralCrop;

import android.app.Activity;
import android.content.Context;
import android.graphics.*;
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
     */
    private View backView;
    private View cancelView;
    private TextView title;
    private View nextStepView;
    private View okView;

    //用来展示剪裁，变换结果的ImageView
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
    private static final int LENGNUAN = 5;
    private static final int BAOHEDU = 6;
    private static final int RUIHUA = 7;

    private int currentOperation = NOTHING;//没有操作

    /**
     * 当前的操作的各种数值*
     */
    private static final int CENTER_VALUE = 100;
    private int lingDuInt = CENTER_VALUE;
    private int duiBiDuInt = CENTER_VALUE;
    private int lengNuanInt = CENTER_VALUE;
    private int baoHeDuInt = CENTER_VALUE;
    private int ruiHuaInt = CENTER_VALUE;

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
            case LENGNUAN:
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
        Bitmap lastBmp = copyBmp;
        copyBmp = Bitmap.createBitmap(imageViewWidth, imageViewHeight, Bitmap.Config.ARGB_8888);
        int brightness = progress - CENTER_VALUE;
        ColorMatrix cMatrix = new ColorMatrix();
        cMatrix.set(new float[]{1, 0, 0, 0, brightness, 0, 1,
                0, 0, brightness,// 改变亮度
                0, 0, 1, 0, brightness, 0, 0, 0, 1, 0});
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColorFilter(new ColorMatrixColorFilter(cMatrix));

        Canvas canvas = new Canvas(copyBmp);
        Matrix matrix = new Matrix();
        matrix.postScale(scaleRatio, scaleRatio);
        canvas.drawBitmap(currentBmp, matrix, paint);
        showImageView.setImageBitmap(copyBmp);
        if (lastBmp != null && lastBmp != copyBmp && !lastBmp.isRecycled()) {
            lastBmp.recycle();
        }
    }

    private void changeBmpBaoHeDu(int progress) {
        baoHeDuInt = progress;
        Bitmap lastBmp = copyBmp;
        copyBmp = Bitmap.createBitmap(imageViewWidth, imageViewHeight, Bitmap.Config.ARGB_8888);
        ColorMatrix cMatrix = new ColorMatrix();
        cMatrix.setSaturation((float) ((progress - CENTER_VALUE) / 100.0));
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColorFilter(new ColorMatrixColorFilter(cMatrix));

        Canvas canvas = new Canvas(copyBmp);
        Matrix matrix = new Matrix();
        matrix.postScale(scaleRatio, scaleRatio);
        canvas.drawBitmap(currentBmp, matrix, paint);
        showImageView.setImageBitmap(copyBmp);
        if (lastBmp != null && lastBmp != copyBmp && !lastBmp.isRecycled()) {
            lastBmp.recycle();
        }
    }

    private void changeBmpDuiBiDu(int progress) {
        duiBiDuInt = progress;
        Bitmap lastBmp = copyBmp;
        copyBmp = Bitmap.createBitmap(imageViewWidth, imageViewHeight, Bitmap.Config.ARGB_8888);
        float contrast = (float) ((progress - CENTER_VALUE) / 100.0);
        ColorMatrix cMatrix = new ColorMatrix();
        cMatrix.set(new float[]{contrast, 0, 0, 0, 0, 0,
                contrast, 0, 0, 0,// 改变对比度
                0, 0, contrast, 0, 0, 0, 0, 0, 1, 0});
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColorFilter(new ColorMatrixColorFilter(cMatrix));

        Canvas canvas = new Canvas(copyBmp);
        Matrix matrix = new Matrix();
        matrix.postScale(scaleRatio, scaleRatio);
        canvas.drawBitmap(currentBmp, matrix, paint);
        showImageView.setImageBitmap(copyBmp);
        if (lastBmp != null && lastBmp != copyBmp && !lastBmp.isRecycled()) {
            lastBmp.recycle();
        }
    }

    private void changeBmpRuiHua(int progress) {
        ruiHuaInt = progress;
        Bitmap lastBmp = copyBmp;
        // 拉普拉斯矩阵
        int[] laplacian = new int[]{-1, -1, -1, -1, 9, -1, -1, -1, -1};

        int width = currentBmp.getWidth();
        int height = currentBmp.getHeight();
        Bitmap ruiHuaBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        int pixR = 0;
        int pixG = 0;
        int pixB = 0;

        int pixColor = 0;

        int newR = 0;
        int newG = 0;
        int newB = 0;

        int idx = 0;
        float alpha = progress * 1.0f / 200.0f;
        int[] pixels = new int[width * height];
        currentBmp.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 1, length = height - 1; i < length; i++) {
            for (int k = 1, len = width - 1; k < len; k++) {
                idx = 0;
                for (int m = -1; m <= 1; m++) {
                    for (int n = -1; n <= 1; n++) {
                        pixColor = pixels[(i + n) * width + k + m];
                        pixR = Color.red(pixColor);
                        pixG = Color.green(pixColor);
                        pixB = Color.blue(pixColor);

                        newR = newR + (int) (pixR * laplacian[idx] * alpha);
                        newG = newG + (int) (pixG * laplacian[idx] * alpha);
                        newB = newB + (int) (pixB * laplacian[idx] * alpha);
                        idx++;
                    }
                }

                newR = Math.min(255, Math.max(0, newR));
                newG = Math.min(255, Math.max(0, newG));
                newB = Math.min(255, Math.max(0, newB));

                pixels[i * width + k] = Color.argb(255, newR, newG, newB);
                newR = 0;
                newG = 0;
                newB = 0;
            }
        }

        ruiHuaBmp.setPixels(pixels, 0, width, 0, 0, width, height);

        //得到锐化的bitmap,然后缩放到showimageView大小,展示锐化效果
        copyBmp = Bitmap.createBitmap(imageViewWidth, imageViewHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(copyBmp);
        Matrix matrix = new Matrix();
        matrix.postScale(scaleRatio, scaleRatio);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        canvas.drawBitmap(ruiHuaBmp, matrix, paint);
        showImageView.setImageBitmap(copyBmp);
        if (ruiHuaBmp != null && ruiHuaBmp != copyBmp && !ruiHuaBmp.isRecycled()) {
            ruiHuaBmp.recycle();
        }
        if (lastBmp != null && lastBmp != copyBmp && !lastBmp.isRecycled()) {
            lastBmp.recycle();
        }
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
                changeTitleBarState(true, "");
                changeBottomBarState(true);
                lingDuInt = CENTER_VALUE;
                //显示原图像，并且释放产生的临时的bitmap
                showImageView.setImageBitmap(currentBmp);
                if (copyBmp != null && !copyBmp.isRecycled()) {
                    copyBmp.recycle();
                    copyBmp = null;
                }
                break;
            case DUIBIDU:
                changeTitleBarState(true, "");
                changeBottomBarState(true);
                duiBiDuInt = CENTER_VALUE;
                //显示原图像，并且释放产生的临时的bitmap
                showImageView.setImageBitmap(currentBmp);
                if (copyBmp != null && !copyBmp.isRecycled()) {
                    copyBmp.recycle();
                    copyBmp = null;
                }
                break;
            case LENGNUAN:
                break;
            case BAOHEDU:
                changeTitleBarState(true, "");
                changeBottomBarState(true);
                baoHeDuInt = CENTER_VALUE;
                //显示原图像，并且释放产生的临时的bitmap
                showImageView.setImageBitmap(currentBmp);
                if (copyBmp != null && !copyBmp.isRecycled()) {
                    copyBmp.recycle();
                    copyBmp = null;
                }
                break;
            case RUIHUA:
                changeTitleBarState(true, "");
                changeBottomBarState(true);
                ruiHuaInt = CENTER_VALUE;
                //显示原图像，并且释放产生的临时的bitmap
                showImageView.setImageBitmap(currentBmp);
                if (copyBmp != null && !copyBmp.isRecycled()) {
                    copyBmp.recycle();
                    copyBmp = null;
                }
                break;
        }
        currentOperation = NOTHING;
    }

    private void doOkAction() {
        switch (currentOperation) {
            case NOTHING:
                break;
            case CROP:
                getCropImageAndShow();
                break;
            case ROTATE:
                break;
            case LIANGDU: {
                changeTitleBarState(true, "");
                changeBottomBarState(true);
                //根据亮度，计算当前的currentBmp的亮度
                Bitmap lastBmp = currentBmp;
                Bitmap temp = Bitmap.createBitmap(bmpWidth, bmpHeight, Bitmap.Config.ARGB_8888);
                int brightness = lingDuInt - CENTER_VALUE;
                ColorMatrix cMatrix = new ColorMatrix();
                cMatrix.set(new float[]{1, 0, 0, 0, brightness, 0, 1,
                        0, 0, brightness,// 改变亮度
                        0, 0, 1, 0, brightness, 0, 0, 0, 1, 0});
                Paint paint = new Paint();
                paint.setColorFilter(new ColorMatrixColorFilter(cMatrix));

                Canvas canvas = new Canvas(temp);
                canvas.drawBitmap(currentBmp, 0, 0, paint);
                currentBmp = temp;
                showBitmap();
                //释放产生的临时的bitmap
                if (lastBmp != null && !lastBmp.isRecycled()) {
                    lastBmp.recycle();
                }
                if (copyBmp != null && !copyBmp.isRecycled()) {
                    copyBmp.recycle();
                    copyBmp = null;
                }
            }
            break;
            case DUIBIDU: {
                changeTitleBarState(true, "");
                changeBottomBarState(true);
                //根据对比度，计算当前的currentBmp的对比度
                Bitmap lastBmp = currentBmp;
                Bitmap temp = Bitmap.createBitmap(bmpWidth, bmpHeight, Bitmap.Config.ARGB_8888);
                float contrast = (float) ((duiBiDuInt - CENTER_VALUE) / 100.0);
                ColorMatrix cMatrix = new ColorMatrix();
                cMatrix.set(new float[]{contrast, 0, 0, 0, 0, 0,
                        contrast, 0, 0, 0,// 改变对比度
                        0, 0, contrast, 0, 0, 0, 0, 0, 1, 0});
                Paint paint = new Paint();
                paint.setColorFilter(new ColorMatrixColorFilter(cMatrix));

                Canvas canvas = new Canvas(temp);
                canvas.drawBitmap(currentBmp, 0, 0, paint);
                currentBmp = temp;
                showBitmap();
                //释放产生的临时的bitmap
                if (lastBmp != null && !lastBmp.isRecycled()) {
                    lastBmp.recycle();
                }
                if (copyBmp != null && !copyBmp.isRecycled()) {
                    copyBmp.recycle();
                    copyBmp = null;
                }
            }
            break;
            case LENGNUAN:
                break;
            case BAOHEDU: {
                changeTitleBarState(true, "");
                changeBottomBarState(true);
                //根据饱和度，计算当前的currentBmp的饱和度
                Bitmap lastBmp = currentBmp;
                Bitmap temp = Bitmap.createBitmap(bmpWidth, bmpHeight, Bitmap.Config.ARGB_8888);
                ColorMatrix cMatrix = new ColorMatrix();
                cMatrix.setSaturation((float) ((baoHeDuInt - CENTER_VALUE) / 100.0));
                Paint paint = new Paint();
                paint.setColorFilter(new ColorMatrixColorFilter(cMatrix));

                Canvas canvas = new Canvas(temp);
                canvas.drawBitmap(currentBmp, 0, 0, paint);
                currentBmp = temp;
                showBitmap();
                //释放产生的临时的bitmap
                if (lastBmp != null && !lastBmp.isRecycled()) {
                    lastBmp.recycle();
                }
                if (copyBmp != null && !copyBmp.isRecycled()) {
                    copyBmp.recycle();
                    copyBmp = null;
                }
            }
            break;
            case RUIHUA: {
                changeTitleBarState(true, "");
                changeBottomBarState(true);
                Bitmap lastBmp = currentBmp;
                // 拉普拉斯矩阵
                int[] laplacian = new int[]{-1, -1, -1, -1, 9, -1, -1, -1, -1};

                int width = currentBmp.getWidth();
                int height = currentBmp.getHeight();
                Bitmap ruiHuaBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

                int pixR = 0;
                int pixG = 0;
                int pixB = 0;

                int pixColor = 0;

                int newR = 0;
                int newG = 0;
                int newB = 0;

                int idx = 0;
                float alpha = ruiHuaInt * 1.0f / 200.0f;
                int[] pixels = new int[width * height];
                currentBmp.getPixels(pixels, 0, width, 0, 0, width, height);
                for (int i = 1, length = height - 1; i < length; i++) {
                    for (int k = 1, len = width - 1; k < len; k++) {
                        idx = 0;
                        for (int m = -1; m <= 1; m++) {
                            for (int n = -1; n <= 1; n++) {
                                pixColor = pixels[(i + n) * width + k + m];
                                pixR = Color.red(pixColor);
                                pixG = Color.green(pixColor);
                                pixB = Color.blue(pixColor);

                                newR = newR + (int) (pixR * laplacian[idx] * alpha);
                                newG = newG + (int) (pixG * laplacian[idx] * alpha);
                                newB = newB + (int) (pixB * laplacian[idx] * alpha);
                                idx++;
                            }
                        }

                        newR = Math.min(255, Math.max(0, newR));
                        newG = Math.min(255, Math.max(0, newG));
                        newB = Math.min(255, Math.max(0, newB));

                        pixels[i * width + k] = Color.argb(255, newR, newG, newB);
                        newR = 0;
                        newG = 0;
                        newB = 0;
                    }
                }

                ruiHuaBmp.setPixels(pixels, 0, width, 0, 0, width, height);

                currentBmp = ruiHuaBmp;
                showBitmap();
                if (lastBmp != null && lastBmp != copyBmp && !lastBmp.isRecycled()) {
                    lastBmp.recycle();
                }
                if (copyBmp != null && !copyBmp.isRecycled()) {
                    copyBmp.recycle();
                    copyBmp = null;
                }
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
        seekBar.setProgress(lingDuInt);
    }

    //改变图片饱和度
    private void toChangeBaoHeDu() {
        currentOperation = BAOHEDU;
        changeTitleBarState(false, "饱和度");
        changeBottomBarState(false);
        seekBar.setProgress(baoHeDuInt);
    }

    //改变图片对比度
    private void toChangeDuiBiDu() {
        currentOperation = DUIBIDU;
        changeTitleBarState(false, "对比度");
        changeBottomBarState(false);
        seekBar.setProgress(duiBiDuInt);
    }

    //改变图片锐化
    private void toChangeRuiDu() {
        currentOperation = RUIHUA;
        changeTitleBarState(false, "锐化");
        changeBottomBarState(false);
        seekBar.setProgress(ruiHuaInt);
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
