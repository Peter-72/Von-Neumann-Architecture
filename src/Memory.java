import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class Memory {

    Object [] memory = new Object[2];
    Instruction [] instructions = new Instruction[1024];
    int nextFreeSpace = 0;
    byte[] data = new byte[1024 * 4];
    int clk = 1;
    int maxClk;

    boolean stopFetching = false;
    ArrayList<Integer> pcToBe = new ArrayList<>();

    ArrayList<Instruction> decodeBuffer = new ArrayList<>();
    ArrayList<Integer> decodeCounter = new ArrayList<>();
    ArrayList<Integer> decodeInstructionNo = new ArrayList<>();

    ArrayList<int[]> executeBuffer = new ArrayList<>();
    ArrayList<Instruction> executeBufferInstruction = new ArrayList<>();
    ArrayList<Integer> executeBufferType = new ArrayList<>();
    ArrayList<Integer> executeCounter = new ArrayList<>();
    ArrayList<Integer> executeInstructionNo = new ArrayList<>();

    ArrayList<int[]> memoryBuffer = new ArrayList<>();
    ArrayList<Instruction> memoryBufferInstruction = new ArrayList<>();
    ArrayList<Integer> memoryInstructionNo = new ArrayList<>();

    ArrayList<int[]> writeBackBuffer = new ArrayList<>();
    ArrayList<Instruction> writeBackBufferInstruction = new ArrayList<>();
    ArrayList<Integer> writeBackInstructionNo = new ArrayList<>();

    public Memory(){

        this.memory[0] = instructions;
        this.memory[1] = data;
        this.maxClk = 7 + ((this.nextFreeSpace - 1) * 2);
    }

    public int getData(int reference) {

        if(reference >= 1024)
            reference -= 1024;
        reference *= 4;
        byte [] byteData = new byte[]{this.data[reference],this.data[reference + 1],this.data[reference + 2],this.data[reference + 3]};
        BigInteger integerData = new BigInteger(byteData);

        return integerData.intValue();
    }

    public void setData(int intData, int reference) {

        if(reference >= 1024)
            reference -= 1024;
        reference *= 4;
        byte [] byteData = ByteBuffer.allocate(4).putInt(intData).array();
        this.data[reference] = byteData[0];
        this.data[reference + 1] = byteData[1];
        this.data[reference + 2] = byteData[2];
        this.data[reference + 3] = byteData[3];
    }

    public Instruction getAnInstruction(int i){

        if(i < 0 || i > this.instructions.length - 1){
            return null;
        }
        return this.instructions[i];
    }

    public void insertInstruction(Instruction instruction){

        if(nextFreeSpace == this.instructions.length - 1){
            nextFreeSpace = 0;
        }
        this.instructions[nextFreeSpace++] = instruction;
        this.maxClk = 7 + ((this.nextFreeSpace - 1) * 2);
    }

    public int getFromMemory(int reference){

        return this.getData(reference);

    }
    public void setToMemory(Registers r, int value, int reference){

        setData(r.registers[value], reference);

    }
    public void initializeAllMemoryData(int value){
        for(int i = 0; i < this.data.length/4; i ++){
            this.setData(value,i);
        }
    }
    public void initializeMemoryData(int value, int lowerBound, int upperBound){

        if(lowerBound < 0 || upperBound > this.data.length/4)
            return;
        for(int i = lowerBound; i <= upperBound; i++){
            this.setData(value,i);
        }
    }

    public void activate(Registers r){

        System.out.println();
        System.out.println("Start heree.... " );
        System.out.println("-------------------------------------");
        System.out.println();

        while(this.clk <= this.maxClk){

            System.out.println("It is Clk No.: "+ this.clk);
            System.out.println("Current PC is: " + r.pc);

            if(r.pc < this.nextFreeSpace && (this.clk % 2) != 0 && !this.stopFetching){
                this.fetch(r);
            }
            else if(!this.pcToBe.isEmpty()){
                r.pc = this.pcToBe.get(0);
                r.pc = this.pcToBe.remove(0);
            }else if(r.pc < this.nextFreeSpace){
                r.pc++;
            }

            if(!this.decodeBuffer.isEmpty() && this.clk > 1){
                this.decodeBuffer.get(0).decode(this, r);
            }

            if(!this.executeBuffer.isEmpty() && this.clk > 3){

                int[] instruction = this.executeBuffer.get(0);

                if(this.executeBufferType.get(0) == 1){
                    this.executeBufferInstruction.get(0).executeR(instruction[0], instruction[1],
                            instruction[2], instruction[3],instruction[4], this, r);
                }
                else if(this.executeBufferType.get(0) == 2){
                    this.executeBufferInstruction.get(0).executeI(instruction[0], instruction[1],
                            instruction[2], instruction[3], this, r);
                }
                else{
                    this.executeBufferInstruction.get(0).executeJ(instruction[0],instruction[1],this, r);
                }
            }

            if(this.memoryBuffer.size() > 0 && this.clk % 2 == 0 && this.clk > 5){

                int[] instruction = this.memoryBuffer.get(0);
                this.memoryBufferInstruction.get(0).memoryFunc(instruction[0], instruction[1], instruction[2],
                        instruction[3], instruction[4],this, r);
            }

            if(this.writeBackBuffer.size() > 0 && this.clk % 2 != 0 && this.clk > 6){

                int[] instruction = this.writeBackBuffer.get(0);
                this.writeBackBufferInstruction.get(0).writeBack(instruction[0], instruction[1], this, r);
            }

            this.clk++;

            System.out.println("-------------------------------------");
            System.out.println();
        }
    }

    public void fetch(Registers r){

        System.out.println("Instruction No. "+ r.pc +" is in the Fetch() method");

        Instruction temp = this.getAnInstruction(r.pc);

        this.decodeBuffer.add(temp);
        this.decodeCounter.add(this.clk + 2);
        this.decodeInstructionNo.add(r.pc);

    }
}
