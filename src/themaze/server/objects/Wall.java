package themaze.server.objects;

public class Wall extends MazeObject
{
    @Override
    public byte toByte() { return 1; }

    @Override
    public boolean isEnterable() {
        return false;
    }
}
