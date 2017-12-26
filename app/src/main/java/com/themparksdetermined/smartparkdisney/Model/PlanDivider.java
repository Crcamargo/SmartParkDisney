package com.themparksdetermined.smartparkdisney.Model;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Crist on 8/14/2017.
 */

public class PlanDivider extends RecyclerView.ItemDecoration {
    private Drawable divider;

    public PlanDivider(Drawable divider){
        this.divider = divider;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {

        int middle = parent.getWidth()/2;

        final int childCount = parent.getChildCount() - 1; //skip the last one
        for(int i = 0; i < childCount; i++){
            final View child = parent.getChildAt(i);
            final View childChild = parent.getChildAt(i+1);

            int top = child.getBottom() ;
            int bottom = childChild.getTop();

            final int height = (bottom - top)/3;

            top = child.getBottom();
            bottom = top + height;
            final int left = middle - height/2;
            final int right = middle + height/2;

            divider.setBounds(left, top, right, bottom);
            divider.draw(c);
            divider.setBounds(left, top + (height * 2), right, bottom + (height * 2));
            divider.draw(c);
            divider.setBounds(left, top + (height * 4), right, bottom + (height * 4));
            divider.draw(c);
        }
    }
}
