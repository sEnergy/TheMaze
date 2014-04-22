package themaze.server.mobiles;

import themaze.server.Game;
import themaze.Position;
import themaze.Position.Direction;

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
    public void step() throws IOException
    {
        int dir = new Random().nextInt(Direction.values().length);
        Position pos = position.add(Direction.values()[dir]);
        if (game.isEnterable(pos))
        {
            position = pos;
            game.onMove(this);
        }
    }
}
