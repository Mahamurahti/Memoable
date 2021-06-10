package noteApp.view;

import java.awt.event.KeyEvent;

import java.awt.*;

/**
 * Controller for keyboard commands. A robot will execute these commands.
 *
 * @author Eric Keränen
 */
public class KeyControl {

    private Robot robot;

    public KeyControl(){
        try{
            robot = new Robot();
            robot.setAutoDelay(10);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void copy(){
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_C);
        robot.keyRelease(KeyEvent.VK_C);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }

    public void paste(){
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }

    public void cut(){
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_X);
        robot.keyRelease(KeyEvent.VK_X);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }

    public void undo(){
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_Z);
        robot.keyRelease(KeyEvent.VK_Z);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }

    public void redo(){
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_Y);
        robot.keyRelease(KeyEvent.VK_Y);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }
}
