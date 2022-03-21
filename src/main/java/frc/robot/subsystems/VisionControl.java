package frc.robot.subsystems;

import frc.robot.components.*;
import frc.robot.routes.*;
import com.ctre.phoenix.time.StopWatch;
import frc.robot.*;

@SuppressWarnings("unused")
public class VisionControl {
    public enum autoState {
        PATH, SHOOT
    }

    public enum shooterState {
        ALIGN, WAIT, SHOOT, STOP
    }

    private autoState autostate = autoState.PATH;
    public shooterState shooterstate = shooterState.ALIGN;
    private OperatorInterface oi;
    private Vision vision = new Vision();
    private Shooter shooter;
    private Chassis chassis;
    private StopWatch clock = new StopWatch();
    private StopWatch sendTmer = new StopWatch();
    private UDPSender sender = new UDPSender();
    private final int invalid_ball_counter_threshold = 60;
    private static final double align_kP = 0.1;
    public double hoopr = 0;
    public double ballr = 0;
    public double hoopx = 0;
    public double ballx = 0;
    private int gathererOldState = 0;
    private String selector;
    public boolean usingAuto = false;
    private int invalid_ball_counter = 0;
    private FileLogging fl;
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
    public static final double ballChassisThreshold = 1; // angle in degrees
    public static final double hoopChassisThreshold = 2; // angle in degrees
    public static final double maxHoopDistance = 13; // MAX distance in ft
    public static final double shooterThreshold = 50; // threshold in rpm

    // edit these
    private final boolean SQUARE_DRIVER_INPUTS = true;
    private final boolean RECORD_PATH = false;
    private final String FILE_NAME = "path4";

    public VisionControl(OperatorInterface oi, Chassis chassis,
            Shooter shooter) {
        this.selector = "";
        this.oi = oi;
        this.chassis = chassis;
        this.shooter = shooter;
        fl = new FileLogging();
        sendTmer.start();
        if (RECORD_PATH) {
            fl.createfile(FILE_NAME);
        }
    }

    /**
     * @deprecated
     */
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

    /**
     * @deprecated
     */
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
        if (RECORD_PATH)
            chassis.initOdometry();
    }

    public void teleopPeriodic() {
        update();
        // visionData.Print();
        if (RECORD_PATH && recordCounter <= MAX_SIZE) {
            record(-oi.pilot.getLeftY(), oi.pilot.getRightX());
            recordCounter++;
        } else if (RECORD_PATH && recordCounter > MAX_SIZE) {
            System.out.println("Max recording size has been reached");
        }

        if (oi.autoSteerToHoopButton()) {
            Robot.PDM.setSwitchableChannel(true);
            usingAuto = true;
            double ySpeed = -oi.pilot.getLeftY();
            if (SQUARE_DRIVER_INPUTS)
                ySpeed = -Math.copySign(ySpeed * ySpeed, ySpeed);
            if (!trackHoop(ySpeed))
                chassis.main();
        } else if (oi.autoCollectButton()) { // <-- PDM not turned off in this case
            if (!usingAuto) {
                gathererOldState = shooter.gathererState;
            }
            usingAuto = true;
            double ySpeed = -oi.pilot.getLeftY();
            if (SQUARE_DRIVER_INPUTS)
                ySpeed = -1.0 * Math.copySign(ySpeed * ySpeed, ySpeed);
            if (!trackBall(ySpeed))
                chassis.main();
        } else {
            if (usingAuto && !oi.autoCollectButton()) {
                shooter.disableManual = false;
                shooter.gathererState = gathererOldState;
            }
            usingAuto = false;
            Robot.PDM.setSwitchableChannel(true);
        }
    }

    /**
     * Follows a preplanned path using encoder positions
     * 
     * @deprecated
     */
    public void followPath() {
        if (counter < frontLeftSpeed.length) {
            chassis.pathDrive(interpolate(counter, frontLeftSpeed), interpolate(counter, frontRightSpeed),
                    interpolate(counter, backLeftSpeed), interpolate(counter, backRightSpeed));
            counter += counterSpeed;
        } else {
            chassis.drive(0, 0, false);
        }
    }

    public void disable() {
        if (RECORD_PATH) {
            fl.write();
        }
    }

    public boolean trackHoop(double ySpeed) {
        // visionData.Print();
        if (VisionData.isHoopValid()) {
            invalid_ball_counter = 0;
        } else {
            invalid_ball_counter++;
        }
        if (invalid_ball_counter < invalid_ball_counter_threshold) {
            drive(ySpeed, calculateHoopRotation());
            return true;
        } else {
            return false;
        }
    }

    public boolean trackBall(double ySpeed) {
        shooter.disableManual = true;
        if (shooter.gathererState != Shooter.holding && shooter.gathererState != Shooter.gathererDown) {
            shooter.gathererState = Shooter.holding;
        }

        if (VisionData.isBallValid())
            invalid_ball_counter = 0;
        else
            invalid_ball_counter++;
        if (invalid_ball_counter < invalid_ball_counter_threshold) {
            drive(ySpeed, calculateBallRotation());
            return true;
        } else
            return false;
    }

    public void drive(double fs, double r) {
        chassis.drive(fs, r, false);
    }

    /**
     * @deprecated
     * @param selector Auto path to select
     */
    public void setAutoPath(String selector) {
        this.selector = selector;
        System.out.println("Current Path is " + this.selector);
    }

    /**
     * @deprecated
     */
    public void record(double _forwardSpeed, double _sideSpeed) {
        fl.addData(_forwardSpeed + " " + _sideSpeed + " " + chassis.flep + " " + chassis.frep + " " + chassis.blep
                + " " + chassis.brep + " \n");
    }

    public void update() {
        hoopr = VisionData.hr;
        hoopx = VisionData.hx;
        ballr = VisionData.br;
        ballx = VisionData.bx;
    }

    private double calculateHoopRotation() {
        double hoop_rotation = -align_kP * (hoopr / 34.0);

        if (Math.abs(hoopr) <= hoopChassisThreshold) {
            hoop_rotation = 0D;
        }

        return Math.abs(hoop_rotation) < Auto.MAX_TURN ? hoop_rotation : Math.copySign(Auto.MAX_TURN, hoop_rotation);

    }

    private double calculateBallRotation() {
        double ball_rotation = align_kP * (ballr / 34.0);
        if (Math.abs(ballr) <= ballChassisThreshold) {
            ball_rotation = 0D;
        }

        return ball_rotation;
    }

    public void periodic(String color) {
        if (sendTmer.getDuration() >= 1) {
            sender.send(color);
            sendTmer.start();
        }
    }

    public void autoShoot() {
        switch (shooterstate) {
            case ALIGN:
                trackHoop(0);
                if (Math.abs(hoopr) <= hoopChassisThreshold) {
                    shooterstate = shooterState.WAIT;
                }
                break;
            case WAIT:
                drive(0, 0);
                double rpm = VisionData.isHoopValid() ? shooter.calculateShooterRPMS(hoopx) : 0;
                shooter.startShooter(rpm);
                if (Math.abs(shooter.getShooterRpms() - rpm) < shooterThreshold) {
                    shooterstate = shooterState.SHOOT;
                    clock.start();
                }
                break;
            case SHOOT:
                drive(0, 0);
                double rpms = VisionData.isHoopValid() ? shooter.calculateShooterRPMS(hoopx) : 0;
                shooter.startShooter(rpms);
                shooter.startFeeder(shooter.feederSpeed, !shooter.disableManual);
                if (clock.getDuration() == 2) {
                    shooterstate = shooterState.STOP;
                }
                break;
            case STOP:
                drive(0, 0);
                shooter.holdFeeder();
                shooter.stopShooter();
                shooter.stopIntake();
                break;
        }
    }

    public boolean isHoopValid() {
        return VisionData.isHoopValid();
    }

    /**
     * Interpolates between 2 values in an array of doubles
     * 
     * @param counter Current position in the array
     * @param value   Array of values to interpolate between
     * @return The interpolated value
     */
    public double interpolate(double counter, double[] value) {
        int intCounter = (int) counter;
        double percent = (counter - intCounter);

        if (intCounter < value.length - 1) {
            double interpolatedValue = (value[intCounter] + (percent * (value[intCounter + 1] - value[intCounter])));
            return interpolatedValue;
        }

        else {
            return value[value.length - 1];
        }
    }
}
