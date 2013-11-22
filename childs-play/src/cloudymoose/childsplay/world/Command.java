package cloudymoose.childsplay.world;

import java.awt.Point;

import cloudymoose.childsplay.networking.UpdateRequest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public abstract class Command extends UpdateRequest {

	public abstract Runner execute(World world);

	public static abstract class Runner {
		public final Command command;
		protected boolean running = false;

		public Runner(Command command) {
			this.command = command;
		}

		public void start() {
			running = true;
		}

		public boolean run(float dt) {
			if (running) {
				running = update(dt);
			}
			Gdx.app.log("Command:", running ? "running" : "stopping");
			return running;
		}

		protected abstract boolean update(float dt);
	}

	public static class Move extends Command {
		public final int unitId;
		public final float destX;
		public final float destY;

		protected Move() {
			this(0, 0, 0);
		}

		public Move(int unitId, float destX, float destY) {
			super();
			this.unitId = unitId;
			this.destX = destX;
			this.destY = destY;
		}

		@Override
		public String toString() {
			return String.format("Move %d to (%f,%f)", unitId, destX, destY);
		}

		@Override
		public Runner execute(World world) {
			return new MoveRunner(this, world);
		}

		public static class MoveRunner extends Runner {
			private Point destination;
			private Vector2 currentMovement;
			private Unit unit;
			protected final float POSITION_EPSILON = 5f; // Position accuracy

			public MoveRunner(Move command, World world) {
				super(command);
				this.unit = world.getUnit(command.unitId);
				this.destination = new Point((int) command.destX, (int) command.destY);

				// TODO replace with pathfinding
				// angle between the line made by the 2 points and the x-axis
				double angle = Math.atan2((command.destY - unit.position.y), command.destX - unit.position.x);
				currentMovement = new Vector2((float) (unit.movementSpeed * Math.cos(angle)),
						(float) (unit.movementSpeed * Math.sin(angle)));
			}

			@Override
			protected boolean update(float dt) {
				unit.move(currentMovement);
				Gdx.app.log("MR", unit.position.toString());

				return !(Math.abs(unit.position.x - destination.x) < POSITION_EPSILON && Math.abs(unit.position.y
						- destination.y) < POSITION_EPSILON);

			}
		}

	}
}
