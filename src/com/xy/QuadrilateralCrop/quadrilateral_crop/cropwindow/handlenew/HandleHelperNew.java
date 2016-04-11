package com.xy.QuadrilateralCrop.quadrilateral_crop.cropwindow.handlenew;

import android.graphics.Rect;
import com.xy.QuadrilateralCrop.quadrilateral_crop.cropwindow.edgenew.EdgeNew;

/**
 * Created by fc on 15-3-10.
 */
public abstract class HandleHelperNew {

    protected EdgeNew primaryEdge;
    protected EdgeNew secondaryEdge;

    public HandleHelperNew(EdgeNew primaryEdge, EdgeNew secondaryEdge){
        this.primaryEdge = primaryEdge;
        this.secondaryEdge = secondaryEdge;
    }

    public abstract void updateCropWindow(float x, float y, Rect imageRect);
}
