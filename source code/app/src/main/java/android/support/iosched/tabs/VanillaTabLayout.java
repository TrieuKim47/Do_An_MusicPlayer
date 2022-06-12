

package android.support.iosched.tabs;

import android.app.ActionBar;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Simple wrapper for SlidingTabLayout which takes
 * care of setting sane per-platform defaults
 */
public class VanillaTabLayout extends SlidingTabLayout {

	public VanillaTabLayout(Context context) {
		this(context, null);
	}

	public VanillaTabLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public VanillaTabLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setSelectedIndicatorColors(context.getResources().getColor(ch.blinkenlights.android.vanilla.R.color.tabs_active_indicator));
		setDistributeEvenly(false);
	}

	/**
	 * Overrides the default text color
	 */
	@Override
	protected TextView createDefaultTabView(Context context) {
		TextView view = super.createDefaultTabView(context);
		view.setTextColor(getResources().getColorStateList(ch.blinkenlights.android.vanilla.R.color.tab_text_selector));
		view.setBackgroundResource(ch.blinkenlights.android.vanilla.R.drawable.unbound_ripple_light);
		view.setMaxLines(1);
		view.setEllipsize(TextUtils.TruncateAt.END);
		view.setTextSize(14);
		return view;
	}

}
