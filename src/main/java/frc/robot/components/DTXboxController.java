package frc.robot.components;

import java.util.ArrayList;
import com.ctre.phoenix.time.StopWatch;

import edu.wpi.first.wpilibj.XboxController;

public class DTXboxController extends XboxController implements Runnable {
    public enum Side {
        LEFT, RIGHT, BOTH
    }

    private ArrayList<Boolean> bools = new ArrayList<>();
    private double duration = 0;
    private double power;
    private Side side = Side.BOTH;

    private StopWatch stopWatch = new StopWatch();

    private boolean wasDpadPressed = false;
    private boolean wasDpadReleased = false;

    /**
     * Creates a controller object on the specified port
     * 
     * @param port (0-5)
     */
    public DTXboxController(int port) {
        super(port);
        for (int i = 0; i <= 1000; i++) {
            bools.add(null);
        }
    }

    /**
     * returns true if the dpad is pressed at the specified angle
     * 
     * @param angle The desired angle
     * @return Whether or not the dpad is presses at the specified angle
     */
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

    /**
     * Returns the curent dpad angle
     * 
     * @return The current dpad angle
     */
    public int getRawDPad() {
        return getPOV();
    }

    /**
     * Return true when the dpad is depressed
     * 
     * @return Whether the dpad is pressed
     */
    public boolean isDpadPressed() {
        return getPOV() != -1;
    }

    /**
     * Returns true on the rising edge of when the dpad is pressed at the specified
     * angle
     * 
     * @param angle The desired angle
     * @return Whether the dpad has just been pressed at the specifed angle
     */
    public boolean getDPadPress(int angle) {
        int currentAngle = getRawDPadPress();
        if (angle == -1 && currentAngle != -1) {
            return true;
        }

        if (currentAngle == angle) {
            return true;
        }

        else {
            return false;
        }
    }

    /**
     * Returns true on the rising edge of when the dpad is released at the specified
     * angle
     * 
     * @param angle The desired angle
     * @return Whether the dpad has just been released at the specifed angle
     */
    public boolean getDPadRelease(int angle) {
        int currentAngle = getRawDPadRelease();
        if (angle == -1 && currentAngle != -1) {
            return true;
        }

        if (currentAngle == angle) {
            return true;
        }

        else {
            return false;
        }
    }

    /**
     * Returns the current angle on the rising edge of when the dpad is pressed and
     * -1 otherwise
     * 
     * @return The current angle of the dpad if it has just been pressed
     */
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
     * Returns the current angle on the rising edge of when the dpad is released and
     * -1 otherwise
     * 
     * @return The current angle of the dpad if it has just been released
     */
    public int getRawDPadRelease() {
        int out = -1;
        if (!wasDpadReleased) {
            wasDpadReleased = isDpadPressed();
            out = -1;
        }

        else {
            if (wasDpadReleased) {
                out = getPOV();
            } else {
                out = -1;
            }
            wasDpadReleased = isDpadPressed();

        }
        return out;
    }

    /**
     * Returns true on the rising edge of boolean being set to true
     * 
     * @param button The boolean to track
     * @param id     The unique id of the button (0-1000)
     * @return Whether the button has just been pressed
     */
    public boolean getPress(boolean button, int id) {
        try {
            if (bools.get(id) != null)
                bools.set(id, false);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        if (button) {
            if (!bools.get(id)) {
                bools.set(id, true);
                return true;
            }
        } else
            bools.set(id, false);
        return false;
    }

    /**
     * Returns true on the rising edge of boolean being set to false
     * 
     * @param button The boolean to track
     * @param id     The unique id of the button (0-1000)
     * @return Whether the button has just been released
     */
    public boolean getRelease(boolean button, int id) {
        boolean out = false;
        try {
            if (bools.get(id) != null)
                bools.set(id, false);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        if (button) {
            if (!bools.get(id)) {
                bools.set(id, false);
                out = false;
            }
        } else {
            if (bools.get(id))
                out = true;
            else
                out = false;
            bools.set(id, false);
        }
        return out;
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
     * @param side     What side the ruble on <code>LEFT</code>,
     *                 <code>RIGHT</code>, or <code>BOTH</code>
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
     * @param side     What side the ruble on <code>LEFT</code>,
     *                 <code>RIGHT</code>, or <code>BOTH</code>
     */
    public void startRumble(double duration, double power, Side side) {
        this.duration = duration;
        this.power = power;
        this.side = side;
        stopWatch.start();
    }

    /**
     * Runs the rumble periodically
     */
    @Override
    public void run() {
        System.out.println("Rumble periodc started");
        while (true) {
            if (stopWatch.getDuration() < duration && duration > 0 || duration == -1) {
                if (side == Side.LEFT) {
                    setRumble(RumbleType.kLeftRumble, power);
                } else if (side == Side.RIGHT) {
                    setRumble(RumbleType.kRightRumble, power);
                } else {
                    setRumble(RumbleType.kLeftRumble, power);
                    setRumble(RumbleType.kRightRumble, power);
                }
            }

            if (duration > 0 && stopWatch.getDuration() >= duration) {
                duration = 0;
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
    }

    /**
     * Stops the rumble on both sides
     */
    public void stopRumble() {
        stopRumble(Side.BOTH);
    }

    /**
     * Stops the rumble on a certain side
     * 
     * @param side What side to stop the ruble on <code>LEFT</code>,
     *             <code>RIGHT</code>, or <code>BOTH</code>
     */
    public void stopRumble(Side side) {
        duration = 0;
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
