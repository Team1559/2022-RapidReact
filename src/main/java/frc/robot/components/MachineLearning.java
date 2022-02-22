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
            File myObj = new File("/1559data/" + name + ".txt");
            myObj.mkdir();

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
        write(out);
        out = "";
    }

    public void write(String data) {
        try {
            FileWriter myWriter = new FileWriter("/1559data/" + filename + ".txt");

            myWriter.write(data);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        }

        catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public String readFile() {
        return readFile(filename);
    }

    public String readFile(String fileName) {
        File file = new File("/1559data/" + fileName + ".txt");
        String fileContent = "";

        try (FileReader fr = new FileReader(file)) {
            char[] chars = new char[(int) file.length()];
            fr.read(chars);

            fileContent = new String(chars);
        }

        catch (IOException e) {
            e.printStackTrace();
        }

        return fileContent;

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
