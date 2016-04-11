package com.xy.QuadrilateralCrop.quadrilateral_crop.cropwindow.handlenew;

import android.graphics.Rect;
import com.xy.QuadrilateralCrop.quadrilateral_crop.cropwindow.edgenew.EdgeNew;

/**
 * Created by fc on 15-3-10.
 */
public class CornerHandleHelperNew extends HandleHelperNew {

    public CornerHandleHelperNew(EdgeNew singleEdge) {
        super(singleEdge, null);
    }

    @Override
    public void updateCropWindow(float x, float y, Rect imageRect) {
        this.primaryEdge.setxCoordinate(x);
        this.primaryEdge.setyCoordinate(y);
    }


}
