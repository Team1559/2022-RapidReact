package frc.robot.subsystems;

import frc.robot.components.*;
import frc.robot.Constants;
import frc.robot.OperatorInterface;

public class VisionControl {
    private OperatorInterface oi;
    private Vision vision;
    private VisionData visionData;
    // private Chassis chassis;
    // private Shooter shooter;

    public VisionControl(Vision vision, VisionData visionData, OperatorInterface oi) {//, Chassis chassis, Shooter shooter}) {
        this.oi = oi;
        this.vision = vision;
        this.visionData = visionData;
        // this.chassis = chassis;
        // this.shooter = shooter;
    }
    public void auto() {
        //pathfind to cargo, collect it, and score it
    }
    public void main() {
        while(true) {
            visionData = vision.getData();
            double hoopx = visionData.hx;
            double hoopy = visionData.hy; 
            double hoopr = visionData.hr;
            double ballx = visionData.bx;
            double bally = visionData.by;
            double ballr = visionData.br;
            boolean wait = visionData.waitForOtherRobot;
            
            visionData.Print();
            if(oi.pilot.getRawButton(Constants.autoShoot)) {
                //Move the chassis so it is alligned, aim the shooter, and fire the cargo
            }
            else if(oi.pilot.getRawButton(Constants.autoCollect)) {
                //go collect the nearest cargo
            }
            else {
                break;
            }
        }
    }
}
