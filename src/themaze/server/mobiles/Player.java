package themaze.server.mobiles;

import themaze.server.Game;
import themaze.server.objects.*;
import themaze.server.types.*;

import java.io.IOException;

public class Player extends Mobile
{
    private int keys;

    public Player(Game game, Position start) { super(game, start); }

    public final int getKeys() { return keys; }

    public final boolean take()
    {
        MazeObject obj = game.getObject(position.add(direction));
        if (obj instanceof Key && ((Key)obj).take())
        {
            keys++;
            return true;
        }
        return false;
    }

    public final boolean open()
    {
        MazeObject obj = game.getObject(position.add(direction));
        if (obj instanceof Gate && keys > 0 && ((Gate)obj).open())
        {
            keys--;
            return true;
        }
        return false;
    }

    public byte toByte()
    {
        byte b = (byte) (10 + direction.ordinal());
        if (game.getObject(position) instanceof Gate)
            b += 4;
        return b;
    }

    @Override
    public boolean step() throws IOException
    {
        if (super.step())
        {
            if (game.getObject(position) instanceof Finish)
                game.onFinish(this);
            return true;
        }
        return false;
    }
}
