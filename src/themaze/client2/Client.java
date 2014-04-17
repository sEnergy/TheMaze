package themaze.client2;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

public class Client extends JFrame implements ActionListener
{
    public static void main(String[] args)
    {
        javax.swing.SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run() { new Client(); }
        });
    }

    private final GamePanel game = new GamePanel();
    private final JTextField input = new JTextField();
    private Communication comm;

    public Client()
    {
        super("The Maze");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(game);
        panel.add(input);
        add(panel);

        input.addActionListener(this);

        setSize(500, 500);
        setVisible(true);

        try
        {
            comm = new Communication(new Socket("localhost", 50000));
            new ServerThread(comm, game).start();
        }
        catch (IOException e) {  e.printStackTrace(); }
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        try
        {
            comm.sendStrings(e.getActionCommand());
            input.setText(null);
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
        }
    }
}
