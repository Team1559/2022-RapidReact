package frc.robot.subsystems;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.*;
import frc.robot.components.IntakeState;

// @SuppressWarnings("unused")
public class Auto {
    public static String[] stateLabels = {"WAIT","DRIVE","TURN","START_GATHERER","STOP_GATHERER","START_FLYWHEEL","STOP_FLYWHEEL","SHOOT","DRIVE_BALL","DRIVE_HOOP","ALIGN_HOOP","TURN_HOOP"};

    private int stepNumber = 0;
    private int stepCounter = 0;

    private double rightTarget;

    private static final int WAIT = 0;
    private static final int DRIVE = 1;
    private static final int TURN = 2;
    private static final int START_GATHERER = 3;
    private static final int STOP_GATHERER = 4;
    private static final int START_FLYWHEEL = 5;
    private static final int STOP_FLYWHEEL = 6;
    private static final int SHOOT = 7;
    private static final int DRIVE_BALL = 8;
    private static final int DRIVE_HOOP = 9;
    private static final int ALIGN_HOOP = 10;
    private static final int TURN_HOOP = 11;

    private static final int FEEDER_CYCLES = 50;
    boolean holdFeeder = true;

    private static final int MAX_TURN_SECONDS = 3;
    private static final int MAX_BALL_SECONDS = 5;
    private static final int MAX_DRIVE_SECONDS = 5;

    static final int HOOP_ERROR_INCHES = 3;
    static final int HOOP_ERROR_DEGREES = 1;

    static final double MAX_DRIVE = 0.2;
    static final double MAX_TURN = 0.15;

    double ySpeed = 0;
    double shooterSetVelocity = 0;

    private Robot robot;

    private int[][] steps;

    public static final int[][] noAuto = {};

    // Start gatherer, drive X feet, stop gatherer, start flywheel at known RPM,
    // turn 180, shoot, stop flywheel
    public static final int[][] basicAutoSteps = {
            { WAIT, 20 },
            { START_GATHERER },
            { DRIVE, 69 },
            { STOP_GATHERER },
            { START_FLYWHEEL, -1 },
            { TURN, 88 },
            { TURN, 88 },
            { DRIVE, 80 },
            { START_GATHERER },
            { SHOOT },
            { WAIT, 50 },
            { SHOOT },
            { STOP_FLYWHEEL },
            { STOP_GATHERER },
    };

    public static final int[][] oneBallAuto = {
            { WAIT, 20 },
            { DRIVE, -50 },
            { WAIT, 140 },
            { ALIGN_HOOP },
            { TURN, -4 },
            { START_FLYWHEEL, 0 },
            { WAIT, 100 },
            { SHOOT },
            { STOP_FLYWHEEL }
    };

    public static final int[][] basicVisionAuto = {
            { START_GATHERER },
            { DRIVE, 76 },
            { WAIT, 8 },
            { TURN, 88 },
            { TURN, 88 },
            { WAIT, 140 },
            { ALIGN_HOOP },
            { START_FLYWHEEL, 0 },
            { WAIT, 80 },
            { SHOOT },
            { WAIT, 30 },
            { SHOOT },
            { STOP_GATHERER },
            { STOP_FLYWHEEL }
    };

    public static final int[][] minAuto = {
            { DRIVE, 96 },
    };
    /*
     * Drive to ball 71”
     * Wait?
     * Turn 180 degrees
     * Align to target
     * Shoot
     * 
     * Turn left 100 degrees
     * Drive 180”
     * Drive to ball 82”
     * Wait?
     * Turn right 150 degrees
     * Drive to hoop (until 8 ft away)
     * Shoot
     */

    public static final int[][] leftBallAuto = {
            // Get first ball (71" away)
            { DRIVE_BALL, 48 },
            { START_GATHERER },
            // make sure we get the ball by going a few extra inches and waiting a few ticks
            { DRIVE, 48 + 3 },
            { WAIT, 30 },
            { STOP_GATHERER },
            // Shoot first two balls
            { TURN, 180 },
            { START_FLYWHEEL, 0 },
            { ALIGN_HOOP },
            { WAIT, 50 },
            { SHOOT },
            { STOP_FLYWHEEL },

            // Get third ball (82" away)
            { TURN, -100 },
            { DRIVE_BALL, 48 },
            { START_GATHERER },
            // make sure we get the ball by going a few extra inches
            { DRIVE, 48 + 3 },
            // wait 2 sec to give the human time to give us the fourth ball
            { WAIT, 2 * 50 },
            { STOP_GATHERER },
            // Shoot third and fourth balls
            { TURN, 150 },
            { DRIVE_HOOP, 8 },
            { START_FLYWHEEL, 0 },
            { WAIT, 50 },
            { SHOOT },
            { STOP_FLYWHEEL }
    };

    /*
     * Drive to ball 55”
     * Wait?
     * Turn 180 degrees
     * Align to target
     * Shoot
     * 
     * Turn right 103 degrees
     * Drive 172”
     * Drive to ball 84”
     * Wait for human player ball
     * Turn left 150 degrees
     * Drive to hoop (until 8 ft away)
     * Shoot
     */
    public static final int[][] rightBallAuto = {
            // Get 1st ball
            { DRIVE_BALL, 48 },
            // make sure we get the ball by going a few extra inches and waiting a few ticks
            { DRIVE, 48 + 3 },
            { WAIT, 30 },
            { STOP_GATHERER },
            // Shoot 1st two balls
            { TURN, 180 },
            { START_FLYWHEEL, 0 },
            { ALIGN_HOOP },
            { WAIT, 50 },
            { SHOOT },
            { STOP_FLYWHEEL },

            // Get third ball
            { TURN, 103 },
            { DRIVE, 172 },
            { DRIVE_BALL, 48 },
            { START_GATHERER },
            { DRIVE, 48 + 3 },
            // wait 2 sec to give the human time to give us the fourth ball
            { WAIT, 2 * 50 },
            { STOP_GATHERER },
            // Shoot third and fourth balls
            { TURN, -150 },
            { DRIVE_HOOP, 8 },
            { START_FLYWHEEL, 0 },
            { WAIT, 50 },
            { SHOOT },
            { STOP_FLYWHEEL }
    };

    /*
     * Drive to ball 55”
     * Wait?
     * Turn 180 degrees
     * Align to target
     * Shoot
     * 
     * Turn left 168 degrees
     * Drive 80”
     * Drive to ball 83”
     * Wait for human player ball
     * Turn right 168 degrees
     * Drive to hoop (until 8 ft away)
     * Shoot
     */
    public static final int[][] midBallAuto = {
            // Get 1st ball
            { DRIVE_BALL, 48 },
            { START_GATHERER },
            { DRIVE, 48 + 3 },
            { WAIT, 30 },
            { STOP_GATHERER },
            // Shoot 1st 2 balls
            { TURN, 180 },
            { ALIGN_HOOP },
            { START_FLYWHEEL, 0 },
            { WAIT, 50 },
            { SHOOT },
            { STOP_FLYWHEEL },
            // Get terminal and human player ball
            { TURN, -168 },
            { DRIVE, 80 },
            { DRIVE_BALL, 48 },
            { START_GATHERER },
            { DRIVE, 48 + 3 },
            { WAIT, 2 * 50 },
            { STOP_GATHERER },
            { TURN, 168 },
            { DRIVE_HOOP, 8 },
            { START_FLYWHEEL, 0 },
            { WAIT, 50 },
            { SHOOT },
            { STOP_FLYWHEEL }
    };

    public static final int[][] testAuto = {
            { START_GATHERER },
            { WAIT, 140 },
            { ALIGN_HOOP },
            { START_FLYWHEEL, 0 },
            { WAIT, 80 },
            { SHOOT },
            { WAIT, 50 },
            { SHOOT },
            { STOP_GATHERER },
            { STOP_FLYWHEEL }
    };

    public static final int[][] testVisionAutoWithNewTurningThatStopsWhenItSeesTheHoopBecauseRylanIsNotSuperBadAtLifeBabaWuestAlsoIsntWuestBad = {
            { WAIT, 20 },
            { START_GATHERER },
            { DRIVE, 30 },
            { DRIVE_BALL, 24 },
            { DRIVE, 22 },
            { WAIT, 25 },
            { STOP_GATHERER },
            { TURN, 88 },
            { TURN, 88 },
            { ALIGN_HOOP },
            { START_FLYWHEEL, 0 },
            { WAIT, 100 },
            { SHOOT },
            { WAIT, 50 },
            { SHOOT },
            { STOP_FLYWHEEL }

    };

    public Auto(Robot robot) {
        this(robot, basicAutoSteps);
    }

    public Auto(Robot robot, int[][] steps) {
        this.robot = robot;
        this.steps = steps;
    }

    public void main() {
        robot.vc.update();
        if (stepNumber >= steps.length) {
            return;
        }
        int[] step = steps[stepNumber];
        int type = (int) step[0];
        int value = 0;

        if (step.length > 1) {
            value = step[1];
        }
        stepCounter++;
        if (holdFeeder) {
            robot.shooter.holdFeeder();
        }
        if (shooterSetVelocity == 0) {
            robot.shooter.stopShooter();
        } else {
            robot.shooter.startShooter(shooterSetVelocity);
        }
        SmartDashboard.putString("Auto state", stateLabels[type]);
        switch (type) {
            case WAIT:
                Wait((int) value);
                break;
            case DRIVE:
                Drive(value);
                break;
            case TURN:
                Turn(value);
                break;
            case START_GATHERER:
                StartGatherer();
                break;
            case STOP_GATHERER:
                StopGatherer();
                break;
            case START_FLYWHEEL:
                StartFlywheel(value);
                break;
            case STOP_FLYWHEEL:
                StopFlywheel();
                break;
            case SHOOT:
                Shoot();
                break;
            case DRIVE_BALL:
                DriveBall(value);
                break;
            case DRIVE_HOOP:
                DriveHoop(value);
                break;
            case ALIGN_HOOP:
                AlignHoop();
                break;
            case TURN_HOOP:
                TurnHoop(value);
                break;
        }
        robot.shooter.gathererState();
    }

    private void Done() {
        stepNumber++;
        stepCounter = 0;
    }

    private void Fail(String errorMessage) {
        stepNumber = steps.length;
        System.err.println("AUTO FAILED: " + errorMessage);
        StopGatherer();
        StopFlywheel();
        robot.shooter.holdFeeder();

    }

    private void Wait(int cycles) {
        if (stepCounter >= cycles)
            Done();
    }

    private void Drive(int inches) {
        robot.chassis.updateEncoders();
        double revs = robot.chassis.inchesToRevolutions(inches);
        double kP = 1.0 / 25.0;
        if (stepCounter == 1)
            rightTarget = robot.chassis.frep + revs;
        double remaining = rightTarget - robot.chassis.frep;
        double driveValue = remaining * kP;
        int inchesDone = (int) robot.chassis.revolutionsToInches(revs - remaining);
        System.out.println("Drive: " + inchesDone + "/" + inches);
        if (stepCounter <= 50.0 * MAX_DRIVE_SECONDS) {
            robot.chassis.drive(Math.abs(driveValue) > MAX_DRIVE ? Math.copySign(MAX_DRIVE, driveValue) : driveValue, 0,
                    false);
        } else {
            robot.chassis.drive(0, 0, false);
            if (Math.abs(inchesDone - inches) <= 6)
                Done();
            else
                Fail("Driving failed :(");
        }

        if (Math.abs(inchesDone - inches) <= 2)
            Done();
    }

    private void Turn(int degrees) {
        if (stepCounter == 1) {
            robot.chassis.imu.zeroYaw();
        }
        double rotation = robot.chassis.degreesToZRotation(degrees);
        robot.chassis.imu.updateValues();
        double turnValue = Math.abs(rotation) > MAX_TURN ? Math.copySign(MAX_TURN, rotation) : rotation;
        robot.chassis.drive(0, turnValue);
        System.out.println(robot.chassis.imu.yaw + " <-- YAW, TARGET --> " + degrees);
        if (Math.abs(degrees - robot.chassis.imu.yaw) % 360 < 2) {
            Done();
        } else if (stepCounter > 50 * MAX_TURN_SECONDS)
            Fail("Turned for too long");
    }

    private void TurnHoop(int degrees) {
        if (stepCounter == 1) {
            robot.chassis.imu.zeroYaw();
        }
        double rotation = robot.chassis.degreesToZRotation(degrees);
        robot.chassis.imu.updateValues();
        double turnValue = Math.abs(rotation) > MAX_TURN ? Math.copySign(MAX_TURN, rotation) : rotation;
        robot.chassis.drive(0, turnValue);
        System.out.println(robot.chassis.imu.yaw + " <-- YAW, TARGET --> " + degrees);
        if (Math.abs(degrees - robot.chassis.imu.yaw) % 360 < 2) {
            Done();
        } else if (stepCounter > 50 * MAX_TURN_SECONDS)
            Fail("Turned for too long");
        else if (robot.vc.isHoopValid()) {
            Done();
        }
    }

    private void StartGatherer() {
        robot.shooter.disableManual = true;
        robot.shooter.gathererState = IntakeState.DOWN;
        Done();
    }

    private void StopGatherer() {
        robot.shooter.disableManual = false;
        robot.shooter.gathererState = IntakeState.HOLDING;
        robot.shooter.gathererState();

        Done();
    }

    private void StartFlywheel(double rpm) {
        if (rpm == 0)
            shooterSetVelocity = robot.shooter
                    .calculateShooterRPMS(robot.vc.hoopx + Shooter.SHOOTER_DISTANCE_FROM_CAMERA + 2);
        else if (rpm == -1) {
            shooterSetVelocity = Shooter.DEFAULT_RPMS;
        } else {
            shooterSetVelocity = rpm;
        }
        Done();
    }

    private void StopFlywheel() {
        shooterSetVelocity = 0;
        Done();
    }

    private void Shoot() {
        holdFeeder = false;
        robot.shooter.disableManual = true;
        robot.shooter.gatherLock = false;
        robot.shooter.startFeeder(robot.shooter.feederSpeed, stepCounter == 1);

        if (stepCounter >= FEEDER_CYCLES) {
            robot.shooter.disableManual = false;
            holdFeeder = true;
            Done();
        }
    }

    private void DriveBall(int desiredDistanceFromBall) { // in inches
        double positionError = 1000;
        if (robot.vc.ballx != 0) {
            positionError = robot.vc.ballx * 12 - desiredDistanceFromBall;
            ySpeed = positionError * 0.04;
            if (ySpeed > MAX_DRIVE)
                ySpeed = MAX_DRIVE;
        }
        if (!robot.vc.trackBall(-ySpeed))
            Fail("No ball found");
        if (positionError < 0 && robot.vc.ballx != 0) {
            robot.chassis.drive(0, 0, false);
            Done();
        }
        if (stepCounter > MAX_BALL_SECONDS * 500) {
            Fail("Took too long");
        }
    }

    private void DriveHoop(int desiredDistanceFromTarget) {
        double positionError = 1000;
        if (robot.vc.hoopx != 0) {
            positionError = robot.vc.hoopx * 12 - desiredDistanceFromTarget;
            ySpeed = positionError * 0.04;
            if (ySpeed > MAX_DRIVE)
                ySpeed = MAX_DRIVE;
        }

        if (ySpeed > MAX_DRIVE) {
            ySpeed = MAX_DRIVE;
        }

        if (!robot.vc.trackHoop(-ySpeed))
            Fail("No hoop found");
        if (Math.abs(positionError) < HOOP_ERROR_INCHES && Math.abs(robot.vc.hoopr) < HOOP_ERROR_DEGREES) {
            robot.chassis.drive(0, 0, false);
        }
    }

    private void AlignHoop() {
        if (!robot.vc.trackHoop(0)) {
            Fail("No hoop found");
        } else if (robot.vc.isHoopValid() && Math.abs(robot.vc.hoopr) <= VisionControl.hoopChassisThreshold)
            Done();
    }
}
