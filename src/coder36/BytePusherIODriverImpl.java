package coder36;

import java.awt.event.*;
import java.awt.image.*;

import javax.sound.sampled.*;

/**
 * 
 * @author Daniele Olmisani <daniele.olmisani@gmail.com>
 */
public class BytePusherIODriverImpl extends KeyAdapter implements
		BytePusherIODriver {

	private SourceDataLine line;
	private int keyPress;
	
	private BufferedImage image;
	private byte[] pixels;

	/**
	 * Initializes the audio system
	 */
	public BytePusherIODriverImpl() {
		try {
			AudioFormat f = new AudioFormat(15360, 8, 1, true, false);
			line = AudioSystem.getSourceDataLine(f);
			line.open();
			line.start();
		} catch (LineUnavailableException l) {
			throw new RuntimeException(l);
		}
		
		image = new BufferedImage(256, 256, BufferedImage.TYPE_BYTE_INDEXED);
		pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
	}

	/**
	 * Get the current pressed key (0-9 A-F)
	 */
	@Override
	public void updateKeys(byte[] data, int offset) {
		
		data[offset] = 0x00;
		data[offset+1] = 0x00;
		
		switch (keyPress) {
		case KeyEvent.VK_0:
			data[offset+1] += 1;
			break;
		case KeyEvent.VK_1:
			data[offset+1] += 2;
			break;
		case KeyEvent.VK_2:
			data[offset+1] += 4;
			break;
		case KeyEvent.VK_3:
			data[offset+1] += 8;
			break;
		case KeyEvent.VK_4:
			data[offset+1] += 16;
			break;
		case KeyEvent.VK_5:
			data[offset+1] += 32;
			break;
		case KeyEvent.VK_6:
			data[offset+1] += 64;
			break;
		case KeyEvent.VK_7:
			data[offset+1] += 128;
			break;
		case KeyEvent.VK_8:
			data[offset] += 1;
			break;
		case KeyEvent.VK_9:
			data[offset] += 2;
			break;
		case KeyEvent.VK_A:
			data[offset] += 4;
			break;
		case KeyEvent.VK_B:
			data[offset] += 8;
			break;
		case KeyEvent.VK_C:
			data[offset] += 16;
			break;
		case KeyEvent.VK_D:
			data[offset] += 32;
			break;
		case KeyEvent.VK_E:
			data[offset] += 64;
			break;
		case KeyEvent.VK_F:
			data[offset] += 128;
			break;
		}
	}
	
	@Override
	public void renderAudioFrame(byte[] data, int offset, int length) {
		line.write(data, offset, length);
	}
	
	/**
	 * Render 256*256 pixels.
	 */
	@Override
	public void renderDisplayFrame(byte[] data, int offset, int length) {
		
		//TODO: find a way to link BufferedImage directly to memory buffer
		System.arraycopy(data, offset, pixels, 0, length);
	}


	/**
	 * Invoked when a key has been pressed. See the class description for
	 * {@link KeyEvent} for a definition of a key pressed event.
	 */
	public void keyPressed(KeyEvent e) {
		keyPress = e.getKeyCode();
	}

	/**
	 * Detect the key being released so that we can clear the key press.
	 */
	public void keyReleased(KeyEvent e) {
		keyPress = 0;
	}

	/**
	 * Get the image
	 * 
	 * @return the bufferedImage
	 */
	public BufferedImage getDisplayImage() {
		return image;
	}

	
}
