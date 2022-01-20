package frc.robot;

import edu.wpi.first.wpilibj.XboxController;

public class DTXboxController extends XboxController {
    public DTXboxController(int port) {
        super(port);
    }

    public boolean isDpadUp() {
        return getPOV() == 0;
    }

    public boolean isDpadRight() {
        return getPOV() == 90;
    }

    public boolean isDpadDown() {
        return getPOV() == 180;
    }

    public boolean isDpadLeft() {
        return getPOV() == 270;
    }

    public boolean isDpadPressed() {
        return getPOV() != -1;
    }
}
