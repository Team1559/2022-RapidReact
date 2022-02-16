package frc.robot.subsystems;
import frc.robot.components.*;
import frc.robot.routes.*;
import frc.robot.*;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

@SuppressWarnings("unused")
public class VisionControl {
    private OperatorInterface oi;
    private Vision vision;
    private VisionData visionData;
    private IMU imu;
    private PIDController hoopDistancePid;

    private double hoopx = 0;
    private double hoopy = 0;
    private double hoopr = 0;
    private double ballx = 0;
    private double bally = 0;
    private double ballr = 0;
    private boolean wait = false;

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

    //other variables
    public boolean usingAuto = false;
    private int invalid_ball_counter = 0;    
    private final int invalid_ball_counter_threshold = 20;
    private MachineLearning ml;
    private path4 p1 = new path4();
    private double skip[] = {0};
    public double counter = 0;
    private double kP = 0;
    private double kI = 0;
    private double kD = 0;
    private double kF = 0;
    private boolean teachTheAI = true;
    private double counterSpeed;
    private double frontRightSpeed[] = {};
    private double frontLeftSpeed[] = {};
    private double backRightSpeed[] = {};
    private double backLeftSpeed[] = {};

    //we need to determine what to set these to
    
    // thresholds
    private final double ballChassisThreshold = 1; //angle in degrees
    private final double hoopChassisThreshold = 1; //angle in degrees
    private final double shooterThreshold = 2;

    private final double[] hoopRange = {3, 50};//min, max in feet

    private final boolean SQUARE_DRIVER_INPUTS = true;
    private final double kpasta = 0.3;
    private final double kicing = 0;
    private final double kdonut = 0.001;

    //edit these
    private final boolean RECORD_PATH = false;
    private final String FILE_NAME = "path4";
    private String selector; 
    
    // private Shooter shooter;

    public VisionControl(Vision vision, VisionData visionData, OperatorInterface oi, Chassis chassis, IMU imu) {// , Shooter shooter}) {
        this.selector = "";
        this.oi = oi;
        this.vision = vision;
        this.visionData = visionData;
        this.chassis = chassis;
        this.imu = imu;
        ml = new MachineLearning(RECORD_PATH, FILE_NAME);
        hoopDistancePid = new PIDController(kpasta, kicing, kdonut);
        // this.shooter = shooter;
    }

    public VisionControl(Vision vision, VisionData visionData, OperatorInterface oi, Chassis chassis, IMU imu, String selector) {// , Shooter shooter}) {
        this.selector = selector;
        this.oi = oi;
        this.vision = vision;
        this.visionData = visionData;
        this.chassis = chassis;
        this.imu = imu;
        ml = new MachineLearning(RECORD_PATH, FILE_NAME);
        // this.shooter = shooter;
    }

    public void autoInit() {
        chassis.initOdometry();
        counter = 0;
        
        if(selector == "default") {
            kP = 0.005; //.03
            kI = 0.0;
            kD = 0.0;
            kF = 0.0;
            frontRightSpeed = p1.generated_frontRightEncoderPositions;
            frontLeftSpeed = p1.generated_frontLeftEncoderPositions;
            backRightSpeed = p1.generated_backRightEncoderPositions;
            backLeftSpeed = p1.generated_backLeftEncoderPositions;
            counterSpeed = 1.0; 
        }

        else {
            kP = 0.0;
            kI = 0.0;
            kD = 0.0;
            kF = 0.0;
            frontRightSpeed = skip;
            frontLeftSpeed = skip;
            backRightSpeed = skip;
            backLeftSpeed = skip;
            counterSpeed = 0.0;
        }

        chassis.setPid(kP, kI, kD, kF);
    }

    public void autoPeriodic() { // pathfind to cargo, collect it, and score it
        imu.getvalues();

        if(!RECORD_PATH) {
            update();
            followPath();
        }

        else {
            System.out.println("Please enable in teleop to record a new path");
        }
    }

    public void main() {
        boolean new_ball_data = false;
        update();
        imu.getvalues();

        if(RECORD_PATH) {
            record(oi.pilot.getLeftY(), oi.pilot.getRightX());
        }

        // visionData.Print();
        
        if (oi.autoShootButton()) { // Move the chassis so it is alligned, aim the shooter, and fire the cargo
            usingAuto = true;
            
            if(visionData.isHoopValid()) {
                calculateHoopChassis();
                // shooter.setAngle(desiredAngle);
                // Shooter.setPower(desiredPower);
                if (Math.abs(hoopr) > hoopChassisThreshold) {
                    drive(oi.pilot.getLeftY(), hoop_sidespeed, hoop_rotation);
                }

                else {
                    chassis.main();
                    // shooter.shoot();
                    
                }
                
            }

            else {
                chassis.main();
            }
        }

        else if (oi.autoCollectButton()) { // go collect the nearest cargo
            usingAuto = true;
            if(visionData.isBallValid()) {
                new_ball_data = true;
                invalid_ball_counter = 0;
            } 

            else {
                invalid_ball_counter++;
            }

          //  System.out.println("Got data? " + new_ball_data);
            
            if(invalid_ball_counter < invalid_ball_counter_threshold) {
                calculateBallChassis();
                // printData();
                double ySpeed = -oi.pilot.getLeftY();

                if(SQUARE_DRIVER_INPUTS) {
                    ySpeed = -1.0 * Math.copySign(ySpeed * ySpeed, ySpeed);
                }
                SmartDashboard.putNumber("rotation", -ball_rotation);
                drive(ySpeed, 0 , -ball_rotation);
            }
            
            else {
                //System.out.println("Invalid data... remaining in manual control");
                chassis.main();
            }
        } 
        else {
            usingAuto = false;
        }

        System.out.println(invalid_ball_counter);

    }
    
    public void followPath() {
        if(counter < frontLeftSpeed.length) {
            System.out.println("driving, counter is : " + counter);
            chassis.pathDrive(ml.interpolate(counter, frontLeftSpeed), ml.interpolate(counter, frontRightSpeed), ml.interpolate(counter, backLeftSpeed), ml.interpolate(counter, backRightSpeed));
            counter += counterSpeed; //good at 1.5 with .03 kp ONLY FOR BR PATH
        }
        else{
            chassis.drive(0, 0, 0, false);
        }
    }

    public void disable() {
        if(RECORD_PATH) {
            ml.write();
        }
    }

    public void drive(double fs, double ss, double r) {
        chassis.drive(fs, ss , r, false);
    }

    public void setAutoPath(String selector) {
        this.selector = selector;
        System.out.println("Current Path is " + this.selector);
    }

    public void record(double _forwardSpeed, double _sideSpeed) {
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
        chassis.updateEncoders();
    }

    private void calculateShooter() {
        // desiredAngle = __calculated_angle__;
        // desiredPower = __calculated_RPMS__;
    }

    private void calculateHoopChassis() {
        System.out.println(hoopr);
        hoop_rotation = 1.0 * (hoopr / 34.0);
        //hoop_rotation = -pid.calculate(balla, 0);
        /*if(hoopx < hoopRange[0]) {
            hoop_forward_speed = hoopDistancePid.calculate(hoopx, hoopRange[0] + 3);
        }

        else if(hoopx > hoopRange[1]) {
            hoop_forward_speed = hoopDistancePid.calculate(hoopx, hoopRange[1] - 3);
        }

        else {
            hoop_forward_speed = 0D;
        }*/

        if(Math.abs(hoopr) <= hoopChassisThreshold) {
            hoop_rotation = 0D;
            System.out.println("set to zero");
        }
    }

    private void calculateBallChassis() {
        // ball_forward_speed = __calculated_forward_speed__;
        // ball_sidespeed = __calculated_side_speed__;
        ball_rotation = 1.5 * (ballr / 34.0);
        // ball_rotation = -pid.calculate(balla, 0);

        if(Math.abs(ballr) <= ballChassisThreshold) {
            ball_rotation = 0D;
        }

    }

    private void printData() {
        System.out.println("rotation value is " + ball_rotation);
    }
}
