package com.xy.QuadrilateralCrop.loading_view;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import com.xy.QuadrilateralCrop.R;

/**
 * Created by liangyu on 2016/3/22.
 */
public class DialogUtil {
    private static DialogUtil instance;


    private DialogUtil() {

    }

    public static DialogUtil getInstance() {
        if (instance == null) {
            synchronized (DialogUtil.class) {
                if (instance == null) {
                    instance = new DialogUtil();
                }
            }
        }
        return instance;
    }

    public boolean isShowing(Activity activity) {
        View loadingView = activity.findViewById(R.id.loading_view_id);
        if (loadingView != null && loadingView.getVisibility() == View.VISIBLE) {
            return true;
        }
        return false;
    }

    public void showLoading(Activity activity) {
        View loadingView = null;
        FrameLayout frameLayout = (FrameLayout) activity.getWindow().getDecorView().findViewById(android.R.id.content);
        loadingView = activity.findViewById(R.id.loading_view_id);
        if (loadingView != null) {
            loadingView.setVisibility(View.VISIBLE);
        } else {
            LayoutInflater inflater = LayoutInflater.from(activity);
            loadingView = inflater.inflate(R.layout.loading_view, null);
            loadingView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
            loadingView.setId(R.id.loading_view_id);
            frameLayout.addView(loadingView);
        }
    }

    public void dismissLoading(Activity activity) {
        View loadingView = activity.findViewById(R.id.loading_view_id);
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }
    }
}
