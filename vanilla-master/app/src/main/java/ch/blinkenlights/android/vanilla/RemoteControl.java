

package ch.blinkenlights.android.vanilla;

import android.content.Context;
import android.os.Build;


public class RemoteControl {

	/**
	 * Returns a RemoteControl.Client implementation
	 */
	public RemoteControl.Client getClient(Context context) {
		return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ?
			new RemoteControlImplLp(context) :
			new RemoteControlImplICS(context)  // legacy implementation, kept until we drop 4.x support
		);
	}

	/**
	 * Interface definition of our RemoteControl API
	 */
	public interface Client {
		public void initializeRemote();
		public void unregisterRemote();
		public void reloadPreference();
		public void updateRemote(Song song, int state, boolean keepPaused);
	}
}
