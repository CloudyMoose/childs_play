package cloudymoose.childsplay.networking;

import java.util.Queue;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;

public abstract class NetworkPeer {

	protected Connection connection;

	/** Must be called before any network traffic. */
	protected void registerMessages(Kryo kryo) {
		kryo.register(UpdateRequest[].class);
		kryo.register(UpdateRequest.Print.class);
		kryo.register(UpdateRequest.Move.class);
		// kryo.register(SomeResponse.class);
	}

	public void send(Queue<UpdateRequest> outgoingUpdateRequests) {
		connection.sendTCP(outgoingUpdateRequests.toArray(new UpdateRequest[outgoingUpdateRequests.size()]));
	}

}
