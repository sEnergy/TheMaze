package themaze.server.mobiles;

import themaze.server.Game;
import themaze.server.Position;
import themaze.server.Position.Direction;

import java.io.IOException;
import java.util.Random;

public class Guard extends Mobile
{
    public Guard(Game game, Position start)
    {
        super(game, start);
    }

    @Override
    public byte toByte()
    {
        return (byte) 50;
    }

    @Override
    public void step() throws IOException
    {
        int dir = new Random().nextInt(Direction.values().length);
        Position pos = position.add(Direction.values()[dir]);
        if (game.isEnterable(pos))
        {
            position = pos;
            game.onMove();
        }
    }
}
