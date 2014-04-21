package themaze.server.mobiles;

import themaze.server.Game;
import themaze.server.Position;

import java.io.IOException;

public class Player extends Mobile
{
    private byte keys;
    private Color color;

    public Player(Game game, Position start, Color color)
    {
        super(game, start);
        this.color = color;
    }

    public byte getKeys() { return keys; }
    public void leave() throws IOException { game.leave(this, color); }
    public void take() throws IOException
    {
        if (game.take(this, position.add(direction)))
            keys++;
    }

    public void open() throws IOException
    {
        if (game.open(keys > 0, this, position.add(direction)))
            keys--;
    }

    @Override
    public byte toByte()
    { return (byte) (10 + 10 * color.ordinal() + direction.ordinal()); }

    @Override
    public boolean step() throws IOException
    {
        if (super.step())
        {
            game.onFinish(this, position);
            return true;
        }
        return false;
    }

    public enum Color
    {
        Red, Green, Blue, TheFourth
    }
}
