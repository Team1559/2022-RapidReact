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
import frc.robot.components.FileLogging;

/**
 * All things chassis
 */
public class Chassis implements Runnable {
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
    private FileLogging fl;
    private Thread thread;

    public static final double AUTO_RAMP_RATE = 0.2;
    public static final double TELEOP_RAMP_RATE = 0.0;

    // these need to be set once
    // private final double differpercent = 12 / 25.5; // percent the front needs to
    // move compared to the back
    private final double SLOWMODE_COEFFICIENT = 0.5;

    // these can be changed when needed
    private final boolean LOGDATA = false;

    /**
     * Creates a Sparkmax object and sets default values for the pid controller
     * 
     * @param id The CAN ID of the Sparkmax
     * @return The Sparkmax object
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

    /**
     * Preps the chassis for auto
     */
    public void autoInit() {
        this.initOdometry();
        this.setRampRate(AUTO_RAMP_RATE);
    }

    /**
     * Preps the chassis for teleop
     */
    public void teleopInit() {
        this.imu.zeroYaw();
        this.setPid(6e-5, 0, 0, 0.000015);
        this.setRampRate(TELEOP_RAMP_RATE);
    }

    /**
     * Sets the ramp rate of the chassis motors
     * 
     * @param rate The time in seconds for the motors to reach full power
     */
    public void setRampRate(double rate) {
        CANSparkMax1.setClosedLoopRampRate(rate);
        CANSparkMax2.setClosedLoopRampRate(rate);
        CANSparkMax3.setClosedLoopRampRate(rate);
        CANSparkMax4.setClosedLoopRampRate(rate);
        CANSparkMax1.setOpenLoopRampRate(rate);
        CANSparkMax2.setOpenLoopRampRate(rate);
        CANSparkMax3.setOpenLoopRampRate(rate);
        CANSparkMax4.setOpenLoopRampRate(rate);
    }

    /**
     * Creates the encoder objects
     */
    private void initEncoders() {
        flEncoder = CANSparkMax1.getEncoder();
        frEncoder = CANSparkMax2.getEncoder();
        blEncoder = CANSparkMax3.getEncoder();
        brEncoder = CANSparkMax4.getEncoder();

    }

    /**
     * Creates a new Chassis object, it handles all aspects of the drivetrain for
     * the robot
     * 
     * @param oi  OperatorInterface used to get drive values from the controller
     * @param imu Imu is used to calculate the rotation error of the drivetrain
     */
    public Chassis(OperatorInterface oi, IMU imu) {
        thread = new Thread(this);
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
        drive = new DevilDrive(CANSparkMax1, CANSparkMax2, CANSparkMax3, CANSparkMax4);
        fl = new FileLogging();
        if (LOGDATA) {
            fl.createfile("encoders");
        }
        thread.start();
    }

    /**
     * main method run during periodic
     */
    public void main() {
        SmartDashboard.putNumber("IMU", this.imu.yaw);
        drive(oi.pilot.getLeftY(), oi.pilot.getLeftX(), oi.pilot.getRightX());
        if (LOGDATA) {
            SmartDashboard.putNumber("Front left encoder velocity is: ", flEncoder.getVelocity());
            SmartDashboard.putNumber("Front right encoder velocity is: ", frEncoder.getVelocity());
            SmartDashboard.putNumber("Back left encoder velocity is: ", blEncoder.getVelocity());
            SmartDashboard.putNumber("Back right encoder velocity is: ", brEncoder.getVelocity());
            fl.addData(oi.pilot.getLeftY() + " " + oi.pilot.getRightX() + " " + flEncoder.getVelocity() + " "
                    + frEncoder.getVelocity() + " " + blEncoder.getVelocity() + brEncoder.getVelocity() + " \n");
        }
    }

    /**
     * Updates the encoder positions
     */
    private void updateEncoders() {
        flep = -flEncoder.getPosition();
        frep = -frEncoder.getPosition();
        blep = -blEncoder.getPosition();
        brep = -brEncoder.getPosition();
    }

    /**
     * Runs periodically
     */
    @Override
    public void run() {
        while (true) {
            updateEncoders();
        }
    }

    /**
     * Drives the chassis in velocity mode
     * 
     * @param forwardSpeed The speed forward and backwards
     * @param rotation     The rotational speed
     */
    public void drive(double forwardSpeed, double rotation) {
        drive(forwardSpeed, 0, rotation, true);
    }

    /**
     * Drives the chassis in velocity mode
     * 
     * @param forwardSpeed The speed forward and backwards
     * @param sideSpeed    The straife speed
     * @param rotation     The rotational speed
     */
    public void drive(double forwardSpeed, double sideSpeed, double rotation) {
        drive(forwardSpeed, sideSpeed, rotation, true);
    }

    /**
     * Drives the chassis in velocity mode
     * 
     * @param forwardSpeed The speed forward and backwards
     * @param rotation     The rotational speed
     * @param squareInputs Whether or not to square the driver inputs
     */
    public void drive(double forwardSpeed, double rotation, boolean squareInputs) {
        drive(forwardSpeed, 0, rotation, squareInputs);
    }

    /**
     * Drives the chassis in velocity mode
     * 
     * @param forwardSpeed The speed forward and backwards
     * @param sideSpeed    The straife speed
     * @param rotation     The rotational speed
     * @param squareInputs Whether or not to square the driver inputs
     */
    public void drive(double forwardSpeed, double sideSpeed, double rotation, boolean squareInputs) {
        forwardSpeed *= oi.slowModeButton() ? SLOWMODE_COEFFICIENT : 1;
        rotation *= oi.slowModeButton() ? SLOWMODE_COEFFICIENT : 1;
        sideSpeed *= oi.slowModeButton() ? SLOWMODE_COEFFICIENT : 1;
        drive.driveCartesian(-forwardSpeed, sideSpeed, rotation, squareInputs);
    }

    /**
     * Drives the chassis according to the provided encoder positions
     * 
     * @param fl The position for the front left motor
     * @param fr The position for the front right motor
     * @param bl The position for the back left motor
     * @param br The position for the back right motor
     * @deprecated
     */
    public void pathDrive(double fl, double fr, double bl, double br) {
        drive.pathDrive(fl, fr, bl, br);
    }

    /**
     * Configures the pidf controllers of all the chassis motors
     * 
     * @param kp New value for kp
     * @param ki New value for ki
     * @param kd New value for kd
     * @param kf New value for kf
     */
    public void setPid(double kp, double ki, double kd, double kf) {
        setPid(kp, ki, kd, kf, 0);
    }

    /**
     * Configures the pidf controllers of all the chassis motors
     * 
     * @param kp  New value for kp
     * @param ki  New value for ki
     * @param kd  New value for kd
     * @param kf  New value for kf
     * @param kiz New value for ki zone
     */
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

    /**
     * Sets the value for kp
     * 
     * @param kp The new value for kp
     */
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

    /**
     * Sets the value for ki
     * 
     * @param ki The new value for ki
     */
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

    /**
     * Sets the value for the ki zone
     * 
     * @param kiz The new value for ki zone
     */
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

    /**
     * Sets the value for kd
     * 
     * @param kd The new value for kd
     */
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

    /**
     * Sets the value for kf
     * 
     * @param kf The new value for kf
     */
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

    /**
     * Converts fron degrees to a rotational speed to pass into the
     * {@link #drive(double, double)} function
     * 
     * @param desiredAngle The desired angle
     * @return The error to the desired angle
     */
    public double degreesToZRotation(double desiredAngle) {
        return (desiredAngle - this.imu.yaw) * 0.12; // TODO: modify proportion (and calibrate IMU yaw)
    }

    /**
     * Converts fron inches to wheel revolutions to pass into the
     * {@link #drive(double, double)} function
     * 
     * @param inches the distance in inches
     * @return the number of wheel revolutions required to reach the desired
     *         distance
     */
    public double inchesToRevolutions(double inches) {
        // inches to Revolutions
        return CHASSIS_GEAR_RATIO * inches / (2 * Math.PI * WHEEL_RADIUS_INCHES_MECANUM);
    }

    /**
     * Converts fron inches to wheel revolutions to pass into the
     * {@link #pathDrive(double, double, double, double)} function
     * 
     * @param inches the distance in inches
     * @return the number of encoder tics required to reach the desired distance
     */
    public double inchesToEncoderTicks(double inches) {
        return TICKS_PER_REVOLUTION * inchesToRevolutions(inches);
        // return inches * 2.1;
    }

    /**
     * Converts from encoder ticks to inches
     * 
     * @param ticks The number of encoder ticks
     * @return The distance in inches
     */
    public double encoderTicksToInches(double ticks) {
        return revolutionsToInches(ticks / TICKS_PER_REVOLUTION);
        // return ticks / 2.1;
    }

    /**
     * Converts from wheel revolutions to inches
     * 
     * @param revs The number of wheel revolution
     * @return The distance in inches
     */
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

    /**
     * Resets the encoder positions for all the drivetrain motors
     */
    public void zeroEncoders() {
        flEncoder.setPosition(0);
        frEncoder.setPosition(0);
        blEncoder.setPosition(0);
        brEncoder.setPosition(0);
    }

    /**
     * Preps the drivetrain for odemetry
     */
    public void initOdometry() {
        zeroEncoders();
    }

    /**
     * Runs during {@link #disabledInit()} and preps the chassis to be disables
     */
    public void disable() {
        if (LOGDATA) {
            fl.write();
        }
    }
}