package themaze.server.objects;

public class Finish extends MazeObject
{
    @Override
    public byte toByte() { return 5; }

    @Override
    public boolean isEnterable() {
        return true;
    }
}