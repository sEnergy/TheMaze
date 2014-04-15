package themaze.server.objects;

public class Key extends MazeObject
{
    private boolean taken = false;

    @Override
    public char toChar() { return taken ? ' ' : 'K'; }

    public boolean take() { return !taken && (taken = true); }

    @Override
    public boolean isEnterable() {
        return true;
    }
}
