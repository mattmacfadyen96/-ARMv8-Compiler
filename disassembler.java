import java.io.*;
import java.util.ArrayList;
/**
 * Write a description of class disassembler here.
 * 
 * @author Matthew MacFadyen
 * @version November 28, 2016
 */
public class disassembler
{
    private int length;
    private String [] list;

    public disassembler(int maxSize)
    {
        length = 0;
        list = (String []) new String [maxSize];
    }

    public String findType(String instruction)
    {
        // variable to store the binary of the instruction 
        
        
        switch (instruction) 
        {
            case "ADD":
            instruction = "10001011000";
            break; 
            
            case "SUB":
            instruction = "11001011000";
            break;
            
            case "ADDI":
            
            

            
        }
        
        return instruction; 
    }
}
