import java.util.Arrays;

public class Registers extends Memory{

    int [] registers;
    final int zeroRegister;
    int nextFreeSpace;
    int pc;

    public Registers(){
        this.registers = new int[32];
        this.zeroRegister = 0;
        this.nextFreeSpace = 1;
        this.pc = 0;
        this.registers[0] = zeroRegister;

    }

    public int setAndLocate(int input){

        int location = nextFreeSpace;
        this.registers[nextFreeSpace] = input;
        this.nextFreeSpace++;
        return location;
    }

    public void set(int input){

        this.registers[nextFreeSpace] = input;
        this.nextFreeSpace++;
    }

    public void setIn(int location, int input){

        if(location > 0 && location < 32)
            this.registers[location] = input;
    }

    public int getValueIn(int location){

        if(location >= 0 && location < 32){
            return this.registers[location];
        }
        else if(location < 0)
            return this.registers[location * -1];
        else return this.registers[this.nextFreeSpace];

    }

    public void initializeAllGPR(int value){
        Arrays.fill(this.registers, 1, registers.length, value);
    }
    public void initializeGPR(int value, int lowerBound, int upperBound){

        if(lowerBound < 0 && upperBound > this.registers.length){
            Arrays.fill(this.registers, (lowerBound + 1), this.nextFreeSpace, value);
        }
        else if(lowerBound == 0){
            Arrays.fill(this.registers, (lowerBound + 1), upperBound, value);
        }
        else Arrays.fill(this.registers, lowerBound, upperBound, value);


    }
}
