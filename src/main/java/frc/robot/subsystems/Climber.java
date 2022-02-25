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

public class Climber {

    private OperatorInterface oi;

    private SupplyCurrentLimitConfiguration climberLimit = new SupplyCurrentLimitConfiguration(true, 20, 20, 0);
    private final int TIMEOUT = 0;
    private final double cLR = 0.1;

    private double climberRpms = 7500;

    private TalonFX climber;
    private Solenoid climberSolenoid;


    public Climber(OperatorInterface operatorinterface) {
        oi = operatorinterface;
        
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
        // climber.configSupplyCurrentLimit(climberLimit);
        climber.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 0);
        climber.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 0);
    }

    public void runClimber() {
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

        // Control for moving pistons
        if (oi.extendClimberPistonsButton()) {
            extendPistons();
        } 
        else if(oi.retractClimberPistonsButton()) {
            retractPistons();
        } 
    }

    public void raiseRobot() {
        climber.set(TalonFXControlMode.PercentOutput, 1.0);
    }

    public void lowerRobot() {
        climber.set(TalonFXControlMode.PercentOutput, -1.0);
    }
    public void holdRobot() {
        climber.set(TalonFXControlMode.PercentOutput, -0.0);
    }

    public void extendPistons() {
        climberSolenoid.set(true);
    }

    public void retractPistons() {
        climberSolenoid.set(false);
    }
}
