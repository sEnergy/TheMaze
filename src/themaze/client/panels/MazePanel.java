package themaze.client.panels;

import themaze.Position;
import themaze.client.Mobile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

public class MazePanel extends JPanel
{
    private final Map<Integer, Image> images = new HashMap<>();
    private int color, rows, columns;
    private byte[] data;
    private Map<Integer, Mobile> mobiles = new HashMap<>();
    private Map<Integer, Mobile> corpses = new HashMap<>();
    private GameState state;

    public MazePanel()
    {
        super();

        images.put(0, new ImageIcon("lib/floor.png").getImage());
        images.put(1, new ImageIcon("lib/wall.png").getImage());
        images.put(2, new ImageIcon("lib/gate_closed.png").getImage());
        images.put(3, new ImageIcon("lib/gate_open.png").getImage());
        images.put(4, new ImageIcon("lib/key.png").getImage());
        images.put(5, new ImageIcon("lib/finish.png").getImage());

        images.put(10, new ImageIcon("lib/pl1.png").getImage());
        images.put(20, new ImageIcon("lib/pl1.png").getImage());
        images.put(30, new ImageIcon("lib/pl1.png").getImage());
        images.put(40, new ImageIcon("lib/pl1.png").getImage());

        images.put(100, new ImageIcon("lib/dir_up.png").getImage());
        images.put(101, new ImageIcon("lib/dir_right.png").getImage());
        images.put(102, new ImageIcon("lib/dir_down.png").getImage());
        images.put(103, new ImageIcon("lib/dir_left.png").getImage());

        setToolTipText("");
    }

    public boolean isReady() { return state == GameState.Started; }

    public void setMaze(byte color, byte rows, byte columns, byte[] data)
    {
        this.color = color * 10;
        this.rows = rows;
        this.columns = columns;
        this.data = data;
        this.mobiles.clear();
        state = GameState.Init;
        setPreferredSize(new Dimension(columns * 20, rows * 20));
        repaint();
    }

    public void start()
    {
        state = GameState.Started;
        System.out.println("Game started.");
    }

    public void finish(boolean winner)
    {
        state = winner ? GameState.Won : GameState.Lost;
        repaint();
    }

    public void change(int row, int column, byte newData)
    {
        if (newData < 10)
            data[column + row * columns] = newData;
        else
        {
            int dir = newData < 50 ? newData % 10 : 0;
            int m = newData - dir;

            if (row < 0 || column < 0)
                mobiles.remove(m);
            else
            {
                Mobile mobile = mobiles.get(m);
                if (mobile == null)
                    mobiles.put(m, mobile = new Mobile());
                mobile.setDirection(dir);
                mobile.setPosition(row, column);
            }
        }
        repaint();
    }

    public void onInfo(int data, int steps)
    {
        boolean dead = data < 0;
        data = Math.abs(data);
        int dir = data < 50 ? data % 10 : 0;
        int m = data - dir;

        Mobile mobile;
        if (dead)
        {
            mobile = mobiles.remove(m);
            corpses.put(m, mobile);
            if (m == color)
                state = GameState.Killed;
            else
                System.out.printf("Player %d has been killed.\n", m / 10);
        }
        else
            mobile = mobiles.get(m);

        mobile.setInfo(steps);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if (data == null || data.length != rows * columns)
            return;

        for (int r = 0; r < rows; r++)
            for (int c = 0; c < columns; c++)
            {
                int i = data[c + r * columns];
                g.drawImage(images.get(i), c * 20, r * 20, null);
            }

        for (Map.Entry<Integer, Mobile> entry : mobiles.entrySet())
        {
            Mobile mobile = entry.getValue();
            int r = mobile.getPosition().row;
            int c = mobile.getPosition().column;
            int m = entry.getKey();
            if (m >= 50)
            {
                g.setColor(Color.RED);
                g.drawOval(c * 20, r * 20, 20, 20);
            }
            else
            {
                g.drawImage(images.get(m), c * 20, r * 20, null);
                g.drawImage(images.get(100 + mobile.getDirection().ordinal()), c * 20, r * 20, null);
            }
            if (m == color)
            {
                g.setColor(Color.RED);
                g.fillOval(c * 20 + 9, r * 20 + 9, 2, 2);
            }
        }

        if (state == GameState.Killed)
            drawStatus(g, "You have been killed!", Color.RED);
        else if (state == GameState.Won)
            drawStatus(g, "You have won!", Color.GREEN);
        else if (state == GameState.Lost)
            drawStatus(g, "You have lost.", Color.YELLOW);
    }

    private void drawStatus(Graphics g, String str, Color color)
    {
        g.setColor(color);
        g.setFont(g.getFont().deriveFont(Font.BOLD, 20));
        FontMetrics fm = g.getFontMetrics();
        Rectangle2D r = fm.getStringBounds(str, g);
        int x = (columns * 20 - (int) r.getWidth()) / 2;
        int y = (rows * 20 - (int) r.getHeight()) / 2 + fm.getAscent();
        g.drawString(str, x, y);
    }

    @Override
    public String getToolTipText(MouseEvent event)
    {
        for (Map.Entry<Integer, Mobile> entry : mobiles.entrySet())
            if (entry.getKey() < 50)
            {
                Mobile mobile = entry.getValue();
                Position position = new Position(event.getY() / 20, event.getX() / 20);
                if (position.equals(mobile.getPosition()))
                    return mobile.toString();
            }

        for (Map.Entry<Integer, Mobile> entry : corpses.entrySet())
        {
            Mobile mobile = entry.getValue();
            Position position = new Position(event.getY() / 20, event.getX() / 20);
            if (position.equals(mobile.getPosition()))
                return mobile.toString();
        }
        return null;
    }

    private enum GameState
    {
        Init, Started, Killed, Won, Lost
    }
}
