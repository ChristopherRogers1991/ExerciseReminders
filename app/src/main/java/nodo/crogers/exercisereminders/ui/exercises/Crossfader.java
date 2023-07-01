package nodo.crogers.exercisereminders.ui.exercises;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

public class Crossfader {
    private static final int DDURATION = 300;

    public static void crossfade(View from, View to) {
        to.setAlpha(0F);
        to.setVisibility(View.VISIBLE);
        to.animate()
                .alpha(1F)
                .setDuration(DDURATION)
                .setListener(null);

        from.animate()
                .alpha(0F)
                .setDuration(DDURATION)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        from.setVisibility(View.GONE);
                    }
                });
    }
}
