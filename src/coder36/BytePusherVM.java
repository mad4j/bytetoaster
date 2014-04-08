package coder36;

import java.io.*;
import java.util.Arrays;

/**
 * See: http://esolangs.org/wiki/ByteToaster
 * 
 * ByteToaster is a minimalist vitual machine: Framerate: 60 frmaes per second
 * CPU: ByteByteJunp with 3 byte addresses CPU Speed: 65536 instructions per
 * frame (3932160 instructions per second. ~3.93 MHz) Memory: 16Mb RAM Graphics:
 * 256*256 pixels, 1 byte per pixel, 216 fixed colors Sound: 8-bit mono, signed
 * values, 256 samples per frame (15360 samples per second)
 * 
 * Usage: every 60th of a second { vm.run() }
 * 
 * Author: Mark Middleton
 */
public class BytePusherVM {
	
	private byte[] mem = new byte[0x01000000];
	private BytePusherIODriver ioDriver;

	public BytePusherVM(BytePusherIODriver ioDriver) {
		this.ioDriver = ioDriver;
	}

	/**
	 * Load ROM into memory
	 * 
	 * @param rom
	 */
	public void load(InputStream rom) throws IOException {
		
		Arrays.fill(mem, (byte)0x00);
		
		int pc = 0;
		int i = 0;
		while ((i = rom.read()) != -1) {
			mem[pc++] = (byte) i;
		}
	}

	public byte[] getMemory() {
		return mem;
	}

	/**
	 * CPU loop, to be called every 60th of a second
	 */
	public void run() {
		
		// run 65536 instructions
		
		long startTime = System.currentTimeMillis();
		
		ioDriver.updateKeys(mem, 0x000000);
		
		int pc = getAddress(2);
		for(int i=0; i<0x010000; i++) {
			mem[getAddress(pc+3)] = mem[getAddress(pc)];
			pc = getAddress(pc+6);
		}
		
		ioDriver.renderAudioFrame(mem, getWord(6) << 8, 256);
		ioDriver.renderDisplayFrame(mem, getByte(5) << 16, 256*256);
	}
	
	public int getByte(int offset) {
		return (mem[offset] & 0xFF);
	}
	
	public int getWord(int offset) {
		
		return (((int)mem[offset] & 0xFF) << 8) + ((int)mem[offset+1] & 0xFF);
	}
	
	public int getAddress(int offset) {
		return (((int)(mem[offset]) & 0xFF) << 16) + (((int)(mem[offset+1]) & 0xFF) << 8) + (int)(mem[offset+2] & 0xFF);
	}
	
}
