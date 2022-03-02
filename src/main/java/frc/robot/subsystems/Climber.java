package frc.robot.subsystems;

import frc.robot.OperatorInterface;
import frc.robot.DTXboxController.Side;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.*;
import frc.robot.*;
import com.revrobotics.*;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.PneumaticsControlModule;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;

@SuppressWarnings("unused")

public class Climber {

    private OperatorInterface oi;

    private SupplyCurrentLimitConfiguration climberLimit = new SupplyCurrentLimitConfiguration(true, 40, 90, 2);
    private final int TIMEOUT = 0;
    private final double cLR = 0.1;

    private double climberRpms = 7500;

    private boolean resetEncoder = true;

    // TODO Tune this PID controller, It will need an I value
    private final double kF = 0.015;
    private final double kP = 0.1;
    private final double kD = 0.06;
    private final double kI = 0.000;
    private final double kiz = 0;

    private final double pkF = 0.0;
    private final double pkP = 0.05;
    private final double pkD = 0.06;
    private final double pkI = 0.00;
    private final double pkiz = 0;

    private TalonFX climber;
    private Solenoid climberSolenoid;

    private double climberSpeed = 1; // FIXME CHANGE THIS FOR MOTOR SPEED

    public Climber(OperatorInterface oi) {
        this.oi = oi;
        climberSolenoid = new Solenoid(Wiring.PNEUMATICS_HUB, PneumaticsModuleType.REVPH, Wiring.CLIMBER_SOLENOID);

        // MotorController Config

        climber = new TalonFX(Wiring.climberMotor);

        // climber Velocity mode configs
        climber.set(TalonFXControlMode.Velocity, 0.0);
        climber.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor, 0, TIMEOUT);
        climber.configClosedloopRamp(cLR, TIMEOUT);
        climber.configNominalOutputForward(0, TIMEOUT);
        climber.configNominalOutputReverse(0, TIMEOUT);
        climber.configPeakOutputForward(+1, TIMEOUT);
        climber.configPeakOutputReverse(-1, TIMEOUT);
        climber.setNeutralMode(NeutralMode.Brake);
        climber.configSupplyCurrentLimit(climberLimit, TIMEOUT);
        climber.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 0);
        climber.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 0);
        climber.config_kF(0, kF, TIMEOUT);
        climber.config_kP(0, kP, TIMEOUT);
        climber.config_kD(0, kD, TIMEOUT);
        climber.config_kI(0, kI, TIMEOUT);
        climber.config_kF(0, kF, TIMEOUT);
        climber.config_IntegralZone(0, kiz, TIMEOUT);
        climber.config_kP(1, pkP, TIMEOUT);
        climber.config_kD(1, pkD, TIMEOUT);
        climber.config_kI(1, pkI, TIMEOUT);
        climber.config_IntegralZone(1, pkiz, TIMEOUT);
        climber.setInverted(true);
        climber.selectProfileSlot(0, 0);

    }

    public void main() {
        // Control for Winch
        if (oi.climberEnableButton()) {
            oi.pilot.startRumble(-1, 1, Side.BOTH);
            if (oi.climberUpButton()) {
                raiseRobot();
            } else if (oi.climberDownButton()) {
                lowerRobot();
            } else {
                holdRobot();
            }
        }

        else {
            holdRobot();
        }

        // Control for moving pistons
        if (oi.extendClimberPistonsButton()) {
            extendPistons();
        } else if (oi.retractClimberPistonsButton()) {
            retractPistons();
        }
    }

    public void raiseRobot() {
        if (!resetEncoder) {
            climber.selectProfileSlot(0, 0);
            resetEncoder = true;
        }
        climber.set(TalonFXControlMode.Velocity, climberRpms);
    }

    public void lowerRobot() {
        if (!resetEncoder) {
            climber.selectProfileSlot(0, 0);
            resetEncoder = true;
        }
        climber.set(TalonFXControlMode.Velocity, -climberRpms);
    }

    public void holdRobot() {
        oi.pilot.stopRumble();
        if (resetEncoder) {
            climber.setSelectedSensorPosition(0);
            resetEncoder = false;
            climber.selectProfileSlot(1, 0);
        }
        climber.set(TalonFXControlMode.Position, 0);
    }

    public void stopClimber() {
        climber.set(TalonFXControlMode.PercentOutput, 0.0);
    }

    public void extendPistons() {
        climberSolenoid.set(true);
    }

    public void retractPistons() {
        climberSolenoid.set(false);
    }

    public void disable() {
        stopClimber();
        climber.setNeutralMode(NeutralMode.Coast);
        retractPistons();
    }

}
