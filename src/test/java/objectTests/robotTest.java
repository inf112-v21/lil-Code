package objectTests;

import Cards.MovementCard;
import Cards.TurningCard;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.Vector2;
import objects.Robot;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;

import static org.junit.Assert.assertEquals;

public class robotTest {
    Robot robot;
    Game game;

    @BeforeAll
    public void setUP(){

    }

    /**
     * Checks if setPosition() actually updates position
     */
    @Test
    public void setPositionShouldChangePosition(){
        robot = new Robot(2,2);
        robot.setPosition(3,4);

        assertEquals(3, robot.getX(), 0.1);
        assertEquals(4, robot.getY(), 0.1);
    }

    /**
     * Checks if setDirection() updates direction, and that only N, S, E or W is accepted.
     */
    @Test
    public void setDirectionShouldChangeDirection(){
        robot = new Robot(2,2);

        robot.setDirection("N");
        assertEquals("N", robot.getDir());

        robot.setDirection("M");
        assertEquals("N", robot.getDir());
    }

    /**
     * Checks if move() moves the Robot as it should
     */
    @Test
    public void checkIfMoveWorks(){
        robot = new Robot(0,0);

        // Checks vertical movement
        float initialYCoordinate = robot.getY();
        robot.setDirection("N");
        robot.move(3, robot.getDir(), false, false);
        assertEquals(initialYCoordinate + 3, robot.getY(), 0.1);

        // Check horisontal movement
        float initialXCoordinate = robot.getX();
        robot.setDirection("W");
        robot.move(3, robot.getDir(), false, false);
        assertEquals(initialXCoordinate + -3, robot.getX(), 0.1);
    }

    /**
     * Test moveBasedOnNextCard
     */
    @Test
    public void checkIfMoveBasedOnNextCardWorks(){
        robot = new Robot(0,0);
        robot.setDirection("N");

        // Checks if robot moves 1 up
        robot.chooseCardFromHand(new MovementCard(1,0));
        robot.moveBasedOnNextCard(true, false);
        assertEquals(1, robot.getY(), 0.1);

        // Checks if robot turns 1 time to the right
        robot.chooseCardFromHand(new TurningCard(true, false, 0));
        robot.moveBasedOnNextCard(true, false);
        assertEquals("E", robot.getDir());

        // Checks if robot can do a sequence of cards
        robot.chooseCardFromHand(new MovementCard(2,0));
        robot.chooseCardFromHand(new TurningCard(true, false, 0));
        robot.chooseCardFromHand(new MovementCard(3,0));

        Vector2 originalPosition = new Vector2(robot.getX(), robot.getY());
        robot.moveBasedOnNextCard(true, false);
        robot.moveBasedOnNextCard(true, false);
        robot.moveBasedOnNextCard(true, false);
        assertEquals(new Vector2(originalPosition.x + 2, originalPosition.y - 3), new Vector2(robot.getX(), robot.getY()));

    }

    @Test
    public void turnLeftTest(){
        robot = new Robot(0,0);
        robot.setDirection("S");
        robot.turnLeft(false);
        assertEquals("E", robot.getDir());
    }

    @Test
    public void turnRightTest(){
        robot = new Robot(0,0);
        robot.setDirection("S");
        robot.turnRight(false);
        assertEquals("W", robot.getDir());
    }


}
