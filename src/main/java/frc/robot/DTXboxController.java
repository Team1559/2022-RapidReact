package frc.robot;

import edu.wpi.first.wpilibj.XboxController;

public class DTXboxController extends XboxController {
    private boolean wasDpadPressed = false;

    public DTXboxController(int port) {
        super(port);
    }

    public boolean getDpad(int angle) { // name should be changed
        int pov = getPOV();
        return (angle == -1 && pov != -1) || (pov == angle);
    }

    public int getRawDPad() {
        return getPOV();
    }

    public boolean isDpadPressed() {
        return getPOV() != -1;
    }
    
    /**
     * updates the wasDpadPressed field and returns the old value for use
     * 
     * @return wasDpadPressed 
     */
    public boolean updateWasDpadPressed(){
        boolean b = wasDpadPressed; // get current wasDpadPressed
        wasDpadPressed = isDpadPressed(); // update wasDpadPressed
        return b;
    }

    public boolean getDPadPress(int angle) {
        return !updateWasDpadPressed() && ((angle == -1 && getPOV() != -1) || (getPOV() == angle));
    }

    public int getRawDPadPress() {
        return !updateWasDpadPressed() ? getPOV() : -1;
    }
}
