package frc.robot;

import com.ctre.phoenix.time.StopWatch;

import edu.wpi.first.wpilibj.XboxController;

public class DTXboxController extends XboxController {
    public enum Side {
        LEFT, RIGHT, BOTH
    }

    private double duration = 0;
    private double power;
    private Side side = Side.BOTH;

    private StopWatch stopWatch = new StopWatch();

    private boolean wasDpadPressed = false;

    public DTXboxController(int port) {
        super(port);
    }

    public boolean getDpad(int angle) {
        int pov = getPOV();

        if (angle == -1 && pov != -1) {
            return true;
        }

        else if (pov == angle) {
            return true;
        }

        else {
            return false;
        }
    }

    public int getRawDPad() {
        return getPOV();
    }

    public boolean isDpadPressed() {
        return getPOV() != -1;
    }

    public boolean getDPadPress(int angle) {
        if (!wasDpadPressed) {
            wasDpadPressed = isDpadPressed();

            if (angle == -1 && getPOV() != -1) {
                return true;
            }

            if (getPOV() == angle) {
                return true;
            }

            else {
                return false;
            }
        }

        else {
            wasDpadPressed = isDpadPressed();

            return false;
        }
    }

    public int getRawDPadPress() {
        if (!wasDpadPressed) {
            wasDpadPressed = isDpadPressed();
            return getPOV();
        }

        else {
            wasDpadPressed = isDpadPressed();

            return -1;
        }
    }

    /**
     * Sets the rumble on the controller
     * 
     * @param duration Time in seconds for the rumble to last
     */
    public void startRumble(double duration) {
        startRumble(duration, 1);

    }

    /**
     * Sets the rumble on the controller
     * 
     * @param duration Time in seconds for the rumble to last
     * @param side     What side the ruble on <code>LEFT<code>,
     *                 <code>RIGHT<code>, <code>BOTH<code>
     */
    public void startRumble(double duration, Side side) {
        startRumble(duration, 1, side);

    }

    /**
     * Sets the rumble on the controller
     * 
     * @param duration Time in seconds for the rumble to last
     * @param power    Strength of rumble. Values range from 0-1
     */
    public void startRumble(double duration, double power) {
        startRumble(duration, power, Side.BOTH);

    }

    /**
     * Sets the rumble on the controller
     * 
     * @param duration Time in seconds for the rumble to last
     * @param power    Strength of rumble. Values range from 0-1
     * @param side     What side the ruble on <code>LEFT<code>,
     *                 <code>RIGHT<code>, <code>BOTH<code>
     */
    public void startRumble(double duration, double power, Side side) {
        this.duration = duration;
        this.power = power;
        this.side = side;
    }

    /**
     * Runs the rumble periodically
     */
    public void rumblePeriodic() {
        if (duration <= 0 && duration != -1) {
            return;
        }

        stopWatch.start();
        if (side == Side.LEFT) {
            setRumble(RumbleType.kLeftRumble, power);
        } else if (side == Side.RIGHT) {
            setRumble(RumbleType.kRightRumble, power);
        } else {
            setRumble(RumbleType.kLeftRumble, power);
            setRumble(RumbleType.kRightRumble, power);
        }

        if (duration > 0 && stopWatch.getDuration() >= duration) {
            if (side == Side.LEFT) {
                setRumble(RumbleType.kLeftRumble, 0);
            } else if (side == Side.RIGHT) {
                setRumble(RumbleType.kRightRumble, 0);
            } else {
                setRumble(RumbleType.kLeftRumble, 0);
                setRumble(RumbleType.kRightRumble, 0);
            }
        }
    }

    /**
     * Stops the rumbe on both sides
     */
    public void stopRumble() {
        stopRumble(Side.BOTH);
    }

    /**
     * Stops the rumbe on a certain side
     * 
     * @param side What side to stop the ruble on <code>LEFT<code>,
     *                 <code>RIGHT<code>, <code>BOTH<code>
     */
    public void stopRumble(Side side) {
        if (side == Side.LEFT) {
            setRumble(RumbleType.kLeftRumble, 0);
        } else if (side == Side.RIGHT) {
            setRumble(RumbleType.kRightRumble, 0);
        } else {
            setRumble(RumbleType.kLeftRumble, 0);
            setRumble(RumbleType.kRightRumble, 0);
        }
    }
}
