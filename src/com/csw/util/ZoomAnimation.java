package com.csw.util;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;

public class ZoomAnimation {

	
	
	public static void scaleButton_dada(View v){
		
		AnimationSet animationSet = new AnimationSet(false);
		ScaleAnimation scaleAnimation =new ScaleAnimation(1.0f, 1.05f, 1.0f, 1.05f, 
				Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF, 0.5f);
		animationSet.setFillAfter(true);
		animationSet.setFillBefore(false);
		animationSet.setDuration(150);
		animationSet.addAnimation(scaleAnimation);
		v.startAnimation(animationSet);
		
	}
    
}
