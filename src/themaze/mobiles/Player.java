package themaze.mobiles;

import themaze.Maze;
import themaze.objects.Key;
import themaze.objects.MazeObject;

public class Player extends AbstractMobile
{
    private int keys = 0;

    public Player(Maze maze, int x, int y)
    {
        super(maze, x, y);
    }

    public boolean take() {

        int key_x = this.x;
        int key_y = this.y;

        MazeObject tmp = null;

        switch(this.direction)
        {
            case UP :
                tmp =  maze.getObject(--key_x, key_y);
            case RIGHT:
                tmp =  maze.getObject(key_x, ++key_y);
            case DOWN:
                tmp =  maze.getObject(++key_x, key_y);
            case LEFT:
                tmp =  maze.getObject(key_x, --key_y);
        }

        if(tmp instanceof Key)
        {
            maze.eraseKey(x, y);
            keys++;
            return true;
        }

        return false;
    }
}
