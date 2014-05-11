package themaze.server.objects;

/**
 * Abstraktní třída pro statické objekty v bludišti.
 *
 * @author Jaroslav Kubík
 */
public abstract class MazeObject
{
    public abstract byte toByte();
    public abstract boolean isEnterable();
}