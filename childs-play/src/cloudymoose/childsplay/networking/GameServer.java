package cloudymoose.childsplay.networking;

import java.io.IOException;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class GameServer extends NetworkPeer {

	private Server server;

	public void start() throws IOException {
		server = new Server();

		registerMessages(server.getKryo());

		server.start();
		server.bind(54555, 54777);

		server.addListener(new Listener() {
			@Override
			public void connected(Connection connection) {
				GameServer.this.connection = connection;
				connection.addListener(new UpdateRequestListener());
			}

		});
	}
}
