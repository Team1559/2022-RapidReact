package frc.robot;

import edu.wpi.first.wpilibj.XboxController;

public class DTXboxController extends XboxController {
    public boolean isDPadPressed = false;

    public DTXboxController(int port) {
        super(port);
    }

    public boolean getDpad(int angle) {
        int pov = getPOV();

        if (angle == -1 && pov != -1) {
            return true;
        }

        else if (pov == angle) {
            return true;
        }

        else {
            return false;
        }
    }

    public int getRawDPad() {
        return getPOV();
    }

    public boolean getDPad(int angle) {
        if (getPOV() == angle) {
            return true;
        }

        else {
            return false;
        }
    }

    public boolean isDpadPressed() {
        return getPOV() != -1;
    }

    public boolean getDPadPress(int angle) {
        if (!isDPadPressed) {
            isDPadPressed = isDpadPressed();

            if (angle == -1 && getPOV() != -1) {
                return true;
            }

            if (getPOV() == angle) {
                return true;
            }

            else {
                return false;
            }
        }

        else {
            isDPadPressed = isDpadPressed();

            return false;
        }
    }

    public int getRawDPadPress() {
        if (!isDPadPressed) {
            isDPadPressed = isDpadPressed();
            return getPOV();
        }

        else {
            isDPadPressed = isDpadPressed();

            return -1;
        }
    }
}
