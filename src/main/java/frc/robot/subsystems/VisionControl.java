package frc.robot.subsystems;

import frc.robot.components.*;
import frc.robot.*;
import frc.robot.routes.*;

@SuppressWarnings("unused")
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
    private double hoopChassisThreshold = 2;
    private double shooterThreshold = 2;

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
 
    private MachineLearning ml;
    private path1 p1 = new path1();

    private double skip[] = {0};
    public double counter = 0;
    private double kP = 0;
    private boolean teachTheAI = true;
    private double counterSpeed;
    private double frontRightSpeed[] = {};
    private double frontLeftSpeed[] = {};
    private double backRightSpeed[] = {};
    private double backLeftSpeed[] = {};


    //edit these
    private String selector = "path1"; 

    private final boolean SQUARE_DRIVER_INPUTS = true;
    private final boolean RECORD_PATH = true;
    private final String FILE_NAME = "path1";
    // private Shooter shooter;

    public VisionControl(Vision vision, VisionData visionData, OperatorInterface oi, Chassis chassis) {// , Shooter shooter}) {
        this.oi = oi;
        this.vision = vision;
        this.visionData = visionData;
        this.chassis = chassis;
        ml = new MachineLearning(RECORD_PATH, FILE_NAME);
        // this.shooter = shooter;
    }

    public void autoInit() {
        chassis.initOdometry();
        counter = 0;
        if(selector == "path1"){
            kP = 0.025; //.03
            frontRightSpeed = p1.generated_frontRightEncoderPositions;
            frontLeftSpeed = p1.generated_frontLeftEncoderPositions;
            backRightSpeed = p1.generated_backRightEncoderPositions;
            backLeftSpeed = p1.generated_backLeftEncoderPositions;
            counterSpeed = 1.0; 
          }
          else{
            kP = 0.0;
            frontRightSpeed = skip;
            frontLeftSpeed = skip;
            backRightSpeed = skip;
            backLeftSpeed = skip;
            counterSpeed = 0.0;
          }
    }

    public void autoPeriodic() { // pathfind to cargo, collect it, and score it
        update();
        followPath();
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
    
    public void followPath(){
        if(counter < frontLeftSpeed.length){
            chassis.pathDrive(ml.interpolate(counter, frontLeftSpeed), ml.interpolate(counter, frontRightSpeed), ml.interpolate(counter, backLeftSpeed), ml.interpolate(counter, backRightSpeed));
            counter += counterSpeed; //good at 1.5 with .03 kp ONLY FOR BR PATH
        }
    }

    public void disable(){
        if(RECORD_PATH){
            ml.write();
        }
    }
    public void drive(double fs, double ss, double r){
        chassis.drive(fs, ss , r, false);
    }

    public void record(double _forwardSpeed, double _sideSpeed){
        chassis.updateEncoders();
        ml.periodic(_forwardSpeed, _sideSpeed, chassis.flep, chassis.frep, chassis.blep, chassis.brep);
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
