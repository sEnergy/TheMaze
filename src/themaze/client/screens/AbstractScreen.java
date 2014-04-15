package themaze.client.screens;

import java.awt.Graphics;
import themaze.client.renderlayer.*;

public abstract class AbstractScreen {
	
	protected RenderLayer layer;
	
	public enum SCREEN {
		MENU,
		GAME
	}
	
	public AbstractScreen (RenderLayer layer) {
		this.layer = layer;
	}
	
	public abstract void render (Graphics g, byte[] input);
	public abstract void update ();
}
