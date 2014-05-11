package themaze.client;

import javax.swing.*;
import java.io.*;

/**
 * GUI komponenta, která zobrazuje text posílaný do konzole (System.out.print*).
 *
 * @author Jaroslav Kubík
 * @author Marcel Fiala
 */
public class JConsoleOutput extends JScrollPane
{
    private final JTextArea textArea = new JTextArea();

    public JConsoleOutput()
    {
        super();
        setViewportView(textArea);
        textArea.setEditable(false);

        OutputStream out = new OutputStream()
        {
            @Override
            public void write(int b) throws IOException
            { updateTextArea(String.valueOf((char) b)); }

            @Override
            public void write(byte[] b, int off, int len) throws IOException
            { updateTextArea(new String(b, off, len)); }

            @Override
            public void write(byte[] b) throws IOException
            { write(b, 0, b.length); }
        };

        System.setOut(new PrintStream(out, true));
    }

    private void updateTextArea(final String text)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run() { textArea.append(text); }
        });
    }
}
