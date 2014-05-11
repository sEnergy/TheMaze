package themaze.server.objects;

/**
 * Třída reprezentující cíl.
 *
 * @author Jaroslav Kubík
 */
public class Finish extends MazeObject
{
    @Override
    public byte toByte() { return 5; }

    @Override
    public boolean isEnterable() { return true; }
}