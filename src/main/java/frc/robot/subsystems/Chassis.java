package frc.robot.subsystems;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;


import com.revrobotics.SparkMaxPIDController;
import frc.robot.components.DevilDrive;
import frc.robot.OperatorInterface;
import frc.robot.Wiring;
import frc.robot.components.IMU;
import frc.robot.components.SplitDrive;

public class Chassis {
    private static final int TIMEOUT = 20;
    public static final double WHEEL_RADIUS_INCHES_MECANUM = 3;
    public static final double MAX_SPEED_FPS_TRACTION = 9.67 * 1.01;
    public static final double MAX_TICKS_PER_100MS = MAX_SPEED_FPS_TRACTION * 4096.0 / (Math.PI * WHEEL_RADIUS_INCHES_MECANUM * 2.0 / 12.0) / 10.0;
    private DevilDrive drive;
    private SplitDrive front;
    private SplitDrive back;
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
    private IMU imu;

    
    private final double differpercent = 40; //percent the front needs to move compared to the back, needs to be justed


    /**
     * private static final double kF = 0.14614285; //F-gain = (100% X 1023) /
     * 7350 F-gain = 0.139183673 - (7350 is max speed) private static final
     * double kP = 0.475; // P-gain = (.1*1023)/(155) = 0.66 - (350 is average
     * error) private static final double kD = (5.0*kP); private static final
     * double cLR = 0.1; This was ctrl c ctrl v'd from 2020. I don't now what
     * these values mean so I don't want to delete them.
     */

    private CANSparkMax initMotor(CANSparkMax sparky, int id) {
        sparky = new CANSparkMax(id, MotorType.kBrushless);
        SparkMaxPIDController pid = sparky.getPIDController();
        sparky.restoreFactoryDefaults();
        sparky.setCANTimeout(TIMEOUT);
        final double kP= 6e-5;
        final double kI = 0;
        final double kD = 0;
        final double kIz = 0;
        final double kFF = 0.000015;
        final double kMaxOutput = 1;
        final double kMinOutput = -1;
        pid.setP(kP);
        pid.setI(kI);
        pid.setD(kD);
        pid.setIZone(kIz);
        pid.setFF(kFF);
        pid.setOutputRange(kMinOutput, kMaxOutput);
        return sparky;
    }
    public void initEncoders(){
        flEncoder = CANSparkMax1.getEncoder();
        frEncoder = CANSparkMax2.getEncoder();
        blEncoder = CANSparkMax3.getEncoder();
        brEncoder = CANSparkMax4.getEncoder();
    }

    public Chassis(OperatorInterface oi, IMU imu) {
        this.oi = oi;
        this.imu = imu;
        CANSparkMax1 = initMotor(CANSparkMax1, Wiring.flMotor);
        CANSparkMax2 = initMotor(CANSparkMax2, Wiring.frMotor);
        CANSparkMax3 = initMotor(CANSparkMax3, Wiring.blMotor);
        CANSparkMax4 = initMotor(CANSparkMax4, Wiring.brMotor);

        //encoders
        CANSparkMax2.setInverted(true);
        CANSparkMax4.setInverted(true);
        initEncoders();

        front = new SplitDrive(CANSparkMax1, CANSparkMax2);
        back = new SplitDrive(CANSparkMax3, CANSparkMax4);
        drive = new DevilDrive(CANSparkMax1, CANSparkMax3, CANSparkMax2, CANSparkMax4);

    }

    public void main() {
        //System.out.println("forward "+ 0.5 * oi.pilot.getLeftY() +" strafe "+ 0.5 * oi.pilot.getLeftX() +" rotate "+ 0.5 * oi.pilot.getRightX());
        drive(0.995 * oi.pilot.getLeftY(), -0.995 * oi.pilot.getRightX());
        updateEncoders();
        imu.getvalues();
        System.out.println("Front left encoder velocity is: " + flEncoder.getVelocity() + " Front right encoder velocity is: " + frEncoder.getVelocity() + 
                           "\nBack left encoder velocity is: " + blEncoder.getVelocity() + " Back right encoder velocity is: " + brEncoder.getVelocity());
    }

    public void updateEncoders() {
        flep = flEncoder.getPosition();
        frep = frEncoder.getPosition();
        blep = blEncoder.getPosition();
        brep = brEncoder.getPosition();
    }

    public void drive(double ySpeed, double zRotation) {
        drive(ySpeed, zRotation, true);
    }

    public void drive(double ySpeed, double zRotation, boolean squareInputs) {
        front.splitDrive(ySpeed, (differpercent / 100.0) * zRotation, squareInputs);
        back.splitDrive(ySpeed, zRotation,squareInputs);
        // drive.driveCartesian(ySpeed, 0, zRotation, squareInputs);
    }

    public void pathDrive(double fl, double fr, double bl, double br) {
        drive.pathDrive(fl, fr, bl, br);
    }

    public void setPid(double kp, double ki, double kd, double kf) {
        setKP(kp);
        setKI(ki);
        setKD(kd);
        setKF(kf);
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

    public void initOdometry() {
        flEncoder.setPosition(0);
        frEncoder.setPosition(0);
        blEncoder.setPosition(0);
        brEncoder.setPosition(0);
    }
}
