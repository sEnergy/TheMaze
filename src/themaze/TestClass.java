
package themaze;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestClass {

    @Before
    public void setUp() {
        System.out.println("\nZacatek testovani:");
        System.out.println("(prikaz go je jiz implementovan - provede krok o jedno pole)");
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testTapeObject01() {

        themaze.TheMaze.handleCommand("game test");

    }
}