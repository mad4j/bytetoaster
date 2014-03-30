package dolmisani.toys.bytetoaster.tools;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

public class ToasterJIT {
	
	protected static final int PAGE_SIZE = 256;
	protected static final int BANK_SIZE = PAGE_SIZE*PAGE_SIZE;
	protected static final int MEMORY_SIZE = PAGE_SIZE*BANK_SIZE;
	
	protected static final int ADDR_SIZE = 3;
	protected static final int INSTR_SIZE = 3*ADDR_SIZE;
	
	protected static final int KEYS_REG_ADDR = 0x00;
	protected static final int RESET_REG_ADDR = 0x02;
	protected static final int PIXELS_REG_ADDR = 0x05;
	protected static final int SAMPLES_REG_ADDR = 0x06;
	
	protected byte[] memory;
	protected int pc;
	protected int lastAddr;
	
	
	private static Logger LOGGER = Logger.getLogger(ToasterJIT.class.getSimpleName());
	
	public ToasterJIT() {
		initialize();
	}

	
	public void initialize() {
		
		memory = new byte[MEMORY_SIZE];
		
		pc = 0;
		lastAddr = MEMORY_SIZE;
	}
	
	public void nextInstr() {
		pc = (pc + INSTR_SIZE) % MEMORY_SIZE;
	}
	
	public void prevInstr() {
		pc -= INSTR_SIZE;
		if (pc < 0) {
			pc += MEMORY_SIZE;
		}
	}
	
	public int reservePage() {
		int value = pc;
		pc = (pc & ~(PAGE_SIZE-1)) + PAGE_SIZE;
		return value;
	}
	
	public int reserveBank() {
		int value = pc;
		pc = (pc & ~(BANK_SIZE-1)) + BANK_SIZE;
		System.out.println(pc);
		return value;
	}
	
	public byte[] getMemory() {
		return memory;
	}
	
	public int getPC() {
		return pc;
	}
	
	public int dByte(int value) {
		memory[pc++] = (byte)(value & 0xFF);
		return pc;
	}
	
	public int dWord(int value) {
		
		dByte(value >> 8);
		dByte(value);
		return pc;
	}
	
	public int dAddr(int value) {
		dByte(value >> 16);
		dWord(value);
		return pc;
	}
	
	public int dColor(int r, int g, int b) {
		dByte(r*36+g*6+b);
		return pc;
	}
	
	public int cOrg(int addr) {
		pc = addr;
		return pc;
	}
	
	public void iBBJ(int a, int b, int c) {
		dAddr(a);
		dAddr(b);
		dAddr(c);
	}
	
	public void iWait() {
		iBBJ(0, 0, pc);
	}
	
	
	public void saveFile(String fileName) throws IOException {
		
		LOGGER.info(String.format("saving %d bytes in '%s'", lastAddr, fileName));
		
		FileOutputStream fos = new FileOutputStream(fileName);
		fos.write(memory, 0, lastAddr);
		fos.flush();
		fos.close();
	}
}
