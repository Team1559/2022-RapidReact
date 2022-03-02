// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.PowerDistribution.ModuleType;
import frc.robot.subsystems.Shooter;
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
    private IMU imu;
    private VisionData vData;
    public static VisionControl vc;
    public boolean usingVision = false;
    private String m_autoSelected;
    private final SendableChooser<String> m_chooser = new SendableChooser<>();
    public Chassis chassis;
    public Climber climber;
    public static PowerDistribution PDM = new PowerDistribution(Wiring.PDP, ModuleType.kRev);

    private static final String DEFAULT_PATH = "Default Path";
    private static final String PATH_1 = "Path 1";
    private static final String PATH_2 = "Path 2";
    private static final String PATH_3 = "Path 3";
    private static final String PATH_4 = "Path 4";

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
        m_chooser.setDefaultOption("Default Auto Path", DEFAULT_PATH);
        m_chooser.addOption("Path 1", PATH_1);
        m_chooser.addOption("Path 2", PATH_2);
        m_chooser.addOption("Path 3", PATH_3);
        m_chooser.addOption("Path 4", PATH_4);

        SmartDashboard.putData("Auto Paths", m_chooser);
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
        if (FeatureFlags.doImu && FeatureFlags.imuInitialized) {
            imu.updateValues();
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
        if (FeatureFlags.doImu && FeatureFlags.imuInitialized) {
            imu.zeroYaw();
        }
        if (FeatureFlags.doVision && FeatureFlags.visionInitialized) {
            m_autoSelected = m_chooser.getSelected();
            PDM.setSwitchableChannel(true);

            switch (m_autoSelected) {
                case DEFAULT_PATH:
                default:
                    vc.setAutoPath("");
                    break;

                case PATH_1:
                    vc.setAutoPath("path1");
                    break;

                case PATH_2:
                    vc.setAutoPath("path2");
                    break;

                case PATH_3:
                    vc.setAutoPath("path3");
                    break;
                case PATH_4:
                    vc.setAutoPath("path4");
                    break;
            }
            vc.autoInit();
        }
    }

    /** This function is called periodically during autonomous. */

    @Override
    public void autonomousPeriodic() {
        if (FeatureFlags.doCompressor && FeatureFlags.compressorInitialized) {
            compressorControl.enable();
        }

        if (FeatureFlags.doVision && FeatureFlags.visionInitialized) {
            vc.autoPeriodic();
        }
    }

    /** This function is called once when teleop is enabled. */

    @Override
    public void teleopInit() {
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

        if (FeatureFlags.doCompressor && FeatureFlags.compressorInitialized) {
            compressorControl.enable();
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
