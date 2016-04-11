package com.xy.QuadrilateralCrop.quadrilateral_crop.cropwindow.drawer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import com.xy.QuadrilateralCrop.quadrilateral_crop.cropwindow.edge.Edge;
import com.xy.QuadrilateralCrop.quadrilateral_crop.cropwindow.edgenew.EdgeNew;

/**
 * Created by fc on 15-3-10.
 */
public class NewDrawer implements Drawer {
    @Override
    public void drawBackground(Canvas canvas, Rect bitmapRect, Paint backgroundPaint) {
        Path path = new Path();
        path.moveTo(0, 0);
        path.lineTo(bitmapRect.width(), 0);
        path.lineTo(bitmapRect.width(), bitmapRect.height());
        path.lineTo(0, bitmapRect.height());
        path.lineTo(EdgeNew.BOTTOM_LEFT.getxCoordinate(), EdgeNew.BOTTOM_LEFT.getyCoordinate());
        path.lineTo(EdgeNew.BOTTOM_RIGHT.getxCoordinate(), EdgeNew.BOTTOM_RIGHT.getyCoordinate());
        path.lineTo(EdgeNew.TOP_RIGHT.getxCoordinate(), EdgeNew.TOP_RIGHT.getyCoordinate());
        path.lineTo(EdgeNew.TOP_LEFT.getxCoordinate(), EdgeNew.TOP_LEFT.getyCoordinate());
        path.lineTo(EdgeNew.BOTTOM_LEFT.getxCoordinate(), EdgeNew.BOTTOM_LEFT.getyCoordinate());
        path.lineTo(0, bitmapRect.height());
        path.lineTo(0, 0);

        canvas.drawPath(path, backgroundPaint);
    }

    @Override
    public void drawRuleOfThirdsGuidelines(Canvas canvas, Paint guidePaint) {
//        设A1(X1,Y1),A2(X2,Y2)
//        三等分点为A3(X3,Y3),A4(X4,Y4)
//        X3=(X2+2X1)/3,Y3=(Y2+2Y1)/3
//        X4=(2X2+X1)/3,Y4=(2Y2+Y1)/3
        //左上点坐标
        final float leftTopX = EdgeNew.TOP_LEFT.getxCoordinate();
        final float leftTopY = EdgeNew.TOP_LEFT.getyCoordinate();

        //左下点坐标
        final float leftBottomX = EdgeNew.BOTTOM_LEFT.getxCoordinate();
        final float leftBottomY = EdgeNew.BOTTOM_LEFT.getyCoordinate();

        //右上点坐标
        final float rightTopX = EdgeNew.TOP_RIGHT.getxCoordinate();
        final float rightTopY = EdgeNew.TOP_RIGHT.getyCoordinate();

        //右下点坐标
        final float rightBottomX = EdgeNew.BOTTOM_RIGHT.getxCoordinate();
        final float rightBottomY = EdgeNew.BOTTOM_RIGHT.getyCoordinate();

        //计算左边的三等分点
        float leftXOne = (leftBottomX + 2 * leftTopX) / 3;
        float leftYOne = (leftBottomY + 2 * leftTopY) / 3;
        float leftXTwo = (2 * leftBottomX + leftTopX) / 3;
        float leftYTwo = (2 * leftBottomY + leftTopY) / 3;

        //计算右边的三等分点
        float rightXOne = (rightBottomX + 2 * rightTopX) / 3;
        float rightYOne = (rightBottomY + 2 * rightTopY) / 3;
        float rightXTwo = (2 * rightBottomX + rightTopX) / 3;
        float rightYTwo = (2 * rightBottomY + rightTopY) / 3;

        //计算上边的三等分点
        float topXOne = (rightTopX + 2 * leftTopX) / 3;
        float topYOne = (rightTopY + 2 * leftTopY) / 3;
        float topXTwo = (2 * rightTopX + leftTopX) / 3;
        float topYTwo = (2 * rightTopY + leftTopY) / 3;

        //计算下边的三等分点
        float bottomXOne = (rightBottomX + 2 * leftBottomX) / 3;
        float bottomYOne = (rightBottomY + 2 * leftBottomY) / 3;
        float bottomXTwo = (2 * rightBottomX + leftBottomX) / 3;
        float bottomYTwo = (2 * rightBottomY + leftBottomY) / 3;

        //draw hor lines
        canvas.drawLine(leftXOne, leftYOne, rightXOne, rightYOne, guidePaint);
        canvas.drawLine(leftXTwo, leftYTwo, rightXTwo, rightYTwo, guidePaint);

        //draw ver lines
        canvas.drawLine(topXOne, topYOne, bottomXOne, bottomYOne, guidePaint);
        canvas.drawLine(topXTwo, topYTwo, bottomXTwo, bottomYTwo, guidePaint);
    }

    @Override
    public void drawRect(Canvas canvas, Paint borderPaint) {
        Path path = new Path();
        path.moveTo(EdgeNew.TOP_LEFT.getxCoordinate(), EdgeNew.TOP_LEFT.getyCoordinate());
        path.lineTo(EdgeNew.TOP_RIGHT.getxCoordinate(), EdgeNew.TOP_RIGHT.getyCoordinate());
        path.lineTo(EdgeNew.BOTTOM_RIGHT.getxCoordinate(), EdgeNew.BOTTOM_RIGHT.getyCoordinate());
        path.lineTo(EdgeNew.BOTTOM_LEFT.getxCoordinate(), EdgeNew.BOTTOM_LEFT.getyCoordinate());
        path.lineTo(EdgeNew.TOP_LEFT.getxCoordinate(), EdgeNew.TOP_LEFT.getyCoordinate());

        canvas.drawPath(path, borderPaint);
    }

    @Override
    public void drawCorner(Canvas canvas, float cornerRadius, Paint cornerPaint) {
        canvas.drawCircle(EdgeNew.TOP_LEFT.getxCoordinate(), EdgeNew.TOP_LEFT.getyCoordinate(), cornerRadius, cornerPaint);
        canvas.drawCircle(EdgeNew.BOTTOM_LEFT.getxCoordinate(), EdgeNew.BOTTOM_LEFT.getyCoordinate(), cornerRadius, cornerPaint);
        canvas.drawCircle(EdgeNew.TOP_RIGHT.getxCoordinate(), EdgeNew.TOP_RIGHT.getyCoordinate(), cornerRadius, cornerPaint);
        canvas.drawCircle(EdgeNew.BOTTOM_RIGHT.getxCoordinate(), EdgeNew.BOTTOM_RIGHT.getyCoordinate(), cornerRadius, cornerPaint);

        canvas.drawCircle(midX(EdgeNew.TOP_LEFT, EdgeNew.TOP_RIGHT), midY(EdgeNew.TOP_LEFT, EdgeNew.TOP_RIGHT), cornerRadius, cornerPaint);
        canvas.drawCircle(midX(EdgeNew.TOP_LEFT, EdgeNew.BOTTOM_LEFT), midY(EdgeNew.TOP_LEFT, EdgeNew.BOTTOM_LEFT), cornerRadius, cornerPaint);
        canvas.drawCircle(midX(EdgeNew.BOTTOM_LEFT, EdgeNew.BOTTOM_RIGHT), midY(EdgeNew.BOTTOM_LEFT, EdgeNew.BOTTOM_RIGHT), cornerRadius, cornerPaint);
        canvas.drawCircle(midX(EdgeNew.TOP_RIGHT, EdgeNew.BOTTOM_RIGHT), midY(EdgeNew.TOP_RIGHT, EdgeNew.BOTTOM_RIGHT), cornerRadius, cornerPaint);
    }

    private static final float midX(EdgeNew edgeA, EdgeNew edgeB) {
        return (edgeA.getxCoordinate() + edgeB.getxCoordinate()) / 2;
    }

    private static final float midY(EdgeNew edgeA, EdgeNew edgeB) {
        return (edgeA.getyCoordinate() + edgeB.getyCoordinate()) / 2;
    }
}
