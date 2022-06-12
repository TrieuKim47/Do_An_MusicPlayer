

package ch.blinkenlights.android.vanilla;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.BaseAdapter;

/**
 * CursorAdapter backed by MediaStore playlists.
 */
public class TabOrderAdapter extends BaseAdapter {
	private final TabOrderActivity mActivity;
	private final LayoutInflater mInflater;
	private int[] mTabIds;

	/**
	 * Create a tab order adapter.
	 *
	 * @param activity The activity that will own this adapter. The activity
	 * will be notified when items have been moved.
	 */
	public TabOrderAdapter(TabOrderActivity activity)
	{
		mActivity = activity;
		mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/**
	 * Set the array containing the order of tab ids.
	 */
	public void setTabIds(int[] ids)
	{
		mTabIds = ids;
		notifyDataSetChanged();
	}

	/**
	 * Returns the array containing the order of tab ids. Do not modify the array.
	 */
	public int[] getTabIds()
	{
		return mTabIds;
	}

	@Override
	public int getCount()
	{
		return LibraryPagerAdapter.MAX_ADAPTER_COUNT;
	}

	@Override
	public Object getItem(int position)
	{
		return null;
	}

	@Override
	public long getItemId(int position)
	{
		return mTabIds[position];
	}

	@Override
	public View getView(int position, View convert, ViewGroup parent)
	{
		DraggableRow view;
		if (convert == null) {
			view = (DraggableRow)mInflater.inflate(R.layout.draggable_row, parent, false);
			view.setupLayout(DraggableRow.LAYOUT_CHECKBOXES);
		} else {
			view = (DraggableRow)convert;
		}
		view.setText(mActivity.getResources().getText(LibraryPagerAdapter.TITLES[mTabIds[position]]));
		return view;
	}

	@Override
	public boolean hasStableIds()
	{
		return true;
	}
}