package cloudymoose.childsplay.networking;

import cloudymoose.childsplay.world.World;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage.KeepAlive;
import com.esotericsoftware.kryonet.Listener;

public class UpdateRequestListener extends Listener {
	@Override
	public void received(Connection connection, Object object) {
		if (object instanceof KeepAlive) {
			return; // They are sent automatically by the library
		}

		if (object instanceof UpdateRequest[]) {
			for (UpdateRequest request : (UpdateRequest[]) object) {
				onRequestReceived(request);
			}
		} else if (object instanceof UpdateRequest) {
			onRequestReceived((UpdateRequest) object);
		} else {
			System.err.println(connection.toString() + " received: " + object.toString());
		}
	}

	private void onRequestReceived(UpdateRequest request) {
		World.getInstance().addIncomingUpdateRequest(request);
	}
}
