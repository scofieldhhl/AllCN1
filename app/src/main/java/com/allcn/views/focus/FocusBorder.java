package com.allcn.views.focus;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by owen on 2017/7/20.
 */

public interface FocusBorder {

    void setVisible(boolean visible);

    boolean isVisible();

    void onFocus(@NonNull View focusView, Options options, boolean ignoreOld);

    void boundGlobalFocusListener(@NonNull OnFocusCallback callback);

    void unBoundGlobalFocusListener();

    void reset();

    interface OnFocusCallback {
        Options onFocus(View oldFocus, View newFocus);
    }

    abstract class Options {
    }

    class Builder {
        public final ColorFocusBorder.Builder asColor() {
            return new ColorFocusBorder.Builder();
        }

        public final DrawableFocusBorder.Builder asDrawable() {
            return new DrawableFocusBorder.Builder();
        }
    }

    class OptionsFactory {
        public static final Options get() {
            return AbsFocusBorder.Options.get();
        }

        public static final Options get(float scaleX, float scaleY) {
            return AbsFocusBorder.Options.get(scaleX, scaleY);
        }

        public static final Options get(float scaleX, float scaleY, int exWidth, int exHeight) {
            return AbsFocusBorder.Options.get(scaleX, scaleY, exWidth, exHeight);
        }

        public static final Options get(float scaleX, float scaleY, int exWidth, int exHeight,
                                        int freeWidth, int freeHeight) {
            return AbsFocusBorder.Options.get(scaleX, scaleY, exWidth, exHeight,
                    freeWidth, freeHeight);
        }

        public static final Options get(int exWidth, int exHeight) {
            return AbsFocusBorder.Options.get(exWidth, exHeight);
        }

        public static final Options getWHXY(int exWidth, int exHeight, int freeX, int freeY) {
            return AbsFocusBorder.Options.getWHXY(exWidth, exHeight, freeX, freeY);
        }

        public static final Options get(int exWidth, int exHeight, int freeWidth, int freeHeight) {
            return AbsFocusBorder.Options.get(exWidth, exHeight, freeWidth, freeHeight);
        }

        public static final Options get(float scaleX, float scaleY, float roundRadius) {
            return ColorFocusBorder.Options.get(scaleX, scaleY, roundRadius);
        }
    }
}
