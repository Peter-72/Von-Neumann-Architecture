public class Instruction extends Registers {

    int [] instruction;
    int type;

    public Instruction(int opcode, int r1, int r2, int r3, int shamt, Memory m){

        instruction = new int[5];
        instruction[0] = opcode;
        instruction[1] = r1;
        instruction[2] = r2;
        instruction[3] = r3;
        instruction[4] = shamt;
        type = 1;

        m.insertInstruction(this);
    }
    public Instruction(int opcode, int r1, int r2, int immediate, Memory m){
        instruction = new int[4];
        instruction[0] = opcode;
        instruction[1] = r1;
        instruction[2] = r2;
        instruction[3] = immediate;
        type = 2;

        m.insertInstruction(this);
    }
    public Instruction(int opcode, int address, Memory m){
        instruction = new int[2];
        instruction[0] = opcode;
        instruction[1] = address;
        type = 3;

        m.insertInstruction(this);
    }

    public int getType(){

        return this.type;
    }

    public String toString(){

        String x = "[";
        for(int i = 0; i < this.instruction.length; i++){

            if(i == this.instruction.length - 1 ){
                x += this.instruction[i];
            }
            else
                x += this.instruction[i] + ", ";
        }
        x += "]";
        return x;
    }

    public void decode(Memory m, Registers r) {

        if(m.decodeCounter.get(0) > m.clk){
            System.out.println("Instruction No. "+ m.decodeInstructionNo.get(0) +" is in the Decode() method");
            return;
        }

        System.out.println("Instruction No. "+ m.decodeInstructionNo.get(0) +" is STILL in the Decode() method");
        Instruction instruction = m.decodeBuffer.get(0);

        if (instruction.getType() == 1) {

            m.executeBuffer.add(new int[]{instruction.instruction[0], instruction.instruction[1], r.getValueIn(instruction.instruction[2]),
                    r.getValueIn(instruction.instruction[3]), instruction.instruction[4]});

            m.executeBufferType.add(1);

        } else if (instruction.getType() == 2) {

            m.executeBuffer.add(new int[]{instruction.instruction[0], instruction.instruction[1],
                    instruction.instruction[2], instruction.instruction[3]});
            m.executeBufferType.add(2);

        } else{

            m.executeBuffer.add(new int[]{instruction.instruction[0], instruction.instruction[1]});
            m.executeBufferType.add(3);

        }

        m.executeBufferInstruction.add(this);
        m.executeCounter.add(m.clk + 2);
        m.executeInstructionNo.add(m.decodeInstructionNo.get(0));

        m.decodeBuffer.remove(0);
        m.decodeCounter.remove(0);
        m.decodeInstructionNo.remove(0);
    }

    public void executeR(int opcode, int rd, int rs, int rt, int shamt, Memory m, Registers r){

        if(m.executeCounter.get(0) != m.clk){
            System.out.println("Instruction No. " + m.executeInstructionNo.get(0) + " is in the Execute() Method");
            return;
        }

        System.out.println("Instruction No. " + m.executeInstructionNo.get(0) + " is STILL in the Execute() Method");

        int result = 0;
        switch(opcode){
            case 0:
                result = rs + rt;
                break;

            case 1:
                result = rs - rt;
                break;

            case 8:
                result = rs;
                result = result << shamt;
                break;

            case 9:
                result = rs;
                result = result >> shamt;
                break;
        }

        m.memoryBuffer.add(new int[]{result, opcode, rd, rs, 0});
        m.memoryBufferInstruction.add(this);
        m.memoryInstructionNo.add(m.executeInstructionNo.get(0));

        m.executeBuffer.remove(0);
        m.executeBufferInstruction.remove(0);
        m.executeBufferType.remove(0);
        m.executeCounter.remove(0);
        m.executeInstructionNo.remove(0);

    }

    public void executeI(int opcode, int rd, int rs, int immediate, Memory m, Registers r){

        if(m.executeCounter.get(0) != m.clk){
            System.out.println("Instruction No. " + m.executeInstructionNo.get(0) + " is in the Execute() Method");
            return;
        }

        System.out.println("Instruction No. " + m.executeInstructionNo.get(0) + " is STILL in the Execute() Method");

        int result = 0;
        switch(opcode){

            case 2: //MULTI
                result = r.getValueIn(rs) * immediate;
                break;

            case 3: //ADDI
                result = r.getValueIn(rs) + immediate;
                break;

            case 4: //BNE
                if(r.getValueIn(rd) != r.getValueIn(rs)){
                    result = r.pc + 1 + immediate;
                }
                break;

            case 5: //ANDI
                result = r.getValueIn(rs) & immediate;
                break;

            case 6: //ORI
                result = r.getValueIn(rs) | immediate;
                break;

            case 10, 11: //LW and SW
                break;
        }

        if(opcode == 4){
            m.memoryBuffer.add(new int[]{result, opcode, r.pc, 0, 0});
        }
        else {
            m.memoryBuffer.add(new int[]{result, opcode, rd, rs, immediate});
        }

        m.memoryBufferInstruction.add(this);
        m.memoryInstructionNo.add(m.executeInstructionNo.get(0));

        m.executeBuffer.remove(0);
        m.executeBufferInstruction.remove(0);
        m.executeBufferType.remove(0);
        m.executeCounter.remove(0);
        m.executeInstructionNo.remove(0);
    }

    public void executeJ(int opcode, int address, Memory m, Registers r){

        if(m.executeCounter.get(0) != m.clk){
            System.out.println("Instruction No. " + m.executeInstructionNo.get(0) + " is in the Execute() Method");
            return;
        }

        System.out.println("Instruction No. " + m.executeInstructionNo.get(0) + " is STILL in the Execute() Method");

        int result = 0;
        if(opcode == 7){
            result = (r.pc >> 28) | address;
        }

        m.memoryBufferInstruction.add(this);
        m.memoryInstructionNo.add(m.executeInstructionNo.get(0));

        m.memoryBuffer.add(new int[]{result, opcode, r.pc, 0, 0});

        m.executeBuffer.remove(0);
        m.executeBufferInstruction.remove(0);
        m.executeBufferType.remove(0);
        m.executeCounter.remove(0);
        m.executeInstructionNo.remove(0);

    }

    public void memoryFunc(int result, int opcode, int rd, int rs, int immediate, Memory m, Registers r){

        System.out.println("Instruction No. " + m.memoryInstructionNo.get(0) + " is in the Memory() Method");
        if(opcode == 10){
            System.out.println("The content of Register " + rd + " HAD: " + r.getValueIn(rd));
            r.setIn(rd, m.getFromMemory(rs + immediate));
            System.out.println("The content of Register " + rd + " BECAME: " + r.getValueIn(rd));
        }
        else if(opcode == 11){
            System.out.println("immediate issssssssssss " + immediate);
            System.out.println("rs issssssssssss " + rs);
            System.out.println("The content of MemoryData location " + (rs + immediate) + " HAD: " + m.getData(rs + immediate));
            m.setToMemory(r, rd, (rs + immediate));
            System.out.println("The content of MemoryData location " + (rs + immediate) + " BECAME: " + m.getData(rs + immediate));
        }

        m.writeBackBuffer.add(new int[]{result, rd});
        m.writeBackBufferInstruction.add(this);
        m.writeBackInstructionNo.add(m.memoryInstructionNo.get(0));

        m.memoryBufferInstruction.remove(0);
        m.memoryBuffer.remove(0);
        m.memoryInstructionNo.remove(0);
    }

    public void writeBack(int result, int rd, Memory m, Registers r){

        System.out.println("Instruction No. " + m.writeBackInstructionNo.get(0) + " is in the WriteBack() Method");
        Instruction instruction = m.writeBackBufferInstruction.get(0);

        instruction.operationFinder(m, r);

        if(instruction.instruction[0] == 4){
            if(result > m.nextFreeSpace){
                m.stopFetching = true;
            }
            if(result != 0) {
                if(r.pc == result - 1){
                    //do nothing
                }
                else if(result == r.pc){
                    m.maxClk += 2;
                }else if(result > r.pc){
                    m.maxClk -= 2 * (instruction.instruction[3] - 1);
                }else{
                    m.maxClk += 2 * (r.pc - result + 1);
                }
                m.pcToBe.add(result);

                m.decodeBuffer.clear();
                m.decodeInstructionNo.clear();
                m.decodeCounter.clear();
            }
        }
        else if(instruction.instruction[0] == 7){

            if(result > m.nextFreeSpace){
                m.stopFetching = true;
            }
            if(result <= r.pc){
                m.maxClk += 2 * (r.pc - result + 1);
            }else{
                m.maxClk -= 2 * (result - r.pc - 1);
            }
            m.pcToBe.add(result);

            m.decodeBuffer.clear();
            m.decodeInstructionNo.clear();
            m.decodeCounter.clear();
        }


        else if(instruction.instruction[0] != 10 && instruction.instruction[0] != 11){
            System.out.println("The content of Register " + rd + " HAD: " + r.getValueIn(rd));
            r.setIn(rd, result);
            System.out.println("The content of Register " + rd + " BECAME: " + r.getValueIn(rd));
        }

        m.writeBackBufferInstruction.remove(0);
        m.writeBackBuffer.remove(0);
        m.writeBackInstructionNo.remove(0);

    }

    public void operationFinder(Memory m ,Registers r){
        int opcode = this.instruction[0];
        int r1 = 0;
        int r2 = 0;
        if(opcode != 7){
            r1 = r.getValueIn(this.instruction[2]);
            r2 = this.instruction[3];
        }
        else r1 = this.instruction[1];

        System.out.print("Instruction No. "+ m.writeBackInstructionNo.get(0) +"'s operation is: ");
        switch (opcode){
            case 0:
                r2 = r.getValueIn(this.instruction[3]);
                System.out.println(r1 + " + " + r2); break;
            case 1:
                r2 = r.getValueIn(this.instruction[3]);
                System.out.println(r1 + " - " + r2); break;

            case 2: System.out.println(r1 + " * " + r2); break;

            case 3: System.out.println(r1 + " + " + r2); break;

            case 4: System.out.println("BNE " + r.getValueIn(this.instruction[1]) + " " + r.getValueIn(this.instruction[2])); break;

            case 5: System.out.println(r1 + " & " + r2); break;

            case 6: System.out.println(r1 + " | " + r2); break;

            case 7: System.out.println("Jump to " + r1); break;

            case 8:
                r2 = this.instruction[4];
                System.out.println(r1 + " << " + r2); break;

            case 9:
                r2 = this.instruction[4];
                System.out.println(r1 + " >> " + r2); break;

            case 10: System.out.println("LW in MEM[" + r1 + " + " + r2 + "]"); break;

            case 11: System.out.println("SW in the in MEM[" + r1 + " + " + r2 + "] the value " + r.getValueIn(this.instruction[1])); break;
        }
    }

 }



