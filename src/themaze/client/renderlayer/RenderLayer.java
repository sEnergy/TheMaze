package themaze.client.renderlayer;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.ImageIcon;

import themaze.client.Client;
import themaze.client.screens.AbstractScreen;
import themaze.client.screens.GameScreen;

// real

public class RenderLayer extends Canvas implements Runnable {

	private static final long serialVersionUID = 1L;
	private final Client frame;
	
	private Thread t;
	private boolean exit = false;

	int x =0, y = 0;
	
	//private SCREEN cScreen = SCREEN.MAIN_MENU;
	
	public RenderLayer (int w, int h, Client frame) {
		super();
		this.frame = frame;
		this.setSize(new Dimension(w, h));
		this.t = new Thread(this);
	}

	@Override
	public void run() {
		
		long lastOutputTime = System.currentTimeMillis(); // last info output time
		
		DataOutputStream dos = null;
		DataInputStream dis = null;
		
		try {
			Socket socket = new Socket ("localhost", 50000);
			dos = new DataOutputStream(socket.getOutputStream());
			dis = new DataInputStream(socket.getInputStream());
			dos.writeUTF("game");
			dos.writeUTF("test");
			dos.flush();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		int fps = 0;
		
		while (!exit)
		{
			if (true) // fake game choose
			{
				GameScreen gs  = new GameScreen(this, 3);
				this.render(gs);
				
				while (true) 
				{	
					try {
				
						switch (dis.readByte())
						{
							case 1:
								x = dis.readByte();
								y = dis.readByte();
								Dimension newDim = new Dimension (x*20,y*20);
								this.setSize(new Dimension (newDim));
								this.frame.setSize(new Dimension (newDim));
								gs.setX(x);
								gs.setY(y);
								break;
							case 2:
								byte[] input = new byte[x*y];
                                dis.read(input, 0, x*y);
								this.render(gs, input);
						}
						
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					fps++;

					
					if (System.currentTimeMillis() - lastOutputTime > 1000)
					{
						System.out.println("FPS:"+fps);
						
						lastOutputTime += 1000;
						fps = 0;
					}
				}
			}
		}	
		
		System.exit(0);
	}

	private void render (AbstractScreen as, byte[] input) {
		
		BufferStrategy buffer = this.getBufferStrategy();
		
		if (buffer == null)
		{
			System.out.println("Je to v piči");
			this.createBufferStrategy(3);
			return;
		}
		else
		{
			Graphics g = buffer.getDrawGraphics();
			as.render(g, input);
			g.dispose();
			
			buffer.show();
		}
	}
	
private void render (AbstractScreen as) {
		
		BufferStrategy buffer = this.getBufferStrategy();
		
		if (buffer == null)
		{
			System.out.println("Je to v piči - ok");
			this.createBufferStrategy(3);
			return;
		}
		else
		{
			Graphics g = buffer.getDrawGraphics();
			g.fillOval(x, y, y, x);
			g.dispose();
			
			buffer.show();
		}
	}

	public void start () {
		t.start();
	}
	
}
