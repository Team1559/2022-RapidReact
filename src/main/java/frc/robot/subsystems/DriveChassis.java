package frc.robot.subsystems;





import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import frc.robot.*;

import frc.robot.subsystems.*;
import edu.wpi.first.wpilibj.drive.MecanumDrive;


public class DriveChassis 
{
	private static final int TIMEOUT = 0;
	public static final double WHEEL_RADIUS_INCHES_MECANUM = 3;
	public static final double MAX_SPEED_FPS_TRACTION = 9.67 * 1.01;
	public static final double MAX_TICKS_PER_100MS = MAX_SPEED_FPS_TRACTION * 4096.0 / (Math.PI * WHEEL_RADIUS_INCHES_MECANUM * 2.0 / 12.0) / 10.0;
	private static DevilDrive drive;
	public WPI_TalonSRX FL_TALON, RL_TALON, FR_TALON, RR_TALON;
	private static final double kF = 0.14614285; //F-gain = (100% X 1023) / 7350 F-gain = 0.139183673 - (7350 is max speed)
	private static final double kP = 0.475; // P-gain = (.1*1023)/(155) = 0.66 - (350 is average error)
	private static final double kD = (5.0*kP);
	private static final double cLR = 0.1;


	public DriveChassis() 
	{
		FL_TALON = new WPI_TalonSRX(Wiring.FRONT_LEFT_MOTOR);
		RL_TALON = new WPI_TalonSRX(Wiring.REAR_LEFT_MOTOR);
		FR_TALON = new WPI_TalonSRX(Wiring.FRONT_RIGHT_MOTOR);
		RR_TALON = new WPI_TalonSRX(Wiring.REAR_RIGHT_MOTOR);

		FL_TALON.set(ControlMode.Velocity, 0);	
		FL_TALON.configClosedloopRamp(cLR, TIMEOUT);
		FL_TALON.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);
		FL_TALON.config_kF(0, kF);
		FL_TALON.config_kP(0, kP);
		FL_TALON.config_kD(0, kD);
		FL_TALON.configNominalOutputForward(0, TIMEOUT);
		FL_TALON.configNominalOutputReverse(0, TIMEOUT);
		FL_TALON.configPeakOutputForward(+1, TIMEOUT);
		FL_TALON.configPeakOutputReverse(-1, TIMEOUT);

		FR_TALON.set(ControlMode.Velocity, 0);
		FR_TALON.configClosedloopRamp(cLR, TIMEOUT);
		FR_TALON.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);	
		FR_TALON.config_kF(0, kF);
		FR_TALON.config_kP(0, kP);
		FR_TALON.config_kD(0, kD);
		FR_TALON.configNominalOutputForward(0, TIMEOUT);
		FR_TALON.configNominalOutputReverse(0, TIMEOUT);
		FR_TALON.configPeakOutputForward(+1, TIMEOUT);
		FR_TALON.configPeakOutputReverse(-1, TIMEOUT);

		RL_TALON.set(ControlMode.Velocity, 0);
		RL_TALON.configClosedloopRamp(cLR, TIMEOUT);
		RL_TALON.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);	
		RL_TALON.config_kF(0, kF);
		RL_TALON.config_kP(0, kP);
		RL_TALON.config_kD(0, kD);
		RL_TALON.configNominalOutputForward(0, TIMEOUT);
		RL_TALON.configNominalOutputReverse(0, TIMEOUT);
		RL_TALON.configPeakOutputForward(+1, TIMEOUT);
		RL_TALON.configPeakOutputReverse(-1, TIMEOUT);

		RR_TALON.set(ControlMode.Velocity, 0);
		RR_TALON.configClosedloopRamp(cLR, TIMEOUT);
		RR_TALON.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);	
		RR_TALON.config_kF(0, kF);
		RR_TALON.config_kP(0, kP);
		RR_TALON.config_kD(0, kD);
		RR_TALON.configNominalOutputForward(0, TIMEOUT);
		RR_TALON.configNominalOutputReverse(0, TIMEOUT);
		RR_TALON.configPeakOutputForward(+1, TIMEOUT);
		RR_TALON.configPeakOutputReverse(-1, TIMEOUT);

		drive = new DevilDrive(FL_TALON, RL_TALON, FR_TALON, RR_TALON);
		drive.setMaxOutput(7000);

	}

	public void driveCartesian(double ySpeed, double xSpeed, double zRotation) 
	{
		drive.driveCartesian(ySpeed, xSpeed, zRotation);
	}
}


