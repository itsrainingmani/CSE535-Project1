/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
/**
 *
 * @author Manikandan
 */
public class cse535 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        String filename = "terms.idx";
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filename));
        }
        catch (FileNotFoundException e) {
            System.out.println("Input file " + filename + " not found");
            System.exit(1);
        }
    }
    
}
