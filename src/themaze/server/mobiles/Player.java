package themaze.server.mobiles;

import themaze.server.Game;
import themaze.server.objects.*;
import themaze.server.types.*;

import java.io.IOException;

public class Player extends Mobile
{
    private byte keys;

    public Player(Game game, Position start) { super(game, start); }

    public byte getKeys() { return keys; }
    public void leave() throws IOException { game.leave(this); }
    public void take() throws IOException
    {
        Position pos = position.add(direction);
        MazeObject obj = game.getObject(pos);
        if (obj instanceof Key && ((Key) obj).take())
        {
            keys++;
            game.onTake(this, pos);
        }
        else
            game.onTake(this, null);
    }

    public void open() throws IOException
    {
        Position pos = position.add(direction);
        MazeObject obj = game.getObject(pos);
        if (obj instanceof Gate && keys > 0 && ((Gate) obj).open())
        {
            keys--;
            game.onOpen(true, this, pos);
        }
        else
            game.onOpen(keys > 0, this, null);
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
