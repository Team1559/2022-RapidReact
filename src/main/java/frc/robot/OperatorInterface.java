/*----------------------------------------------------------------------------*/
/* Copyright (c) 2021-2022 FIRST. All Rights Reserved. */
/* Open Source Software - may be modified and shared by FRC teams. The code */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project. */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;

public class OperatorInterface {
    private boolean isPilotDPadPressed;
    private boolean isCopilotDPadPressed;

    public final Joystick pilot;
    public final Joystick copilot;

    private final DTButton[] pilotButtons;
    private final DTButton[] copilotButtons;

    // optional
    private DTButton[]    cocopilotButtons;
    private final boolean cocopilotButtonsActive;

    public OperatorInterface() {
        this.pilot = new Joystick(Constants.PILOT_JOYSTICK);
        this.copilot = new Joystick(Constants.COPILOT_JOYSTICK);

        this.copilotButtons = new DTButton[20];
        for (int i = 0; i < copilotButtons.length; i++) {
            this.copilotButtons[i] = new DTButton(this.copilot, i + 1);
        }

        this.pilotButtons = new DTButton[20];
        for (int i = 0; i < pilotButtons.length; i++) {
            this.pilotButtons[i] = new DTButton(this.pilot, i + 1);
        }

        this.cocopilotButtonsActive = this.cocopilotButtons != null;

        this.isPilotDPadPressed = false;
        this.isCopilotDPadPressed = false;
    }

    /**
     * Gets the left joystick x-value on the pilot controller
     */
    public double pilotLeftStickX() {
        return this.pilot.getRawAxis(Buttons.LEFT_JOYSTICK_X);
    }

    /**
     * Gets the left joystick y-value on the pilot controller
     */
    public double pilotLeftStickY() {
        return this.pilot.getRawAxis(Buttons.LEFT_JOYSTICK_Y);
    }

    /**
     * Gets the right joystick x-value on the pilot controller
     */
    public double pilotRightStickX() {
        return this.pilot.getRawAxis(Buttons.RIGHT_JOYSTICK_X);
    }

    /**
     * Gets the right joystick y-value on the pilot controller
     */
    public double pilotRightStickY() {
        return this.pilot.getRawAxis(Buttons.RIGHT_JOYSTICK_Y);
    }

    /**
     * Gets the value of the left trigger on the pilot controller
     */
    public double pilotLeftTrigger() {
        return this.pilot.getRawAxis(Buttons.LEFT_TRIGGER);
    }

    /**
     * Gets the value of the right trigger on the pilot controller
     */
    public double pilotRightTrigger() {
        return this.pilot.getRawAxis(Buttons.RIGHT_TRIGGER);
    }

    public double getPilotX() {
        // gets the y axis on the ps4 controller (forward and back)
        return squareKeepSign(this.pilotLeftStickX());
    }

    public double getPilotY() {
        // gets the y axis on the ps4 controller (forward and back)
        return squareKeepSign(this.pilotLeftStickY());
    }

    public double getPilotZ() {
        // gets the z axis on the ps4 controller (rotation)
        return squareKeepSign(this.pilotRightStickX());
    }

    public DTButton getCopilotButton(int num) {
        // this gets the id number of the button on the copilot box
        return this.copilotButtons[num];
    }

    public boolean coButtonIsPressed(int button) {
        // this will tell us if a button is pressed on the copilot box and
        // return true
        return this.copilot.getRawButton(button);
    }

    public double getCopilotAxis(int num) {
        // gets the axis on the copilot box
        return this.copilot.getRawAxis(num);
    }

    public DTButton getCocopilotButton(int num) {
        // returns the id num of the copilot box
        if (this.cocopilotButtonsActive) {
            return cocopilotButtons[num];
        } else {
            return null;
        }
    }

    public boolean axisToButtonIsPressed(int axis) {
        // returns true if the axis button is pressed
        return (this.copilot.getRawAxis(axis) >= Constants.FULL_PRESS_MARGIN);
    }

    public int getRawDPadPilot() {
        return this.pilot.getPOV(0);
    }

    public int getRawDPadCopilot() {
        return this.copilot.getPOV(0);
    }

    public int getDPadPilotPress() {
        if (!this.isPilotDPadPressed) {
            this.isPilotDPadPressed = true;
            return this.pilot.getPOV(0);
        } else {
            if (this.pilot.getPOV(0) == 0) {
                this.isPilotDPadPressed = false;
            }
            return 0;
        }
    }

    public int getDPadCopilotPress() {
        if (!this.isCopilotDPadPressed) {
            this.isCopilotDPadPressed = true;
            return this.copilot.getPOV(0);
        } else {
            if (this.copilot.getPOV(0) == 0) {
                this.isCopilotDPadPressed = false;
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
