package themaze.server.mobiles;

import themaze.server.Game;
import themaze.server.objects.*;
import themaze.server.types.*;

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

    public final char getChar()
    {
        switch(this.direction)
        {
            case UP :
                return '8';
            case RIGHT:
                return '6';
            case DOWN:
                return '2';
            case LEFT:
                return '4';
        }
        return 0;
    }

    @Override
    public boolean step()
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
