package com.chunkserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
//import java.util.Arrays;


import com.interfaces.ChunkServerInterface;

/**
 * implementation of interfaces at the chunkserver side
 * @author Shahram Ghandeharizadeh
 * 
 * FAQ
 * 1. Is a chunk a txt file?  No.  Treat a chunk as an array of bytes per in-class lectures/discussions.  Hint:  See RandomAccessFile of Java
 * 2. Does each chunkserver own only 1 chunk (in which the file name is the filePath member variable)?  OR is the filePath the directory in which multiple chunks can be created/stored?   
 * The later.  A design may call for the filePath to be a directory pertaining to multiple chunks stored on a chunkserver.  This directory consists of multiple files where each file is a chunk.
 * 3.  I was confused because the java file has this line:  final static String filePath = "C:\\Users\\shahram\\Documents\\TinyFS-2\\csci485Disk\\";   //or C:\\newfile.txt  
 * (wasn't sure if the filepath was a txt file or a directory)?  This is only a hint for possible designs.  
 * One is to setup a directory that contains all the chunks where each chunk is a file.  Ignore "newfile.txt" if it makes no sense to you.
 */

public class ChunkServer implements ChunkServerInterface {
//	final static String filePath = "C:\\Users\\shahram\\Documents\\TinyFS-2\\csci485Disk\\";	//or C:\\newfile.txt
//	private static long counter;
	final static String counterFilePath = "count";
	private File counterFile;
	
	/**
	 * Initialize the chunk server
	 */
	public ChunkServer(){
		counterFile = new File(counterFilePath);
		BufferedWriter bw = null;
	    try {
			if(!counterFile.exists() || counterFile.isDirectory()) { 
				bw = new BufferedWriter(new FileWriter(counterFile));
				bw.write("0");
			}
	    } catch( IOException e){
	    	e.printStackTrace();
	    } finally {
	    	try {
		    	if(bw != null) bw.close();
	    	} catch (IOException e){
	    		e.printStackTrace();
	    	}
	    }
	    
	}
	
	/**
	 * Each chunk corresponds to a file.
	 * Return the chunk handle of the last chunk in the file.
	 */
	public String createChunk() {
		BufferedReader br = null;
		BufferedWriter bw = null;
		try {
			br = new BufferedReader(new FileReader(counterFile));
			long counter = Long.parseLong(br.readLine());
			String handle = "chunk" + counter;
			bw = new BufferedWriter(new FileWriter(counterFile));
			counter++;
			bw.write("" + counter);
			return handle;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) br.close();
				if (bw != null) bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * Write the byte array to the chunk at the specified offset
	 * The byte array size should be no greater than 4KB
	 */
	//write(byte[] b, int off, int len)
	public boolean writeChunk(String ChunkHandle, byte[] payload, int offset) {
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(ChunkHandle, "rws");
			raf.write(payload, offset, payload.length);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (raf != null) raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	/**
	 * read the chunk at the specific offset
	 */
	//	read(byte[] b, int off, int len)
	public byte[] readChunk(String ChunkHandle, int offset, int NumberOfBytes) {
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(ChunkHandle, "rws");
			byte[] chunkArr = new byte[ChunkServer.ChunkSize];
			raf.read(chunkArr, offset, NumberOfBytes);
			return chunkArr;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (raf != null) raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	

}
