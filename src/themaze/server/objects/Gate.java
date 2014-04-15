package themaze.server.objects;

public class Gate extends MazeObject
{
    private boolean open = false;

    @Override
    public byte toByte() { return (byte) (open ? 3 : 2); }

    public boolean open() { return !open && (open = true); }

    @Override
    public boolean isEnterable() {
        return open;
    }
}