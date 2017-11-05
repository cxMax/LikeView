package com.cxmax.widget.utils;

import android.animation.Animator;

/**
 * @describe :
 * @usage :
 * <p>
 * </p>
 * Created by caixi on 17-11-5.
 */

public class AnimUtil {

    public static void remove(Animator... animators) {
        for (Animator animator: animators) {
            if (animator != null) {
                animator.cancel();
                animator.removeAllListeners();
            }
        }
    }
}
