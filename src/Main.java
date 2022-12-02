import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {
    public static String inputPS = "bound-sprellpsd.smf";
    public static double scale = 1.0;
    public static double xTrans = 0;
    public static double yTrans = 0;
    public static float lowerX;
    public static float lowerY;
    public static float upperX;
    public static float upperY;

    public final static int xWindowLength = 501;

    public final static int yWindowLength = 501;

    public static int viewportLowerX = 0;

    public static int viewportLowerY = 0;

    public static int viewportUpperX = 500;

    public static int viewportUpperY = 500;

    public static float xPRP = 0.0f;
    public static float yPRP = 0.0f;
    public static float zPRP = 1.0f;

    public static float xVRP = 0.0f;
    public static float yVRP = 0.0f;
    public static float zVRP = 0.0f;

    public static float xVPN = 0.0f;
    public static float yVPN = 0.0f;
    public static float zVPN = -1.0f;

    public static float xVUP = 0.0f;
    public static float yVUP = 1.0f;
    public static float zVUP = 0.0f;

    public static float umin = -0.7f;
    public static float vmin = -0.7f;
    public static float umax = 0.7f;
    public static float vmax = 0.7f;

    public static float front = .6f;

    public static float back = -.6f;

    public static boolean parallel = false;

    public static boolean backfaceCulling = false;

    public static String green;

    public static String blue;

    public static float zmin = -1.0f;

    public static float zmaxparallel = 0.0f;

    public static float nPRP = findNorm(xPRP, yPRP, zPRP);

    public static float zmaxperspective = (nPRP - front)/(back - nPRP);
    public static ArrayList<ArrayList<Float>> scanFillList = new ArrayList<>(); //intersect
    public static ArrayList<ArrayList<Float>> scanFillList2 = new ArrayList<>(); //edge

    public static ArrayList<ArrayList<Float>> verticesList = new ArrayList<>();

    public static ArrayList<ArrayList<Integer>> faceList = new ArrayList<>();

    public static ArrayList<ArrayList<Float>> coordinateList = new ArrayList<>();

    public static void main(String[] args) throws FileNotFoundException {

        int counter = 0;
        int xOrigin = 0;
        int yOrigin = 0;
        int drawingCounter = 0;
        double rotateRad = 0;
        double xRotatePrime;
        double yRotatePrime;
        float d = zPRP/(back-zPRP);
        ArrayList<Double> list = new ArrayList<>();
        ArrayList<Integer> emptyIntList = new ArrayList<>();
        int[][] coordinate = new int[501][501];
        ArrayList<Float> clippedList1 = new ArrayList<>();
        ArrayList<Float> clippedList2 = new ArrayList<>();
        ArrayList<Float> clippedList3 = new ArrayList<>();
        ArrayList<Float> finalClippedList = new ArrayList<>();
        ArrayList<Float> verticePoints = new ArrayList<>();
        ArrayList<ArrayList<Float>> intListOfDrawing = new ArrayList<>();
        ArrayList<ArrayList<Float>> afterClippingList = new ArrayList<>();
        ArrayList<ArrayList<Integer>> viewPortList = new ArrayList<>();


        for (int i = 0; i < args.length; i+=2) {
            switch ((args[i])) {
                case "-f":
                    inputPS = args[i + 1];
                    break;
                case "-s":
                    scale = Double.parseDouble(args[i + 1]);
                    break;
                case "-m":
                    xTrans = Double.parseDouble(args[i + 1]);
                    break;
                case "-n":
                    yTrans = Double.parseDouble(args[i + 1]);
                    break;
                case "-a":
                    lowerX = Integer.parseInt(args[i + 1]);
                    xOrigin = Integer.parseInt(args[i + 1]);
                    break;
                case "-c":
                    upperX = Integer.parseInt(args[i + 1]);
                    break;
                case "-d":
                    upperY = Integer.parseInt(args[i + 1]);
                    break;
                case "-j":
                    viewportLowerX = Integer.parseInt(args[i + 1]);
                    break;
                case "-k":
                    viewportLowerY = Integer.parseInt(args[i + 1]);
                    break;
                case "-o":
                    viewportUpperX = Integer.parseInt(args[i + 1]);
                    break;
                case "-p":
                    viewportUpperY = Integer.parseInt(args[i + 1]);
                    break;
                case "-x":
                    xPRP = Float.parseFloat(args[i + 1]);
                    break;
                case "-y":
                    yPRP = Float.parseFloat(args[i + 1]);
                    break;
                case "-z":
                    zPRP = Float.parseFloat(args[i + 1]);
                    break;
                case "-X":
                    xVRP = Float.parseFloat(args[i + 1]);
                    break;
                case "-Y":
                    yVRP = Float.parseFloat(args[i + 1]);
                    break;
                case "-Z":
                    zVRP = Float.parseFloat(args[i + 1]);
                    break;
                case "-q":
                    xVPN = Float.parseFloat(args[i + 1]);
                    break;
                case "-r":
                    yVPN = Float.parseFloat(args[i + 1]);
                    break;
                case "-w":
                    zVPN = Float.parseFloat(args[i + 1]);
                    break;
                case "-Q":
                    xVUP = Float.parseFloat(args[i + 1]);
                    break;
                case "-R":
                    yVUP = Float.parseFloat(args[i + 1]);
                    break;
                case "-W":
                    zVUP = Float.parseFloat(args[i + 1]);
                    break;
                case "-u":
                    umin = Float.parseFloat(args[i + 1]);
                    break;
                case "-v":
                    vmin = Float.parseFloat(args[i + 1]);
                    break;
                case "-U":
                    umax = Float.parseFloat(args[i + 1]);
                    break;
                case "-V":
                    vmax = Float.parseFloat(args[i + 1]);
                    break;
                case "-P":
                    parallel = true;
                    break;
                case "-b":
                    backfaceCulling = true;
                    break;
                case "-F":
                    front = Float.parseFloat(args[i + 1]);
                    break;
                case "-B":
                    back = Float.parseFloat(args[i + 1]);
                    break;
                case "-g":
                    green = args[i + 1];
                    break;
                case "-i":
                    blue = args[i + 1];
                    break;
            }
        }

        String path = (System.getProperty("user.dir")  + "/" + inputPS);
        File file = new File(path);
        Scanner scan = new Scanner(file);


        while(scan.hasNextLine()){ //read file
            String line = scan.nextLine();
            String[] splitLine = line.split("\\s+"); //https://stackoverflow.com/questions/7899525/how-to-split-a-string-by-space
            if(splitLine[0].equals("v")){
                getInputLineValue(splitLine, verticesList);
            }
            else if (splitLine[0].equals("f")){
                ArrayList<Integer> inputLine = new ArrayList<>(); // need to subtract one because I will need to get the indices, and it starts at 0 while faceList starts at 1
                inputLine.add(Integer.parseInt(splitLine[1])-1);
                inputLine.add(Integer.parseInt(splitLine[2])-1);
                inputLine.add(Integer.parseInt(splitLine[3])-1);

                faceList.add(inputLine);
            }
        }

        float[][] Rot_nVRP = dotProduct(rotationMatrix(), negativeVRPMatrix());
        float[][] Shear_Rot_nVRP = dotProduct(shearMatrix(), Rot_nVRP);

        if(parallel){
            float[][] TransPara_Shear_Rot_nVRP = dotProduct(translateParaMatrix(), Shear_Rot_nVRP);
            float[][] ScalePara_TransPara_Shear_Rot_nVRP = dotProduct(scaleParaMatrix(), TransPara_Shear_Rot_nVRP);

            for (int i = 0; i < verticesList.size() ; i++) {
                float[][] inputLine = new float[4][1];
                inputLine[0][0] = verticesList.get(i).get(0);
                inputLine[1][0] = verticesList.get(i).get(1);
                inputLine[2][0] = verticesList.get(i).get(2);
                inputLine[3][0] = verticesList.get(i).get(3);

                float[][] pointPrime = dotProduct(ScalePara_TransPara_Shear_Rot_nVRP, inputLine);

                ArrayList<Float> tempInputList = new ArrayList<>();

                tempInputList.add(pointPrime[0][0]);
                tempInputList.add(pointPrime[1][0]);

                verticesList.set(i, tempInputList);

            }
        }

        else {
            float[][] TransPer_Shear_Rot_nVRP = dotProduct(translatePerMatrix(), Shear_Rot_nVRP);
            float[][] ScalePer_TransPer_Shear_Rot_nVRP = dotProduct(scalePerMatrix(), TransPer_Shear_Rot_nVRP);

            for (int i = 0; i < verticesList.size() ; i++) {

                float[][] inputLineList = new float[4][1];

                inputLineList[0][0] = verticesList.get(i).get(0);
                inputLineList[1][0] = verticesList.get(i).get(1);
                inputLineList[2][0] = verticesList.get(i).get(2);
                inputLineList[3][0] = verticesList.get(i).get(3);

                float[][] pointPrime = dotProduct(ScalePer_TransPer_Shear_Rot_nVRP, inputLineList);

                ArrayList<Float> tempInputList = new ArrayList<>();

                tempInputList.add(pointPrime[0][0]);
                tempInputList.add(pointPrime[1][0]);
                tempInputList.add(pointPrime[2][0]);

                verticesList.set(i, tempInputList);
            }
        }

        if(parallel) {
            lowerX = -1.0f;
            lowerY = -1.0f;

            upperX = 1.0f;
            upperY = 1.0f;

            for (int i = 0; i < verticesList.size(); i++) {
                verticePoints.add(verticesList.get(i).get(0));
                verticePoints.add(verticesList.get(i).get(1));
                verticePoints.add(verticesList.get(i).get(2));
            }
            intListOfDrawing.add(verticePoints);
        }
        else {
            float dTemp = Math.abs(d);
            lowerX = -1*dTemp;
            lowerY = -1*dTemp;

            upperX = dTemp;
            upperY = dTemp;

            for(int i=0; i<verticesList.size(); i++) {
                float x = verticesList.get(i).get(0);
                float y = verticesList.get(i).get(1);
                float z = verticesList.get(i).get(2);

                x = x/(z/d);
                y = y/(z/d);

                verticePoints.add(x);
                verticePoints.add(y);
                verticePoints.add(verticesList.get(i).get(2));
            }
            intListOfDrawing.add(verticePoints);
        } //   AFTER NORMALIZING

        //System.out.println(verticePoints.size());

        for (int i = 0; i < intListOfDrawing.size(); i++) {
            float viewX = viewportUpperX-viewportLowerX;
            float viewY = viewportUpperY-viewportLowerY;
            float worldX = upperX-lowerX;
            float worldY = upperY-lowerY;

            for (int j = 0; j < intListOfDrawing.get(i).size(); j+=2) {//set origin
                float x = intListOfDrawing.get(i).get(j);
                float y = intListOfDrawing.get(i).get(j + 1);

                x = x - lowerX;
                y = y - lowerY;

                intListOfDrawing.get(i).set(j, (x));
                intListOfDrawing.get(i).set(j+1, (y));
            }

            for (int j = 0; j < intListOfDrawing.get(i).size(); j+=2) { //viewport scaling
                float x = intListOfDrawing.get(i).get(j);
                float y = intListOfDrawing.get(i).get(j+1);

                x = x * (viewX/worldX);
                y = y * (viewY/worldY);

                intListOfDrawing.get(i).set(j, x);
                intListOfDrawing.get(i).set(j+1, y);

            }

            for (int j = 0; j < intListOfDrawing.get(i).size(); j+=2) {//viewport translating
                float x = intListOfDrawing.get(i).get(j);
                float y = intListOfDrawing.get(i).get(j + 1);

                x = x + viewportLowerX;
                y = y + viewportLowerY;

                intListOfDrawing.get(i).set(j, x);
                intListOfDrawing.get(i).set(j+1, y);
            }
        } // end of viewport transformation

//        for (int i = 0; i < afterClippingList.get(0).size(); i+=2) {
//            ArrayList<Float> temp = new ArrayList<>();
//            temp.add(afterClippingList.get(0).get(i));
//            temp.add(afterClippingList.get(0).get(i+1));
//            coordinateList.add(temp);
//        }


        //System.out.println((afterClippingList.get(afterClippingList.size() - 2)) + " " + (afterClippingList.get(afterClippingList.size() - 1)) + " m");

//        for (int i = 0; i < afterClippingList.get(0).size() - 1; i += 2) {
//            System.out.println(afterClippingList.get(0).get(i) + " " + afterClippingList.get(0).get(i+1) + " l");
//        }

//        System.out.println("after viewport " + afterClippingList);

        ArrayList<ArrayList<Integer>> ListOfDrawing = new ArrayList<>(intListOfDrawing.size());
        ArrayList<Integer> tempList = new ArrayList<>();

        for (int i = 0; i < intListOfDrawing.size(); i++) { // change floats to int
            for (int j = 0; j < intListOfDrawing.get(i).size(); j++) {

                tempList.add(Math.round(intListOfDrawing.get(i).get(j)));
            }
            ListOfDrawing.add(tempList);
        }


        for (int i = 0; i < ListOfDrawing.size(); i++) { //for each polygon

            int min;
            int max;

            if(ListOfDrawing.get(i).size() != 0){
                min = findMin(ListOfDrawing.get(i));
                max = findMax(ListOfDrawing.get(i));
            }
            else {
                min = 0;
                max = 0;
            }

            for (int j =min; j <= max; j++) { //from min to max create an empty list for each line
                scanFillList2.add(new ArrayList<Float>());
                scanFillList.add(new ArrayList<Float>());
            }

            for (int j = 0; j < ListOfDrawing.get(i).size() - 3; j+=3) {//adding edges to edge list
                int y0edge = ListOfDrawing.get(i).get(j+1);
                int y1edge = ListOfDrawing.get(i).get(j+4);

                for (int k = (int) min; k <= max; k++) {
                    if(!Objects.equals(y0edge, y1edge)){
                        if((k >= y0edge && k < y1edge) || (k >= y1edge && k < y0edge)){
                            int x0 = ListOfDrawing.get(i).get(j);
                            int x1 = ListOfDrawing.get(i).get(j+3);

                            int edgeRowArrayNum = (int) (k-min);

                            scanFillList2.get(edgeRowArrayNum).add(x0);
                            scanFillList2.get(edgeRowArrayNum).add(y0edge);
                            scanFillList2.get(edgeRowArrayNum).add(x1);
                            scanFillList2.get(edgeRowArrayNum).add(y1edge);
                        }
                    }
                }
            }

            for (int j = (int) min; j <= max -1; j++) { // for each line in polygon
                scanFillList.clear();

                int edgeRowArrayNum = (int) (j-min);

                for (int k = (int) min; k <= max; k++) {
                    scanFillList.add(new ArrayList<Float>());
                }

                for (int k = 0; k < (scanFillList2.get((edgeRowArrayNum)).size()); k+=4) {

                    int x0 = scanFillList2.get(edgeRowArrayNum).get(k);
                    int y0 = scanFillList2.get(edgeRowArrayNum).get(k+1);
                    int x1 = scanFillList2.get(edgeRowArrayNum).get(k+2);
                    int y1 = scanFillList2.get(edgeRowArrayNum).get(k+3);

                    if(y0 != y1){
                        intersect(j, x0, y0, x1, y1, min); //calculate intersect
                    }
                }

                for (int k = 0; k < scanFillList.size(); k++) { //for each line sort the intersects
                    Collections.sort(scanFillList.get(k));
                }


                try{//try catch to eliminate bad intersection points
                    Float xmin = (scanFillList2.get(edgeRowArrayNum).get(0));
                    Float xmax = (scanFillList2.get(edgeRowArrayNum).get(0));

                    for (int k = 0; k < ((scanFillList2.get( (edgeRowArrayNum)).size())); k+=2) {
                        if((scanFillList2.get((edgeRowArrayNum)).get(k)) < xmin){
                            xmin = scanFillList2.get(edgeRowArrayNum).get(k);
                        }

                        if((scanFillList2.get(edgeRowArrayNum).get(k)) > xmax){
                            xmax = scanFillList2.get(edgeRowArrayNum).get(k);
                        }
                    }


                    for (int k = 0; k < ((scanFillList2.get(edgeRowArrayNum).size())) - 2; k+=2) { //if not inside the frame
                        for (int l = 0; l < scanFillList.get(edgeRowArrayNum).size(); l++) {
                            if(scanFillList.get(edgeRowArrayNum).get(l) < xmin || scanFillList.get(edgeRowArrayNum).get(l) > xmax){
                                scanFillList.get(edgeRowArrayNum).remove(scanFillList.get(edgeRowArrayNum).get(l));
                            }
                        }
                    }
                }catch (Exception e){

                }

                for (int k = 0;k < scanFillList.get(edgeRowArrayNum).size(); k+=2) {

                    int listSize = scanFillList.get(edgeRowArrayNum).size();
                    int firstIntersect;
                    int secondIntersect;
                    firstIntersect = Math.round(scanFillList.get((int) (j-min)).get(k));
                    secondIntersect = Math.round(scanFillList.get((int) (j-min)).get(k+1));

                    if(listSize%2 == 1){
                        if(scanFillList.get((int) (j-min)).get(k+2) > 500){
                            for (int l = firstIntersect; l < secondIntersect; l++) {
                                coordinate[l][j] = 1;
                            }
                            break;
                        }
                        else {
                            secondIntersect = Math.round(scanFillList.get((int) (j-min)).get(k+2));
                            for (int l = firstIntersect; l < secondIntersect; l++) {
                                coordinate[l][j] = 1;
                            }

                            break;
                        }

                    }else {
                        for (int l = firstIntersect; l < secondIntersect; l++) {
                            coordinate[l][j] = 1;
                        }
                    }
                }

            }

            scanFillList.clear();
            scanFillList2.clear();
        }

//        for (int x = 0; x < 501; x++) {
//            for (int y = 0; y < 501; y++) {
//                System.out.print(coordinate[y][xWindowLength - x - 1]);
//            }
//            System.out.println();
//        }
    }

    private static void getInputLineValue(String[] splitLine, ArrayList<ArrayList<Float>> faceList) {
        ArrayList<Float> inputLine = new ArrayList<>();
        inputLine.add(Float.valueOf(splitLine[1]));
        inputLine.add(Float.valueOf(splitLine[2]));
        inputLine.add(Float.valueOf(splitLine[3]));
        inputLine.add(1.0f);

        faceList.add(inputLine);
    }

    public static int findMin(ArrayList<Integer> list){ //changed to i+=3 bc theres z

        int min;

        min = list.get(0);

        for (int i = 0; i < list.size(); i+=3) {
            if(list.get(i+1) < min){
                min = list.get(i+1);
            }
        }

        return min;
    }

    public static int findMax(ArrayList<Integer> list){ //changed to i+=3 bc theres z

        int max = 0;

        for (int i = 0; i < list.size(); i+=3) {
            if(list.get(i+1) > max){
                max = list.get(i+1);
            }
        }

        return max;
    }

    public static void intersect(int j, int x0, int y0, int x1, int y1, int min){
        float dx = x1-x0;
        float dy = y1-y0;

        float x = Math.round(x0 + (dx/dy)*(j - y0));

        scanFillList.get((int) (j-min)).add(x);
    }
    public static float[][] negativeVRPMatrix() {
        float[][] negative_VRP = new float[4][4];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                negative_VRP[i][j] = 0;
            }
        }

        negative_VRP[0][0] = 1; //translating
        negative_VRP[0][3] = (xVRP * -1);
        negative_VRP[1][1] = 1;
        negative_VRP[1][3] = (yVRP * -1);
        negative_VRP[2][2] = 1;
        negative_VRP[2][3] = (zVRP * -1);
        negative_VRP[3][3] = 1;

        return negative_VRP;
    }

    public static float[][] rotationMatrix(){
        float[][] rotation = new float[4][4];
        float VPN_Rotation = findNorm(xVPN, yVPN, zVPN);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                rotation[i][j] = 0;
            }
        }

        float rotateZ_1 = xVPN / VPN_Rotation;
        float rotateZ_2 = yVPN / VPN_Rotation;
        float rotateZ_3 = zVPN / VPN_Rotation;

        rotation[2][0] = rotateZ_1; //rotate of Z
        rotation[2][1] = rotateZ_2;
        rotation[2][2] = rotateZ_3;

        List<Float> xCrossProductVUP = crossProduct(xVUP, yVUP, zVUP, rotateZ_1, rotateZ_2, rotateZ_3);
        float normX = findNorm(xCrossProductVUP.get(0), xCrossProductVUP.get(1), xCrossProductVUP.get(2));

        float rotateX_1 = xCrossProductVUP.get(0) / normX;
        float rotateX_2 = xCrossProductVUP.get(1) / normX;
        float rotateX_3 = xCrossProductVUP.get(2) / normX;

        rotation[0][0] = rotateX_1; //rotate of X
        rotation[0][1] = rotateX_2;
        rotation[0][2] = rotateX_3;

        List<Float> yCrossProductVUP = crossProduct(rotateZ_1, rotateZ_2, rotateZ_3, rotateX_1, rotateX_2, rotateX_3);

        float rotateY_1 = yCrossProductVUP.get(0);
        float rotateY_2 = yCrossProductVUP.get(1);
        float rotateY_3 = yCrossProductVUP.get(2);

        rotation[1][0] = rotateY_1;  //rotate of Y
        rotation[1][1] = rotateY_2;
        rotation[1][2] = rotateY_3;

        rotation[3][3] = 1;

        return rotation;
    }

    public static float[][] shearMatrix(){
        float[][] shear = new float[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                shear[i][j] = 0;
            }
        }

        shear[0][0] = 1;
        shear[0][2] = (((0.5f * (umax + umin)) - xPRP) / zPRP);
        shear[1][1] = 1;
        shear[1][2] = (((0.5f * (vmax + vmin)) - yPRP) / zPRP);
        shear[2][2] = 1;
        shear[3][3] = 1;

        return shear;
    }

    public static float[][] translateParaMatrix(){
        float[][] translateParallel = new float[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                translateParallel[i][j] = 0;
            }
        }

        translateParallel[0][0] = 1;
        translateParallel[0][3] = -(umax + umin)/2;

        translateParallel[1][1] = 1;
        translateParallel[1][3] = -(vmax + vmin)/2;

        translateParallel[2][2] = 1;
        translateParallel[2][3] = -1*front;

        translateParallel[3][3] = 1;

        return translateParallel;
    }

    public static float[][] scaleParaMatrix(){
        float [][] scaleParallel = new float[4][4];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                scaleParallel[i][j] = 0;
            }
        }

        scaleParallel[0][0] = 2/(umax - umin);
        scaleParallel[1][1] = 2/(vmax - vmin);
        scaleParallel[2][2] = 1/(front - back);
        scaleParallel[3][3] = 1;

        return scaleParallel;
    }

    public static float[][] translatePerMatrix(){
        float [][] transPerspective = new float[4][4];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                transPerspective[i][j] = 0;
            }
        }

        transPerspective[0][0] = 1;
        transPerspective[0][3] = -1*xPRP;
        transPerspective[1][1] = 1;
        transPerspective[1][3] = -1*yPRP;
        transPerspective[2][2] = 1;
        transPerspective[2][3] = -1*zPRP;
        transPerspective[3][3] = 1;

        return transPerspective;
    }

    public static float[][] scalePerMatrix(){
        float [][] scalePerspective = new float [4][4];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                scalePerspective[i][j] = 0;
            }
        }

        scalePerspective[0][0] = (2 * zPRP)/((umax - umin) * (zPRP - back));
        scalePerspective[1][1] = (2 * zPRP)/((vmax - vmin) * (zPRP - back));
        scalePerspective[2][2] = 1/(zPRP - back);
        scalePerspective[3][3] = 1;

        return scalePerspective;
    }

    private static void printMatrix(float[][] product1) {
        int counter;
        for (int i = 0; i < 4; i++) {
            counter = 0;
            for (int j = 0; j < 4; j++) {

                System.out.print(product1[i][j] + ", ");
                counter++;
            }
            if(counter%4 == 0){
                System.out.println();
            }
        }
    }

    public static float[][] dotProduct(float[][] u, float[][] v){// https://www.geeksforgeeks.org/java-program-to-multiply-two-matrices-of-any-size/
        //method to implement dot product of two array

        int rowLength = u.length; //used to get row and column length, so we can make a new 2D array with those lengths
        int colLength = u[0].length;
        int colLength2 = v[0].length;

//        System.out.println(rowLength + " " + colLength + " " + rowLength2);

        float[][] product = new float[rowLength][colLength2];

        for (int i = 0; i < rowLength; i++) {

            for (int j = 0; j < colLength2; j++) {

                for (int k = 0; k < colLength; k++) {
                    product[i][j] += u[i][k] * v[k][j];
                }
            }
        }

        return product;

    }

    public static float findNorm(float x, float y, float z){
        return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
    }

    public static List<Float> crossProduct(float x, float y, float z, float x2, float y2, float z2){
        List<Float> crossProductList = new ArrayList<>();

        crossProductList.add(((y * z2) - (z * y2)));
        crossProductList.add(((z * x2) - (x * z2)));
        crossProductList.add(((x * y2) - (y * x2)));

        return crossProductList;
    }


}

