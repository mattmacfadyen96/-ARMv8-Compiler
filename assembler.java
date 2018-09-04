import java.io.*;
import java.util.*;
/**
 * This class will read from a test file that contains ARM instructions and translate them to their binary machine 
 * language equivalent and then it will write that binary to a file. This class will contains other methods that
 * will help me with the assembly language manipulation. Some of these methods will convert strings to their correct binary formatted strings and it will 
 * also handle special cases like dealing with the XZR, SP, and LR registers. 
 * 
 * @author Matthew MacFadyen and Andrew Lopez 
 * @version December 13, 2016
 */
public class assembler
{   
    public static String specialCases(String register) 
    {
        // if the register being used is the constant zero register 
        if(register.equals("XZR") )
        {
            register = "11111";
        }
        // if the registrer being used is the Linked Register/Return address 
        else if(register.equals("LR") )
        {
            register = "11110";
        }
        // if the register being used is the Stack pointer 
        else if(register.equals("SP") )
        {
            register = "11100";
        }
        return register;
    }

    public static String getFormat(String instruction)
    {
        if(instruction.contains("I") && instruction.contains("B.") == false)
        {
            instruction = "I";
        }
        else if(instruction.contains("CB") || instruction.contains("B." ) )
        {
            instruction = "CB";
        } 
        else if(instruction.equals("LDUR") || instruction.equals("STUR") )
        {
            instruction = "D";
        }
        else if(instruction.equals("B") || instruction.equals("BL") )
        {
            instruction = "B";
        }
        else {
            instruction = "R";
        }
        return instruction;
    }

    public static String getBcond(String instruction)
    {
        // handle the B.conditional values in this conditional statement 
        if(instruction.equals("B.EQ") )
        {
            instruction = "00000";
        }
        else if(instruction.equals("B.NE") )
        {
            instruction = "00001";
        }
        else if(instruction.equals("B.HS") )
        {
            instruction = "0010";
        }
        else if(instruction.equals("B.LO") )
        {
            instruction = "0011";
        }
        else if(instruction.equals("B.MI") )
        {
            instruction = "0100";
        }
        else if(instruction.equals("B.PL") )
        {
            instruction = "0101";
        }
        else if(instruction.equals("B.VS") )
        {
            instruction = "0110";
        }
        else if(instruction.equals("B.VC") )
        {
            instruction = "0111";
        }
        else if(instruction.equals("B.HI") )
        {
            instruction = "1000";
        }
        else if(instruction.equals("B.LS") )
        {
            instruction = "1001";
        }
        else if(instruction.equals("B.GE") )
        {
            instruction = "1010";
        }
        else if(instruction.equals("B.LT") )
        {
            instruction = "1011";
        }
        else if(instruction.equals("B.GT" ) )
        {
            instruction = "1100";
        }
        else if(instruction.equals("B.LE") )
        {
            instruction = "1101";
        }
        else if(instruction.equals("B.AL") )
        {
            instruction = "1110";
        }
        else if(instruction.equals("B.NV") )
        {
            instruction = "1111";
        }  
        return instruction;
    }

    /**This method will be used to convert ARM assembly language instruction into the specified binary opcode that is needed 
     * to perform the needed command 
     * 
     * @param the core instruction in ARM in the form of a string 
     * @return the binary opcode of the instruction entered in the param
     */
    public static String getOpCode(String instruction) 
    {
        /*
         * I format instructions will be handled here
         * if the instruction contains an I then we know that it is an I Format instruction 
         */      
        if(instruction.contains("I") )
        { 
            if(instruction.equals("ADDI") )
            {
                instruction = "1001000100";
            }
            else if(instruction.equals("ADDIS") )
            {
                instruction = "1011000100";
            }
            else if(instruction.equals("ANDI") )
            {
                instruction = "1001001000";
            }
            else if(instruction.equals("ANDIS") )
            {
                instruction = "1111001000";
            }
            else if(instruction.equals("EORI") )
            {
                instruction = "1101001000";
            }
            else if(instruction.equals("ORRI") )
            {
                instruction = "1011001000";
            }
            else if(instruction.equals("SUBI") )
            {
                instruction = "1101000100";
            }
            else if(instruction.equals("SUBIS") )
            {
                instruction = "1111000100";
            }
        }

        // if not then we can deduce that it is one of the other formatted instructions 
        // handle the other formats starting with the are formatted instructions 
        switch (instruction) 
        {
            // R formatted instructions 
            case "ADD":
            instruction = "10001011000";
            break; 

            case "SUB":
            instruction = "11001011000";
            break;

            case "ADDS":
            instruction = "10101011000";
            break;

            case "AND":
            instruction = "10001010000";
            break;

            case "ANDS": 
            instruction = "11101010000";
            break;

            case "BR":
            instruction = "11010110000";
            break;

            case "EOR":
            instruction = "11001010000";
            break;

            case "LSL":
            instruction = "11010011011";
            break; 

            case "LSR":
            instruction = "11010011010";
            break;

            case "SUBS": 
            instruction = "11101011000";
            break;

            case "ORR":
            instruction = "10101010000";
            break; 

            case "ASR":
            instruction = "10010011010";
            break;

            // CB formatted instructions 
            case "CBNZ":
            instruction = "10110101";
            break;

            case "CBZ":
            instruction = "10110100";
            break;

            // D-formatted instructions 
            case "LDUR":
            instruction = "11111000010";
            break;

            case "STUR":
            instruction = "11111000000";
            break;

            // B format instruction 
            case "B":
            instruction = "000101";
            break;

            case "BL":
            instruction = "100101";
            break;

            //pseudo instructions 
            case "NEG":
            instruction = "11001011000";
            break;

            case "NOP":
            instruction = "000101";
        }

        if(instruction.contains("B.") )
        {
            instruction = "01010100";
        }

        if(instruction.contains("RET") )
        {
            instruction = "11010110000";
        }
        return instruction;
    }

    /**
     * This method will be used to format the binary register strings so that they are always 5 bits long. If they are not five bits long then this method 
     * will apend to the end of the string and make it so that it is 5 bits in length
     * @param unformatted binary string 
     * @return formatted binary string that is five bits in length for all of the registers 
     */
    public static String getBinaryFormatted(String binary_value)
    {
        // keep looping as long as the length of the string is not 4 elements long 
        while(!(binary_value.length() == 5) )
        {
            // if the binary string is less then a length of four then we need to add some zeroes to it 

            binary_value = "0" + binary_value; 

        }
        return binary_value;
    }

    public static void main(String [] args) throws IOException 
    {
        Scanner scnr = new Scanner (System.in); 

        System.out.println("Welcome to the ARM assembler!"); 

        System.out.print("Please enter the name of a file: ");
        String fileName = scnr.next(); 

        FileInputStream inputFile = new FileInputStream(fileName);
        Scanner fileIn = new Scanner(inputFile);

        // create a scanner for the seonnd pass through
        FileInputStream inputFile2 = new FileInputStream(fileName);
        Scanner fileIn2 = new Scanner(inputFile2);

        FileInputStream correct_Output  = new FileInputStream("basic_output1.txt");
        Scanner output = new Scanner(correct_Output);

        // create an object of the PrintWriter class so that we can write to the file m
        PrintWriter writer = new PrintWriter("output.txt");

        // create an array list that will store the contents of the line of code 
        ArrayList<String> list = new ArrayList<String>();

        // create a second array list to keep track of the labels 
        ArrayList<String> list_labels = new ArrayList<String>();

        // string that entire line of text 
        String line;

        // keep track of the line number for labels and flags 
        int line_Num = 0;

        /*
         * keep looping as long as their is another line to read, the first loop through will look for labels in the code 
         * and keep track of whhich line number they occur on in some sort of temp 
         * 
         */ 
        // check the file for psuedo and labels 
        while(fileIn.hasNext() )
        {
            // create a string to store the contents of the line that is contained in the file being loaded 
            String nextLine = fileIn.nextLine();

            // splits the line into a string when we see a space and stores each entry into an array of strings
            String lineOfCode[] = nextLine.split(" ");

            // if the first string in the array has a colon then this is a label

            if(lineOfCode[0].contains(":") )
            { 
                // remove the colon from the string and store it into a temp 
                String label = lineOfCode[0].replaceAll(":", " ");

                //trim the whitespace off of the label
                label = label.trim();

                // in this if statement we will keep track of labels in a second array list
                list_labels.add(label);

            }
            // if the first entry in the array of strings does not contain a label then add a null to the array list
            else {
                list_labels.add(null); 

            }
        }

        while(fileIn2.hasNextLine() )
        { 
            // create a string to store the contents of the line that is contained in the file being loaded 
            String nextLine = fileIn2.nextLine();

            writer.println(nextLine);

            // splits the line into a string when we see a space and stores each entry into an array of strings
            String lineOfCode[] = nextLine.split(" ");

            // read in the splitted array of strings into an array list for futher manipulation and remove the labels 

            // add the contents of the operations to the array list, if their is a label then do not add it to the array list 

            // if the element contains a colon then it is a label and we will not add it to the array list

            boolean mov = false; 
            for(int i = 0; i < lineOfCode.length; i++)
            {
                if(lineOfCode[i].contains(":") )
                {
                    // add the label to the second array list so that we can you it for the comparison statements 
                }
                // we will handle comments here in this if statement 
                else if(lineOfCode[i].contains(";") )
                {
                    lineOfCode[i] = lineOfCode[i].replaceAll("\\s+", "");
                    lineOfCode[i] = lineOfCode[i].replaceAll(";" , "");
                }
                else if(lineOfCode[i].equals("CMP") )
                {
                    lineOfCode[i] = "SUBS";
                    list.add(lineOfCode[i] );
                    list.add("XZR");
                }
                else if(lineOfCode[i].equals("CMPI") )
                {
                    lineOfCode[i] = "SUBIS";
                    list.add(lineOfCode[i] );
                    list.add("XZR");
                }
                else if(lineOfCode[i].equals("MOV") )
                {
                    lineOfCode[i] = "ADD";
                    list.add(lineOfCode[i] );
                    mov = true;
                }     
                else {
                    // add the array of strings to an array list for futher manipulation if it is not a label 
                    list.add(lineOfCode[i]);
                }
            }

            // if it is a MOV instruction then add the XZR to the end of the array list 
            if(mov == true )
            {
                list.add("XZR");
            }
            // if the lists first entry contains a colon then we know that is not the opcode that we want to translate 

            // print out the opcode because it is the first string of binary in any format 
            writer.print(getOpCode(list.get(0) ) + " " ); 

            // get the format of the instruction 
            String format = getFormat(list.get(0) );

            // this variable will be used to hold a binary number as a string 
            String binary_string; 

            // this variable will be used to store the binary string as an integer when we call the parseInt() method to convert to base 2 
            int binary; 

            // this variable will store the base 2 conversion a string of binary numbers 
            String base2_string;

            // the second loop is used for translating the ARM into machine language
            for (int j = list.size() - 1; j > 0; j--)
            {
                // if the format instruction is an R format instruction 
                if(format.equals("R") )
                {
                    // make sure that the instruction is not a LSL or LSR so we do not need to handle shifting 
                    if (!(list.get(0).contains("LSL") || list.get(0).contains("LSR") || list.get(0).contains("BR") || list.get(0).contains("ASR") || list.get(0).contains("NEG") || list.get(0).contains("RET" ) ) )
                    {
                        // if the index is at 2 then print out the shamt because their is not shift needed for this instruction
                        if(j == 2) 
                        {
                            // define an shamt variable on the outside of the loop 
                            writer.print(" 000000 ");
                        }
                        // if the register contains a special register like XZR then these cases will be checked first 
                        if(list.get(j).contains("XZR") )
                        {
                            // print out a 32 in binary 
                            String XZR = list.get(j).replaceAll("," , " ");
                            XZR = XZR.trim();

                            writer.print(" " + specialCases(XZR) );
                        }
                        // is the register the stack pointer 
                        else if(list.get(j).contains("SP") )
                        {
                            String SP = list.get(j).replaceAll("," , " ");
                            SP = SP.trim();

                            writer.print(specialCases(SP) );
                        }
                        // is the register the linked register
                        else if(list.get(j).contains("LR") )
                        {
                            writer.print(specialCases(list.get(j) ) );
                        }
                        // if the entry contains an X then it is a register, if it is not a register then it could be a special case
                        else if(list.get(j).contains("X") )
                        {
                            // replace all the "X"s and store the new string that will be used to convert to an integer 
                            binary_string = list.get(j).replaceAll("X"," " );

                            // replace commas with spaces as well so that it doesn't mess up the parse int method 
                            binary_string = binary_string.replaceAll("," , " ");

                            // trim off the white space that was left over from the replace method 
                            binary_string = binary_string.trim();

                            // convert the string into an integer 
                            // their is an error here when a string contains a comma : FIX ME
                            binary = Integer.parseInt(binary_string);

                            // here we need to format the register so that it is always 5 bits 

                            // convert the base 10 number to a base 2 number 
                            base2_string = Integer.toBinaryString(binary);

                            // format the string so that it is five bits long and then print 
                            writer.print(" " + getBinaryFormatted(base2_string) + " ");

                        }
                    }
                    // if the instruction is either an LSL or an LSR, this case will be handled in this if statement 
                    else if(list.get(0).contains("LSL") || list.get(0).contains("LSR") )
                    {  
                        // check to make sure the first register contains an immediate number 
                        if(list.get(j).contains("#") )
                        {
                            // the first register is empty so print out 5 zeroes 
                            writer.print("00000");

                            //replace all the "#" with blank spaces 
                            binary_string = list.get(j).replaceAll("#", " ");

                            // replace all the commas with blank spaces 
                            binary_string = binary_string.replaceAll(",", " ");

                            // trim the whitespace off of the string so that the number is the only thing left in the string 
                            binary_string = binary_string.trim();

                            // convert the string into an integer 
                            binary = Integer.parseInt(binary_string);

                            // convert the base 10 number to a base 2 number 
                            base2_string = Integer.toBinaryString(binary);

                            if(base2_string.length() < 6 )
                            {
                                while(base2_string.length() < 6 )
                                {
                                    base2_string = "0" + base2_string;
                                }
                            }
                            // format the string so that it is five bits long and then print 
                            writer.print(" " + base2_string + " ");

                            // it needs to bring out 5 zeroes because one of the registers is not being used FIX ME:
                        }
                        // if the entry contains an X then it is a register, if it is not a register then it could be a special case
                        else if(list.get(j).contains("X") )
                        {
                            // replace all the "X"s and store the new string that will be used to convert to an integer 
                            binary_string = list.get(j).replaceAll("X"," " );

                            // replace commas with spaces as well so that it doesn't mess up the parse int method 
                            binary_string = binary_string.replaceAll("," , " ");

                            // trim off the white space that was left over from the replace method 
                            binary_string = binary_string.trim();

                            // convert the string into an integer 
                            // their is an error here when a string contains a comma : FIX ME
                            binary = Integer.parseInt(binary_string);

                            // here we need to format the register so that it is always 5 bits 

                            // convert the base 10 number to a base 2 number 
                            base2_string = Integer.toBinaryString(binary);

                            // format the string so that it is five bits long and then print 
                            writer.print(" " + getBinaryFormatted(base2_string) + " ");

                            if(j == 2) 
                            {
                                // define an shamt variable on the outside of the loop 

                            }

                        }
                    }
                    // if the instruction is a BR formatted instruction 
                    else if(list.get(0).contains("BR") ) 
                    {
                        writer.print(" " + "00000" + " ");
                        writer.print("000000");

                        if(list.get(j).contains("X") )
                        {
                            // replace all the "X"s and store the new string that will be used to convert to an integer 
                            binary_string = list.get(j).replaceAll("X"," " );

                            // replace commas with spaces as well so that it doesn't mess up the parse int method 
                            binary_string = binary_string.replaceAll("," , " ");

                            // trim off the white space that was left over from the replace method 
                            binary_string = binary_string.trim();

                            // convert the string into an integer 
                            // their is an error here when a string contains a comma : FIX ME
                            binary = Integer.parseInt(binary_string);

                            // here we need to format the register so that it is always 5 bits 

                            // convert the base 10 number to a base 2 number 
                            base2_string = Integer.toBinaryString(binary);

                            // format the string so that it is five bits long and then print 
                            writer.print(" " + getBinaryFormatted(base2_string) + " ");

                            writer.print("00000");

                        }         
                    }
                    // handle the ASR Case here
                    else if (list.get(0).contains("ASR") )
                    {
                        if(list.get(j).contains("#") )
                        {
                            // the first register is empty so print out 5 zeroes 
                            writer.print("00000");

                            //replace all the "#" with blank spaces 
                            binary_string = list.get(j).replaceAll("#", " ");

                            // replace all the commas with blank spaces 
                            binary_string = binary_string.replaceAll(",", " ");

                            // trim the whitespace off of the string so that the number is the only thing left in the string 
                            binary_string = binary_string.trim();

                            // convert the string into an integer 
                            binary = Integer.parseInt(binary_string);

                            // convert the base 10 number to a base 2 number 
                            base2_string = Integer.toBinaryString(binary);

                            if(base2_string.length() < 6 )
                            {
                                while(base2_string.length() < 6 )
                                {
                                    base2_string = "0" + base2_string;
                                }
                            }
                            // format the string so that it is five bits long and then print 
                            writer.print(" " + base2_string + " ");

                        }
                        else if(list.get(j).contains("X") )
                        {
                            // replace all the "X"s and store the new string that will be used to convert to an integer 
                            binary_string = list.get(j).replaceAll("X"," " );

                            // replace commas with spaces as well so that it doesn't mess up the parse int method 
                            binary_string = binary_string.replaceAll("," , " ");

                            // trim off the white space that was left over from the replace method 
                            binary_string = binary_string.trim();

                            // convert the string into an integer 
                            // their is an error here when a string contains a comma : FIX ME
                            binary = Integer.parseInt(binary_string);

                            // here we need to format the register so that it is always 5 bits 

                            // convert the base 10 number to a base 2 number 
                            base2_string = Integer.toBinaryString(binary);

                            // format the string so that it is five bits long and then print 
                            writer.print(" " + getBinaryFormatted(base2_string) + " ");

                        }
                    }
                    else if(list.get(0).contains("NEG") )
                    {
                        if(j == 1)
                        {
                            writer.print(" " + "11111" + " ");

                        }

                        // if the register contains a special register like XZR then these cases will be checked first 
                        if(list.get(j).contains("XZR") )
                        {
                            // print out a 32 in binary 
                            String XZR = list.get(j).replaceAll("," , " ");
                            XZR = XZR.trim();

                            writer.print(" " + specialCases(XZR) );
                        }
                        // is the register the stack pointer 
                        else if(list.get(j).contains("SP") )
                        {
                            String SP = list.get(j).replaceAll("," , " ");
                            SP = SP.trim();

                            writer.print(specialCases(SP) );
                        }
                        // is the register the linked register
                        else if(list.get(j).contains("LR") )
                        {
                            writer.print(specialCases(list.get(j) ) );
                        }
                        // if the entry contains an X then it is a register, if it is not a register then it could be a special case
                        else if(list.get(j).contains("X") )
                        {
                            // replace all the "X"s and store the new string that will be used to convert to an integer 
                            binary_string = list.get(j).replaceAll("X"," " );

                            // replace commas with spaces as well so that it doesn't mess up the parse int method 
                            binary_string = binary_string.replaceAll("," , " ");

                            // trim off the white space that was left over from the replace method 
                            binary_string = binary_string.trim();

                            // convert the string into an integer 
                            // their is an error here when a string contains a comma : FIX ME
                            binary = Integer.parseInt(binary_string);

                            // here we need to format the register so that it is always 5 bits 

                            // convert the base 10 number to a base 2 number 
                            base2_string = Integer.toBinaryString(binary);

                            // format the string so that it is five bits long and then print 
                            writer.print(" " + getBinaryFormatted(base2_string) + " ");

                        }
                        if(j== 2)
                        {
                            writer.print(" " + "000000" + " ");

                        }
                    }
                    else if(list.get(j).contains("RET") )
                    {
                        writer.print(" 00000 ");
                        writer.print("000000 ");
                        writer.print("11110 ");
                        writer.print("00000");
                    }
                }

                // this case will be used to handle I formats and immediate values 
                else if (format.equals("I" ) )
                {
                    // check to make sure that the first register is an immediate value 
                    if(list.get(j).contains("#") )
                    {
                        //replace all the "#" with blank spaces 
                        binary_string = list.get(j).replaceAll("#", " ");

                        // handle if their is a comment at the end of the line 
                        binary_string = binary_string.replaceAll(";", " ");

                        // replace all the commas with blank spaces 
                        binary_string = binary_string.replaceAll(",", " ");

                        // trim the whitespace off of the string so that the number is the only thing left in the string 
                        binary_string = binary_string.trim();

                        // convert the string into an integer 
                        binary = Integer.parseInt(binary_string);

                        // convert the base 10 number to a base 2 number 
                        base2_string = Integer.toBinaryString(binary);

                        // loop as long as the length of the immediate value is not 12 so that has the correct amount of bits 
                        while(!(base2_string.length() == 12) )
                        {
                            // if the binary string is less then a length of 12 then we need to add some zeroes to it for the ALU immediate  
                            base2_string = "0" + base2_string; 
                        }

                        // format the string so that it is five bits long and then print 
                        writer.print(" " + base2_string + " ");

                    }
                    // check our special cases like XZR, SP and LR, if they are being used 
                    else if(list.get(j).contains("XZR") )
                    {
                        writer.print(specialCases(list.get(j) ) );
                    }
                    // is the register the stack pointer 
                    else if(list.get(j).contains("SP") )
                    {
                        writer.print(specialCases(list.get(j) ) );
                    }
                    // is the register the linked register
                    else if(list.get(j).contains("LR") )
                    {
                        String LR = list.get(j).replaceAll("," , " ");
                        LR = LR.trim();

                        writer.print(specialCases(LR) );
                    }
                    else if(list.get(j).contains("X") )
                    {
                        // replace all the "X"s and store the new string that will be used to convert to an integer 
                        binary_string = list.get(j).replaceAll("X"," " );

                        // replace commas with spaces as well so that it doesn't mess up the parse int method 
                        binary_string = binary_string.replaceAll("," , " ");

                        // trim off the white space that was left over from the replace method 
                        binary_string = binary_string.trim();

                        // convert the string into an integer 
                        // their is an error here when a string contains a comma : FIX ME
                        binary = Integer.parseInt(binary_string);

                        // here we need to format the register so that it is always 5 bits 

                        // convert the base 10 number to a base 2 number 
                        base2_string = Integer.toBinaryString(binary);

                        // format the string so that it is five bits long and then print 
                        writer.print(" " + getBinaryFormatted(base2_string) + " ");
                    }
                }
                // if the instruction is a B format instruction 

                else if(format.equals("D") )
                {
                    // print out the op number in the second run through the loop 
                    if(j == 2) 
                    {
                        // define an shamt variable on the outside of the loop 
                        writer.print(" 00 ");
                    }

                    // if it contains a pound sign then we know that it is the destination adress 
                    if(list.get(j).contains("#") ) 
                    {
                        //replace all the "#" with blank spaces 
                        binary_string = list.get(j).replaceAll("#", " ");

                        binary_string = binary_string.replaceAll("]", " ");
                        
                        binary_string = binary_string.replaceAll(";", "");

                        // trim off the white space that was left over from the replace method 
                        binary_string = binary_string.trim();

                        // convert the string into an integer 
                        binary = Integer.parseInt(binary_string);

                        // convert the base 10 number to a base 2 number 
                        base2_string = Integer.toBinaryString(binary);

                        // if the format has two many one bits on the end 
                        if(base2_string.length() > 9) 
                        {
                            base2_string = base2_string.substring(23, 32);

                        }
                        // if not then it has less than 9 bits and zeroes need to be added to the destination address 
                        else {
                            while(base2_string.length() < 9 )
                            {
                                base2_string = "0" + base2_string; 
                            }
                        }
                        writer.print(base2_string);
                    }
                    else if(list.get(j).contains("SP") )
                    {
                        String SP = list.get(j).substring(1);
                        SP = SP.replaceAll(",", " ");
                        SP = SP.trim();
                        writer.print(specialCases(SP) );
                    }
                    // check to see if it is a register 
                    else if(list.get(j).contains("X") )
                    {
                        // replace all the "X"s and store the new string that will be used to convert to an integer 
                        binary_string = list.get(j).replaceAll("X"," " );

                        // replace commas with spaces as well so that it doesn't mess up the parse int method 
                        binary_string = binary_string.replaceAll("," , " ");

                        // create an if statement for if their is a bracket, substring 
                        if(binary_string.contains("[") )
                        {
                            binary_string = binary_string.substring(2);
                        }
                        // trim off the white space that was left over from the replace method 
                        binary_string = binary_string.trim();

                        // convert the string into an integer 
                        // their is an error here when a string contains a bracket here 

                        binary = Integer.parseInt(binary_string);
                        // here we need to format the register so that it is always 5 bits 
                        // convert the base 10 number to a base 2 number 
                        base2_string = Integer.toBinaryString(binary);

                        // format the string so that it is five bits long and then print 
                        writer.print(" " + getBinaryFormatted(base2_string) + " ");
                    }
                }
                // handle the CB format instructions in this case 
                else if(format.equals("CB") )
                {
                    if(list.get(0).contains("B.") )
                    { 
                        // if the first entry in the list does not contain a B formatted instruction find the label 
                        for(int k = 0; k < list_labels.size(); k++)
                        {
                            // check for matching labels 
                            if(list.get(j).equals(list_labels.get(k) ) )
                            {
                                // find the number of line jumps needed to get to where the label is
                                int br_address;
                                String base2_conversion;
                                if(k < line_Num )
                                {
                                    br_address = k - line_Num - 1;
                                    base2_conversion = Integer.toBinaryString(br_address);

                                    // if the length is greater than 19 bits shrink it down so that it is 
                                    if(base2_conversion.length() > 19)
                                    {
                                        writer.print(" " + base2_conversion.substring(13, 32) + " ");
                                    }
                                    else 
                                    {
                                        // if it is less then 19 bits keep looping and adding zeroes until it is the proper length
                                        while(base2_conversion.length() < 19 )
                                        {
                                            base2_conversion = "0" + base2_conversion;
                                        }
                                        writer.print(" " + base2_conversion + " ");
                                    }
                                }
                                else {
                                    br_address = k - line_Num - 1;
                                    base2_conversion = Integer.toBinaryString(br_address);

                                    // if the length is greater than 19 bits shrink it down so that it is 
                                    if(base2_conversion.length() > 19)
                                    {
                                        writer.print(" " + base2_conversion.substring(13, 32) + " ");
                                    }
                                    else 
                                    {
                                        // if it is less then 19 bits keep looping and adding zeroes until it is the proper length
                                        while(base2_conversion.length() < 19 )
                                        {
                                            base2_conversion = "0" + base2_conversion;
                                        }
                                        writer.print(" " + base2_conversion + " ");
                                    }
                                }
                            }
                        }
                        writer.print(" " + "0" + " ");
                        writer.print(" " + getBcond(list.get(0) ) + " ");
                    }
                    // if the instructions is either a CBZ or a CBNZ we have to account for this 
                    else if(list.get(0).contains("CBZ") || list.get(0).contains("CBNZ") )
                    {
                        for(int k = 0; k < list_labels.size(); k++)
                        {
                            // check for matching labels 
                            if(list.get(j).equals(list_labels.get(k) ) )
                            {
                                // find the number of line jumps needed to get to where the label is
                                int br_address;
                                String base2_conversion;
                                if(k < line_Num )
                                {
                                    br_address = k - line_Num - 1;
                                    base2_conversion = Integer.toBinaryString(br_address);

                                    // if the length is greater than 19 bits shrink it down so that it is 
                                    if(base2_conversion.length() > 19)
                                    {
                                        writer.print(" " + base2_conversion.substring(13, 32) + " ");
                                    }
                                    else 
                                    {
                                        // if it is less then 19 bits keep looping and adding zeroes until it is the proper length
                                        while(base2_conversion.length() < 19 )
                                        {
                                            base2_conversion = "0" + base2_conversion;
                                        }
                                        writer.print(" " + base2_conversion + " ");
                                    }
                                }
                                else {
                                    br_address = k - line_Num - 1;
                                    base2_conversion = Integer.toBinaryString(br_address);

                                    // if the length is greater than 19 bits shrink it down so that it is 
                                    if(base2_conversion.length() > 19)
                                    {
                                        writer.print(" " + base2_conversion.substring(13, 32) + " ");
                                    }
                                    else 
                                    {
                                        // if it is less then 19 bits keep looping and adding zeroes until it is the proper length
                                        while(base2_conversion.length() < 19 )
                                        {
                                            base2_conversion = "0" + base2_conversion;
                                        }
                                        writer.print(" " + base2_conversion + " ");

                                    }
                                }
                            }
                        }
                        // check our special cases like XZR, SP and LR, if they are being used 
                        if(list.get(j).contains("XZR") )
                        {
                            writer.print(specialCases(list.get(j) ) );
                        }
                        // is the register the stack pointer 
                        else if(list.get(j).contains("SP") )
                        {
                            String SP = list.get(j).replaceAll("," , " ");
                            SP = SP.trim();

                            writer.print(specialCases(SP) );
                        }
                        // is the register the linked register
                        else if(list.get(j).contains("LR") )
                        {
                            writer.print(specialCases(list.get(j) ) );
                        }
                        else if(list.get(j).contains("X") )
                        {
                            // replace all the "X"s and store the new string that will be used to convert to an integer 
                            binary_string = list.get(j).replaceAll("X"," " );

                            // replace commas with spaces as well so that it doesn't mess up the parse int method 
                            binary_string = binary_string.replaceAll("," , " ");

                            // create an if statement for if their is a bracket, substring 
                            if(binary_string.contains("[") )
                            {
                                binary_string = binary_string.substring(2);
                            }
                            // trim off the white space that was left over from the replace method 
                            binary_string = binary_string.trim();

                            // convert the string into an integer 
                            // their is an error here when a string contains a bracket here 

                            binary = Integer.parseInt(binary_string);
                            // here we need to format the register so that it is always 5 bits 
                            // convert the base 10 number to a base 2 number 
                            base2_string = Integer.toBinaryString(binary);

                            // format the string so that it is five bits long and then print 
                            writer.print(" " + getBinaryFormatted(base2_string) + " ");
                            // after finding the label print out the register that you are comparing 
                        }
                    }
                }
                else if(format.equals("B") )
                {
                    for(int k = 0; k < list_labels.size(); k++)
                    {
                        // check for matching labels 
                        if(list.get(j).equals(list_labels.get(k) ) )
                        {
                            // find the number of line jumps needed to get to where the label is
                            int br_address;
                            String base2_conversion;
                            if(k < line_Num)
                            {
                                br_address = k - line_Num - 1;
                                base2_conversion = Integer.toBinaryString(br_address);

                                // if the length is greater than 19 bits shrink it down so that it is 
                                if(base2_conversion.length() > 26)
                                {
                                    writer.print(" " + base2_conversion.substring(13, 32) + " ");
                                }
                                else 
                                {
                                    // if it is less then 19 bits keep looping and adding zeroes until it is the proper length
                                    while(base2_conversion.length() < 26 )
                                    {
                                        base2_conversion = "0" + base2_conversion;
                                    }
                                    writer.print(" " + base2_conversion + " ");
                                }
                            }
                            else {
                                br_address = k - line_Num - 1;
                                base2_conversion = Integer.toBinaryString(br_address);

                                // if the length is greater than 19 bits shrink it down so that it is 
                                if(base2_conversion.length() > 26)
                                {
                                    writer.print(" " + base2_conversion.substring(13, 32) + " ");
                                }
                                else 
                                {
                                    // if it is less then 19 bits keep looping and adding zeroes until it is the proper length
                                    while(base2_conversion.length() < 26 )
                                    {
                                        base2_conversion = "0" + base2_conversion;
                                    }
                                    writer.print(" " + base2_conversion + " ");
                                }
                            }
                        }
                    }
                }
            }
            // the only instruction that could produce this possibility is the NOP instruction 
            if(list.get(0).equals("NOP") )
            {
                writer.print("00000000000000000000000000");
            }

            if(list.get(0).contains("RET") )
            {
                writer.print(" 00000");
                writer.print(" 000000 ");
                writer.print("11110 ");
                writer.print("00000");

            }

            // increment what line we are currently on to help keep track of labels for conditional branch statements
            ++line_Num;

            //empty the list
            list.clear();

            // skip to the next line for next instruction 
            writer.println();

            writer.println();
            writer.println();

        }
        writer.close();

        System.out.println();
        System.out.println();

        System.out.print("Your instructions have been translated to machine language!"); 
    } 
}
