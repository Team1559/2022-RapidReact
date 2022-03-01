package frc.robot.subsystems;

import frc.robot.OperatorInterface;
import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.*;
import frc.robot.*;
import com.revrobotics.*;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.PneumaticsControlModule;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;

@SuppressWarnings("unused")

public class Climber {

    private OperatorInterface oi;

    private SupplyCurrentLimitConfiguration climberLimit = new SupplyCurrentLimitConfiguration(true, 40, 90, 2);
    private final int TIMEOUT = 0;
    private final double cLR = 0.1;

    private double climberRpms = 7500;

    // TODO Tune this PID controller, It will need an I value
    private final double kF = 0.045;
    private final double kP = 0.4;
    private final double kD = 0;
    private final double kI = 0.000;
    private final double kiz = 0;

    private TalonFX climber;
    private Solenoid climberSolenoid;

    private double climberSpeed = 1; // FIXME CHANGE THIS FOR MOTOR SPEED

    public Climber(OperatorInterface oi) {
        this.oi = oi;
        climberSolenoid = new Solenoid(Wiring.PNEUMATICS_HUB, PneumaticsModuleType.REVPH, Wiring.CLIMBER_SOLENOID);

        // MotorController Config

        climber = new TalonFX(Wiring.climberMotor);

        // climber Velocity mode configs
        climber.set(TalonFXControlMode.PercentOutput, 0.0);
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
        climber.config_IntegralZone(0, kiz, TIMEOUT);
        climber.setInverted(true);
    }

    public void main() {
        // Control for Winch
        if (oi.climberEnableButton()) {
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
        climber.set(TalonFXControlMode.Velocity, climberRpms);
    }

    public void lowerRobot() {
        climber.set(TalonFXControlMode.Velocity, -climberRpms);
    }

    public void holdRobot() {
        climber.set(TalonFXControlMode.Velocity, 0.0);
    }

    public void extendPistons() {
        climberSolenoid.set(true);
    }

    public void retractPistons() {
        climberSolenoid.set(false);
    }

    public void disable() {
        climber.setNeutralMode(NeutralMode.Coast);
        retractPistons();
    }
}
