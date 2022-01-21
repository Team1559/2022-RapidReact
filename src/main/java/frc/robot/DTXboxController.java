package frc.robot;

import edu.wpi.first.wpilibj.XboxController;

public class DTXboxController extends XboxController {
    public DTXboxController(int port) {
        super(port);
    }

    public boolean isDpadUp() {
        int pov = getPOV();
        return pov == 315 || pov == 0 || pov == 45;
    }

    public boolean isDpadRight() {
        int pov = getPOV();
        return pov == 45 || pov == 90 || pov == 135;
    }

    public boolean isDpadDown() {
        int pov = getPOV();
        return pov == 135 || pov == 180 || pov == 225;
    }

    public boolean isDpadLeft() {
        int pov = getPOV();
        return pov == 225 || pov == 270 || pov == 315;
    }

    public boolean isDpadPressed() {
        return getPOV() != -1;
    }

    public int getDpad() {
        return getPOV();
    }
}
