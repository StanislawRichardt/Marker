package com.company;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Main {

    static BufferedReader bufferedReader = null;
    static String filePath = "E:\\Repozytoria\\Marker\\example\\testFile.txt";
    static File file = new File(filePath);
    static List<String[]> symbolCounterList = new ArrayList<>();
    static int fileLength = 0;
    static String data;
    static double marker;
    static DecimalFormat numberFormat = new DecimalFormat("#0.00000");

    // Functionalities: Do a bunch of magic to prepare a file to be read
    public static void fileUtility(){

        try {
            FileInputStream fileStream = new FileInputStream(file);
            fileStream.getChannel().position(0);
            InputStreamReader input = new InputStreamReader(fileStream);
            bufferedReader = new BufferedReader(input);
        }catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    /* Functionalities:
                1. Open file through fileUtility()
                2. Sets fileLength through charCount()
                3. Iterate through file and sends chars to searchAndSave()
                4. Counts probability through probabilityCount()
            */
    private static void dictionaryCreation() {

        try {
            symbolCounterList.add(new String[]{" - ", " - ", "0"});
            fileUtility();
            while ((data = bufferedReader.readLine()) != null) {

                fileLength += data.length();
                for(int j=0;j<data.length();j++)
                {
                    searchAndSave(data.charAt(j));
                }
            }
            probabilityCount();
            cumulativeDistributionCount();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*Functionalities:
        1. Search letter through List
        2. Update letter counter
        3. Write new letter to List
    */
    private static void searchAndSave(char letter) {
        for (int i = 1; i < symbolCounterList.size(); i++) {
            if (symbolCounterList.get(i)[0].charAt(0) == letter) {
                int counter = Integer.parseInt(symbolCounterList.get(i)[1]) + 1;
                symbolCounterList.set(i, new String[]{Character.toString(letter), Integer.toString(counter)});
                return;
            }
        }
        symbolCounterList.add(new String[]{Character.toString(letter), "1"});
    }

    //Functionality: Changes quantity to probability
    private static void probabilityCount() {
        for (int i = 1; i < symbolCounterList.size(); i++) {
            double counter = Double.parseDouble(symbolCounterList.get(i)[1]) / fileLength;
            symbolCounterList.set(i, new String[]{symbolCounterList.get(i)[0], Double.toString(counter)});
        }
    }

    //Functionality: Adds new column with cumulative distribution of every symbol
    private static void cumulativeDistributionCount() {
        double cumulativeDistribution=0;

        for(int i=1;i< symbolCounterList.size();i++){
            cumulativeDistribution = cumulativeDistribution + Double.parseDouble(symbolCounterList.get(i)[1]) ;
            symbolCounterList.set(i, new String[]{symbolCounterList.get(i)[0], symbolCounterList.get(i)[1], Double.toString(cumulativeDistribution)});
        }
    }

    //Functionality: Displays a List
    private static void dictionaryDisplay() {
        dictionaryCreation();
        for (int i = 1; i < symbolCounterList.size(); i++) {

            System.out.println(symbolCounterList.get(i)[0]+'\t'+" | "+
                    symbolCounterList.get(i)[1]+'\t'+" | "+
                    symbolCounterList.get(i)[2]);
        }
    }

    //Functionality: calculate marker
    private static double calculateMarker() {
        double newLowValue=0, newHighValue=1, oldLowValue=0, oldHighValue=1;
        fileUtility();

        try {
            while ((data = bufferedReader.readLine()) != null) {
                for (int i = 0; i < data.length(); i++) {
                        for (int j = 1; j < symbolCounterList.size(); j++) {
                            if (symbolCounterList.get(j)[0].charAt(0) == data.charAt(i)) {
                                newLowValue = oldLowValue + (oldHighValue - oldLowValue) * Double.parseDouble(symbolCounterList.get(j-1)[2]);
                                newHighValue = oldLowValue + (oldHighValue - oldLowValue) * Double.parseDouble(symbolCounterList.get(j)[2]);
                                break;
                            }
                        }
                    oldLowValue=newLowValue;
                    oldHighValue=newHighValue;
                    }
                }
            marker=(oldHighValue+oldLowValue)/2;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return marker;
    }

    //Functionality: displays marker
    private static void displayMarker(){
        System.out.println("Value of the marker is: " + calculateMarker());
    }

    private static void decodingMarker(){
        System.out.println();
        double newLowValue=0, newHighValue=1, oldLowValue=0, oldHighValue=1, t;
        for(int j=0;j<=fileLength;j++) {
            for (int i = symbolCounterList.size() - 1; i > 0; i--) {

                t = (marker - oldLowValue) / (oldHighValue - oldLowValue);

                if (Double.parseDouble(symbolCounterList.get(i)[2]) >= t && Double.parseDouble(symbolCounterList.get(i - 1)[2]) <= t) {

                        System.out.print(symbolCounterList.get(i)[0]);
                    newLowValue = oldLowValue + (oldHighValue - oldLowValue) * Double.parseDouble(symbolCounterList.get(i - 1)[2]);
                    newHighValue = oldLowValue + (oldHighValue - oldLowValue) * Double.parseDouble(symbolCounterList.get(i)[2]);
                        break;
                }
            }
            oldLowValue=newLowValue;
            oldHighValue=newHighValue;
        }
    }

    private static double roundingUp(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static void main(String[] args) {
        dictionaryDisplay();
        displayMarker();
	    decodingMarker();
    }
}
