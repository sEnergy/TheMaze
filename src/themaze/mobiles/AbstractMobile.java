package themaze.mobiles;

import themaze.Maze;
import themaze.objects.*;

public class AbstractMobile {

    protected int x, y;
    protected Directions direction;
    protected Maze maze = null;

    protected enum Directions {UP, RIGHT, DOWN, LEFT}

    public AbstractMobile (Maze maze, int x, int y) {
        this.maze = maze;
        this.x = x;
        this.y = y;
        this.setDirection();
    }

    protected final void setDirection () { this.direction = Directions.UP; }
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

    public final void turnRight() {

        switch(this.direction)
        {
            case UP :
                this.direction = Directions.RIGHT;
                break;
            case RIGHT:
                this.direction = Directions.DOWN;
                break;
            case DOWN:
                this.direction = Directions.LEFT;
                break;
            case LEFT:
                this.direction = Directions.UP;
                break;
        }
    }

    public final void turnLeft() {

        switch(this.direction)
        {
            case UP :
                this.direction = Directions.LEFT;
                break;
            case RIGHT:
                this.direction = Directions.UP;
                break;
            case DOWN:
                this.direction = Directions.RIGHT;
                break;
            case LEFT:
                this.direction = Directions.DOWN;
                break;
        }
    }

    public final boolean isAt (int x, int y)
    {
        return this.x == x && this.y == y;
    }

    public final boolean go() {

        int tmp_x = this.x;
        int tmp_y = this.y;

        MazeObject tmp = null;

        switch(this.direction)
        {
            case UP :
                tmp =  maze.getObject(--tmp_x, tmp_y);
                break;
            case RIGHT:
                tmp =  maze.getObject(tmp_x, ++tmp_y);
                break;
            case DOWN:
                tmp =  maze.getObject(++tmp_x, tmp_y);
                break;
            case LEFT:
                tmp =  maze.getObject(tmp_x, --tmp_y);
                break;
        }

        if(tmp == null || tmp.isEnterable())
        {
            this.x = tmp_x;
            this.y = tmp_y;
            return true;
        }

        return false;
    }


}
