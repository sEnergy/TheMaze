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

    public static boolean handleCommand(String str)
    {
        try
        {
            Command cmd = parseCommand(str);
            if (maze == null && cmd != Command.Game && cmd != Command.Close)
                throw new Exception("You have to start a game first");
            switch (cmd)
            {
                case Game:
                    if (maze != null)
                        throw new Exception("Game is already started!");
                    maze = new Maze(str.substring(4).trim());
                    System.out.println("Game started.");
                    break;
                case Show:
                    System.out.println(maze.toString());
                    break;
                case Close:
                    System.out.println("Game closed.");
                    return false;
                case Left:
                    maze.getPlayer().turnLeft();
                    System.out.println("You have turned left.");
                    break;
                case Right:
                    maze.getPlayer().turnRight();
                    System.out.println("You have turned right.");
                    break;
                case Step:
                    if (!maze.getPlayer().step())
                        System.out.println("You can't step that way.");
                    else
                    	System.out.println("You made step forward!");
                    if (maze.getPlayer().inFinish())
                    {
                        System.out.println("You have won!");
                        return false;
                    }
                    break;
                case Keys:
                    System.out.printf("You have %d key(s)\n", maze.getPlayer().getKeys());
                    break;
                case Take:
                    if (!maze.getPlayer().take())
                        System.out.println("There is no key in front of you.");
                    else
                    	System.out.println("You picked up a key.");
                    break;
                case Open:
                    if (!maze.getPlayer().open())
                        System.out.println("Can't open gate. (no gate or no key)");
                    else
                    	System.out.println("You opened the gate.");
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
            case "step":
                return Command.Step;
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
        Game, Show, Close, Left, Right, Step, Keys, Take, Open
    }
}