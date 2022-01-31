import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FilletONeumann extends Registers{

    public void parser(Memory m, String file) throws IOException {

        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String currentLine = br.readLine();

        while(currentLine!=null){

            String[] instruction = currentLine.split(" ");

            switch(instruction[0]){
                case "ADD":
                    if(instruction.length == 4){
                        new Instruction(0, Integer.parseInt(instruction[1].substring(1)),
                                Integer.parseInt(instruction[2].substring(1)), Integer.parseInt(instruction[3].substring(1)),
                                0, m); break;
                    }
                    else new Instruction(0, Integer.parseInt(instruction[1].substring(1)),
                            Integer.parseInt(instruction[2].substring(1)), Integer.parseInt(instruction[3].substring(1)),
                            Integer.parseInt(instruction[4]), m); break;
                case "SUB":
                    if(instruction.length == 4){
                        new Instruction(1, Integer.parseInt(instruction[1].substring(1)),
                                Integer.parseInt(instruction[2].substring(1)), Integer.parseInt(instruction[3].substring(1)),
                                0, m); break;
                    }
                    else new Instruction(1, Integer.parseInt(instruction[1].substring(1)),
                            Integer.parseInt(instruction[2].substring(1)), Integer.parseInt(instruction[3].substring(1)),
                            Integer.parseInt(instruction[4]), m); break;
                case "MULTI":
                    new Instruction(2, Integer.parseInt(instruction[1].substring(1)),
                            Integer.parseInt(instruction[2].substring(1)), Integer.parseInt(instruction[3]), m); break;
                case "ADDI":
                    new Instruction(3, Integer.parseInt(instruction[1].substring(1)),
                            Integer.parseInt(instruction[2].substring(1)), Integer.parseInt(instruction[3]), m); break;
                case "BNE":
                    new Instruction(4, Integer.parseInt(instruction[1].substring(1)),
                            Integer.parseInt(instruction[2].substring(1)), Integer.parseInt(instruction[3]), m); break;
                case "ANDI":
                    new Instruction(5, Integer.parseInt(instruction[1].substring(1)),
                            Integer.parseInt(instruction[2].substring(1)), Integer.parseInt(instruction[3]), m); break;
                case "ORI":
                    new Instruction(6, Integer.parseInt(instruction[1].substring(1)),
                            Integer.parseInt(instruction[2].substring(1)), Integer.parseInt(instruction[3]), m); break;
                case "J":
                    new Instruction(7, Integer.parseInt(instruction[1]), m); break;
                case  "SLL":
                    new Instruction(8, Integer.parseInt(instruction[1].substring(1)),
                            Integer.parseInt(instruction[2].substring(1)), 0,
                            Integer.parseInt(instruction[3]), m); break;
                case "SRL":
                    new Instruction(9, Integer.parseInt(instruction[1].substring(1)),
                            Integer.parseInt(instruction[2].substring(1)), 0,
                            Integer.parseInt(instruction[3]), m); break;
                case "LW":
                    new Instruction(10, Integer.parseInt(instruction[1].substring(1)),
                            Integer.parseInt(instruction[2].substring(1)), Integer.parseInt(instruction[3]), m); break;
                case "SW":
                    new Instruction(11, Integer.parseInt(instruction[1].substring(1)),
                            Integer.parseInt(instruction[2].substring(1)), Integer.parseInt(instruction[3]), m); break;
            }
            currentLine = br.readLine();
        }
    }

    public static void main(String[]args) throws IOException {

        FilletONeumann project = new FilletONeumann();

        Memory memory = new Memory();
        Registers regs = new Registers();

        memory.initializeAllMemoryData(91);
        /*regs.initializeGPR(9, 1, 12);
        regs.initializeGPR(100, 13, 30);*/

        project.parser(memory, "test.txt");
        memory.activate(regs);

        for(int i = 0; i < regs.registers.length; i++)
            System.out.println("Register No. "+ i +" has: "+ regs.getValueIn(i));

        /*for(int i = 0; i < (memory.data.length)/4; i++)
            System.out.println("Memory No. "+ i +" has: "+ memory.getFromMemory(i));*/

    }
}
