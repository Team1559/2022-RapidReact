package frc.robot.subsystems;

import frc.robot.components.*;

import com.ctre.phoenix.time.StopWatch;
import edu.wpi.first.wpilibj.DriverStation;
import frc.robot.*;

public class VisionControl {
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
    private int invalid_frame_counter = 0;
    private final int invalid_frame_counter_threshold = 40;
    public static final double TELEOP_ALIGN_KP = 0.5;
    public static final double AUTO_ALIGN_KP = 0.19;
    public static final double AUTO_MAX_TURN = 0.05;
    public static final double TELEOP_MAX_TURN = 0.1;
    public double maxTurn = AUTO_MAX_TURN;
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

    public boolean trackHoop(double ySpeed) {
        // visionData.Print();
        if (vision.isHoopValid()) {
            invalid_frame_counter = 0;
        } else {
            invalid_frame_counter++;
        }
        if (invalid_frame_counter < invalid_frame_counter_threshold) {
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
            invalid_frame_counter = 0;
        else
            invalid_frame_counter++;
        if (invalid_frame_counter < invalid_frame_counter_threshold) {
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
        double hoop_rotation = Math.abs(hoopr) <= hoopChassisThreshold ? 0D : -getAlignKP()*hoopr/34D;
        return Math.abs(hoop_rotation) < maxTurn ? hoop_rotation : Math.copySign(maxTurn, hoop_rotation);
    }

    private double calculateBallRotation() {
        double ball_rotation = Math.abs(ballr) <= ballChassisThreshold ? 0D : getAlignKP()*ballr/34D;
        return Math.abs(ball_rotation) < maxTurn ? ball_rotation : Math.copySign(maxTurn, ball_rotation);

    }

    public void periodic(String color) {
        if (sendTmer.getDuration() >= 1) {
            sender.send(color);
            sendTmer.start();
        }
    }

    public double getAlignKP(){
        return DriverStation.isTeleop() ? TELEOP_ALIGN_KP : AUTO_ALIGN_KP;
    }

    public double getMaxTurn(){
        return DriverStation.isTeleop() ? TELEOP_MAX_TURN : AUTO_MAX_TURN;
    }

    public boolean isHoopValid() {
        return vision.isHoopValid();
    }
}
