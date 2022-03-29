package frc.robot.subsystems;

import frc.robot.components.*;

import com.ctre.phoenix.time.StopWatch;

import frc.robot.*;

public class VisionControl {
    enum ShooterState {
        ALIGN, WAIT, SHOOT, STOP
    }

    public ShooterState shooterstate = ShooterState.ALIGN;

    private OperatorInterface oi;
    private Vision vision = new Vision();

    private Shooter shooter;
    private Chassis chassis;
    private Robot robot;

    public double hoopr = 0;
    public double ballr = 0;
    public double hoopx = 0;
    public double ballx = 0;


    // other variables
    public boolean usingAuto = false;
    private int invalid_ball_counter = 0;
    private final int invalid_ball_counter_threshold = 40;
    public static final double teleop_align_kP = 0.5;
    public static final double auto_align_kP = 0.19;
    public static final double AUTO_MAX_TURN = 0.05;
    public static final double TELEOP_MAX_TURN = 0.1;
    public double align_kP = auto_align_kP;
    public double maxTurn = AUTO_MAX_TURN;
    private StopWatch clock = new StopWatch();
    private StopWatch sendTmer = new StopWatch();
    private UDPSender sender = new UDPSender();

    // we need to determine what to set these to

    // thresholds
    public static final double ballChassisThreshold = 1; // angle in degrees
    public static final double hoopChassisThreshold = 2; // angle in degrees
    public static final double maxHoopDistance = 13; // MAX distance in ft
    public static final double shooterThreshold = 50; // threshold in rpm

    private final boolean SQUARE_DRIVER_INPUTS = true;

    public VisionControl(Robot robot, OperatorInterface oi, Chassis chassis,
            Shooter shooter) {
        this.robot = robot;
        this.oi = oi;
        this.chassis = chassis;
        this.shooter = shooter;
        sendTmer.start();
    }

    public void teleopInit() {}

    public void teleopPeriodic() {
        update();
        // visionData.Print();
        if (oi.autoSteerToHoopButton()) {
            robot.PDM.setSwitchableChannel(true);
            usingAuto = true;
            double ySpeed = -oi.pilot.getLeftY();
            if (SQUARE_DRIVER_INPUTS)
                ySpeed = Math.copySign(ySpeed * ySpeed, ySpeed);
            if (!trackHoop(ySpeed))
                chassis.main();
        } else if (oi.autoCollectButton()) { // <-- PDM not turned off in this case
            usingAuto = true;
            double ySpeed = -oi.pilot.getLeftY();
            if (SQUARE_DRIVER_INPUTS)
                ySpeed = -1.0 * Math.copySign(ySpeed * ySpeed, ySpeed);
            if (!trackBall(ySpeed))
                chassis.main();
        } else {
            usingAuto = false;
            robot.PDM.setSwitchableChannel(true);
        }
    }

    public void disable() {}

    public boolean trackHoop(double ySpeed) {
        // visionData.Print();
        if (vision.isHoopValid()) {
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
        if (shooter.gathererState != IntakeState.HOLDING && shooter.gathererState != IntakeState.DOWN) {
            shooter.gathererState = IntakeState.HOLDING;
        }

        if (vision.isBallValid())
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

    public void update() {
        vision.update();
        hoopr = vision.hr + 0.5;
        hoopx = vision.hx;
        ballr = vision.br;
        ballx = vision.bx;
        chassis.updateEncoders();
    }

    private double calculateHoopRotation() {
        double hoop_rotation = -align_kP * (hoopr / 34.0);

        if (Math.abs(hoopr) <= hoopChassisThreshold) {
            hoop_rotation = 0D;
        }
        return Math.abs(hoop_rotation) < maxTurn ? hoop_rotation : Math.copySign(maxTurn, hoop_rotation);
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
                    shooterstate = ShooterState.WAIT;
                }
                break;
            case WAIT:
                drive(0, 0);
                double rpm = vision.isHoopValid() ? shooter.calculateShooterRPMS(hoopx) : 0;
                shooter.startShooter(rpm);
                if (Math.abs(shooter.getShooterRpms() - rpm) < shooterThreshold) {
                    shooterstate = ShooterState.SHOOT;
                    clock.start();
                }
                break;
            case SHOOT:
                drive(0, 0);
                double rpms = vision.isHoopValid() ? shooter.calculateShooterRPMS(hoopx) : 0;
                shooter.startShooter(rpms);
                shooter.startFeeder(shooter.feederSpeed, !shooter.disableManual);
                if (clock.getDuration() == 2) {
                    shooterstate = ShooterState.STOP;
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
        return vision.isHoopValid();
    }
}
