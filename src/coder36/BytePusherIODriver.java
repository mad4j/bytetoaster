package coder36;

/**
 * ByteToaster hardware abstraction layer
 * @author Mark Middleton
 */
public interface BytePusherIODriver {
	  /**
	   * Get the current pressed key (0-9 A-F)
	  */
	  short getKeyPress();


	  /**
	   * Render 256 bytes of audio 
	  */
	  void renderAudioFrame(byte[] data);


	  /**
	   * Render 256*256 pixels.  
	  */
	  void renderDisplayFrame(byte[] data);
}
