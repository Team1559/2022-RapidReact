package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.SparkMaxPIDController;
import frc.robot.components.DevilDrive;
import frc.robot.OperatorInterface;

public class Chassis {
    private static final int TIMEOUT = 0;
    public static final double WHEEL_RADIUS_INCHES_MECANUM = 3;
    public static final double MAX_SPEED_FPS_TRACTION = 9.67 * 1.01;
    public static final double MAX_TICKS_PER_100MS = MAX_SPEED_FPS_TRACTION * 4096.0 / (Math.PI * WHEEL_RADIUS_INCHES_MECANUM * 2.0 / 12.0) / 10.0;

    private static final int FL_motor = 1;
    private static final int FR_motor = 2;
    private static final int RL_motor = 3;
    private static final int RR_motor = 4;

    private DevilDrive drive;

    public CANSparkMax CANSparkMax1;
    public CANSparkMax CANSparkMax2;
    public CANSparkMax CANSparkMax3;
    public CANSparkMax CANSparkMax4;

    private OperatorInterface oi;

    private SparkMaxPIDController FL_pidController;
    private SparkMaxPIDController FR_pidController;
    private SparkMaxPIDController RL_pidController;
    private SparkMaxPIDController RR_pidController;

    public final double kP;
    public final double kI;
    public final double kD;
    public final double kIz;
    public final double kFF;
    public final double kMaxOutput;
    public final double kMinOutput;
    public final double maxRPM;

    /**
     * private static final double kF = 0.14614285; //F-gain = (100% X 1023) /
     * 7350 F-gain = 0.139183673 - (7350 is max speed) private static final
     * double kP = 0.475; // P-gain = (.1*1023)/(155) = 0.66 - (350 is average
     * error) private static final double kD = (5.0*kP); private static final
     * double cLR = 0.1; This was ctrl c ctrl v'd from 2020. I don't now what
     * these values mean so I don't want to delete them.
     */

    public Chassis(OperatorInterface oi) {
        this.oi = oi;
        CANSparkMax1 = new CANSparkMax(FL_motor, MotorType.kBrushless);
        CANSparkMax2 = new CANSparkMax(FR_motor, MotorType.kBrushless);
        CANSparkMax3 = new CANSparkMax(RL_motor, MotorType.kBrushless);
        CANSparkMax4 = new CANSparkMax(RR_motor, MotorType.kBrushless);

        CANSparkMax1.restoreFactoryDefaults();
        CANSparkMax2.restoreFactoryDefaults();
        CANSparkMax3.restoreFactoryDefaults();
        CANSparkMax4.restoreFactoryDefaults();

        FL_pidController = CANSparkMax1.getPIDController();
        FR_pidController = CANSparkMax2.getPIDController();
        RL_pidController = CANSparkMax3.getPIDController();
        RR_pidController = CANSparkMax4.getPIDController();

        kP = 6e-5;
        kI = 0;
        kD = 0;
        kIz = 0;
        kFF = 0.000015;
        kMaxOutput = 1;
        kMinOutput = -1;
        maxRPM = 5700;

        FL_pidController.setP(kP);
        FL_pidController.setI(kI);
        FL_pidController.setD(kD);
        FL_pidController.setIZone(kIz);
        FL_pidController.setFF(kFF);
        FL_pidController.setOutputRange(kMinOutput, kMaxOutput);

        FR_pidController.setP(kP);
        FR_pidController.setI(kI);
        FR_pidController.setD(kD);
        FR_pidController.setIZone(kIz);
        FR_pidController.setFF(kFF);
        FR_pidController.setOutputRange(kMinOutput, kMaxOutput);

        RL_pidController.setP(kP);
        RL_pidController.setI(kI);
        RL_pidController.setD(kD);
        RL_pidController.setIZone(kIz);
        RL_pidController.setFF(kFF);
        RL_pidController.setOutputRange(kMinOutput, kMaxOutput);

        RR_pidController.setP(kP);
        RR_pidController.setI(kI);
        RR_pidController.setD(kD);
        RR_pidController.setIZone(kIz);
        RR_pidController.setFF(kFF);
        RR_pidController.setOutputRange(kMinOutput, kMaxOutput);

        drive = new DevilDrive(CANSparkMax1, CANSparkMax2, CANSparkMax3, CANSparkMax4);
    }
    public void main(){
        driveCartesian(oi.pilot.getLeftX(), oi.pilot.getLeftY(), oi.pilot.getRightX());
    }

    public void driveCartesian(double ySpeed, double xSpeed, double zRotation) {
        drive.driveCartesian(ySpeed, xSpeed, zRotation);
    }
}
