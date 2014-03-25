package themaze.mobiles;

public class Player
{
    private int x, y;

    public Player()
    {

    }

    public boolean isAt(int x, int y)
    {
        return this.x == x && this.y == y;
    }
}
