package com.sunhapper.x.spedit.gif.span;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.style.ImageSpan;

public abstract class IsoheightImageSpan extends ImageSpan  {
    private boolean resized = false;
    private static final char[] ELLIPSIS_NORMAL = {'\u2026'}; // this is "..."
    private static final char[] ELLIPSIS_TWO_DOTS = {'\u2025'}; // this is ".."
    private int drawableHeight = 0;
    private FontMetricsInt fm;

    public IsoheightImageSpan(Drawable d) {
        super(d);
    }

    public IsoheightImageSpan(Context context, Uri uri) {
        super(context, uri);
    }

    @Override
    public int getSize(Paint paint, CharSequence text,
            int start, int end, FontMetricsInt fm) {
        if (fm != null) {
            this.fm = fm;
            drawableHeight = fm.descent - fm.ascent;
        }
        Drawable drawable = getResizedDrawable();
        Rect bounds = drawable.getBounds();
        return bounds.right;
    }

    protected Drawable getResizedDrawable() {
        Drawable d = getDrawable();
        if (drawableHeight == 0) {
            return d;
        }
        if (!resized) {
            resized = true;
            d.setBounds(new Rect(0, 0,
                    (int) (1f * drawableHeight * d.getIntrinsicWidth() / d.getIntrinsicWidth()),
                    drawableHeight));
        }
        return d;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y,
            int bottom, Paint paint) {
        final String s = text.toString();
        String subS = s.substring(start, end);
        if (ELLIPSIS_NORMAL[0] == subS.charAt(0)
                || ELLIPSIS_TWO_DOTS[0] == subS.charAt(0)) {
            canvas.save();
            canvas.drawText(subS, x, y, paint);
            canvas.restore();
        } else {
            Drawable d = getResizedDrawable();
            canvas.save();
            int transY;
            fm = paint.getFontMetricsInt();
            transY = y + fm.ascent;
            canvas.translate(x, transY);
            d.draw(canvas);
            canvas.restore();
        }
    }


}