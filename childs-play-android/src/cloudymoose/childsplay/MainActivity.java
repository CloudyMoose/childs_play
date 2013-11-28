package cloudymoose.childsplay;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class MainActivity extends AndroidApplication implements NotificationService {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
		cfg.useGL20 = false;

		initialize(new ChildsPlayGame(this), cfg);

	}

	@Override
	protected void onResume() {
		((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(0);
		super.onResume();
	}

	@Override
	public void notifySomething() {
		Gdx.app.log("MainActivity", "Should notify something");
		// prepare intent which is triggered if the notification is selected

		Intent intent = new Intent(this, MainActivity.class);
		intent.setAction(Intent.ACTION_MAIN);
		// intent.setAction(Intent.ACTION_USER_FOREGROUND); // Requires API 17
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

		// build notification
		Notification n = new Notification.Builder(this).setContentTitle("You can play now!").setContentText("")
				.setSmallIcon(R.drawable.ic_launcher).setContentIntent(pIntent).setAutoCancel(true).build();

		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		notificationManager.notify(0, n);
	}
}