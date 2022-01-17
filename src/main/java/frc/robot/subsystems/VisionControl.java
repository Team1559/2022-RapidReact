package frc.robot.subsystems;

import frc.robot.Constants;
import frc.robot.OperatorInterface;

public class VisionControl {
    private OperatorInterface oi;
    // private Chassis chassis;
    // private Shooter shooter;
    public VisionControl(OperatorInterface oi){//, Chassis chassis, Shooter shooter}){
        this.oi = oi;
        // this.chassis = chassis;
        // this.shooter = shooter;
    }
    public void main(){
        while(true){
            if(oi.pilot.getRawButton(Constants.autoShoot)){
                //Move the chassis so it is alligned, aim the shooter, and fire the cargo
            }
            else if(oi.pilot.getRawButton(Constants.autoCollect)){
                //go collect the nearest cargo
            }
            else {
                break;
            }
        }
    }
}
