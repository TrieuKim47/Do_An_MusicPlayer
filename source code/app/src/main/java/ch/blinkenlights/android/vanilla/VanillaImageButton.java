

package ch.blinkenlights.android.vanilla;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageButton;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;

import androidx.annotation.Nullable;


public class VanillaImageButton extends ImageButton {

	private Context mContext;
	private static int mNormalTint;
	private static int mActiveTint;
	/**
	 * The paint used to draw this buttons circle.
	 */
	private @Nullable Paint mCirclePaint;

	public VanillaImageButton(Context context) {
		this(context, null);
	}

	public VanillaImageButton(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public VanillaImageButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		mNormalTint = fetchAttrColor(R.attr.controls_normal);
		mActiveTint = fetchAttrColor(R.attr.controls_active);

		if (attrs != null) {
			TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.VanillaImageButton, 0, 0);
			final int color = a.getColor(R.styleable.VanillaImageButton_backgroundCircleColor, 0);
			a.recycle();

			mCirclePaint = new Paint();
			mCirclePaint.setColor(color);
			mCirclePaint.setAntiAlias(true);
		}

		updateImageTint(-1);
	}

	@Override
	public void setImageResource(int resId) {
		super.setImageResource(resId);
		this.updateImageTint(resId);
	}

	@Override
	public void onDraw(Canvas canvas) {
		if (mCirclePaint != null) {
			// Draw a circle on the background, but only
			// if the color is set.
			final int x = getWidth() / 2;
			final int y = getHeight() / 2;
			final float r = (x > y ? y : x);

			canvas.drawCircle(x, y, r*0.80f, mCirclePaint);
		}
		super.onDraw(canvas);
	}

	private void updateImageTint(int resHint) {
		int filterColor = mNormalTint;

		switch (resHint) {
			case R.drawable.repeat_active:
			case R.drawable.repeat_current_active:
			case R.drawable.stop_current_active:
			case R.drawable.shuffle_active:
			case R.drawable.shuffle_album_active:
			case R.drawable.random_active:
				filterColor = mActiveTint;
		}

		this.setColorFilter(filterColor);
	}

	private int fetchAttrColor(int attr) {
		TypedValue typedValue = new TypedValue();
		TypedArray a = mContext.obtainStyledAttributes(typedValue.data, new int[] { attr });
		int color = a.getColor(0, 0);
		a.recycle();
		return color;
	}

}
