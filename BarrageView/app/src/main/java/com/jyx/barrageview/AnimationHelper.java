package com.jyx.barrageview;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class AnimationHelper {

    public static ObjectAnimator createTranslateAnim(View view, int fromX, int toX){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view,"translationX",fromX,toX);
        long duration = (long) (Math.abs(toX - fromX)*1.0f/DpPxUtils.getScreenWidth()*3000);
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new DecelerateAccelerateInterpolator());
        return objectAnimator;
    }
}
