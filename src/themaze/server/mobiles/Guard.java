package themaze.server.mobiles;

import themaze.server.Game;
import themaze.server.types.Position;

public class Guard extends Mobile
{
    public Guard(Game game, Position start)
    {
        super(game, start);
    }

    @Override
    public byte toByte()
    {
        return 0;
    }
}
