// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.PowerDistribution.ModuleType;
import frc.robot.components.VisionData;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.subsystems.*;
import frc.robot.components.*;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */

public class Robot extends TimedRobot {
    private OperatorInterface oi = new OperatorInterface();
    private Auto auto;
    private IMU imu;
    private VisionData vData;
    public static VisionControl vc;
    public boolean usingVision = false;
    
    private String m_autoSelected;
    private final SendableChooser<String> m_chooser = new SendableChooser<>();

    private String color = "invalid";
    private final SendableChooser<String> colorSelector = new SendableChooser<>();

    public Chassis chassis;
    public Climber climber;
    public static PowerDistribution PDM = new PowerDistribution(Wiring.PDP, ModuleType.kRev);
    public static DriverStation ds;
    private static Alliance alliance;

    private static final String AUTO = "Automatic";
    private static final String RED = "Red Allience";
    private static final String BLUE = "Blue Allience";
    private static final String INVALID = "Invalid";

    private static final String NONE = "No Auto";
    private static final String BASIC_AUTO_STEPS = "Basic Auto";
    private static final String BASIC_VISION_AUTO = "Basic Auto Using Vision";
    private static final String MINI_AUTO = "Leave The Starting Area";
    private static final String LEFT_BALL_AUTO = "Left Ball Auto";
    private static final String RIGHT_BAALL_AUTO = "Right Ball Auto";
    private static final String MID_BALL_AUTO = "Mid Ball Auto";

    private Shooter shooter;

    private CompressorControl compressorControl;

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */

    @Override
    public void robotInit() {
        initialize();
        PDM.setSwitchableChannel(false);

        colorSelector.setDefaultOption(AUTO, AUTO);
        colorSelector.addOption(RED, RED);
        colorSelector.addOption(BLUE, BLUE);
        colorSelector.addOption(INVALID, INVALID);

        m_chooser.setDefaultOption(NONE, NONE);
        m_chooser.addOption(BASIC_AUTO_STEPS, BASIC_AUTO_STEPS);
        m_chooser.addOption(BASIC_VISION_AUTO, BASIC_VISION_AUTO);
        m_chooser.addOption(MINI_AUTO, MINI_AUTO);
        m_chooser.addOption(LEFT_BALL_AUTO, LEFT_BALL_AUTO);
        m_chooser.addOption(RIGHT_BAALL_AUTO, RIGHT_BAALL_AUTO);
        m_chooser.addOption(MID_BALL_AUTO, MID_BALL_AUTO);

        SmartDashboard.putData("Auto Paths", m_chooser);
        SmartDashboard.putData("Allience Color", colorSelector);
    }

    /**
     * This function is called every robot packet, no matter the mode. Use this
     * for items like diagnostics that you want ran during disabled, autonomous,
     * teleoperated and test.
     * <p>
     * This runs after the mode specific periodic functions, but before
     * LiveWindow and SmartDashboard integrated updating.
     */

    @Override
    public void robotPeriodic() {
        updateColor();
        oi.pilot.rumblePeriodic();
        oi.copilot.rumblePeriodic();
    }

    private void updateColor(){
        alliance = DriverStation.getAlliance();
        if (FeatureFlags.doImu && FeatureFlags.imuInitialized) {
            imu.updateValues();
        }
        color = colorSelector.getSelected();
        switch (color) {
            case AUTO:
            default:
                switch (alliance) {
                    case Red:
                        vc.periodic("red");
                        break;
                    case Blue:
                        vc.periodic("blue");
                        break;
                    case Invalid:
                        vc.periodic("invalid");
                        break;
                }
                break;
            case RED:
                vc.periodic("red");

                break;
            case BLUE:
                vc.periodic("blue");

                break;
            case INVALID:
                vc.periodic("invalid");

                break;
        }
    }

    /**
     * This autonomous (along with the chooser code above) shows how to select
     * between different autonomous modes using the dashboard. The sendable
     * chooser code works with the Java SmartDashboard. If you prefer the
     * LabVIEW Dashboard, remove all of the chooser code and uncomment the
     * getString line to get the auto name from the text box below the Gyro
     * <p>
     * You can add additional auto modes by adding additional comparisons to the
     * switch structure below with additional strings. If using the
     * SendableChooser make sure to add them to the chooser code above as well.
     */

    @Override
    public void autonomousInit() {
        if (FeatureFlags.doCompressor && FeatureFlags.compressorInitialized) {
            compressorControl.enable();
        }

        if (FeatureFlags.doImu && FeatureFlags.imuInitialized) {
            imu.zeroYaw();
        }

        m_autoSelected = m_chooser.getSelected();
        PDM.setSwitchableChannel(true);

        switch (m_autoSelected) {
            case NONE:
            default:
                auto = new Auto(Auto.noAuto);
                break;
            case BASIC_AUTO_STEPS:
                auto = new Auto(Auto.basicAutoSteps);
                break;
            case BASIC_VISION_AUTO:
                auto = new Auto(Auto.basicVisionAuto);
                break;
            case MINI_AUTO:
                auto = new Auto(Auto.minAuto);
                break;
            case LEFT_BALL_AUTO:
                auto = new Auto(Auto.leftBallAuto);
                break;
            case RIGHT_BAALL_AUTO:
                auto = new Auto(Auto.rightBallAuto);
                break;
            case MID_BALL_AUTO:
                auto = new Auto(Auto.midBallAuto);
                break;
        }
    }

    /** This function is called periodically during autonomous. */

    @Override
    public void autonomousPeriodic() {
        auto.main();
    }

    /** This function is called once when teleop is enabled. */

    @Override
    public void teleopInit() {
        if (FeatureFlags.doCompressor && FeatureFlags.compressorInitialized) {
            compressorControl.enable();
        }

        if (FeatureFlags.doVision && FeatureFlags.visionInitialized) {
            vc.teleopInit();
            usingVision = vc.usingAuto;
        }

        if (FeatureFlags.doChassis && FeatureFlags.chassisInitialized) {
            chassis.setPid(6e-5, 0, 0, 0.000015);
        }
    }

    /** This function is called periodically during operator control. */

    @Override
    public void teleopPeriodic() {
        if (FeatureFlags.doVision && FeatureFlags.visionInitialized) {
            vc.teleopPeriodic();
        }

        if (FeatureFlags.doChassis && FeatureFlags.chassisInitialized && !usingVision) {
            chassis.main();
        }

        if (FeatureFlags.doShooter && FeatureFlags.shooterInitialized) {
            shooter.main();
        }

        if (FeatureFlags.doClimber && FeatureFlags.climberInitialized) {
            climber.main();
        }
    }

    /** This function is called once when the robot is disabled. */

    @Override
    public void disabledInit() {
        PDM.setSwitchableChannel(false);

        if (FeatureFlags.doChassis && FeatureFlags.chassisInitialized && !usingVision) {
            chassis.disable();
        }
        if (FeatureFlags.doCompressor && FeatureFlags.compressorInitialized) {
            compressorControl.disable();
        }
        if (FeatureFlags.doVision && FeatureFlags.visionInitialized) {
            vc.disable();
        }
    }

    /** This function is called periodically when disabled. */

    @Override
    public void disabledPeriodic() {

    }

    /** This function is called once when test mode is enabled. */

    @Override
    public void testInit() {
        PDM.setSwitchableChannel(false);

        if (FeatureFlags.doClimber && FeatureFlags.climberInitialized) {
            climber.disable();
        }

        if (FeatureFlags.doShooter && FeatureFlags.shooterInitialized) {
            shooter.disable();
        }
        if (FeatureFlags.doCompressor && FeatureFlags.compressorInitialized) {
            compressorControl.disable();
        }
    }

    /** This function is called periodically during test mode. */

    @Override
    public void testPeriodic() {

    }

    public void initialize() {
        FeatureFlags.updateDependencies();

        if (FeatureFlags.doCompressor && !FeatureFlags.compressorInitialized) {
            compressorControl = new CompressorControl();
            FeatureFlags.compressorInitialized = true;
        }

        if (FeatureFlags.doImu && !FeatureFlags.imuInitialized) {
            imu = new IMU();
            imu.zeroYaw();
            FeatureFlags.imuInitialized = true;
        }

        if (FeatureFlags.doChassis && !FeatureFlags.chassisInitialized) {
            chassis = new Chassis(oi, imu);
            FeatureFlags.chassisInitialized = true;
        }

        if (FeatureFlags.doShooter && !FeatureFlags.shooterInitialized) {
            shooter = new Shooter(oi, chassis);
            FeatureFlags.shooterInitialized = true;
        }

        if (FeatureFlags.doClimber && !FeatureFlags.climberInitialized) {
            climber = new Climber(oi);
            FeatureFlags.climberInitialized = true;
        }

        if (FeatureFlags.doVision && !FeatureFlags.visionInitialized) {
            vc = new VisionControl(vData, oi, chassis, imu, shooter);
            FeatureFlags.visionInitialized = true;
        }
    }
}
