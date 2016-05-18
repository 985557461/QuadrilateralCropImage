package com.xy.QuadrilateralCrop.quadrilateral_crop;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.xy.QuadrilateralCrop.R;
import com.xy.QuadrilateralCrop.quadrilateral_crop.cropwindow.QuadrilateralCropOverlayView;
import com.xy.QuadrilateralCrop.quadrilateral_crop.cropwindow.edgenew.EdgeNew;
import com.xy.QuadrilateralCrop.quadrilateral_crop.util.ImageViewUtil;

/**
 * Created by fc on 15-3-11.
 */
public class QuadrilateralCropImageView extends FrameLayout {
    // Private Constants ///////////////////////////////////////////////////////
    private static final Rect EMPTY_RECT = new Rect();

    // Sets the default image guidelines to show when resizing
    public static final int DEFAULT_GUIDELINES = 1;
    public static final boolean DEFAULT_FIXED_ASPECT_RATIO = false;
    public static final int DEFAULT_ASPECT_RATIO_X = 1;
    public static final int DEFAULT_ASPECT_RATIO_Y = 1;

    private static final int DEFAULT_IMAGE_RESOURCE = 0;

    private static final String DEGREES_ROTATED = "DEGREES_ROTATED";

    private ImageView mImageView;
    private QuadrilateralCropOverlayView mCropOverlayView;

    private Bitmap mBitmap;
    private int mDegreesRotated = 0;

    private int mLayoutWidth;
    private int mLayoutHeight;

    // Instance variables for customizable attributes
    private int mGuidelines = DEFAULT_GUIDELINES;
    private boolean mFixAspectRatio = DEFAULT_FIXED_ASPECT_RATIO;
    private int mAspectRatioX = DEFAULT_ASPECT_RATIO_X;
    private int mAspectRatioY = DEFAULT_ASPECT_RATIO_Y;
    private int mImageResource = DEFAULT_IMAGE_RESOURCE;

    /**
     * 图片距离屏幕左右两边的距离
     **/
    private int leftRightMargin = 0;
    /**
     * 图片距离屏幕上下两边的距离
     **/
    private int topBottomMargin = 0;

    /**
     * 显示的图片的宽度和高度
     **/
    private int imageViewWidth = 0;
    private int imageViewHeight = 0;
    private float scaleRadio = 1.0f;
    private Rect showRect;

    public QuadrilateralCropImageView(Context context) {
        super(context);
        init(context);
    }

    public QuadrilateralCropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CropImageView, 0, 0);

        try {
            mGuidelines = ta.getInteger(R.styleable.CropImageView_guidelines, DEFAULT_GUIDELINES);
            mFixAspectRatio = ta.getBoolean(R.styleable.CropImageView_fixAspectRatio, DEFAULT_FIXED_ASPECT_RATIO);
            mAspectRatioX = ta.getInteger(R.styleable.CropImageView_aspectRatioX, DEFAULT_ASPECT_RATIO_X);
            mAspectRatioY = ta.getInteger(R.styleable.CropImageView_aspectRatioY, DEFAULT_ASPECT_RATIO_Y);
            mImageResource = ta.getResourceId(R.styleable.CropImageView_imageResource, DEFAULT_IMAGE_RESOURCE);
        } finally {
            ta.recycle();
        }
        init(context);
    }

    private void init(Context context) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View v = inflater.inflate(R.layout.crop_image_view_new, this, true);
        mImageView = (ImageView) v.findViewById(R.id.ImageView_image);
        setImageResource(mImageResource);
        mCropOverlayView = (QuadrilateralCropOverlayView) v.findViewById(R.id.CropOverlayView);

        float scale = context.getResources().getDisplayMetrics().density;
        leftRightMargin = (int) (15 * scale + 0.5f);;
        topBottomMargin = (int) (15 * scale + 0.5f);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putInt(DEGREES_ROTATED, mDegreesRotated);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            if (mBitmap != null) {
                // Fixes the rotation of the image when orientation changes.
                mDegreesRotated = bundle.getInt(DEGREES_ROTATED);
                int tempDegrees = mDegreesRotated;
                rotateImage(mDegreesRotated);
                mDegreesRotated = tempDegrees;
            }
            super.onRestoreInstanceState(bundle.getParcelable("instanceState"));
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (mBitmap != null) {
            final Rect bitmapRect = ImageViewUtil.getBitmapRectCenterInside(mBitmap, this);
            mCropOverlayView.setBitmapRect(bitmapRect);
        } else {
            mCropOverlayView.setBitmapRect(EMPTY_RECT);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (mBitmap != null) {
            //摆放ImageView的位置和大小
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int bmpWidth = mBitmap.getWidth();
            int bmpHeight = mBitmap.getHeight();

            int fullWidthExceptHorMargin = widthSize - leftRightMargin * 2;
            int fullHeightExceptVermargin = heightSize - topBottomMargin * 2;

            float horScaleRadio = fullWidthExceptHorMargin * 1.0f / bmpWidth;
            float verScaleRadio = fullHeightExceptVermargin * 1.0f / bmpHeight;

            if (horScaleRadio < verScaleRadio) {//图片竖直缩放
                imageViewWidth = fullWidthExceptHorMargin;
                imageViewHeight = (int) (bmpHeight * horScaleRadio);
                scaleRadio = horScaleRadio;
            } else {//图片水平缩放
                imageViewHeight = fullHeightExceptVermargin;
                imageViewWidth = (int) (bmpWidth * verScaleRadio);
                scaleRadio = verScaleRadio;
            }

            mImageView.post(new Runnable() {
                @Override
                public void run() {
                    FrameLayout.LayoutParams params = (LayoutParams) mImageView.getLayoutParams();
                    if (params != null) {
                        params.width = imageViewWidth;
                        params.height = imageViewHeight;
                    } else {
                        params = new FrameLayout.LayoutParams(imageViewWidth, imageViewHeight);
                        params.gravity = Gravity.CENTER;
                    }
                    mImageView.setLayoutParams(params);
                }
            });

            mLayoutWidth = widthSize;
            mLayoutHeight = heightSize;

            //传递当前显示的ImageView的Rect的范围
            showRect = new Rect();
            int left = (int) ((widthSize - imageViewWidth) * 1.0f / 2);
            int top = (int) ((heightSize - imageViewHeight) * 1.0f / 2);
            int right = left + imageViewWidth;
            int bottom = top + imageViewHeight;
            showRect.set(left, top, right, bottom);
            mCropOverlayView.setBitmapRect(showRect);

            // MUST CALL THIS
            setMeasuredDimension(mLayoutWidth, mLayoutHeight);
        } else {
            mCropOverlayView.setBitmapRect(EMPTY_RECT);
            setMeasuredDimension(widthSize, heightSize);
        }
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        super.onLayout(changed, l, t, r, b);

        if (mLayoutWidth > 0 && mLayoutHeight > 0) {
            // Gets original parameters, and creates the new parameters
            final ViewGroup.LayoutParams origparams = this.getLayoutParams();
            origparams.width = mLayoutWidth;
            origparams.height = mLayoutHeight;
            setLayoutParams(origparams);
        }
    }

    public int getImageResource() {
        return mImageResource;
    }

    public void setImageBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        mImageView.setImageBitmap(mBitmap);

        if (mCropOverlayView != null) {
            mCropOverlayView.resetCropOverlayView();
        }
    }

    public void setImageResource(int resId) {
        if (resId != 0) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);
            setImageBitmap(bitmap);
        }
    }

    public Bitmap getCroppedImage() {
        //原始的纹理图片
        Bitmap texsBitmap = Bitmap.createBitmap(imageViewWidth, imageViewHeight, Bitmap.Config.ARGB_8888);
        Matrix matrix = new Matrix();
        matrix.postScale(scaleRadio, scaleRadio);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Canvas texSCanvas = new Canvas(texsBitmap);
        texSCanvas.drawBitmap(mBitmap, matrix, paint);

        //得到最后结果图片的大小
        int minLeft = (int) (EdgeNew.TOP_LEFT.getxCoordinate() > EdgeNew.BOTTOM_LEFT.getxCoordinate() ? EdgeNew.BOTTOM_LEFT.getxCoordinate() : EdgeNew.TOP_LEFT.getxCoordinate());
        int maxRight = (int) (EdgeNew.TOP_RIGHT.getxCoordinate() > EdgeNew.BOTTOM_RIGHT.getxCoordinate() ? EdgeNew.TOP_RIGHT.getxCoordinate() : EdgeNew.BOTTOM_RIGHT.getxCoordinate());
        int minTop = (int) (EdgeNew.TOP_LEFT.getyCoordinate() > EdgeNew.TOP_RIGHT.getyCoordinate() ? EdgeNew.TOP_RIGHT.getyCoordinate() : EdgeNew.TOP_LEFT.getyCoordinate());
        int maxBottom = (int) (EdgeNew.BOTTOM_LEFT.getyCoordinate() > EdgeNew.BOTTOM_RIGHT.getyCoordinate() ? EdgeNew.BOTTOM_LEFT.getyCoordinate() : EdgeNew.BOTTOM_RIGHT.getyCoordinate());
        int width = Math.abs(maxRight - minLeft);
        int height = Math.abs(maxBottom - minTop);

        Bitmap resultBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        //初始化纹理坐标
        float[] mTexs = new float[12];
        //纹理三角形1
        setXY(mTexs, 0, EdgeNew.TOP_LEFT.getxCoordinate() - showRect.left, EdgeNew.TOP_LEFT.getyCoordinate() - showRect.top);
        setXY(mTexs, 1, EdgeNew.TOP_RIGHT.getxCoordinate() - showRect.left, EdgeNew.TOP_RIGHT.getyCoordinate() - showRect.top);
        setXY(mTexs, 2, EdgeNew.BOTTOM_LEFT.getxCoordinate() - showRect.left, EdgeNew.BOTTOM_LEFT.getyCoordinate() - showRect.top);
        //纹理三角形2
        setXY(mTexs, 3, EdgeNew.TOP_RIGHT.getxCoordinate() - showRect.left, EdgeNew.TOP_RIGHT.getyCoordinate() - showRect.top);
        setXY(mTexs, 4, EdgeNew.BOTTOM_RIGHT.getxCoordinate() - showRect.left, EdgeNew.BOTTOM_RIGHT.getyCoordinate() - showRect.top);
        setXY(mTexs, 5, EdgeNew.BOTTOM_LEFT.getxCoordinate() - showRect.left, EdgeNew.BOTTOM_LEFT.getyCoordinate() - showRect.top);

        //初始化顶点坐标
        float[] mVerts = new float[12];
        setXY(mVerts,0,0,0);
        setXY(mVerts,1,width,0);
        setXY(mVerts,2,0,height);
        setXY(mVerts,3,width,0);
        setXY(mVerts,4,width,height);
        setXY(mVerts,5,0,height);

        //初始化纹理画笔
        Paint paintTexs = new Paint();
        Shader shader = new BitmapShader(texsBitmap, Shader.TileMode.CLAMP,Shader.TileMode.CLAMP);
        paintTexs.setShader(shader);
        paintTexs.setAntiAlias(true);

        //绘制
        Canvas finalCanvas = new Canvas(resultBmp);
        finalCanvas.drawVertices(Canvas.VertexMode.TRIANGLES, mVerts.length, mVerts, 0,mTexs, 0, null, 0, null, 0, 0, paintTexs);

        return resultBmp;
    }

    private static void setXY(float[] array, int index, float x, float y) {
        array[index * 2 + 0] = x;
        array[index * 2 + 1] = y;
    }

    public void rotateImage(int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
        setImageBitmap(mBitmap);

        mDegreesRotated += degrees;
        mDegreesRotated = mDegreesRotated % 360;
    }

    private static int getOnMeasureSpec(int measureSpecMode, int measureSpecSize, int desiredSize) {

        // Measure Width
        int spec;
        if (measureSpecMode == MeasureSpec.EXACTLY) {
            // Must be this size
            spec = measureSpecSize;
        } else if (measureSpecMode == MeasureSpec.AT_MOST) {
            // Can't be bigger than...; match_parent value
            spec = Math.min(desiredSize, measureSpecSize);
        } else {
            // Be whatever you want; wrap_content
            spec = desiredSize;
        }

        return spec;
    }

    public void reset() {
        mCropOverlayView.resetCropOverlayView();

    }

    public void setSelectionRect(Point topLeft, Point topRight, Point bottomLeft, Point bottomRight) {
        mCropOverlayView.setSelectionRect(topLeft, topRight, bottomLeft, bottomRight);
        mCropOverlayView.invalidate();
    }
}
