package themaze.objects;

public class Gate extends MazeObject
{
    private boolean open = false;

    @Override
    public char toChar() { return open ? 'O' : 'G'; }

    public boolean open() {
        return open = true;
    }

    @Override
    public boolean isEnterable() {
        return open;
    }
}
