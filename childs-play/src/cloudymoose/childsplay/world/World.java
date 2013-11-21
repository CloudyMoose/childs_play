package cloudymoose.childsplay.world;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import cloudymoose.childsplay.ChildsPlayGame;
import cloudymoose.childsplay.networking.NetworkPeer;
import cloudymoose.childsplay.networking.UpdateRequest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;

public class World {

	List<Player> players;
	WorldMap map;

	private final Queue<UpdateRequest> incomingUpdateRequests;
	private final Queue<UpdateRequest> outgoingUpdateRequests;
	private final NetworkPeer networkPeer = ChildsPlayGame.instance.networkPeer;

	private LocalPlayer localPlayer;

	/** Singletonized for easy access in other threads and stuff */
	private static World INSTANCE;

	private static final String TAG = "World";

	protected World() {
		incomingUpdateRequests = new LinkedList<UpdateRequest>();
		outgoingUpdateRequests = new LinkedList<UpdateRequest>();
	}

	public static World getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new World();
			// Local player id should be used to create the local player from the right
			// item in the list of players
			INSTANCE.createDemoWorld();
		}
		return INSTANCE;
	}

	public void addIncomingUpdateRequest(UpdateRequest request) {
		synchronized (incomingUpdateRequests) {
			incomingUpdateRequests.add(request);
		}
	}

	public void processIncomingUpdateRequests() {
		synchronized (incomingUpdateRequests) {
			while (!incomingUpdateRequests.isEmpty()) {
				UpdateRequest request = incomingUpdateRequests.remove();
				Gdx.app.log(TAG, request.toString());
			}
		}
	}

	public void addOutgoingUpdateRequest(UpdateRequest request) {
		synchronized (outgoingUpdateRequests) {
			outgoingUpdateRequests.add(request);
		}
	}

	public void sendUpdateRequests() {
		synchronized (outgoingUpdateRequests) {
			if (!outgoingUpdateRequests.isEmpty()) {
				networkPeer.send(outgoingUpdateRequests);
				outgoingUpdateRequests.clear();
			}
		}
	}

	public void registerUpdate() {
		synchronized (outgoingUpdateRequests) {
			// TODO: create update request and add it to the list.
		}
	}

	private void createDemoWorld() {
		map = new WorldMap();

		localPlayer = new LocalPlayer(1);
		localPlayer.units.add(new Child(localPlayer, 10, 10));
		localPlayer.units.add(new Child(localPlayer, -10, 10));
		localPlayer.units.add(new Child(localPlayer, 10, -10));
		localPlayer.units.add(new Child(localPlayer, -10, -10));

		players = new ArrayList<Player>();
		players.add(localPlayer);
	}

	public LocalPlayer getLocalPlayer() {
		return localPlayer;
	}

	public void fixedUpdate(float dt) {
		for (Player p : players) {
			for (Unit u : p.units) {
				u.update(dt);
			}
		}
	}

	public Unit hit(Vector3 worldCoordinates) {
		// TODO: change to a better way to look for the clicked unit (using map areas or something similar)
		for (Player p : players) {
			for (Unit u : p.units) {
				if (u.isHit(worldCoordinates)) {
					return u;
				}
			}
		}
		return null;
	}
}
