/*----------------------------------------------------------------------------
 * Copyright (c) 2017-2018 FIRST. All Rights Reserved.
 * Open Source Software - may be modified and shared by FRC teams. The code
 * must be accompanied by the FIRST BSD license file in the root directory of
 * the project.
 * ---------------------------------------------------------------------------
 */

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;

public class OperatorInterface {
    public static final int PILOT_JOYSTICK   = 0;
    public static final int COPILOT_JOYSTICK = 1;

    public static final double FULL_PRESS_MARGIN = 0.99D;

    private boolean isPilotDPadPressed;
    private boolean isCopilotDPadPressed;

    public final Joystick pilot;
    public final Joystick copilot;

    private final DTButton[] pilotButtons;
    private final DTButton[] copilotButtons;

    public OperatorInterface() {
        pilot = new Joystick(PILOT_JOYSTICK);
        copilot = new Joystick(COPILOT_JOYSTICK);

        copilotButtons = new DTButton[20];
        for (int i = 0; i < copilotButtons.length; i++) {
            copilotButtons[i] = new DTButton(copilot, i + 1);
        }

        pilotButtons = new DTButton[20];
        for (int i = 0; i < pilotButtons.length; i++) {
            pilotButtons[i] = new DTButton(pilot, i + 1);
        }

        isPilotDPadPressed = false;
        isCopilotDPadPressed = false;
    }

    /**
     * Gets the left joystick x-value on the pilot controller
     */
    public double pilotLeftStickX() {
        return pilot.getRawAxis(Buttons.leftJoystick_x);
    }

    /**
     * Gets the left joystick y-value on the pilot controller
     */
    public double pilotLeftStickY() {
        return pilot.getRawAxis(Buttons.leftJoystick_y);
    }

    /**
     * Gets the right joystick x-value on the pilot controller
     */
    public double pilotRightStickX() {
        return pilot.getRawAxis(Buttons.rightJoystick_x);
    }

    /**
     * Gets the right joystick y-value on the pilot controller
     */
    public double pilotRightStickY() {
        return pilot.getRawAxis(Buttons.rightJoystick_y);
    }

    /**
     * Gets the value of the left trigger on the pilot controller
     */
    public double pilotLeftTrigger() {
        return pilot.getRawAxis(Buttons.leftTrigger);
    }

    /**
     * Gets the value of the right trigger on the pilot controller
     */
    public double pilotRightTrigger() {
        return pilot.getRawAxis(Buttons.rightTrigger);
    }

    public double getPilotX() {
        // gets the y axis on the ps4 controller (forward and back)
        return squareKeepSign(pilotLeftStickX());
    }

    public double getPilotY() {
        // gets the y axis on the ps4 controller (forward and back)
        return squareKeepSign(pilotLeftStickY());
    }

    public double getPilotZ() {
        // gets the z axis on the ps4 controller (rotation)
        return squareKeepSign(pilotRightStickX());
    }

    public DTButton getCopilotButton(int num) {
        // this gets the id number of the button on the copilot box
        return copilotButtons[num];
    }

    public boolean coButtonIsPressed(int button) {
        // this will tell us if a button is pressed on the copilot box and return true
        return copilot.getRawButton(button);
    }

    public double getCopilotAxis(int num) {
        // gets the axis on the copilot box
        return copilot.getRawAxis(num);
    }

    public boolean axisToButtonIsPressed(int axis) {
        // returns true if the axis button is pressed
        return (copilot.getRawAxis(axis) >= FULL_PRESS_MARGIN);
    }

    public int getRawDPadPilot() {
        return pilot.getPOV(0);
    }

    public int getRawDPadCopilot() {
        return copilot.getPOV(0);
    }

    public int getDPadPilotPress() {
        if (!isPilotDPadPressed) {
            isPilotDPadPressed = true;
            return pilot.getPOV(0);
        } else {
            if (pilot.getPOV(0) == 0) {
                isPilotDPadPressed = false;
            }
            return 0;
        }
    }

    public int getDPadCopilotPress() {
        if (!isCopilotDPadPressed) {
            isCopilotDPadPressed = true;
            return copilot.getPOV(0);
        } else {
            if (copilot.getPOV(0) == 0) {
                isCopilotDPadPressed = false;
            }
            return 0;
        }
    }

    public static double squareKeepSign(double d) {
        if (d > 0D) {
            return Math.pow(d, 2);
        } else {
            return -Math.pow(d, 2);
        }
    }
}
