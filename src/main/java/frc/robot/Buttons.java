package frc.robot;

public class Buttons {
    // Use this class to label each button sowe don't accidentily assign the same button to 2 different functions
    private static OperatorInterface oi;
    
    public Buttons(OperatorInterface oi){
        this.oi = oi;
    }
    public static boolean autoShootButton(){
        return oi.pilot.getAButton();
    }
    public static boolean autoCollectButton(){
        return oi.pilot.getAButton();
    }
}
