package themaze;

import themaze.mobiles.Player;
import themaze.objects.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Maze
{
    private MazeObject[][] matrix;

    private Player player = null;
    public Player getPlayer() { return player; }

    public Maze(String name) throws IOException
    {
        try (BufferedReader br = new BufferedReader(new FileReader("examples/" + name + ".maz")))
        {
            String[] data;
            String line = br.readLine();
            if (line == null || (data = line.split("\\s+")).length != 2)
                throw new IOException("Invalid maze format");

            int rows = Integer.parseInt(data[0]);
            int columns = Integer.parseInt(data[1]);
            matrix = new MazeObject[rows][columns];

            for (int x = 0; x < rows; x++)
            {
                line = br.readLine();
                if (line == null || (data = line.split("\\s+")).length != columns)
                    throw new IOException("Invalid maze format");

                for (int y = 0; y < columns; y++)
                    matrix[x][y] = createObject(data[y]);
            }
        }

        player = new Player(this, 1, 8);
    }

    private MazeObject createObject(String type) throws IllegalArgumentException
    {
        switch (type)
        {
            case "G":
                return new Gate();
            case "W":
                return new Wall();
            case "K":
                return new Key();
            case "F":
                return new Finish();
            case "S":
                return new Start();
            case "-":
                return null;
        }
        throw new IllegalArgumentException("Invalid maze object");
    }

    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder();
        for (int x = 0; x < matrix.length; x++)
        {
            for (int y = 0; y < matrix[x].length; y++)
            {
                if (player.isAt(0, 0))
                    str.append('P');
                else if (matrix[x][y] != null)
                    str.append(matrix[x][y].toChar());
                else
                    str.append('-');
                str.append(' ');
            }
            str.append('\n');
        }
        return str.toString();
    }

    public MazeObject getObject (int x, int y) {
        return matrix[x][y];
    }

    public void eraseKey (int x, int y) {
         matrix[x][y] = null;
    }
}
