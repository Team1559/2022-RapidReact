package frc.robot.subsystems;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.SparkMaxPIDController;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import frc.robot.OperatorInterface;
import frc.robot.Wiring;
import frc.robot.components.IMU;
import frc.robot.components.DevilDrive;

public class Chassis {
    private static final int TIMEOUT = 20;
    public static final double WHEEL_RADIUS_INCHES_MECANUM = 3;
    public static final double MAX_SPEED_FPS_TRACTION = 9.67 * 1.01;
    public static final double MAX_TICKS_PER_100MS = MAX_SPEED_FPS_TRACTION * 4096.0
            / (Math.PI * WHEEL_RADIUS_INCHES_MECANUM * 2.0 / 12.0) / 10.0;
    public static final double TICKS_PER_REVOLUTION = 42;
    public static final double CHASSIS_GEAR_RATIO = (60.0 / 12.0) * (26.0 / 22.0) * 1.2; // CHASSIS_GEAR_RATIO:1, ~6:1
    private DevilDrive drive;
    public CANSparkMax CANSparkMax1;
    public CANSparkMax CANSparkMax2;
    public CANSparkMax CANSparkMax3;
    public CANSparkMax CANSparkMax4;
    public RelativeEncoder flEncoder;
    public RelativeEncoder frEncoder;
    public RelativeEncoder blEncoder;
    public RelativeEncoder brEncoder;
    public double flep;
    public double frep;
    public double blep;
    public double brep;
    private OperatorInterface oi;
    public IMU imu;

    public static final double AUTO_RAMP_RATE = 0.05;
    public static final double TELEOP_RAMP_RATE = 0.0;

    // these need to be set once
    // private final double differpercent = 12 / 25.5; // percent the front needs to
    // move compared to the back
    private final double SLOWMODE_COEFFICIENT = 0.5;

    // these can be changed when needed
    private final boolean LOGDATA = false;

    /**
     * private static final double kF = 0.14614285; //F-gain = (100% X 1023) /
     * 7350 F-gain = 0.139183673 - (7350 is max speed) private static final
     * double kP = 0.475; // P-gain = (.1*1023)/(155) = 0.66 - (350 is average
     * error) private static final double kD = (5.0*kP); private static final
     * double cLR = 0.1; This was ctrl c ctrl v'd from 2020. I don't now what
     * these values mean so I don't want to delete them.
     */
    private CANSparkMax initMotor(int id) {
        CANSparkMax sparky = new CANSparkMax(id, MotorType.kBrushless);
        SparkMaxPIDController pid = sparky.getPIDController();
        final double kP = 6e-5;
        final double kI = 0;
        final double kD = 0;
        final double kIz = 0;
        final double kFF = 0.000015;
        final double kMaxOutput = 1;
        final double kMinOutput = -1;
        final double rr = 0.1;
        sparky.restoreFactoryDefaults();
        sparky.setCANTimeout(TIMEOUT);
        sparky.setClosedLoopRampRate(rr);
        sparky.setOpenLoopRampRate(rr);
        pid.setP(kP);
        pid.setI(kI);
        pid.setD(kD);
        pid.setIZone(kIz);
        pid.setFF(kFF);
        pid.setOutputRange(kMinOutput, kMaxOutput);
        return sparky;
    }

    public void autoInit() {
        this.initOdometry();
        this.setRampRate(AUTO_RAMP_RATE);
    }

    public void teleopInit() {
        this.imu.zeroYaw();
        this.setPid(6e-5, 0, 0, 0.000015);
        this.setRampRate(TELEOP_RAMP_RATE);
    }

    public void setRampRate(double rate) {
        CANSparkMax1.setClosedLoopRampRate(rate);
        CANSparkMax2.setClosedLoopRampRate(rate);
        CANSparkMax3.setClosedLoopRampRate(rate);
        CANSparkMax4.setClosedLoopRampRate(rate);
    }

    public void initEncoders() {
        flEncoder = CANSparkMax1.getEncoder();
        frEncoder = CANSparkMax2.getEncoder();
        blEncoder = CANSparkMax3.getEncoder();
        brEncoder = CANSparkMax4.getEncoder();

    }

    public Chassis(OperatorInterface oi, IMU imu) {
        this.oi = oi;
        this.imu = imu;
        CANSparkMax1 = initMotor(Wiring.FLMOTOR);
        CANSparkMax2 = initMotor(Wiring.FRMOTOR);
        CANSparkMax3 = initMotor(Wiring.BLMOTOR);
        CANSparkMax4 = initMotor(Wiring.BRMOTOR);
        // encoders
        CANSparkMax1.setInverted(true);
        CANSparkMax3.setInverted(true);
        initEncoders();

        // front = new SplitDrive(CANSparkMax1, CANSparkMax2);
        // back = new SplitDrive(CANSparkMax3, CANSparkMax4);
        drive = new DevilDrive(CANSparkMax1, CANSparkMax3, CANSparkMax2, CANSparkMax4);
        drive.setMaxOutput(30000);
    }

    public void main() {
        imu.updateValues();
        SmartDashboard.putNumber("IMU", this.imu.yaw);
        drive(-oi.pilot.getLeftY(), oi.pilot.getLeftX(), oi.pilot.getRightX());
        updateEncoders();
    }

    public void updateEncoders() {
        flep = flEncoder.getPosition();
        frep = frEncoder.getPosition();
        blep = blEncoder.getPosition();
        brep = brEncoder.getPosition();
    }

    public void drive(double ySpeed, double zRotation) {
        drive(ySpeed, 0, zRotation, true);
    }

    public void drive(double ySpeed, double xSpeed, double zRotation) {
        drive(ySpeed, xSpeed, zRotation, true);
    }

    public void drive(double ySpeed, double zRotation, boolean squareInputs) {
        drive(ySpeed, 0, zRotation, squareInputs);
    }

    public void drive(double ySpeed, double xSpeed, double zRotation, boolean squareInputs) {
        ySpeed *= oi.slowModeButton() ? SLOWMODE_COEFFICIENT : 1;
        zRotation *= oi.slowModeButton() ? SLOWMODE_COEFFICIENT : 1;
        xSpeed *= oi.slowModeButton() ? SLOWMODE_COEFFICIENT : 1;
        if (squareInputs) {
            ySpeed = Math.copySign(ySpeed * ySpeed, ySpeed);
            xSpeed = Math.copySign(xSpeed * xSpeed, xSpeed);
        }
        drive.driveCartesian(ySpeed, xSpeed, zRotation);
    }

    public void setPid(double kp, double ki, double kd, double kf) {
        setPid(kp, ki, kd, kf, 0);
    }

    public void setPid(double kp, double ki, double kd, double kf, double kiz) {
        setKP(kp);
        setKI(ki);
        setKD(kd);
        setKF(kf);
        setKIZ(kiz);
    }

    /**
     * Averages the rpm of all 4 wheels
     * 
     * @return The average velocity of the chassis wheels in RPM
     */
    public double getFrontAverageWheelRPM() {
        return (flEncoder.getVelocity() / CHASSIS_GEAR_RATIO + frEncoder.getVelocity() / CHASSIS_GEAR_RATIO) / 2;
    }

    public void setKP(double kp) {
        SparkMaxPIDController pid1 = CANSparkMax1.getPIDController();
        SparkMaxPIDController pid2 = CANSparkMax2.getPIDController();
        SparkMaxPIDController pid3 = CANSparkMax3.getPIDController();
        SparkMaxPIDController pid4 = CANSparkMax4.getPIDController();
        pid1.setP(kp);
        pid2.setP(kp);
        pid3.setP(kp);
        pid4.setP(kp);
    }

    public void setKI(double ki) {
        SparkMaxPIDController pid1 = CANSparkMax1.getPIDController();
        SparkMaxPIDController pid2 = CANSparkMax2.getPIDController();
        SparkMaxPIDController pid3 = CANSparkMax3.getPIDController();
        SparkMaxPIDController pid4 = CANSparkMax4.getPIDController();
        pid1.setI(ki);
        pid2.setI(ki);
        pid3.setI(ki);
        pid4.setI(ki);
    }

    public void setKIZ(double kiz) {
        SparkMaxPIDController pid1 = CANSparkMax1.getPIDController();
        SparkMaxPIDController pid2 = CANSparkMax2.getPIDController();
        SparkMaxPIDController pid3 = CANSparkMax3.getPIDController();
        SparkMaxPIDController pid4 = CANSparkMax4.getPIDController();
        pid1.setIZone(kiz);
        pid2.setIZone(kiz);
        pid3.setIZone(kiz);
        pid4.setIZone(kiz);
    }

    public void setKD(double kd) {
        SparkMaxPIDController pid1 = CANSparkMax1.getPIDController();
        SparkMaxPIDController pid2 = CANSparkMax2.getPIDController();
        SparkMaxPIDController pid3 = CANSparkMax3.getPIDController();
        SparkMaxPIDController pid4 = CANSparkMax4.getPIDController();
        pid1.setD(kd);
        pid2.setD(kd);
        pid3.setD(kd);
        pid4.setD(kd);
    }

    public void setKF(double kf) {
        SparkMaxPIDController pid1 = CANSparkMax1.getPIDController();
        SparkMaxPIDController pid2 = CANSparkMax2.getPIDController();
        SparkMaxPIDController pid3 = CANSparkMax3.getPIDController();
        SparkMaxPIDController pid4 = CANSparkMax4.getPIDController();
        pid1.setP(kf);
        pid2.setP(kf);
        pid3.setP(kf);
        pid4.setP(kf);
    }

    public double degreesToZRotation(double desiredAngle) {
        return (desiredAngle - this.imu.yaw) * 0.015; // TODO: modify proportion (and calibrate IMU yaw)
    }

    public double inchesToRevolutions(double inches) {
        // inches to Revolutions
        return CHASSIS_GEAR_RATIO * inches / (2 * Math.PI * WHEEL_RADIUS_INCHES_MECANUM);
    }

    public double inchesToEncoderTicks(double inches) {
        return TICKS_PER_REVOLUTION * inchesToRevolutions(inches);
        // return inches * 2.1;
    }

    public double encoderTicksToInches(double ticks) {
        return revolutionsToInches(ticks / TICKS_PER_REVOLUTION);
        // return ticks / 2.1;
    }

    public double revolutionsToInches(double revs) {
        return revs * 2 * Math.PI * WHEEL_RADIUS_INCHES_MECANUM / CHASSIS_GEAR_RATIO;
    }

    /**
     * Converts rotations per minute into feet per second
     * 
     * @param rpm The wheel speed in RPM
     * @return The current velocity in feet per second
     */
    public double rpmToFps(double rpm) {
        return (rpm * 2 * Math.PI * (WHEEL_RADIUS_INCHES_MECANUM / 12)) / 60;
    }

    public void initOdometry() {
        flEncoder.setPosition(0);
        frEncoder.setPosition(0);
        blEncoder.setPosition(0);
        brEncoder.setPosition(0);
    }

    public void disable() {}
}