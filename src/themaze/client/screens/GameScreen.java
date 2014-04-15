package themaze.client.screens;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;

import themaze.client.renderlayer.*;

public class GameScreen extends AbstractScreen {
	
	Image dir_down, dir_left, dir_right, dir_up, finish, floor, gate_closed, gate_open, gate_pl1, key, pl1, wall;
	
	int xSize = 0, ySize=0;
	
	public GameScreen (RenderLayer layer, int players) {
		super(layer);
		
		dir_down = new ImageIcon("lib/gui/dir_down.png").getImage();
		dir_left = new ImageIcon("lib/gui/dir_left.png").getImage();
		dir_right = new ImageIcon("lib/gui/dir_right.png").getImage();
		dir_up = new ImageIcon("lib/gui/dir_up.png").getImage();
		finish = new ImageIcon("lib/gui/finish.png").getImage();
		floor = new ImageIcon("lib/gui/floor.png").getImage();
		gate_closed = new ImageIcon("lib/gui/gate_closed.png").getImage();
		gate_open = new ImageIcon("lib/gui/gate_open.png").getImage();
		gate_pl1 = new ImageIcon("lib/gui/gate_pl1.png").getImage();
		key = new ImageIcon("lib/gui/key.png").getImage();
		pl1 = new ImageIcon("lib/gui/pl1.png").getImage();
		wall = new ImageIcon("lib/gui/wall.png").getImage();
	}

	@Override	
	public void render (Graphics g, byte[] input) {
		g.setColor(new Color(247,247,247));
		g.fillRect(0, 0, this.layer.getWidth(), this.layer.getHeight());
		
		g.setColor(Color.BLACK);
		g.drawString("Test", 20, 20);
		
		g.drawImage(pl1, 100, 100, layer);
		g.drawImage(dir_up, 100, 100, layer);
		
		System.out.println("okokoko");
		
		for (int x = 0; x < ySize; ++x)
		{
			for (int y = 0; y < xSize; ++y)
			{
				switch(input[x+y*ySize])
				{
					case 0:
						g.drawImage(floor, x*20, y*20, layer);
						break;
					case 1:
						g.drawImage(wall, x*20, y*20, layer);
						break;
					case 2:
						g.drawImage(gate_closed, x*20, y*20, layer);
						break;
					case 3:
						g.drawImage(gate_open, x*20, y*20, layer);
						break;
					case 4:
						g.drawImage(key, x*20, y*20, layer);
						break;
					case 5:
						g.drawImage(finish, x*20, y*20, layer);
						break;
					case 10:
						g.drawImage(pl1, x*20, y*20, layer);
						g.drawImage(dir_up, x*20, y*20, layer);
						break;
					case 11:
						g.drawImage(pl1, x*20, y*20, layer);
						g.drawImage(dir_right, x*20, y*20, layer);
						break;
					case 12:
						g.drawImage(pl1, x*20, y*20, layer);
						g.drawImage(dir_down, x*20, y*20, layer);
						break;
					case 13:
						g.drawImage(pl1, x*20, y*20, layer);
						g.drawImage(dir_left, x*20, y*20, layer);
						break;
					case 14:
						g.drawImage(gate_pl1, x*20, y*20, layer);
						g.drawImage(dir_up, x*20, y*20, layer);
						break;
					case 15:
						g.drawImage(gate_pl1, x*20, y*20, layer);
						g.drawImage(dir_right, x*20, y*20, layer);
						break;
					case 16:
						g.drawImage(gate_pl1, x*20, y*20, layer);
						g.drawImage(dir_down, x*20, y*20, layer);
						break;
					case 17:
						g.drawImage(gate_pl1, x*20, y*20, layer);
						g.drawImage(dir_up, x*20, y*20, layer);
						break;
				}
			}
		}
	}

	@Override
	public void update() {}

	public void setX(int x) {
		this.xSize = x;
	}

	public void setY(int y) {
		this.ySize = y;
	}	
}
