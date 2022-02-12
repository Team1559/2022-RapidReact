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
    private double ballChassisThreshold = 2;
    private double hoopChassisThreshold = 10;
    private double shooterThreshold = 10;

    // shooter variables
    private double desiredAngle;// angle of the shooter in degrees
    private double desiredPower;// desired shooter power in RPMS
    // chassis variables
    private double hoop_forward_speed = 0; // speed front to back between 0-1
    private double hoop_sidespeed = 0; // straife speed between 0-1
    private double hoop_rotation = 0; // rotation speed between 0-1
    private double ball_forward_speed = 0; // speed front to back between 0-1
    private double ball_sidespeed = 0; // straife speed between 0-1
    private double ball_rotation = 0; // rotation speed between 0-1
    private Chassis chassis;
    public boolean usingAuto = false;
    private int invalid_ball_counter = 0;    
    private final int invalid_ball_counter_threshold = 20;
    private final boolean SQUARE_DRIVER_INPUTS = true;
    private MachineLearning ml= new MachineLearning();
    private final boolean RECORD_PATH = true;


    // private Shooter shooter;

    public VisionControl(Vision vision, VisionData visionData, OperatorInterface oi, Chassis chassis) {// , Shooter shooter}) {
        this.oi = oi;
        this.vision = vision;
        this.visionData = visionData;
        this.chassis = chassis;
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

    public void record(double _forwardSpeed, double _sideSpeed){
        ml.periodic(_forwardSpeed, _sideSpeed, chassis.flep, chassis.frep, chassis.blep, chassis.brep);
    }

    public void main() {
        if(RECORD_PATH){
            record(oi.pilot.getLeftY(), oi.pilot.getRightX());
        }
        if (true) {
            update();
            boolean new_ball_data = false;
            visionData.Print();
            if (oi.autoShootButton()) { // Move the chassis so it is alligned, aim the shooter, and fire the cargo
                usingAuto = true;
                if(visionData.isHoopValid()){
                    double error = 0;

                    // shooter.setAngle(desiredAngle);
                    // Shooter.setPower(desiredPower);
                    if (Math.abs(error) > hoopChassisThreshold) {
                        drive(hoop_forward_speed, hoop_sidespeed, hoop_rotation);
                    }
                    else{
                        // shooter.shoot();
                        
                    }
                }
                else{
                    System.out.println("Invalid data... aborting");
                }
            }
            else if (oi.autoCollectButton()) { // go collect the nearest cargo
                if(visionData.isBallValid()){
                    new_ball_data = true;
                    invalid_ball_counter = 0;
                } else 
                    invalid_ball_counter++;
                System.out.println("Got data? " + new_ball_data);
                if(invalid_ball_counter < invalid_ball_counter_threshold){
                    System.out.println("in auto");
                    double ball_rotation = calculateBallChassis();
                    printData();
                    double ySpeed = -oi.pilot.getLeftY();
                    if(SQUARE_DRIVER_INPUTS){
                        ySpeed = Math.copySign(ySpeed * ySpeed, ySpeed);
                    }
                    drive(ySpeed, 0 , ball_rotation);
                }
                else{
                    System.out.println("Invalid data... remaining in manual control");
                    chassis.main();
                }
            } 
            else if (oi.autoShootButton()){
                if(visionData.isHoopValid()){
                    double hoop_rotation = calculateHoopChassis();
                    drive(0,0,hoop_rotation);
                }
            }
            else {
                usingAuto = false;
            }
        }
    }
    public void disable(){
        ml.write();
    }
    public void drive(double fs, double ss, double r){
        chassis.drive(fs, ss , r, false);
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

    private double calculateHoopChassis() {
        this.hoop_rotation = 0.5 * (hoopr / 34.0);
        //hoop_rotation = -pid.calculate(balla, 0);
        if(Math.abs(hoopr) <= hoopChassisThreshold)
            this.hoop_rotation = 0;
        return this.hoop_rotation;
    }
    private double calculateBallChassis() {
        // ball_forward_speed = __calculated_forward_speed__;
        // ball_sidespeed = __calculated_side_speed__;
        this.ball_rotation = 0.5 * (ballr / 34.0);
    //    ball_rotation = -pid.calculate(balla, 0);
        if(Math.abs(ballr) <= ballChassisThreshold)
            this.ball_rotation = 0;
        return this.ball_rotation;
    }
    private void printData(){
        System.out.println("rotation value is " + ball_rotation);
    }
}
