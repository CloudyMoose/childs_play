package cloudymoose.childsplay.networking;

import cloudymoose.childsplay.world.Command;

import com.esotericsoftware.kryo.Kryo;

public abstract class NetworkUtils {

	private NetworkUtils() {
	}

	/** Must be called before any network traffic. */
	public static void registerMessages(Kryo kryo) {
		kryo.register(UpdateRequest.Print.class);
		kryo.register(UpdateRequest.Init.class);
		kryo.register(UpdateRequest.Ack.class);
		kryo.register(UpdateRequest.StartTurn.class);
		kryo.register(Command[].class);
		kryo.register(Command.Move.class);
	}
}
