package frc.robot;

import edu.wpi.first.wpilibj.XboxController;

public class DTXboxController extends XboxController {
    public boolean isDPadPressed = false;
    public DTXboxController(int port) {
        super(port);
    }

    public boolean getDpad(int angle) {
        int pov = getPOV();
        if(angle == -1 && pov != -1) {
            return true;
        }
        else if(pov == angle){
            return true;
        }
        else{
            return false;
        }
    }
    public int getRawDPad(){
        return getPOV();
    }

    public boolean isDpadPressed() {
        return getPOV() != -1;
    }

    public boolean getDPadPress(int angle) {
        if (!isDPadPressed) {
            isDPadPressed = true;
            if(angle == -1 && getPOV() != -1) {
                return true;
            }
            if(getPOV() == angle){
                return true;
            }
            else{
                return false;
            }
        } else {
            if (getPOV() == -1) {
                isDPadPressed = false;
            }
            return false;
        }
    }

    public int getRawDPadPress() {
        if (!isDPadPressed) {
            isDPadPressed = true;
            return getPOV();
        } else {
            if (getPOV() == -1) {
                isDPadPressed = false;
            }
            return -1;
        }
    }
}
