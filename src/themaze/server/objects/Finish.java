package themaze.server.objects;

public class Finish extends MazeObject
{
    @Override
    public char toChar() { return 'F'; }

    @Override
    public boolean isEnterable() {
        return true;
    }
}