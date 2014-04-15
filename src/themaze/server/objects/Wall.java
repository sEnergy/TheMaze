package themaze.server.objects;

public class Wall extends MazeObject
{
    @Override
    public char toChar() { return 'W'; }

    @Override
    public boolean isEnterable() {
        return false;
    }
}
