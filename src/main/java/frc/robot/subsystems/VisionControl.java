package frc.robot.subsystems;

import frc.robot.components.*;
import frc.robot.*;

public class VisionControl {
    private OperatorInterface oi;
    private Vision vision;
    private VisionData visionData;
    private double hoopx = visionData.hx;
    private double hoopy = visionData.hy;
    private double hoopr = visionData.hr;
    private double ballx = visionData.bx;
    private double bally = visionData.by;
    private double ballr = visionData.br;
    private boolean wait = visionData.waitForOtherRobot;
    // thresholds
    private double chassisThreshold = 10;
    private double shooterThreshold = 10;

    // shooter variables
    private double desiredAngle;// angle of the shooter in degrees
    private double desiredPower;// desired shooter power in RPMS
    // chassis variables
    private double forward_speed = 0; // speed front to back between 0-1
    private double sidespeed = 0; // straife speed between 0-1
    private double rotation = 0; // rotation speed between 0-1
    // private Chassis chassis;
    // private Shooter shooter;

    public VisionControl(Vision vision, VisionData visionData, OperatorInterface oi) {// , Chassis chassis, Shooter shooter}) {
        this.oi = oi;
        this.vision = vision;
        this.visionData = visionData;
        // this.chassis = chassis;
        // this.shooter = shooter;
    }

    public void auto() { // pathfind to cargo, collect it, and score it
        update();
        if(visionData.isBallValid() && visionData.isHoopValid()){
            // auto code will go here
        }
        else{
            System.out.println("Invalid data... aborting");
        }
    }

    public void main() {
        while (true) {
            update();

            visionData.Print();
            if (oi.pilot.getRawButton(Buttons.autoShoot)) { // Move the chassis so it is alligned, aim the shooter, and fire the cargo
                if(visionData.isHoopValid()){
                    double error = 0;

                    // shooter.setAngle(desiredAngle);
                    // Shooter.setPower(desiredPower);
                    if (Math.abs(error) > chassisThreshold) {
                        // chassis.drive(forward_speed, sidespeed, rotation);
                    }
                    // shooter.shoot();
                }
                else{
                    System.out.println("Invalid data... aborting");
                }
            } 
            else if (oi.pilot.getRawButton(Buttons.autoCollect)) { // go collect the nearest cargo
                if(visionData.isBallValid()){
                    // shooter.gather();
                }
                else{
                    System.out.println("Invalid data... aborting");
                }
            } 
            else {
                break;
            }
        }
    }

    private void update() {
        visionData = vision.getData();
        hoopx = visionData.hx;
        hoopy = visionData.hy;
        hoopr = visionData.hr;
        ballx = visionData.bx;
        bally = visionData.by;
        ballr = visionData.br;
        wait = visionData.waitForOtherRobot;
    }

    private void calculateShooter() {
        // desiredAngle = __calculated_angle__;
        // desiredPower = __calculated_RPMS__;

    }

    private void calculateChassis() {
        // forward_speed = __calculated_forward_speed__;
        // sidespeed = __calculated_side_speed__;
        // rotation = __calculated_rotation__;
    }
}
