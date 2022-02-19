package frc.robot.subsystems;

import frc.robot.OperatorInterface;
import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.*;
import frc.robot.*;
import com.revrobotics.*;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;

@SuppressWarnings("unused")

public class Shooter {

    private OperatorInterface oi;

    private SupplyCurrentLimitConfiguration shooterLimit = new SupplyCurrentLimitConfiguration(true, 20, 20, 0);
    private final int TIMEOUT = 0;
    private final double cLR = 0.1;

    private double shooter_kF = 0.045;
    private double shooter_kP = 0.4;
    private double shooter_kD = 0;
    private double shooter_kI = 0.000;
    private double shooterRpms = 7500;
    private double feederSpeed = .2;
    private double intakeSpeed = .4;

    private TalonFX shooter;
    private CANSparkMax feeder;
    private Solenoid lowerIntake;
    private TalonSRX intake;
    private VisionControl vc;

    // States for gatherer

    public static final int gathererUp = 0;
    public static final int gathererDown = 1;
    public static final int holding = 3;

    public int state = gathererUp;

    public Shooter(OperatorInterface operatorinterface) {
        oi = operatorinterface;
        this.vc = Robot.vc;

        // MotorController Config

        shooter = new TalonFX(Wiring.shooterMotor);
        feeder = new CANSparkMax(Wiring.feederMotor, MotorType.kBrushless);
        lowerIntake = new Solenoid(PneumaticsModuleType.REVPH, Wiring.lowerIntake);
        intake = new TalonSRX(Wiring.intake);

        // Note about pneumatics :

        // PneumaticsModuleType.CTREPCM for ctre stuff
        // PneumaticsModuleType.REVPH for rev stuff

        // Set all motors to 0 and pistons up
        feeder.set(0);
        lowerIntake.set(false);
        intake.set(TalonSRXControlMode.PercentOutput, 0);

        // Shooter Velocity mode configs
        shooter.configClosedloopRamp(cLR, TIMEOUT);
        shooter.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor); // shooter.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);
        shooter.config_kF(0, shooter_kF);
        shooter.config_kP(0, shooter_kP);
        shooter.config_kD(0, shooter_kD);
        shooter.config_kI(0, shooter_kI);
        shooter.configNominalOutputForward(0, TIMEOUT);
        shooter.configNominalOutputReverse(0, TIMEOUT);
        shooter.configPeakOutputForward(+1, TIMEOUT);
        shooter.configPeakOutputReverse(-1, TIMEOUT);
        shooter.setNeutralMode(NeutralMode.Coast);
        shooter.configSupplyCurrentLimit(shooterLimit);

    }

    public void runShooter() {
        // System.out.println(shooter.getSelectedSensorVelocity()); PRINTS FOR VELOCITY
        // CONTROL

        // Control for FlyWheel
        if (oi.runFlyWheelButtonManual()) {
            startShooter(shooterRpms);
        } else {
            stopShooter();
        }

        // Control for lowering intake
        if (oi.manualIntakeButton() && state == gathererUp) {
            lowerIntake();
            startIntake();
        } else if (!oi.manualIntakeButton() && state == gathererDown) {
            stopIntake();
        } else if (oi.manualIntakeButton() && state == holding) {
            stopIntake();
            raiseIntake();
        }

    }

    public void startShooter(double rpms) {
        shooter.set(TalonFXControlMode.Velocity, rpms);
        if (oi.shootButton()) {
            feeder.set(feederSpeed);
        }

        else {
            feeder.set(0);
        }
    }

    public void stopShooter() {
        shooter.set(TalonFXControlMode.PercentOutput, 0);
    }

    public void lowerIntake() {
        state = gathererDown;
        lowerIntake.set(true);
    }

    public void raiseIntake() {
        state = gathererUp;
        lowerIntake.set(false);
    }

    public void startIntake() {
        intake.set(TalonSRXControlMode.PercentOutput, intakeSpeed);
    }

    public void stopIntake() {
        state = holding;
        intake.set(TalonSRXControlMode.PercentOutput, 0);
    }

}