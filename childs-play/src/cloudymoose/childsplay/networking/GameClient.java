package cloudymoose.childsplay.networking;

import java.io.IOException;

import com.esotericsoftware.kryonet.Client;

public class GameClient extends NetworkPeer {

	public void connect() throws IOException {

		Client client = new Client();
		connection = client;

		registerMessages(client.getKryo());

		client.start();
		client.connect(5000, "localhost", 54555, 54777);

		UpdateRequest request = new UpdateRequest.Print("Here is the request");
		client.sendTCP(request);

		connection.addListener(new UpdateRequestListener());
	}
}
