package themaze.objects;

public class Gate extends MazeObject
{
    private boolean open = false;

    @Override
    public char toChar() { return 'G'; }

    public boolean open() {
        open = true;
        return open;
    }

    @Override
    public boolean isEnterable() {
        return open;
    }
}
