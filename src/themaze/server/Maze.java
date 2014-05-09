package themaze.server;

import themaze.Position;
import themaze.server.objects.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Maze
{
    public final byte rows, columns;
    public final List<Position> starts;
    public final List<Position> guards;
    private final String name;
    private final MazeObject[][] maze;

    public Maze(Maze m) throws IllegalAccessException, InstantiationException
    {
        this.rows = m.rows;
        this.columns = m.columns;
        this.name = m.name;
        this.starts = new ArrayList<>(m.starts);
        this.guards = new ArrayList<>(m.guards);
        maze = new MazeObject[rows][columns];
        for (byte r = 0; r < rows; r++)
            for (byte c = 0; c < columns; c++)
                if (m.maze[r][c] != null)
                    maze[r][c] = m.maze[r][c].getClass().newInstance();
    }

    public Maze(File file) throws IOException
    {
        name = file.getName().substring(0, file.getName().length() - 4);
        starts = new ArrayList<>();
        guards = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            String[] data;
            String line = br.readLine();
            if (line == null || (data = line.split("\\s+")).length != 2)
                throw new IOException("Invalid maze format");

            rows = Byte.parseByte(data[0]);
            columns = Byte.parseByte(data[1]);
            if (rows > 50 || columns > 50 || rows < 20 || columns < 20)
                throw new IOException("Invalid maze format");
            maze = new MazeObject[rows][columns];

            for (byte r = 0; r < rows; r++)
            {
                line = br.readLine();
                if (line == null || (data = line.split("\\s+")).length != columns)
                    throw new IOException("Invalid maze format");

                for (byte c = 0; c < columns; c++)
                    parseObject(r, c, data[c]);
            }
        }
    }

    private void parseObject(byte row, byte column, String type) throws IOException
    {
        switch (type)
        {
            case "S":
                starts.add(new Position(row, column));
                break;
            case "X":
                guards.add(new Position(row, column));
                break;
            case "G":
                maze[row][column] = new Gate();
                break;
            case "W":
                maze[row][column] = new Wall();
                break;
            case "K":
                maze[row][column] = new Key();
                break;
            case "F":
                maze[row][column] = new Finish();
                break;
            case "-":
                break;
            default:
                throw new IOException("Invalid maze format");
        }
    }

    public MazeObject at(Position position) { return maze[position.row][position.column]; }
    public byte[] toBytes()
    {
        byte[] data = new byte[rows * columns];
        for (byte r = 0; r < rows; r++)
            for (byte c = 0; c < columns; c++)
                if (maze[r][c] != null)
                    data[c + r * columns] = maze[r][c].toByte();
        return data;
    }

    @Override
    public String toString() { return name; }
}
