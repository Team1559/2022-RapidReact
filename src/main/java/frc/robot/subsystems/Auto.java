package frc.robot.subsystems;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.*;

// @SuppressWarnings("unused")
public class Auto {

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

    private static final int FEEDER_CYCLES = 200;
    boolean holdFeeder = true;

    private static final int MAX_TURN_SECONDS = 3;
    private static final int MAX_BALL_SECONDS = 5;

    static final int HOOP_ERROR_INCHES = 3;
    static final int HOOP_ERROR_DEGREES = 1;

    static final double MAX_DRIVE = 0.2;
    static final double MAX_TURN = 0.5;

    double ySpeed = 0;

    private Robot robot;

    private int[][] steps;

    // No Auto
    public static final int[][] noAuto = {};

    // Start gatherer, drive X feet, stop gatherer, start flywheel at known RPM,
    // turn 180, shoot, stop flywheel
    public static final int[][] basicAutoSteps = {
            { WAIT, 100 },
            { START_GATHERER },
            { DRIVE, 69 },
            // { WAIT, 25 },
            { STOP_GATHERER },
            { START_FLYWHEEL, 7500 },
            { TURN, 88 },
            { TURN, 88 },
            { DRIVE, 126 },
            { START_GATHERER },
            { SHOOT },
            { STOP_FLYWHEEL },
            { STOP_GATHERER },
    };

    public static final int[][] basicVisionAuto = {
            { WAIT, 50 },
            { DRIVE_BALL, 48 },
            { START_GATHERER },
            { DRIVE, 48 + 3 },
            { WAIT, 30 },
            { STOP_GATHERER },
            { TURN, 180 },
            { START_FLYWHEEL, 0 },
            { ALIGN_HOOP },
            { WAIT, 50 },
            { SHOOT },
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
            // Get 1st ball
            { START_GATHERER },
            { WAIT, 20 },
            { DRIVE_BALL, 36 },
            { DRIVE, 20 },
            { TURN, 90 },
            { TURN, 60 },
            { START_FLYWHEEL, 0 },
            { DRIVE_HOOP, 60 },
            { WAIT, 50 },
            { SHOOT }

    };

    public static final int[][] testVisionAutoWithNewTurningThatStopsWhenItSeesTheHoopBecauseRylanIsNotSuperBadAtLifeBabaWuestAlsoIsntWuestBad = {
        // Get 1st ball
        { START_GATHERER },
        { WAIT, 20 },
        { DRIVE_BALL, 36 },
        { DRIVE, 20 },
        { TURN_HOOP, 90},
        { TURN_HOOP, 60 },
        { START_FLYWHEEL, 0 },
        { DRIVE_HOOP, 60 }, 
        { WAIT, 50 },
        { SHOOT }

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
        if(holdFeeder){
            robot.shooter.holdFeeder();
        }
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
        System.out.println("Wait: " + stepCounter + "/" + cycles);
        if (stepCounter >= cycles)
            Done();
    }

    private void Drive(int inches) {
        robot.chassis.updateEncoders();
        double revs = robot.chassis.inchesToRevolutions(inches);
        SmartDashboard.putNumber("Revs", revs);
        double kP = 1.0 / 25.0;
        if (stepCounter == 1) {
            // establish setpoints for end of travel
            rightTarget = robot.chassis.frep + revs;
        }
        double remaining = (rightTarget - robot.chassis.frep);
        double driveValue = -(remaining * kP);
        // SmartDashboard.clearPersistent("FLEP");
        // SmartDashboard.clearPersistent("FREP");
        // SmartDashboard.clearPersistent("Remaining");
        SmartDashboard.clearPersistent("Drive value: ");
        // SmartDashboard.clearPersistent("FLEV");
        // SmartDashboard.clearPersistent("FREV");
        // SmartDashboard.clearPersistent("Revs");
        // SmartDashboard.putNumber("FLEP", robot.chassis.flep);
        // SmartDashboard.putNumber("FREP", robot.chassis.frep);
        // SmartDashboard.putNumber("Remaining", remaining);
        SmartDashboard.putNumber("Drive value: ", driveValue);
        // SmartDashboard.putNumber("FLEV", robot.chassis.flEncoder.getVelocity());
        // SmartDashboard.putNumber("FREV", robot.chassis.frEncoder.getVelocity());
        double done = revs - remaining;
        System.out.println("Done:" + done);
        int inchesDone = (int) robot.chassis.revolutionsToInches(done);
        System.out.println("Drive: " + inchesDone + "/" + inches);
        System.out.println("stepCounter: " + stepCounter);
        if (stepCounter <= 50.0 * 4.5) {
            robot.chassis.drive(-Math.abs(Math.abs(driveValue) > MAX_DRIVE ? MAX_DRIVE : driveValue), 0,
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
        SmartDashboard.putNumber("IMU", robot.chassis.imu.yaw);
        double turnValue = Math.abs(rotation) > MAX_TURN ? Math.copySign(MAX_TURN, rotation) : rotation;
        SmartDashboard.putNumber("turnValue", turnValue);
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
        SmartDashboard.putNumber("IMU", robot.chassis.imu.yaw);
        double turnValue = Math.abs(rotation) > MAX_TURN ? Math.copySign(MAX_TURN, rotation) : rotation;
        SmartDashboard.putNumber("turnValue", turnValue);
        robot.chassis.drive(0, turnValue);
        System.out.println(robot.chassis.imu.yaw + " <-- YAW, TARGET --> " + degrees);
        if (Math.abs(degrees - robot.chassis.imu.yaw) % 360 < 2) {
            Done();
        } else if (stepCounter > 50 * MAX_TURN_SECONDS)
            Fail("Turned for too long");
        else if(robot.vc.isHoopValid()){
            Done();
        }
    }

    private void StartGatherer() {
        robot.shooter.disableManual = true;
        System.out.println("StartGatherer set gathererDown");
        robot.shooter.gathererState = Shooter.gathererDown;
        Done();
    }

    private void StopGatherer() {
        robot.shooter.disableManual = false;
        System.out.println("StopGatherer set holding");
        robot.shooter.gathererState = Shooter.holding;
        Done();
    }

    private void StartFlywheel(double rpm) {
        if (rpm == 0)
            rpm = robot.shooter.calculateShooterRPMS(robot.vc.hoopx);
        robot.shooter.startShooter(rpm);
        Done();
    }

    private void StopFlywheel() {
        robot.shooter.stopShooter();
        Done();
    }

    private void Shoot() {
        holdFeeder = false;
        robot.shooter.disableManual = true;
        robot.shooter.startFeeder(robot.shooter.feederSpeed);
        if (stepCounter >= FEEDER_CYCLES) {
            robot.shooter.disableManual = false;
            holdFeeder = true;
            Done();
        }
    }

    private void DriveBall(int desiredDistanceFromBall) { // in inches
        double positionError = 0;
        if(robot.vc.ballx != 0){
            positionError = robot.vc.ballx * 12 - desiredDistanceFromBall;
            ySpeed = positionError * 0.04;
            if(ySpeed > MAX_DRIVE)
                ySpeed = MAX_DRIVE;
            System.out.println("Drive value: " + ySpeed);
        }
        if (!robot.vc.trackBall(-Math.abs(ySpeed)))
            Fail("No ball found");
        if (positionError < 0 && robot.vc.ballx != 0){
            robot.chassis.drive(0,0,false);
            Done();
        }
        if (stepCounter > MAX_BALL_SECONDS * 500){
            Fail("Took too long");
        }
    }

    private void DriveHoop(int desiredDistanceFromTarget) { // in inches
        // double positionError = desiredDistanceFromTarget - robot.vc.hoopx * 12;
        // double ySpeed = positionError * 0.04;
        double positionError = 1000;
        System.out.println(robot.vc.hoopx);
        if(robot.vc.hoopx != 0){
            positionError = robot.vc.hoopx * 12 - desiredDistanceFromTarget;
            ySpeed = positionError * 0.04;
            if(ySpeed > MAX_DRIVE)
                ySpeed = MAX_DRIVE;
            System.out.println("Drive value: " + ySpeed);
        }

        if(ySpeed > MAX_DRIVE){
            ySpeed = MAX_DRIVE;
        }

        if (!robot.vc.trackHoop(-ySpeed))
            Fail("No hoop found");
        if (Math.abs(positionError) < HOOP_ERROR_INCHES && Math.abs(robot.vc.hoopr) < HOOP_ERROR_DEGREES){
            robot.chassis.drive(0,0,false);
        }
    }

    private void AlignHoop() {
        if (!robot.vc.trackHoop(0))
            Fail("No hoop found");
        else if (Math.abs(robot.vc.hoopr) < HOOP_ERROR_DEGREES)
            Done();
    }
}
