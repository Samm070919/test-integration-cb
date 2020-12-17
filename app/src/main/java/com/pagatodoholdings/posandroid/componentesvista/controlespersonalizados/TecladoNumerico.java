package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.view.KeyEvent;

import com.pagatodoholdings.posandroid.R;

import java.util.List;

public class TecladoNumerico extends KeyboardView {

    private final Context mContext;
    private final Paint paint = new Paint();
    private Drawable drawable;

    public TecladoNumerico(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setPreviewEnabled(false);
    }

    @SuppressLint("DrawAllocation")
    @Override
    public void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(46);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.colorGreyDark));

        final List<Keyboard.Key> keys = getKeyboard().getKeys();

        for (final Keyboard.Key key : keys) {

            if (!key.pressed) {
                setDrawable( ResourcesCompat.getDrawable(getResources(), R.drawable.bg_tecla_default, null));
            } else {
                setDrawable( ResourcesCompat.getDrawable(getResources(), R.drawable.bg_tecla_presionada, null));
            }

            if (key.codes[0] != KeyEvent.KEYCODE_EQUALS) {
                establecerColorEnClick(getDrawable(), key, canvas);
                canvas.drawText(String.valueOf(key.label), (key.x + (key.width /  2)), (key.y + 48), paint);
            }

            if (key.codes[0] == KeyEvent.KEYCODE_DEL) {
                establecerColorEnClick(getDrawable(), key, canvas);
                final Bitmap bmpDelete = obtenerBitmapdeVectorDrawable(mContext, R.drawable.ic_tecla_retroceso);
                canvas.drawBitmap(bmpDelete, (key.x + (key.width / 2) - 16),(key.y + ( key.height / 2) - bmpDelete.getHeight() / 2), paint);
            }

        }
    }

    @SuppressLint("ObsoleteSdkInt")
    public static Bitmap obtenerBitmapdeVectorDrawable(final Context context, final int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            assert drawable != null;
            drawable = DrawableCompat.wrap(drawable).mutate();
        }

        assert drawable != null;
        final Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private void establecerColorEnClick(final Drawable paramDrawable, final Keyboard.Key paramKey, final Canvas paramCanvas) {
        paramDrawable.setBounds(paramKey.x, paramKey.y, paramKey.x + paramKey.width, paramKey.y + paramKey.height);
        paramDrawable.draw(paramCanvas);
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(final Drawable drawable) {
        this.drawable = drawable;
    }
}
