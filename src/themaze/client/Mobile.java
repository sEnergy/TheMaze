package themaze.client;

import themaze.Position;

import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Mobile
{
    private Position pos;
    private Position.Direction dir;
    private int steps = -1;
    private long time = -1;

    public Position getPosition() { return pos; }
    public void setPosition(Position position) { pos = position; }

    public Position.Direction getDirection() { return dir; }
    public void setDirection(int direction)
    { dir = Position.Direction.values()[direction]; }

    public void setInfo(int steps, long time)
    {
        this.steps = steps;
        this.time = time;
    }

    @Override
    public String toString()
    {
        if (steps < 0 || time < 0)
            return null;

        DateFormat format = DateFormat.getTimeInstance();
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return String.format("<html>Steps: %d<br>Time: %s", steps, format.format(new Date(time)));
    }
}
