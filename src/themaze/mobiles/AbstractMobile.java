package themaze.mobiles;

import themaze.Maze;

public class AbstractMobile {

    protected int x, y;
    protected Directions direction;
    protected Maze maze = null;

    protected enum Directions {UP, RIGHT, DOWN, LEFT};

    public AbstractMobile (Maze maze, int x, int y) {
        this.maze = maze;
        this.x = x;
        this.y = y;
        this.setDirection();
    }

    protected final void setDirection () { // nutno volat až po přidání bludiště
        this.direction = Directions.UP;         // udělat podle bludiště
    }

    protected final void turnRight() {

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

    protected final void turnLeft() {

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


}
