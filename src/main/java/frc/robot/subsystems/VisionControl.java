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
    public VisionData visionData;
    private Shooter shooter;
    public double hoopr = 0;
    public double ballr = 0;
    public double hoopx = 0;
    public double ballx = 0;

    private int gathererOldState = 0;

    private Chassis chassis;

    // other variables
    public boolean usingAuto = false;
    private int invalid_ball_counter = 0;
    private final int invalid_ball_counter_threshold = 40;
    private static final double align_kP = 0.5;
    private FileLogging fl;
    private double counter = 0;
    private int recordCounter = 0;
    private double counterSpeed;
    private double frontRightSpeed[] = {};
    private double frontLeftSpeed[] = {};
    private double backRightSpeed[] = {};
    private double backLeftSpeed[] = {};
    private StopWatch clock = new StopWatch();
    private StopWatch sendTmer = new StopWatch();
    private UDPSender sender = new UDPSender();

    // we need to determine what to set these to

    // thresholds
    private final int MAX_SIZE = 2000;// should only need to be 750
    public static final double ballChassisThreshold = 1; // angle in degrees
    public static final double hoopChassisThreshold = 2; // angle in degrees
    public static final double maxHoopDistance = 13; // MAX distance in ft
    public static final double shooterThreshold = 50; // threshold in rpm

    private final boolean SQUARE_DRIVER_INPUTS = true;

    // edit these
    private final boolean RECORD_PATH = false;
    private final String FILE_NAME = "path4";
    private String selector;

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
                ySpeed = Math.copySign(ySpeed * ySpeed, ySpeed);
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
            // if (usingAuto && !oi.autoCollectButton()) {
            // shooter.disableManual = false;
            // shooter.gathererState = gathererOldState;
            // }
            usingAuto = false;
            Robot.PDM.setSwitchableChannel(true);
        }
    }

    public void disable() {
        if (RECORD_PATH) {
            fl.write();
        }
    }

    public boolean trackHoop(double ySpeed) {
        // visionData.Print();
        if (visionData.isHoopValid()) {
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

        if (visionData.isBallValid())
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
        fl.periodic(_forwardSpeed + " " + _sideSpeed + " " + chassis.flep + " " + chassis.frep + " " + chassis.blep
                + " " + chassis.brep + " \n");
    }

    public void update() {
        visionData = vision.getData();
        hoopr = visionData.hr;
        hoopx = visionData.hx;
        ballr = visionData.br;
        ballx = visionData.bx;
        chassis.updateEncoders();
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
                double rpm = visionData.isHoopValid() ? shooter.calculateShooterRPMS(hoopx) : 0;
                shooter.startShooter(rpm);
                if (Math.abs(shooter.getShooterRpms() - rpm) < shooterThreshold) {
                    shooterstate = shooterState.SHOOT;
                    clock.start();
                }
                break;
            case SHOOT:
                drive(0, 0);
                double rpms = visionData.isHoopValid() ? shooter.calculateShooterRPMS(hoopx) : 0;
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
        return visionData.isHoopValid();
    }
}
