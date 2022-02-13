package frc.robot;
import java.util.*;

// import frc.robot.subsystems.Chassis;
import java.io.File;  // Import the File class
import java.io.IOException;  // Import the IOException class to handle errors
import java.io.FileWriter;

    


public class MachineLearning{
    // private Chassis drivetrain;
    private ArrayList<Double> forwardSpeed = new ArrayList<Double>();
    private ArrayList<Double> sideSpeed = new ArrayList<Double>();
    private ArrayList<Double> frontLeftEncoderPosition = new ArrayList<Double>();
    private ArrayList<Double> frontRightEncoderPosition = new ArrayList<Double>();
    private ArrayList<Double> backLeftEncoderPosition = new ArrayList<Double>();
    private ArrayList<Double> backRightEncoderPosition = new ArrayList<Double>();

    private String filename = "path1.txt";
    public void executeCmd(String cmd){
      String[] list = cmd.split(" ");
      if(list.length == 1){
        executeCmd(list[0]);
      }
      else if(list.length == 2){
        executeCmds(list[0], list[1]);
      }
      else if(list.length == 3){
        executeCmds(list[0], list[1], list[2]);
      }
      else if(list.length == 4){
        executeCmds(list[0], list[1], list[2], list[3]);
      }
    }

    private void executeCmds(String cmd){
      executeCmds(cmd, "");
    }

    private void executeCmds(String cmd, String arg1){
      executeCmds(cmd, arg1, "");
    }
    private void executeCmds(String cmd, String arg1, String arg2){
      executeCmds(cmd, arg1, arg2, "");
    }
    
    @SuppressWarnings("unused")
    private void executeCmds(String cmd, String arg1, String arg2, String arg3){
      String[] command = {cmd, arg1, arg2, arg3};
      ProcessBuilder builder = new ProcessBuilder(command);
      builder = builder.directory(new File("/"));
      try{
        Process p = builder.start();
      }catch(IOException e){
        e.printStackTrace();
      }
    }

    // private int counter = 0;
    public MachineLearning(boolean record){
        forwardSpeed.clear();
        sideSpeed.clear();
        frontLeftEncoderPosition.clear();
        frontRightEncoderPosition.clear();
        backLeftEncoderPosition.clear();
        backRightEncoderPosition.clear();
        if(record){
         executeCmd("sudo mkdir /paths");
         executeCmd("sudo chmod a=rwx /paths");
          try {
              File myObj = new File("/paths/" + filename);
              if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
              } else {
                System.out.println("File already exists.");
              }
            } catch (IOException e) {
              System.out.println("An error occurred.");
              e.printStackTrace();
            }
        }
    }

    public void periodic(Double _forwardSpeed, Double _sideSpeed, Double flep, Double frep, Double blep, Double brep){
        // counter ++;
        //if(forwardSpeed.size() <= 3000){
            forwardSpeed.add(_forwardSpeed);
            sideSpeed.add(_sideSpeed);
            frontLeftEncoderPosition.add(flep);
            frontRightEncoderPosition.add(frep);
            backLeftEncoderPosition.add(blep);
            backRightEncoderPosition.add(brep);
        // System.out.print(String.format("%.3f %.3f %.3f %.3f %.3f %.3f ",_forwardSpeed, _sideSpeed, lep, lev, rep, rev));
        // if(counter >= 500){
        //     System.out.println();
        //     counter=0;
        // }
    }

    public void write() {
      try {
        FileWriter myWriter = new FileWriter("/paths/"+ filename);
        for(int i = 0; i < forwardSpeed.size(); i++){
          myWriter.write(forwardSpeed.get(i) + " " + sideSpeed.get(i) + " " + frontLeftEncoderPosition.get(i) + " " + frontRightEncoderPosition.get(i) + " " + backLeftEncoderPosition.get(i) + " " + backRightEncoderPosition.get(i) + " ");
        }
        myWriter.write("Files in Java might be tricky, but it is fun enough!");
        myWriter.close();
        System.out.println("Successfully wrote to the file.");
      } catch (IOException e) {
        System.out.println("An error occurred.");
        e.printStackTrace();
      }
        // System.out.println("This is the end of the trajectory");
        // for(int i = 0; i < forwardSpeed.size(); i++){
        // System.out.print(forwardSpeed.get(i) +" "+ sideSpeed.get(i)+ " "+leftEncoderPosition.get(i) +" "+ LeftEncodervelocity.get(i)+ " "+rightEncoderPosition.get(i) +" "+ rightEncodervelocity.get(i)+ " ");
        // }
    }

    public double interpolate(double counter, double[] value){
        int intCounter = (int)counter;
        double percent = (counter -intCounter); 
        if(intCounter < value.length-1){
            double interpolatedValue = (value[intCounter] + (percent * (value[intCounter+1] - value[intCounter])));
            return interpolatedValue;
        }
        else{ 
            return value[value.length-1];
        }
    }
            // if(counter < forwardSpeed.size()){
            //     System.out.print(forwardSpeed.get(counter) +" "+ sideSpeed.get(counter)+ " "+leftEncoderPosition.get(counter) +" "+ LeftEncodervelocity.get(counter)+ " "+rightEncoderPosition.get(counter) +" "+ rightEncodervelocity.get(counter)+ " ");
            //     counter++;
            // }
            // if(counter == forwardSpeed.size()-1)
            //     System.out.println();

    }