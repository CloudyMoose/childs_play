package cloudymoose.childsplay;

import java.io.IOException;

import android.os.Bundle;
import cloudymoose.childsplay.networking.GameClient;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class MainActivity extends AndroidApplication {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		GameClient client = new GameClient();
		try {
			client.connect();
		} catch (IOException e) {
			throw new RuntimeException("Unable to connect to the server", e);
		}

		AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
		cfg.useGL20 = false;

		initialize(ChildsPlayGame.createInstance(client), cfg);
	}
}