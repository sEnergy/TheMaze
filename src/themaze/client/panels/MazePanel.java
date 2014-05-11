package themaze.client.panels;

import themaze.Position;
import themaze.Position.Direction;
import themaze.client.MainFrame;
import themaze.client.Mobile;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Panel vykreslující bludiště.
 *
 * @author Jaroslav Kubík
 * @author Marcel Fiala
 */
public class MazePanel extends JPanel implements ActionListener
{
    private final Map<Integer, Image> images = new HashMap<>();
    private final Map<Integer, Mobile> mobiles = new HashMap<>();
    private final Map<Integer, Mobile> corpses = new HashMap<>();
    private final Timer timer = new Timer(1000, this);
    private long start;
    private int color, rows, columns;
    private byte[] data;
    private GameState state;

    public MazePanel()
    {
        super();

        images.put(0, new ImageIcon("floor.png").getImage());
        images.put(1, new ImageIcon("wall.png").getImage());
        images.put(2, new ImageIcon("gate_closed.png").getImage());
        images.put(3, new ImageIcon("gate_open.png").getImage());
        images.put(4, new ImageIcon("key.png").getImage());
        images.put(5, new ImageIcon("finish.png").getImage());

        images.put(10, new ImageIcon("pl1.png").getImage());
        images.put(20, new ImageIcon("pl2.png").getImage());
        images.put(30, new ImageIcon("pl3.png").getImage());
        images.put(40, new ImageIcon("pl4.png").getImage());
        images.put(50, new ImageIcon("guard.png").getImage());

        setToolTipText("");
        timer.setInitialDelay(0);
    }

    public boolean isReady() { return state == GameState.Started; }

    /**
     * Nastavení parametrů bludiště.
     */
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
        start = System.currentTimeMillis();
        timer.restart();
        System.out.println("Game started.");
    }

    public void finish(byte winner)
    {
        if (state == GameState.Started)
            state = winner / 10 == color / 10 ? GameState.Won : GameState.Lost;
        timer.stop();
        repaint();
    }

    public void change(Position pos, byte newData)
    {
        if (newData < 10)
            data[pos.column + pos.row * columns] = newData;
        else
        {
            int dir = newData < 50 ? newData % 10 : 0;
            int m = newData - dir;

            if (pos.isValid())
            {
                Mobile mobile = mobiles.get(m);
                if (mobile == null)
                    mobiles.put(m, mobile = new Mobile());
                mobile.setDirection(dir);
                mobile.setPosition(pos);
            }
            else
                mobiles.remove(m);
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

        mobile.setInfo(steps, System.currentTimeMillis() - start);
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
            int x = mobile.getPosition().column * 20;
            int y = mobile.getPosition().row * 20;
            int m = entry.getKey();
            if (m < 50)
            {
                g.drawImage(images.get(m), x, y, null);
                drawDirection(g, m, x, y, mobile.getDirection());
            }
            else
                g.drawImage(images.get(50), x, y, null);
        }

        for (Map.Entry<Integer, Mobile> corpse : corpses.entrySet())
        {
            Mobile mobile = corpse.getValue();
            int x = mobile.getPosition().column * 20;
            int y = mobile.getPosition().row * 20;
            int m = corpse.getKey();
            drawCross(g, m, x, y);
        }

        if (state == GameState.Killed)
            drawStatus(g, "You have been killed!", Color.RED);
        else if (state == GameState.Won)
            drawStatus(g, "You have won!", Color.GREEN);
        else if (state == GameState.Lost)
            drawStatus(g, "You have lost.", Color.YELLOW);
    }

    private void drawDirection(Graphics g, int mobile, int x, int y, Direction dir)
    {
        g.setColor(getPlayerColor(mobile));
        int d1 = mobile == color ? 0 : 7;
        int d2 = mobile == color ? 19 : 12;
        if (dir == Direction.Up)
            g.drawLine(x + d1, y, x + d2, y);
        else if (dir == Direction.Right)
            g.drawLine(x + 19, y + d1, x + 19, y + d2);
        else if (dir == Direction.Down)
            g.drawLine(x + d1, y + 19, x + d2, y + 19);
        else if (dir == Direction.Left)
            g.drawLine(x, y + d1, x, y + d2);
    }

    private void drawCross(Graphics g, int mobile, int x, int y)
    {
        g.setColor(getPlayerColor(mobile));
        switch (mobile)
        {
            case 10:
                g.drawLine(x + 2, y + 1, x + 2, y + 5);
                g.drawLine(x + 1, y + 2, x + 3, y + 2);
                break;
            case 20:
                g.drawLine(x + 17, y + 1, x + 17, y + 5);
                g.drawLine(x + 16, y + 2, x + 18, y + 2);
                break;
            case 30:
                g.drawLine(x + 17, y + 14, x + 17, y + 18);
                g.drawLine(x + 16, y + 15, x + 18, y + 15);
                break;
            case 40:
                g.drawLine(x + 2, y + 14, x + 2, y + 18);
                g.drawLine(x + 1, y + 15, x + 3, y + 15);
                break;
        }
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

    private Color getPlayerColor(int mobile)
    {
        switch (mobile)
        {
            case 10:
                return Color.RED;
            case 20:
                return Color.GREEN;
            case 30:
                return Color.BLUE;
            case 40:
                return Color.CYAN;
        }
        return null;
    }

    @Override
    public String getToolTipText(MouseEvent event)
    {
        Position position = new Position(event.getY() / 20, event.getX() / 20);
        for (Map.Entry<Integer, Mobile> entry : mobiles.entrySet())
            if (entry.getKey() < 50)
            {
                Mobile mobile = entry.getValue();
                if (position.equals(mobile.getPosition()))
                    return mobile.toString();
            }

        for (Map.Entry<Integer, Mobile> entry : corpses.entrySet())
        {
            Mobile mobile = entry.getValue();
            if (position.equals(mobile.getPosition()))
                return mobile.toString();
        }
        return null;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (getParent() != null)
        {
            long diff = System.currentTimeMillis() - start;
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            MainFrame.getInstance().setTitle("The Maze - " + format.format(new Date(diff)));
        }
        else
            timer.stop();
    }

    private enum GameState
    {
        Init, Started, Killed, Won, Lost
    }
}