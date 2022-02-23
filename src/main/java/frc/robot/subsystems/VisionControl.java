package frc.robot.subsystems;

import frc.robot.components.*;
import frc.robot.routes.*;
import frc.robot.*;

@SuppressWarnings("unused")
public class VisionControl {
    public enum autoState {
        PATH, SHOOT
    }

    public enum shooterState {
        ALIGN, WAIT, SHOOT
    }

    private autoState autostate = autoState.PATH;
    public shooterState shooterstate = shooterState.ALIGN;

    private OperatorInterface oi;
    private Vision vision = new Vision();
    private VisionData visionData;
    private IMU imu;
    private Shooter shooter;
    private double hoopr = 0;
    private double ballr = 0;
    private double hoopx = 0;

    private Chassis chassis;

    // other variables
    public boolean usingAuto = false;
    private int invalid_ball_counter = 0;
    private final int invalid_ball_counter_threshold = 20;
    private MachineLearning ml;
    private double counter = 0;
    private int recordCounter = 0;
    private double counterSpeed;
    private double frontRightSpeed[] = {};
    private double frontLeftSpeed[] = {};
    private double backRightSpeed[] = {};
    private double backLeftSpeed[] = {};

    // we need to determine what to set these to

    // thresholds
    private final int MAX_SIZE = 2000;// should only need to be 750
    private final double ballChassisThreshold = 1; // angle in degrees
    private final double hoopChassisThreshold = 1; // angle in degrees
    private final double shooterThreshold = 50; // threshold in rpm

    private final boolean SQUARE_DRIVER_INPUTS = true;

    // edit these
    private final boolean RECORD_PATH = false;
    private final String FILE_NAME = "path4";
    private String selector;

    public VisionControl(VisionData visionData, OperatorInterface oi, Chassis chassis, IMU imu,
            Shooter shooter) {
        this.selector = "";
        this.oi = oi;
        this.visionData = visionData;
        this.chassis = chassis;
        this.imu = imu;
        this.shooter = shooter;
        ml = new MachineLearning();
        if (RECORD_PATH) {
            ml.createfile(FILE_NAME);
        }
    }

    public void autoInit() {
        path4 p1 = new path4();
        double skip[] = { 0 };
        double kP = 0;
        double kI = 0;
        double kD = 0;
        double kF = 0;
        chassis.initOdometry();
        counter = 0;

        if (selector == "path1") {
            kP = 0.005;
            kI = 0.0;
            kD = 0.0;
            kF = 0.0;
            frontRightSpeed = p1.frontRightEncoderPositions;
            frontLeftSpeed = p1.frontLeftEncoderPositions;
            backRightSpeed = p1.backRightEncoderPositions;
            backLeftSpeed = p1.backLeftEncoderPositions;
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

    public void autoPeriodic() {
        switch (autostate) {
            case PATH:
                if (!RECORD_PATH) {
                    update();
                    followPath();
                }

                else {
                    System.out.println("Please enable in teleop to record a new path");
                }
                break;
            case SHOOT:
                break;
        }
    }

    public void teleopInit() {
        if (RECORD_PATH) {
            chassis.initOdometry();
        }
    }

    public void teleopPeriodic() {
        update();

        if (RECORD_PATH && recordCounter <= MAX_SIZE) {
            record(oi.pilot.getLeftY(), oi.pilot.getRightX());
            recordCounter++;
        }

        else if (RECORD_PATH && recordCounter > MAX_SIZE) {
            System.out.println("Max recording size has been reached");
        }

        // visionData.Print();

        if (oi.autoShootButton()) {
            usingAuto = true;
            trackHoop();
        }

        else if (oi.autoCollectButton()) {
            usingAuto = true;
            trackBall();
        }

        else {
            usingAuto = false;
        }
    }

    public void followPath() {
        if (counter < frontLeftSpeed.length) {
            chassis.pathDrive(ml.interpolate(counter, frontLeftSpeed), ml.interpolate(counter, frontRightSpeed),
                    ml.interpolate(counter, backLeftSpeed), ml.interpolate(counter, backRightSpeed));
            counter += counterSpeed;
        }

        else {
            chassis.drive(0, 0, false);
        }
    }

    public void disable() {
        if (RECORD_PATH) {
            ml.write();
        }
    }

    public void trackHoop() {
        if (visionData.isHoopValid()) {
            double ySpeed = -oi.pilot.getLeftY();

            if (SQUARE_DRIVER_INPUTS) {
                ySpeed = -1.0 * Math.copySign(ySpeed * ySpeed, ySpeed);
            }

            if (Math.abs(hoopr) > hoopChassisThreshold) {
                drive(ySpeed, calculateHoopRotation());
            }

            else {
                chassis.main();
            }
        }

        else {
            chassis.main();
        }
    }

    public void trackBall() {
        if (visionData.isBallValid()) {
            invalid_ball_counter = 0;
        }

        else {
            invalid_ball_counter++;
        }

        if (invalid_ball_counter < invalid_ball_counter_threshold) {
            double ySpeed = -oi.pilot.getLeftY();

            if (SQUARE_DRIVER_INPUTS) {
                ySpeed = -1.0 * Math.copySign(ySpeed * ySpeed, ySpeed);
            }

            drive(ySpeed, calculateBallRotation());
        }

        else {
            chassis.main();
        }
    }

    public void drive(double fs, double r) {
        chassis.drive(fs, r, false);
    }

    public void setAutoPath(String selector) {
        this.selector = selector;
        System.out.println("Current Path is " + this.selector);
    }

    public void record(double _forwardSpeed, double _sideSpeed) {
        ml.periodic(_forwardSpeed + " " + _sideSpeed + " " + chassis.flep + " " + chassis.frep + " " + chassis.blep
                + " " + chassis.brep + " \n");
    }

    private void update() {
        visionData = vision.getData();
        hoopr = visionData.hr;
        ballr = visionData.br;
        chassis.updateEncoders();
        imu.getvalues();
    }

    private double calculateHoopRotation() {
        double hoop_rotation = 1.0 * (hoopr / 34.0);

        if (Math.abs(hoopr) <= hoopChassisThreshold) {
            hoop_rotation = 0D;
        }

        return hoop_rotation;
    }

    private double calculateBallRotation() {
        double ball_rotation = 1.5 * (ballr / 34.0);

        if (Math.abs(ballr) <= ballChassisThreshold) {
            ball_rotation = 0D;
        }

        return -ball_rotation;
    }

    public void autoShoot() {
        switch (shooterstate) {
            case ALIGN:
                trackHoop();
                if (Math.abs(hoopr) <= hoopChassisThreshold) {
                    shooterstate = shooterState.WAIT;
                }
                break;
            case WAIT:
                double rpm = calculateShooterRPMS();
                shooter.startShooter(rpm);
                if (Math.abs(shooter.getShooterRpms() - rpm) < shooterThreshold) {
                    shooterstate = shooterState.SHOOT;
                }
                break;
            case SHOOT:
                double rpms = calculateShooterRPMS();
                shooter.startShooter(rpms);
                shooter.startFeeder();
                break;
        }
    }

    public boolean isHoopValid() {
        return visionData.isHoopValid();
    }

    private double calculateShooterRPMS() {
        double shooterRPM = 0;
        final double angle = 45;
        final double diameter = 0.5; // distance in inches
        double velocity = 0;
        if (visionData.isHoopValid()) {
            // math
            velocity = Math.sqrt(hoopx * 9.8 / Math.toDegrees(Math.sin(Math.toRadians(2 * angle))));
            shooterRPM = velocity / diameter;
            return shooterRPM;
        }
        return 0.0;
    }
}
