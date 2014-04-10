
package themaze;

import org.junit.Before;
import org.junit.Test;

public class TestClass {

    @Before
    public void setUp() {
        System.out.println("\n\nTest beginning:");
        System.out.println("The player is represended by number 2/4/6/8 accroding to his direction");
        System.out.println("(corresponding with direction keys on NUM 2/4/6/8)");
    }

    @Test
    public void testTapeObject01() {
    	
    	System.out.println("");
    	
    	System.out.println("Sending command: 'step' (should not work-game not started)");
    	System.out.printf("Game response: ");
    	themaze.TheMaze.handleCommand("step");
    	System.out.printf("\n");
    	
    	System.out.println("Sending command: 'game test' (should open test maze)");
    	System.out.printf("Game response: ");
    	themaze.TheMaze.handleCommand("game test");
    	System.out.printf("\n");
    	
    	System.out.println("Sending command: 'show' (should print current maze state)");
    	System.out.printf("Game response:\n");
    	themaze.TheMaze.handleCommand("show");
    	
    	System.out.println("Now the real fun begins - let's play.\n");
    	
    	System.out.println("Sending command: 'step' (should move player one step forward)");
    	System.out.printf("Game response: ");
    	themaze.TheMaze.handleCommand("step");
    	System.out.printf("\n");
    	themaze.TheMaze.handleCommand("show");
    	
    	System.out.println("Sending command: 'left' (number 2 should change to 6)");
    	System.out.printf("Game response: ");
    	themaze.TheMaze.handleCommand("left");
    	System.out.printf("\n");
    	themaze.TheMaze.handleCommand("show");
    	
    	System.out.println("Sending command: 'left' (number 6 should change to 8)");
    	System.out.printf("Game response: ");
    	themaze.TheMaze.handleCommand("left");
    	System.out.printf("\n");
    	themaze.TheMaze.handleCommand("show");
    	
    	System.out.println("Sending command: 'step' (comming back to spawn)");
    	System.out.printf("Game response: ");
    	themaze.TheMaze.handleCommand("step");
    	System.out.printf("\n");
    	themaze.TheMaze.handleCommand("show");
    	
    	System.out.println("Sending command: 'step' (player should collide with wall)");
    	System.out.printf("Game response: ");
    	themaze.TheMaze.handleCommand("step");
    	System.out.printf("\n");
    	themaze.TheMaze.handleCommand("show");
    	
    	System.out.println("Sending command: 'right' (turn to key direction == 8 changes to 6)");
    	System.out.printf("Game response: ");
    	themaze.TheMaze.handleCommand("right");
    	System.out.printf("\n");
    	themaze.TheMaze.handleCommand("show");
    	
    	System.out.println("Sending command: 'keys' (should say player has no keys)");
    	System.out.printf("Game response: ");
    	themaze.TheMaze.handleCommand("keys");
    	themaze.TheMaze.handleCommand("show");
    	
    	System.out.println("Sending command: 'take' (attempt to take key in distance)");
    	System.out.printf("Game response: ");
    	themaze.TheMaze.handleCommand("take");
    	themaze.TheMaze.handleCommand("show");
    	
    	System.out.println("Sending command: 'step'");
    	System.out.printf("Game response: ");
    	themaze.TheMaze.handleCommand("step");
    	System.out.printf("\n");
    	themaze.TheMaze.handleCommand("show");
    	
    	System.out.println("Sending command: 'step'");
    	System.out.printf("Game response: ");
    	themaze.TheMaze.handleCommand("step");
    	System.out.printf("\n");
    	themaze.TheMaze.handleCommand("show");
    	
    	System.out.println("Sending command: 'step'");
    	System.out.printf("Game response: ");
    	themaze.TheMaze.handleCommand("step");
    	System.out.printf("\n");
    	themaze.TheMaze.handleCommand("show");
    	
    	System.out.println("Sending command: 'right' (turn to gate)");
    	System.out.printf("Game response: ");
    	themaze.TheMaze.handleCommand("right");
    	System.out.printf("\n");
    	themaze.TheMaze.handleCommand("show");
    	
    	System.out.println("Sending command: 'step' (try to step through closed gate)");
    	System.out.printf("Game response: ");
    	themaze.TheMaze.handleCommand("step");
    	System.out.printf("\n");
    	themaze.TheMaze.handleCommand("show");
    	
    	System.out.println("Sending command: 'open' (attempt open gate)");
    	System.out.printf("Game response: ");
    	themaze.TheMaze.handleCommand("open");
    	System.out.printf("\n");
    	themaze.TheMaze.handleCommand("show");
    	
    	System.out.println("Sending command: 'right' (face key)");
    	System.out.printf("Game response: ");
    	themaze.TheMaze.handleCommand("right");
    	System.out.printf("\n");
    	themaze.TheMaze.handleCommand("show");
    	
    	System.out.println("Sending command: 'take' (attempt to take key)");
    	System.out.printf("Game response: ");
    	themaze.TheMaze.handleCommand("take");
    	themaze.TheMaze.handleCommand("show");
    	
    	System.out.println("Sending command: 'keys' (should say player has 1 key)");
    	System.out.printf("Game response: ");
    	themaze.TheMaze.handleCommand("keys");
    	themaze.TheMaze.handleCommand("show");
    	
    	System.out.println("Sending command: 'left' (face gate)");
    	System.out.printf("Game response: ");
    	themaze.TheMaze.handleCommand("left");
    	System.out.printf("\n");
    	themaze.TheMaze.handleCommand("show");
    	
    	System.out.println("Sending command: 'open' (attempt open gate)");
    	System.out.printf("Game response: ");
    	themaze.TheMaze.handleCommand("open");
    	System.out.printf("\n");
    	themaze.TheMaze.handleCommand("show");
    	
    	System.out.println("Sending command: 'step' (try to step through opened gate)");
    	System.out.printf("Game response: ");
    	themaze.TheMaze.handleCommand("step");
    	System.out.printf("\n");
    	themaze.TheMaze.handleCommand("show");
    	
    	System.out.println("Sending command: 'step' (to the finish)");
    	System.out.printf("Game response: ");
    	themaze.TheMaze.handleCommand("step");
    	System.out.printf("\n");
    	themaze.TheMaze.handleCommand("show");
    	
    	System.out.println("Sending command: 'close' (close game)");
    	System.out.printf("Game response: ");
    	themaze.TheMaze.handleCommand("close");
    }
}