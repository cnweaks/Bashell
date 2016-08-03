package com.cnweak.rebash.fileftp;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LayoutAnimationController;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.animation.*;

public class MyAnimation {

	public LayoutAnimationController getController(AnimationSet as) {
        LayoutAnimationController lac = new LayoutAnimationController(as);
        lac.setOrder(LayoutAnimationController.ORDER_NORMAL);
        lac.setDelay(0.1f);
        return lac;
    }
    //AnimationSet相当于animator里的<set/>
	public  AnimationSet getSet(Animation... animations) {
        //true相当于<set/>里的andrroid:shareInterpolator的参数
        AnimationSet Set = new AnimationSet(true);
        for (Animation param : animations) {
          Set.addAnimation(param);
        }
        //如果上面的参数为false，这里的效果就要animation自己设置了
        Set.setStartOffset(0);
        Set.setDuration(100);//效果持续时间
        Set.setRepeatCount(1);
        Set.setFillAfter(true);
        Set.setInterpolator(new DecelerateInterpolator());
		//Set.setInterpolator(new LinearInterpolator());//匀速
		//Set.setInterpolator(new AccelerateInterpolator());//加速
		//Set.setInterpolator(new AccelerateDecelerateInterpolator());//加减加
        return Set;
    }

	public TranslateAnimation getTranslate() {
        return new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
    }

   public AlphaAnimation getAlpha() {
        return new AlphaAnimation(0, 1);
    }

	public ScaleAnimation getScale() {
        return new ScaleAnimation(0.1f, 1, 0.1f, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    }

	public RotateAnimation getRotate() {
        return new RotateAnimation(0, 360, Animation.RELATIVE_TO_PARENT, 0.5f, Animation.RELATIVE_TO_PARENT, 0.5f);
    }


}
