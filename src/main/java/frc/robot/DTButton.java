package frc.robot;

import edu.wpi.first.wpilibj.Joystick;

public class DTButton {
    private final Joystick joystick;
    private final int      buttonID;

    private boolean old;
    private boolean current;

    public DTButton(Joystick j, int buttonID) {
        this.joystick = j;
        this.buttonID = buttonID;
    }

    public void update() {
        update(joystick.getRawButton(buttonID));
    }

    public void update(boolean b) {
        old = current;
        current = b;
    }

    /**
     * @return if button is being held down
     */
    public boolean isDown() {
        return current;
    }

    /**
     * @return Falling edge of button press
     */
    public boolean isReleased() {
        return old && !current;
    }
}
