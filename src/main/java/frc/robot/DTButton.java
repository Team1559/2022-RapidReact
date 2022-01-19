package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
// Mike, this one's for you to handle

public class DTButton {
    private final Joystick joystick;
    private final int      buttonID;

    private boolean old;
    private boolean current;

    public DTButton(Joystick joystick, int buttonID) {
        this.joystick = joystick;
        this.buttonID = buttonID;
    }

    public void update() {
        update(this.joystick.getRawButton(this.buttonID));
    }

    public void update(boolean b) {
        this.old = this.current;
        this.current = b;
    }

    /**
     * @return if button is being held down
     */
    public boolean isDown() {
        return this.current;
    }

    /**
     * @return Falling edge of button press
     */
    public boolean isReleased() {
        return this.old && !this.current;
    }
}
