/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encryption;
import java.io.*;

/**
 *
 * @author Wylder
 */
public class Testing {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // test encryption
        if (Encryption.encrypt("loremipsum.txt", "encryptiontestoutput.txt", "123456789012345")) System.out.println("encrypt complete");
        if (Encryption.keyLengthAnalysis("encryptiontestoutput.txt", "KeyAnalysis.txt", 1, 30)) System.out.println("analysis complete");
    }
    
}
