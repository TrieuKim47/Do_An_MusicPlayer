

package ch.blinkenlights.android.vanilla;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import com.mobeta.android.dslv.DragSortListView;

/**
 * The preferences activity in which one can change application preferences.
 */
public class TabOrderActivity extends Activity
	implements View.OnClickListener,
	           OnItemClickListener,
	           DragSortListView.DropListener
{
	private TabOrderAdapter mAdapter;
	private DragSortListView mList;

	/**
	 * Initialize the activity, loading the preference specifications.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		ThemeHelper.setTheme(this, R.style.BackActionBar);
		super.onCreate(savedInstanceState);

		setTitle(R.string.tabs);
		setContentView(R.layout.tab_order);

		mAdapter = new TabOrderAdapter(this);
		DragSortListView list = (DragSortListView)findViewById(R.id.list);
		list.setAdapter(mAdapter);
		list.setOnItemClickListener(this);
		list.setDropListener(this);
		mList = list;
		load();

		findViewById(R.id.done).setOnClickListener(this);
		findViewById(R.id.restore_default).setOnClickListener(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClick(View view)
	{
		switch (view.getId()) {
		case R.id.done:
			finish();
			break;
		case R.id.restore_default:
			restoreDefault();
			break;
		}
	}

	/**
	 * Restore the default tab order and visibility.
	 */
	public void restoreDefault()
	{
		mAdapter.setTabIds(LibraryPagerAdapter.DEFAULT_TAB_ORDER.clone());
		DragSortListView list = mList;
		for (int i = 0; i != LibraryPagerAdapter.MAX_ADAPTER_COUNT; ++i) {
			list.setItemChecked(i, LibraryPagerAdapter.DEFAULT_TAB_VISIBILITY[i]);
		}
		save();
	}

	/**
	 * Save tab order and visibility to SharedPreferences as a string.
	 */
	public void save()
	{
		int[] ids = mAdapter.getTabIds();
		DragSortListView list = mList;
		char[] out = new char[LibraryPagerAdapter.MAX_ADAPTER_COUNT];
		for (int i = 0; i != LibraryPagerAdapter.MAX_ADAPTER_COUNT; ++i) {
			out[i] = (char)(list.isItemChecked(i) ? 128 + ids[i] : 127 - ids[i]);
		}

		SharedPreferences.Editor editor = SharedPrefHelper.getSettings(this).edit();
		editor.putString(PrefKeys.TAB_ORDER, new String(out));
		editor.apply();
	}

	/**
	 * Load tab order settings from SharedPreferences and apply it to the
	 * activity.
	 */
	public void load()
	{
		String in = SharedPrefHelper.getSettings(this).getString(PrefKeys.TAB_ORDER, PrefDefaults.TAB_ORDER);
		if (in != null && in.length() == LibraryPagerAdapter.MAX_ADAPTER_COUNT) {
			char[] chars = in.toCharArray();
			int[] ids = new int[LibraryPagerAdapter.MAX_ADAPTER_COUNT];
			for (int i = 0; i != LibraryPagerAdapter.MAX_ADAPTER_COUNT; ++i) {
				int v = chars[i];
				v = v < 128 ? -(v - 127) : v - 128;
				if (v >= MediaUtils.TYPE_COUNT) {
					ids = null;
					break;
				}
				ids[i] = v;
			}

			if (ids != null) {
				mAdapter.setTabIds(ids);
				DragSortListView list = mList;
				for (int i = 0; i != LibraryPagerAdapter.MAX_ADAPTER_COUNT; ++i) {
					list.setItemChecked(i, chars[i] >= 128);
				}
			}

			return;
		}

		restoreDefault();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		save();
	}

	/**
	 * Fired from adapter listview  if user moved an item
	 * @param from the item index that was dragged
	 * @param to the index where the item was dropped
	 */
	@Override
	public void drop(int from, int to) {
		if (from == to)
			return;

		int[] ids = mAdapter.getTabIds();
		int tempId = ids[from];

		if (from > to) {
			System.arraycopy(ids, to, ids, to + 1, from - to);
		} else {
			System.arraycopy(ids, from + 1, ids, from, to - from);
		}

		ids[to] = tempId;
		save();
		mAdapter.notifyDataSetChanged();
		// no need to update the copy in mAdapter: We worked on a reference
	}


}
