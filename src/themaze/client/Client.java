package themaze.client;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import themaze.client.renderlayer.RenderLayer;

public class Client extends JFrame {

	private static final long serialVersionUID = 4704860925089425366L;
	public static final String GAME_NAME = "The Maze - alfa version";

	public static void main(String[] args) {
		Client  core = new Client();
		core.init();
	}
	
	private void init () {
		RenderLayer layer = new RenderLayer(300, 300, this);
		this.add(layer);
		this.pack();
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());
		this.setVisible(true);
		this.setTitle(GAME_NAME);
		
		layer.start();
	}

}
