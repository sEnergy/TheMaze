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

public class TheMaze
{
    public static void main(String[] args)
    {
        try
        {
            Maze maze = new Maze("mazejakprase");
            System.out.println(maze);
        }
        catch (Exception e) { e.printStackTrace(); }
    }
}