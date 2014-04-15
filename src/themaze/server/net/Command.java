package themaze.server.net;

import java.io.IOException;

public class Command
{
    public enum CommandType { Game, Join, Close, Show, Left, Right, Step, Keys, Take, Open, Go, Invalid }

    public final CommandType type;
    private String data;

    public Command(String str) throws IOException
    {
        for (CommandType cmd : CommandType.values())
            if (cmd.name().equalsIgnoreCase(str))
            {
                this.type = cmd;
                return;
            }
        this.type = CommandType.Invalid;
    }

    public void setData(String data)
    {
        this.data = data;
    }

    public String getData()
    {
        return data;
    }
}
