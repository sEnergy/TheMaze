package themaze.server;

import themaze.server.objects.*;
import themaze.server.types.Position;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Maze
{
    public final byte rows, columns;
    public final List<Position> starts;
    private final String name;
    private final MazeObject[][] maze;

    public Maze(Maze m)
    {
        this.rows = m.rows;
        this.columns = m.columns;
        this.name = m.name;
        this.starts = new ArrayList<>(m.starts);
        maze = new MazeObject[rows][columns];
        for (byte r = 0; r < rows; r++)
            for (byte c = 0; c < columns; c++)
                maze[r][c] = copy(m.maze[r][c]);
    }

    public Maze(File file) throws IOException
    {
        name = file.getName().substring(0, file.getName().length() - 4);
        starts = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            String[] data;
            String line = br.readLine();
            if (line == null || (data = line.split("\\s+")).length != 2)
                throw new IOException("Invalid maze format");

            rows = Byte.parseByte(data[0]);
            columns = Byte.parseByte(data[1]);
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

    private MazeObject copy(MazeObject obj)
    {
        if (obj instanceof Gate)
            return new Gate();
        if (obj instanceof Wall)
            return new Wall();
        if (obj instanceof Key)
            return new Key();
        if (obj instanceof Finish)
            return new Finish();
        return null;
    }

    private void parseObject(byte row, byte column, String type) throws IOException
    {
        switch (type)
        {
            case "S":
                starts.add(new Position(row, column));
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