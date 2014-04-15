package themaze.server.net;

import java.io.IOException;

public class Command
{
    public enum CommandType { Game, Join, Show, Close, Left, Right, Step, Keys, Take, Open }

    public final CommandType type;

    public Command(CommandType type) {this.type = type;}
    public Command(String str) throws IOException
    {
        for (CommandType cmd : CommandType.values())
            if (cmd.name().equalsIgnoreCase(str))
            {
                this.type = cmd;
                break;
            }
        throw new IOException("Invalid argument");
    }

    public void addData(Object obj)
    {

    }
}
