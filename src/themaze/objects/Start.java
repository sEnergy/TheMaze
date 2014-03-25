package themaze.objects;

public class Start extends MazeObject
{
    @Override
    public char toChar() { return 'S'; }

    @Override
    public boolean isEnterable() {
        return true;
    }
}
