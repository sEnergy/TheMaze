package themaze.server.objects;

/**
 * Třída reprezentující klíč.
 *
 * @author Jaroslav Kubík
 */
public class Key extends MazeObject
{
    private boolean taken = false;

    @Override
    public byte toByte() { return (byte) (taken ? 0 : 4); }

    public boolean take() { return !taken && (taken = true); }

    @Override
    public boolean isEnterable() { return true; }
}
