package themaze.server.mobiles;

import themaze.Position;
import themaze.Position.Direction;
import themaze.server.Game;

import java.io.IOException;
import java.util.Random;

public class Guard extends Mobile
{
    private final int index;

    public Guard(Game game, Position start, int index)
    {
        super(game, start);
        this.index = index;
    }

    @Override
    public byte toByte() { return (byte) (50 + index); }

    @Override
    public boolean step() throws IOException
    {
        synchronized (game)
        {
            int dir = new Random().nextInt(Direction.values().length);
            direction = Direction.values()[dir];
            return super.step();
        }
    }
}
