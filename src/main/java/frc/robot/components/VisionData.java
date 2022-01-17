package frc.robot.components;

public class VisionData {
    
    public double hx;
    public double hy;
    public double hr;
    public double bx;
    public double by;
    public double br;
    public Integer status;

    public boolean IsValid(){
        if(status!=1){
            return false;
        }
        else
            return true;
    }

    public void Print() {
        System.out.printf("%3.1f %3.1f  %3.1f  %d\n", hx, hy, hr, bx, by, br, status);
    }
}