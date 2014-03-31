package dolmisani.toys.bytetoaster.tools;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
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
	
	public int nextPage() {
		pc = (pc & ~(PAGE_SIZE-1)) + PAGE_SIZE;
		return pc;
	}
	
	public int nextBank() {
		pc = (pc & ~(BANK_SIZE-1)) + BANK_SIZE;
		return pc;
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
	
	public int dTitleScreen(BufferedImage image) {
		
		BufferedImage i = new BufferedImage(256, 256, BufferedImage.TYPE_BYTE_INDEXED);
		
		Graphics2D g = (Graphics2D) i.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		g.drawImage(image, 0, 0, 256, 256, null);
		
		byte[] pixels = ((DataBufferByte) i.getRaster().getDataBuffer()).getData();
		
		System.arraycopy(pixels, 0, memory, pc, pixels.length);
		pc += pixels.length;
		
		g.dispose();
		
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
		LOGGER.info(String.format("ROM zero page %02x %02x %02x %02x %02x %02x %02x %02x", memory[0], memory[1], memory[2], memory[3], memory[4], memory[5], memory[6], memory[7]));
		
		FileOutputStream fos = new FileOutputStream(fileName);
		fos.write(memory, 0, lastAddr);
		fos.flush();
		fos.close();
	}
}
