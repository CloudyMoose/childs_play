package cloudymoose.childsplay.networking;

import cloudymoose.childsplay.world.commands.AttackCommand;
import cloudymoose.childsplay.world.commands.CollectCommand;
import cloudymoose.childsplay.world.commands.Command;
import cloudymoose.childsplay.world.commands.MoveCommand;
import cloudymoose.childsplay.world.commands.RecruitCommand;

import com.esotericsoftware.kryo.Kryo;

public abstract class NetworkUtils {

	public static int LAST_TURN = Integer.MAX_VALUE;
	public static int IDGAF_TURN = -1;

	private NetworkUtils() {
	}

	/** Must be called before any network traffic. */
	public static void registerMessages(Kryo kryo) {
		kryo.register(Message.Print.class);
		kryo.register(Message.Init.class);
		kryo.register(Message.Ack.class);
		kryo.register(Message.TurnRecap.class);
		kryo.register(Message.EndGame.class);
		kryo.register(Command[].class);
		kryo.register(int[].class);
		kryo.register(MoveCommand.class);
		kryo.register(AttackCommand.class);
		kryo.register(RecruitCommand.class);
		kryo.register(CollectCommand.class);
	}
}
