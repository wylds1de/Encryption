/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encryption;

import java.io.*;

/**
 * periodic multi-alphabetic encryption
 * @author Wylder
 */
public class Encryption {
    
    private static int OUTPUTDIVISIONS = 10;
    /**
     * bitwise xor between the input file and the key
     * @param inputFilename
     * @param outputFilename
     * @param keyString
     * @return true if the input file was completely processed
     */
    public static boolean encrypt(String inputFilename, String outputFilename, String keyString)
    {
        boolean out = false;
        BufferedReader fileIn;
        BufferedWriter fileOut;
        try
        {
            fileIn = new BufferedReader(new FileReader(inputFilename));
            fileOut = new BufferedWriter(new FileWriter(outputFilename));
            char key[] = keyString.toCharArray();
            try
            {
                int keyIndex = 0;
                while (fileIn.ready())
                {
                    fileOut.write((char) (fileIn.read() ^ key[keyIndex]));
                    if (++keyIndex >= key.length) keyIndex = 0;
                }
                fileIn.close();
                fileOut.close();
                out = true;
            }
            catch (IOException e)
            {
                System.out.println("IOException, not sure of specifics yet");
            }
        }
        catch (FileNotFoundException e)
        {
            System.out.println("could not find " + inputFilename);
        }
        catch (IOException e)
        {
            System.out.println("IOException while creating fileOut");
        }
        return out;
    }
    
    public static boolean keyLengthAnalysis(String inputFilename, String outputFilename, int minLength, int maxLength)
    {
        boolean out = true;
        try
        {
            BufferedWriter fileOut = new BufferedWriter(new FileWriter(outputFilename));
            for (int assumedKeyLength = minLength; assumedKeyLength <= maxLength && out == true; assumedKeyLength++)
            {
                try
                {
                    BufferedReader fileIn = new BufferedReader(new FileReader(inputFilename));
                    float distribution[][] = new float[assumedKeyLength][256];
                    for (int keyIndex = 0; fileIn.ready();)
                    {
                        distribution[keyIndex][fileIn.read()]++;//counts the frequency of characters at each key character
                        if (++keyIndex >= assumedKeyLength) keyIndex = 0;
                    }
                    fileIn.close();
                    for (int keyIndex = 0; keyIndex < assumedKeyLength; keyIndex++) Encryption.sort(distribution[keyIndex]);
                    float sums[] = new float[256];
                    for (int keyIndex = 0; keyIndex < assumedKeyLength; keyIndex++)
                        for (int charIndex = 0; charIndex < 256; charIndex++)
                            sums[charIndex] += distribution[keyIndex][charIndex];
                    Encryption.writeDistributionRow(fileOut, assumedKeyLength, sums);
                }
                catch (FileNotFoundException e)
                {
                    System.out.println("could not find " + inputFilename);
                    out = false;
                }
                catch (IOException e)
                {
                    System.out.println("IOException while creating fileIn");
                    out = false;
                }
            }
            fileOut.close();
        }
        catch (IOException e)
        {
            System.out.println("IOException while creating fileOut");
        }
        return out;
    }
    private static void sort(float array[])
    {
        for (int currentIndex = 0; currentIndex < array.length; currentIndex++)
            Encryption.swap(array, currentIndex, Encryption.maxIndex(array, currentIndex, array.length));
    }
    
    private static int maxIndex(float array[], int startIndex, int endIndex)
    {
        int maxIndex = startIndex;
        for (int currentIndex = startIndex; currentIndex < endIndex; currentIndex++)
            if (array[currentIndex] > array[maxIndex])
                maxIndex = currentIndex;
        return maxIndex;
    }
    
    private static void swap(float array[], int index1, int index2)
    {
        float temp = array[index1];
        array[index1] = array[index2];
        array[index2] = temp;
    }

    private static void writeDistributionRow(BufferedWriter fileOut, int assumedKeyLength, float[] sums)
    {
        Encryption.scaleArray(sums, 1 / sums[Encryption.maxIndex(sums, 0, 256)]);
        try
        {
            fileOut.write(String.format("%3d: ", assumedKeyLength));
            int index = 0;
            float currentDivision = Encryption.OUTPUTDIVISIONS;
            while (index < 256)
            {
                if (sums[index] <= currentDivision / Encryption.OUTPUTDIVISIONS)
                {
                    fileOut.write('X');
                    currentDivision--;
                }
                else
                {
                    fileOut.write(' ');
                }
                index++;
            }
            fileOut.newLine();
        }
        catch (IOException e)
        {
            System.out.println("Encryption.writeDistributionRow: " + e);
        }
    }
    
    private static void scaleArray(float array[], float factor)
    {
        for (int Index = 0; Index < array.length; Index++)
            array[Index] *= factor;
    }
}
