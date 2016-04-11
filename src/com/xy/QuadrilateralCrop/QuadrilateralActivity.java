package com.xy.QuadrilateralCrop;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.xy.QuadrilateralCrop.quadrilateral_crop.QuadrilateralCropImageView;

/**
 * Created by fc on 15-3-11.
 */
public class QuadrilateralActivity extends Activity implements View.OnClickListener {
    /**
     * views
     **/
    private TextView titleTV;
    private ImageView showImageView;
    private View cropTV;
    private View showContainer;
    private QuadrilateralCropImageView cropImageView;

    private Bitmap resultBmp;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);

        getViews();
        initViews();
        setListeners();
    }

    private void getViews() {
        titleTV = (TextView) findViewById(R.id.titleTV);
        showImageView = (ImageView) findViewById(R.id.showImageView);
        cropTV = findViewById(R.id.cropTV);
        showContainer = findViewById(R.id.showContainer);
        cropImageView = (QuadrilateralCropImageView) findViewById(R.id.cropImageView);
    }

    private void initViews() {

    }

    private void setListeners() {
        titleTV.setOnClickListener(this);
        cropTV.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleTV:
                resultBmp = cropImageView.getCroppedImage();
                cropImageView.setVisibility(View.GONE);
                showContainer.setVisibility(View.VISIBLE);
                showImageView.setImageBitmap(resultBmp);
                break;
            case R.id.cropTV:
                showCropView();
                break;
        }
    }

    private void showCropView() {
        showContainer.setVisibility(View.GONE);
        cropImageView.setVisibility(View.VISIBLE);
        cropImageView.setImageResource(R.drawable.test);
    }
}
