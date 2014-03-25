package themaze.mobiles;

import themaze.Maze;
import themaze.objects.*;

public class Player extends AbstractMobile
{
    private int keys = 0;
    public final int getKeys() { return keys; }

    public Player(Maze maze, int x, int y)
    {
        super(maze, x, y);
    }

    public boolean inFinish()
    {
        return maze.getObject(x, y) instanceof Finish;
    }

    public boolean take() {

        int key_x = this.x;
        int key_y = this.y;

        MazeObject tmp = null;

        switch(this.direction)
        {
            case UP :
                tmp =  maze.getObject(--key_x, key_y);
                break;
            case RIGHT:
                tmp =  maze.getObject(key_x, ++key_y);
                break;
            case DOWN:
                tmp =  maze.getObject(++key_x, key_y);
                break;
            case LEFT:
                tmp =  maze.getObject(key_x, --key_y);
                break;
        }

        if(tmp instanceof Key)
        {
            maze.eraseKey(key_x, key_y);
            keys++;
            return true;
        }

        return false;
    }

     public boolean open() {

        int gate_x = this.x;
        int gate_y = this.y;

        MazeObject tmp = null;

        switch(this.direction)
        {
            case UP :
                tmp =  maze.getObject(--gate_x, gate_y);
                break;
            case RIGHT:
                tmp =  maze.getObject(gate_x, ++gate_y);
                break;
            case DOWN:
                tmp =  maze.getObject(++gate_x, gate_y);
                break;
            case LEFT:
                tmp =  maze.getObject(gate_x, --gate_y);
                break;
        }

        if(tmp instanceof Gate && keys > 0)
        {
            Gate gate = (Gate)tmp;
            gate.open();
            keys--;
            return true;
        }

        return false;
    }
}
