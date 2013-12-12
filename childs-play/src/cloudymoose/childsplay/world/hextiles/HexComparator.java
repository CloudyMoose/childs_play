package cloudymoose.childsplay.world.hextiles;

import java.util.Comparator;

public class HexComparator implements Comparator<HexTile<?>> {

	@Override
	public int compare(HexTile<?> o1, HexTile<?> o2) {
		int diff = (int) (o2.getPosition().y - o1.getPosition().y);

		if (diff != 0)
			return diff;

		diff = o1.q - o2.q;

		if (diff != 0)
			return diff;

		return o1.r - o2.r;
	}
}
