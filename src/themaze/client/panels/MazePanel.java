package themaze.client.panels;

import themaze.Position;
import themaze.Position.Direction;
import themaze.client.MainFrame;
import themaze.client.Mobile;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
    private final Map<Integer, BufferedImage> images = new HashMap<>();
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

        try
        {
            images.put(0, ImageIO.read(new File("floor.png")));
            images.put(1, ImageIO.read(new File("wall.png")));
            images.put(2, ImageIO.read(new File("gate_closed.png")));
            images.put(3, ImageIO.read(new File("gate_open.png")));
            images.put(4, ImageIO.read(new File("key.png")));
            images.put(5, ImageIO.read(new File("finish.png")));

            images.put(10, ImageIO.read(new File("pl_1.png")));
            images.put(20, ImageIO.read(new File("pl_2.png")));
            images.put(30, ImageIO.read(new File("pl_3.png")));
            images.put(40, ImageIO.read(new File("pl_4.png")));
            images.put(50, ImageIO.read(new File("enemy.png")));

            images.put(200, ImageIO.read(new File("blood.png")));
        }
        catch (IOException ex) { System.err.print(ex.getMessage()); }

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

        boolean blood = false;
        for (Map.Entry<Integer, Mobile> corpse : corpses.entrySet())
        {
            Mobile mobile = corpse.getValue();
            int x = mobile.getPosition().column * 20;
            int y = mobile.getPosition().row * 20;
            int m = corpse.getKey();
            if (!blood)
                g.drawImage(images.get(200), x, y, null);
            blood = true;
            drawCross(g, m, x, y);
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
                g.drawLine(x + 3, y + 1, x + 3, y + 7);
                g.drawLine(x + 1, y + 3, x + 5, y + 3);
                break;
            case 20:
                g.drawLine(x + 16, y + 1, x + 16, y + 7);
                g.drawLine(x + 14, y + 3, x + 18, y + 3);
                break;
            case 30:
                g.drawLine(x + 3, y + 12, x + 3, y + 18);
                g.drawLine(x + 1, y + 14, x + 5, y + 14);
                break;
            case 40:
                g.drawLine(x + 16, y + 12, x + 16, y + 18);
                g.drawLine(x + 14, y + 14, x + 18, y + 14);
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
    { return new Color(images.get(mobile).getRGB(10, 10)); }

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