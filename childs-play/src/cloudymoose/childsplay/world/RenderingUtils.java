package cloudymoose.childsplay.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

public class RenderingUtils {

	/**
	 * 
	 * @param sb
	 * @param texture
	 * @param maxWidth
	 * @param maxHeight
	 * @param drawPosition
	 * @param xAlign
	 *            see {@link Align}
	 * @param yAlign
	 *            see {@link Align}
	 */
	public static void drawScaledTexture(SpriteBatch sb, TextureRegion texture, float maxWidth, float maxHeight,
			Vector3 drawPosition, int xAlign, int yAlign) {

		float xRatio = maxWidth / texture.getRegionWidth();
		float yRatio = maxHeight / texture.getRegionHeight();

		float drawWidth, drawHeight;
		if (xRatio < yRatio) {
			drawWidth = maxWidth;
			drawHeight = texture.getRegionHeight() * xRatio;
		} else if (yRatio != Float.POSITIVE_INFINITY) {
			drawWidth = texture.getRegionWidth() * yRatio;
			drawHeight = maxHeight;
		} else {
			drawWidth = texture.getRegionWidth();
			drawHeight = texture.getRegionHeight();
		}

		// TODO: do clever stuff with bit operators
		float drawX, drawY;
		if (xAlign == Align.left) drawX = drawPosition.x - drawWidth;
		else if (xAlign == Align.right) drawX = drawPosition.x;
		else drawX = drawPosition.x - drawWidth / 2;

		if (yAlign == Align.top) drawY = drawPosition.y - drawHeight;
		else if (yAlign == Align.bottom) drawY = drawPosition.y;
		else drawY = drawPosition.y - drawHeight / 2;

		// Gdx.app.log("drawScaledTexture", drawWidth + " " + drawHeight + " at position: " + drawX + "," + drawY);

		sb.draw(texture, drawX, drawY, drawWidth, drawHeight);
	}

	public static void drawScaledTexture(SpriteBatch sb, TextureRegion texture, float maxWidth, float maxHeight,
			Vector3 drawPosition) {
		drawScaledTexture(sb, texture, maxWidth, maxHeight, drawPosition, Align.center, Align.center);
	}

}
