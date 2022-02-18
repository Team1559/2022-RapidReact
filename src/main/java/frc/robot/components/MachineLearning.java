package frc.robot.components;

import java.io.*;

public class MachineLearning {
    private String filename;
    private String out;

    public MachineLearning() {
        out = "";
        filename = "";
    }

    public void periodic(String data) {
        out += data;
    }

    public void createfile(String name) {
        if (name.equals("")) {
            name = "Default_Path.txt";
        }

        else {
            name += ".txt";
        }
        filename = name;
        try {
            new File("/paths/" + name).mkdirs();
            File myObj = new File("/paths/" + name + ".txt");

            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            }

            else {
                System.out.println("File already exists.");
            }
        }

        catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void write() {
        try {
            FileWriter myWriter = new FileWriter("/paths/" + filename + ".txt");

            myWriter.write(out);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        }

        catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        out = "";
    }

    public double interpolate(double counter, double[] value) {
        int intCounter = (int) counter;
        double percent = (counter - intCounter);

        if (intCounter < value.length - 1) {
            double interpolatedValue = (value[intCounter] + (percent * (value[intCounter + 1] - value[intCounter])));
            return interpolatedValue;
        }

        else {
            return value[value.length - 1];
        }
    }
}
