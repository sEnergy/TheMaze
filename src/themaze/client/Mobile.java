package themaze.client;

import themaze.Position;

public class Mobile
{
    private Position pos;
    private Position.Direction dir;
    private int steps = -1;

    public Position getPosition() { return pos; }
    public void setPosition(int row, int column) { pos = new Position(row, column); }

    public Position.Direction getDirection() { return dir; }
    public void setDirection(int direction)
    { dir = Position.Direction.values()[direction]; }

    public void setInfo(int steps)
    {
        this.steps = steps;
    }

    @Override
    public String toString()
    {
        if (steps >= 0)
            return "Steps: " + steps;
        return null;
    }
}
