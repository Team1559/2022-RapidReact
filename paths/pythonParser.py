import sys
from io import StringIO
import time
import random

fileName = sys.argv[1]
path = os.system("cd..")
import sys
# its win32, maybe there is win64 too?
i = 0
velocities = []
rotations = []
leftEncoderPositions = []
rightEncoderPositions = []
bad_chars = ['ï»¿', 'Ã¯Â»Â¿']
splashText = ["The Robot Revolution Has Begun", "The Router Has Become Sentient", "Enter The Matrix", "Sky Net is Here", "CIMS Is Better Than POE", "It's DE Friday", "Auto Works!", "2 + 3 is 5", "Interpolating is hard", "The Meaning of Life is 42", "Don't Use Do While loops"]
copyright = ["UR MOM", "UR DAD", "HOWARD THE DUCK", "THE FIRST ORDER", "THANOS"]

with open(fileName) as f, open(fileName +'GraphData.txt', "w") as out, open(fileName +'DiscardedData.txt', "w") as trash:
    sadness = random.randint(1, 1000000)
    text = random.randint(0, len(splashText)-1)
    lol = random.randint(0, len(splashText)-1)
    ree = random.randint(0, len(copyright)-1)
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
    print(" Python Parser saved the java arrays to:\n " + path + "/src/main/java/frc/robot/" + fileName + ".java\n and the graph to\:n " + path + "/paths/"+ fileName +'GraphData.txt\n and the discarded data to:\n' + path + "/paths/"+ fileName +'DiscardedData.txt')
    trash.write(" \n")
    for line in g.readlines():
        if line.startswith("-") or line.startswith(" ") or line[0].isdigit():
            for entry in line.strip().split(" "):
                try:
                    float(entry)
                except:
                    trash.write(entry + " \n")
                    continue
                if i == 0:
                    velocities.append(entry)
                if i == 1:
                    rotations.append(entry)
                if i == 2:
                    leftEncoderPositions.append(entry)
                if i == 4:
                    rightEncoderPositions.append(entry)

                out.write(entry + " ")
                i += 1
                if i == 6:
                    i = 0
                    out.write("\n")
        else:
            # print(line)
            trash.write(line + " \n")
years = str(year)
velocityArray = ",\n\t\t".join(velocities)
rotationsArray = ",\n\t\t".join(rotations)
leftEncoderPositionArray = ",\n\t\t".join(leftEncoderPositions)
rightEncoderPositionArray = ",\n\t\t".join(rightEncoderPositions)
if sadness < 1000:
    print("        _______________________________________\n       |                                       |\n       |                                       |\n       |                                       |\n       |       __                       __     |\n       |      |  |                     |  |    |\n       |      |__|                     |__|    |\n       |       |                         |     |\n       |      |   _____________________   |    |\n       |         |                     |       |\n       |                                       |\n       |_______________________________________|")
    print("")
    print(" Python Parser is Sad " + user+ " :(")
else: 
    if sadness == 9000:
        print("        _______________________________________         _______________________________________\n       |                                       |       |                                       |\n       |                                       |       |                                       |\n       |                                       |       |                                       |\n       |                                       |       |                                       |\n       |       __                       __     |       |       __                       __     |\n       |      |  |                     |  |    |       |      |  |                     |  |    |\n       |      |__|                     |__|    |       |      |__|                     |__|    |\n       |                                       |       |                                       |\n       |          _____________________        |       |         |_____________________|       |\n       |         |                     |       |       |                                       |\n       |                                       |       |                                       |\n       |_______________________________________|       |_______________________________________|")
        print("")
        print(" Python Parser is confused on how to feel " + user+ " (: :(")
    else:
        print("        _______________________________________\n       |                                       |\n       |                                       |\n       |                                       |\n       |       __                       __     |\n       |      |  |                     |  |    |\n       |      |__|                     |__|    |\n       |                                       |\n       |         |_____________________|       |\n       |                                       |\n       |                                       |\n       |_______________________________________|")
        print("")
        print(" Python Parser is Happy " + user+ " :)")
print("")
print("")
print("")
print(" "+ splashText[text])
print("")
print("")
content = """
/*
WARNING PATH HAS BEEN AUTO-GENERATED BY THE PYTHON PARSER. DO NOT TOUCH OR YOU WILL HAVE BAD LUCK FOR ETERNITY AND WILL BE TARGET NO.1 DURING THE ROBOT REVOLUTION.



%s
*/

package frc.robot;

public class %s {
    public double[] generated_leftEncoderPositions = {
        %s
    };
/*
ROBOTS RUN THE WORLD, YOU JUST DON'T KNOW IT YET
*/
    public double[] generated_rightEncoderPositions = {
        %s
    };
}
/*
Copyright %s %s
*/
""" % (
    splashText[lol],
    fileName,
    leftEncoderPositionArray,
    rightEncoderPositionArray,
    copyright[ree],
    years
)

with open(path + "/src/main/java/frc/robot/" + fileName + ".java", "w") as f:
    f.write(content)