package bytetoaster;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import dolmisani.toys.bytetoaster.tools.ROMBuilder;
import dolmisani.toys.bytetoaster.tools.ToasterJIT;

public class LennaTitleScreen extends ToasterJIT implements ROMBuilder {
	
	private int samplesPageAddr;
	private int progPageAddr;
	private int pixelsBankAddr;

	BufferedImage titleScreen;
	
	private static Logger LOGGER = Logger.getLogger(ToasterJIT.class.getSimpleName());
	
	public LennaTitleScreen() throws IOException {
		
		super();
		titleScreen = ImageIO.read(new File("lenna.png"));
	}
	
	@Override
	public byte[] build() {
		
		initialize();
		
		initMemMap();
		initZeroPage();
		
		cOrg(pixelsBankAddr);
		dTitleScreen(titleScreen);
		
		cOrg(progPageAddr);
		iWait();
		
		return getMemory();
	}

	
	private void initMemMap() {
		
		cOrg(0x00);
		samplesPageAddr = nextPage();
		progPageAddr = nextPage();
		pixelsBankAddr = nextBank();
		lastAddr = nextBank();
		
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
		
		LennaTitleScreen r = new LennaTitleScreen();
		
		r.build();
		r.saveFile("lenna.BytePusher");
	}
}
