/* *****************************************************************************
 *
 * Project name:        The Maze
 * Course:              IJA
 * Authors:             Marcel Fiala
 *                      Jaroslav Kubik
 * E-mail:              xfiala47@stud.fit.vutbr.cz
 *                      xkubik25@stud.fit.vutbr.cz
 *
 * Date:                15th March 2014
 * Encoding:            UTF-8
 *
*******************************************************************************/

package themaze;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TheMaze
{
    private static Maze maze;

    public static void main(String[] args)
    {
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line;
            while ((line = reader.readLine()) != null)
            {
                if (!handleCommand(line))
                    return;
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private static boolean handleCommand(String str)
    {
        try
        {
            Command cmd = parseCommand(str);
            if (maze == null && cmd != Command.Game)
                throw new Exception("You have to start a game first");
            switch (cmd)
            {
                case Game:
                    if (maze != null)
                        throw new Exception("Game already started");
                    maze = new Maze(str.substring(4).trim());
                    System.out.println("Game started");
                    break;
                case Show:
                    System.out.println(maze.toString());
                    break;
                case Close:
                    System.out.print("Game closed");
                    return false;
                case Left:
                    maze.getPlayer().turnLeft();
                    System.out.print("You have turned left");
                    System.out.print(maze.toString());
                    break;
                case Right:
                    maze.getPlayer().turnRight();
                    System.out.print("You have turned right");
                    System.out.print(maze.toString());
                    break;
                case Go:
                    if (!maze.getPlayer().go())
                        System.out.println("You can't go that way");
                    System.out.print(maze.toString());
                    break;
                case Keys:
                    System.out.printf("You have %d key(s)\n", maze.getPlayer().getKeys());
                    break;
                case Take:
                    if (!maze.getPlayer().take())
                        System.out.println("No key found");
                    System.out.print(maze.toString());
                    break;
                case Open:
                    if (!maze.getPlayer().open())
                        System.out.println("No gate to open");
                    System.out.print(maze.toString());
                    break;
            }
        }
        catch (Exception ex) { System.out.println(ex.getMessage()); }
        return true;
    }

    private static Command parseCommand(String str) throws Exception
    {
        switch (str.toLowerCase())
        {
            case "show":
                return Command.Show;
            case "close":
                return Command.Close;
            case "left":
                return Command.Left;
            case "right":
                return Command.Right;
            case "go":
                return Command.Go;
            case "keys":
                return Command.Keys;
            case "take":
                return Command.Take;
            case "open":
                return Command.Open;
        }
        if (str.toLowerCase().startsWith("game "))
            return Command.Game;
        throw new Exception("Invalid command");
    }

    private enum Command
    {
        Game, Show, Close, Left, Right, Go, Keys, Take, Open
    }
}