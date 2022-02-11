package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.SparkMaxPIDController;
import frc.robot.components.DevilDrive;
import frc.robot.OperatorInterface;
import frc.robot.Wiring;

public class Chassis {
    private static final int TIMEOUT = 0;
    public static final double WHEEL_RADIUS_INCHES_MECANUM = 3;
    public static final double MAX_SPEED_FPS_TRACTION = 9.67 * 1.01;
    public static final double MAX_TICKS_PER_100MS = MAX_SPEED_FPS_TRACTION * 4096.0 / (Math.PI * WHEEL_RADIUS_INCHES_MECANUM * 2.0 / 12.0) / 10.0;



    private DevilDrive drive;

    public CANSparkMax CANSparkMax1;
    public CANSparkMax CANSparkMax2;
    public CANSparkMax CANSparkMax3;
    public CANSparkMax CANSparkMax4;

    private OperatorInterface oi;


    /**
     * private static final double kF = 0.14614285; //F-gain = (100% X 1023) /
     * 7350 F-gain = 0.139183673 - (7350 is max speed) private static final
     * double kP = 0.475; // P-gain = (.1*1023)/(155) = 0.66 - (350 is average
     * error) private static final double kD = (5.0*kP); private static final
     * double cLR = 0.1; This was ctrl c ctrl v'd from 2020. I don't now what
     * these values mean so I don't want to delete them.
     */
    private CANSparkMax initMotor(CANSparkMax sparky, int id){
        sparky = new CANSparkMax(id, MotorType.kBrushless);
        SparkMaxPIDController pid = sparky.getPIDController();
        sparky.restoreFactoryDefaults();
        final double kP= 6e-5;
        final double kI = 0;
        final double kD = 0;
        final double kIz = 0;
        final double kFF = 0.000015;
        final double kMaxOutput = 1;
        final double kMinOutput = -1;
        final double maxRPM = 5700;
        pid.setP(kP);
        pid.setI(kI);
        pid.setD(kD);
        pid.setIZone(kIz);
        pid.setFF(kFF);
        pid.setOutputRange(kMinOutput, kMaxOutput);
        return sparky;
    }
    public Chassis(OperatorInterface oi) {
        this.oi = oi;
        CANSparkMax1 = initMotor(CANSparkMax1, Wiring.flMotor);
        CANSparkMax2 = initMotor(CANSparkMax2, Wiring.frMotor);
        CANSparkMax3 = initMotor(CANSparkMax3, Wiring.blMotor);
        CANSparkMax4 = initMotor(CANSparkMax4, Wiring.brMotor);

        CANSparkMax2.setInverted(true);
        CANSparkMax4.setInverted(true);

        drive = new DevilDrive(CANSparkMax1, CANSparkMax3, CANSparkMax2, CANSparkMax4);
    }
    public void main(){
        // System.out.println("forward "+ 0.5 * oi.pilot.getLeftY() +" strafe "+ 0.5 * oi.pilot.getLeftX() +" rotate "+ 0.5 * oi.pilot.getRightX());
        drive(0.5 * oi.pilot.getLeftY(), -0.5 * oi.pilot.getLeftX(), -0.5 * oi.pilot.getRightX());
    }

    public void drive(double ySpeed, double xSpeed, double zRotation) {
        drive.driveCartesian(ySpeed, xSpeed, zRotation, false);
    }
}
