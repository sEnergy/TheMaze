package themaze.server.objects;

/**
 * Třída reprezentující zeď.
 *
 * @author Jaroslav Kubík
 * @author Marcel Fiala
 */
public class Wall extends MazeObject
{
    @Override
    public byte toByte() { return 1; }

    @Override
    public boolean isEnterable() { return false; }
}
