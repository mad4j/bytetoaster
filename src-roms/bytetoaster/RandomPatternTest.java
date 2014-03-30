package bytetoaster;

import java.io.IOException;
import java.util.Random;
import java.util.logging.Logger;

import dolmisani.toys.bytetoaster.tools.ROMBuilder;
import dolmisani.toys.bytetoaster.tools.ToasterJIT;

public class RandomPatternTest extends ToasterJIT implements ROMBuilder {
	
	private int samplesPageAddr;
	private int progPageAddr;
	private int pixelsBankAddr;

	private static Logger LOGGER = Logger.getLogger(ToasterJIT.class.getSimpleName());
	
	public RandomPatternTest() {
		super();
	}
	
	@Override
	public byte[] build() {
		
		initialize();
		
		initMemMap();
		initZeroPage();
		
		cOrg(pixelsBankAddr);
		Random r = new Random();
		for(int c=0; c<BANK_SIZE; c++) {
			dByte(r.nextInt(256));
		}
		
		cOrg(progPageAddr);
		iWait();
		
		return getMemory();
	}

	
	private void initMemMap() {
		
		cOrg(PAGE_SIZE);
		samplesPageAddr = reservePage();
		progPageAddr = reservePage();
		pixelsBankAddr = reserveBank();
		lastAddr = getPC();
		
		LOGGER.info(String.format(
				"Initialized memory map:\n" + 
				"  samples addr: %d\n" +
				"  prog addr   : %d\n" +
				"  pixels addr : %d\n" +
				"  last addr   : %d",
				samplesPageAddr,
				progPageAddr,
				pixelsBankAddr,
				lastAddr
		));
		
	}
	
	private void initZeroPage() {
		
		cOrg(RESET_REG_ADDR);
		dAddr(progPageAddr);
		dByte(pixelsBankAddr / BANK_SIZE);
		dWord(samplesPageAddr / PAGE_SIZE);
	}
	
	public static void main(String[] args) throws IOException {
		
		RandomPatternTest r = new RandomPatternTest();
		
		r.build();
		r.saveFile("RandomPattern.BytePusher");
	}
}
