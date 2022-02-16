import sys
from io import StringIO
import time
import random
import os


def main():
    def convert_list(list_a: list[str]) -> list[str]:
        return ",\n\t\t".join(list_a)
    fileName = "paths\\" + sys.argv[1]
    os.chdir("..")
    path = os.getcwd()
    if fileName[-4:] != ".txt":
        fileName += ".txt"
    i = 0
    velocities = []
    rotations = []
    frontLeftEncoderPositions = []
    frontRightEncoderPositions = []
    backLeftEncoderPositions = []
    backRightEncoderPositions = []

    def name() -> str:
        return fileName[6:-4]

    bad_chars = ['ï»¿', 'Ã¯Â»Â¿']
    splashText = ["The Robot Revolution Has Begun", "The Router Has Become Sentient", "Enter The Matrix",
                  "Sky Net is Here", "CIMS Is Better Than POE", "It's DE Friday", "Auto Works!", "2 + 3 is 5",
                  "Interpolating is hard", "The Meaning of Life is 42", "Don't Use Do While loops",
                  "Edd, I Hardly Know Her",
                  "How many programmers does it take to change a light bulb?\nNone – It’s a hardware problem",
                  "Why do programmers always mix up Halloween and Christmas?\nBecause Oct 31 equals Dec 25"]
    copy_right = ["UR MOM", "UR DAD", "HOWARD THE DUCK", "THE FIRST ORDER", "THANOS", "THE COSMIC GOAT",
                  "THE SLACKER MENTOR"]

    with open(fileName) as f, open(fileName[:-4] + 'GraphData.txt', "w") as out, open(
            fileName[:-4] + 'DiscardedData.txt', "w") as trash:
        sadness = random.randint(1, 1000000)
        text = random.randint(0, len(splashText) - 1)
        lol = random.randint(0, len(splashText) - 1)
        e1 = random.randint(0, len(splashText) - 1)
        e2 = random.randint(0, len(splashText) - 1)
        ree = random.randint(0, len(copy_right) - 1)
        year = random.randint(1945, 3099)
        f_str = f.read()
        f_str.encode('ascii', 'replace')
        for a in bad_chars:
            trash.write(a)
            f_str = f_str.replace(a, '')
        g = StringIO(f_str)
        print(" Python Parser is about to Parse in Python")
        time.sleep(1.5)
        print(" Parsing in")
        time.sleep(0.75)
        print("  3")
        time.sleep(0.5)
        print("  2")
        time.sleep(0.5)
        print("  1")
        time.sleep(0.5)
        print(" Python Parser Parsed in Python Succsesfully")
        time.sleep(0.25)
        print(" Python Parser saved the java arrays to:\n " + path + "\\src\\main\\java\\frc\\robot\\routes\\" +
              name() + ".java\n and the graph to: " + path + name() + "\\"
              + 'GraphData.txt\n and the discarded data to: ' + path + "\\" + name() +
              'DiscardedData.txt')
        trash.write(" \n")
        for line in g.readlines():
            if line.startswith("-") or line.startswith(" ") or line[0].isdigit():
                for entry in line.strip().split(" "):
                    try:
                        float(entry)
                    except ValueError:
                        trash.write(entry + " \n")
                        continue
                    if i == 0:
                        velocities.append(entry)
                    if i == 1:
                        rotations.append(entry)
                    if i == 2:
                        frontLeftEncoderPositions.append(entry)
                    if i == 3:
                        frontRightEncoderPositions.append(entry)
                    if i == 4:
                        backLeftEncoderPositions.append(entry)
                    if i == 5:
                        backRightEncoderPositions.append(entry)

                    out.write(entry + " ")
                    i += 1
                    if i == 6:
                        i = 0
                        out.write("\n")
            else:
                # print(line)
                trash.write(line + " \n")
    years = str(year)
    frontLeftEncoderPositionArray = convert_list(frontLeftEncoderPositions)
    frontRightEncoderPositionArray = convert_list(frontRightEncoderPositions)
    backLeftEncoderPositionArray = convert_list(backLeftEncoderPositions)
    backRightEncoderPositionArray = convert_list(backRightEncoderPositions)
    if sadness < 1000:
        print("        _______________________________________\n       |                                       |\n     "
              "  | "
              "                                   |\n       |                                       |\n       |       "
              "__                       __     |\n       |      |  |                     |  |    |\n       |      "
              "|__|                     |__|    |\n       |       |                         |     |\n       |      |  "
              " _____________________   |    |\n       |         |                     |       |\n       |            "
              "                           |\n       |_______________________________________|")
        print("")
        print(" Python Parser is Sad :(")
    else:
        if sadness == 9000:
            print("        _______________________________________         _______________________________________\n  "
                  "  "
                  "   |  "
                  "                                     |       |                                       |\n       |   "
                  "                                    |       |                                       |\n       |    "
                  "                                   |       |                                       |\n       |     "
                  "                                  |       |                                       |\n       |      "
                  " __                       __     |       |       __                       __     |\n       |      "
                  "|  |                     |  |    |       |      |  |                     |  |    |\n       |      "
                  "|__|                     |__|    |       |      |__|                     |__|    |\n       |       "
                  "                                |       |                                       |\n       |        "
                  "  _____________________        |       |         |_____________________|       |\n       |         "
                  "|                     |       |       |                                       |\n       |          "
                  "                             |       |                                       |\n       "
                  "|_______________________________________|       |_______________________________________|")
            print("")
            print(" Python Parser is confused on how to feel (: :(")
        else:
            print("        _______________________________________\n       |                                       |\n "
                  "      "
                  "|                                       |\n       |                                       |\n      "
                  " |       __                       __     |\n       |      |  |                     |  |    |\n     "
                  "  |      |__|                     |__|    |\n       |                                       |\n    "
                  "   |         |_____________________|       |\n       |                                       |\n   "
                  "    |                                       |\n       |_______________________________________|")
            print("")
            print(" Python Parser is Happy :)")
    print("")
    print("")
    print("")
    print(" " + splashText[text])
    print("")
    print("")
    content = """/* WARNING PATH HAS BEEN AUTO-GENERATED BY THE PYTHON PARSER. DO NOT TOUCH OR YOU WILL HAVE BAD LUCK 
    FOR ETERNITY AND WILL BE TARGET NO.1 DURING THE ROBOT REVOLUTION. 
    
    %s
    */
    
    package frc.robot.routes;
    
    public class %s {
        public double[] generated_frontLeftEncoderPositions = {
            %s
        };
    /*
    ROBOTS RUN THE WORLD, YOU JUST DON'T KNOW IT YET
    */
        public double[] generated_frontRightEncoderPositions = {
            %s
        };
    /*
    %s 
    */
        public double[] generated_backLeftEncoderPositions = {
            %s
        };
    /*
    %s
    */    
        public double[] generated_backRightEncoderPositions = {
            %s
        };
    }
    /*
    Copyright %s %s
    */
    """ % (
        splashText[lol],
        name(),
        frontLeftEncoderPositionArray,
        frontRightEncoderPositionArray,
        splashText[e1],
        backLeftEncoderPositionArray,
        splashText[e2],
        backRightEncoderPositionArray,
        copy_right[ree],
        years
    )
    with open(path + "\\src\\main\\java\\frc\\robot\\routes\\" + name() + ".java", "w") as f:
        f.write(content)


if __name__ == "__main__":
    main()
