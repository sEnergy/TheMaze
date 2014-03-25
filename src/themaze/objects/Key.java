package themaze.objects;

public class Key extends MazeObject
{
    @Override
    public char toChar() { return 'K'; }

    @Override
    public boolean isEnterable() {
        return true;
    }
}
