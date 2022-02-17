package frc.robot.subsystems;

import frc.robot.OperatorInterface;
import frc.robot.components.CompressorControl;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.*;
import frc.robot.*;
import com.revrobotics.*;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import frc.robot.*;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;

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

    private double shooterSpeed = -0.2;
    private double feederSpeed = .2;
    private double intakeSpeed = .4;

    private TalonFX shooter;
    private CANSparkMax feeder;
    private Solenoid lowerIntake;
    private TalonSRX intake;
    private VisionControl vc;

    public Shooter(OperatorInterface operatorinterface, VisionControl vc) {
        oi = operatorinterface;
        this.vc = vc;

        // MotorController Config

        shooter = new TalonFX(Wiring.shooterMotor);
        feeder = new CANSparkMax(Wiring.feederMotor, MotorType.kBrushless);
        lowerIntake = new Solenoid(PneumaticsModuleType.REVPH, Wiring.lowerIntake);
        intake = new TalonSRX(Wiring.intake);
        // use for
        // PneumaticsModuleType.CTREPCM
        // for
        // ctre
        // stuff
        // or
        // PneumaticsModuleType.REVPH
        // for
        // the
        // rev
        // stuff

        feeder.set(0);
        lowerIntake.set(false);
        intake.set(TalonSRXControlMode.PercentOutput, 0);

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
        if (oi.runFlyWheelButton()) {
            startShooter(vc.calculateShooterRPMS());
        } else {
            stopShooter();
        }

        // Control for lowering intake
        if (oi.lowerIntakeButton()) {
            lowerIntake();
        } else {
            raiseIntake();
        }

        // Control for running intake
        if (oi.intakeButton()) {
            startIntake();
        } else {
            stopIntake();
        }

    }

    public void startShooter(double rpms) {
        shooter.set(TalonFXControlMode.Velocity, rpms);
        if (oi.shootButton()) {
            feeder.set(feederSpeed);
        } else {
            feeder.set(0);
        }
    }

    public void stopShooter() {
        shooter.set(TalonFXControlMode.PercentOutput, 0);
    }

    public void lowerIntake() {
        lowerIntake.set(true);
    }

    public void raiseIntake() {
        lowerIntake.set(false);
    }

    public void startIntake() {
        intake.set(TalonSRXControlMode.PercentOutput, intakeSpeed);
    }

    public void stopIntake() {
        intake.set(TalonSRXControlMode.PercentOutput, 0);
    }

}
