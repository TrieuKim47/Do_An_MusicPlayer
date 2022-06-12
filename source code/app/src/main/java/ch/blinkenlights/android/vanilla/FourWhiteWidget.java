

package ch.blinkenlights.android.vanilla;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.RemoteViews;

/**
 * 1x4 widget that shows title, artist, album art, a play/pause button, and a
 * previous/next button.
 */
public class FourWhiteWidget extends AppWidgetProvider {
	private static boolean sEnabled;

	@Override
	public void onEnabled(Context context)
	{
		sEnabled = true;
	}

	@Override
	public void onDisabled(Context context)
	{
		sEnabled = false;
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager manager, int[] ids)
	{
		Song song = null;
		int state = 0;

		if (PlaybackService.hasInstance()) {
			PlaybackService service = PlaybackService.get(context);
			song = service.getSong(0);
			state = service.getState();
		}

		sEnabled = true;
		updateWidget(context, manager, song, state);
	}

	/**
	 * Check if there are any instances of this widget placed.
	 */
	public static void checkEnabled(Context context, AppWidgetManager manager)
	{
		sEnabled = manager.getAppWidgetIds(new ComponentName(context, FourWhiteWidget.class)).length != 0;
	}

	/**
	 * Populate the widgets with the given ids with the given info.
	 *
	 * @param context A Context to use.
	 * @param manager The AppWidgetManager that will be used to update the
	 * widget.
	 * @param song The current Song in PlaybackService.
	 * @param state The current PlaybackService state.
	 */
	public static void updateWidget(Context context, AppWidgetManager manager, Song song, int state)
	{
		if (!sEnabled)
			return;

		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.four_white_widget);

		if ((state & PlaybackService.FLAG_NO_MEDIA) != 0) {
			views.setViewVisibility(R.id.play_pause, View.INVISIBLE);
			views.setViewVisibility(R.id.previous, View.INVISIBLE);
			views.setViewVisibility(R.id.next, View.INVISIBLE);
			views.setViewVisibility(R.id.title, View.INVISIBLE);
			views.setInt(R.id.artist, "setText", R.string.no_songs);
			views.setViewVisibility(R.id.cover, View.INVISIBLE);
		} else if (song == null) {
			views.setViewVisibility(R.id.play_pause, View.VISIBLE);
			views.setViewVisibility(R.id.previous, View.VISIBLE);
			views.setViewVisibility(R.id.next, View.VISIBLE);
			views.setViewVisibility(R.id.title, View.INVISIBLE);
			views.setInt(R.id.artist, "setText", R.string.app_name);
			views.setViewVisibility(R.id.cover, View.INVISIBLE);
		} else {
			views.setViewVisibility(R.id.play_pause, View.VISIBLE);
			views.setViewVisibility(R.id.previous, View.VISIBLE);
			views.setViewVisibility(R.id.next, View.VISIBLE);
			views.setViewVisibility(R.id.title, View.VISIBLE);
			views.setTextViewText(R.id.title, song.title);
			views.setTextViewText(R.id.artist, song.artist);
			Bitmap cover = song.getMediumCover(context);
			if (cover == null) {
				views.setViewVisibility(R.id.cover, View.INVISIBLE);
			} else {
				views.setViewVisibility(R.id.cover, View.VISIBLE);
				views.setImageViewBitmap(R.id.cover, cover);
			}
		}

		boolean playing = (state & PlaybackService.FLAG_PLAYING) != 0;
		views.setImageViewResource(R.id.play_pause, playing ? R.drawable.widget_pause : R.drawable.widget_play);

		Intent intent;
		PendingIntent pendingIntent;
		int flags = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME;

		intent = new Intent(context, LibraryActivity.class).setAction(Intent.ACTION_MAIN);
		pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		views.setOnClickPendingIntent(R.id.cover, pendingIntent);
		views.setOnClickPendingIntent(R.id.text_layout, pendingIntent);

		intent = ShortcutPseudoActivity.getIntent(context, PlaybackService.ACTION_TOGGLE_PLAYBACK);
		pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		views.setOnClickPendingIntent(R.id.play_pause, pendingIntent);

		intent = ShortcutPseudoActivity.getIntent(context, PlaybackService.ACTION_NEXT_SONG);
		pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		views.setOnClickPendingIntent(R.id.next, pendingIntent);

		intent = ShortcutPseudoActivity.getIntent(context, PlaybackService.ACTION_PREVIOUS_SONG);
		pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		views.setOnClickPendingIntent(R.id.previous, pendingIntent);

		manager.updateAppWidget(new ComponentName(context, FourWhiteWidget.class), views);
	}
}
