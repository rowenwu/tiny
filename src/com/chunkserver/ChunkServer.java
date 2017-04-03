package com.chunkserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
//import java.util.Arrays;


import com.interfaces.ChunkServerInterface;

public class ChunkServer implements ChunkServerInterface {
	private static String filePath;	
	private static long counter;
	private static int port = 9999;
	private static ServerSocket ss;
	private static Socket socket;
	

	public static void main(String[] args) {
		new ChunkServer();
	}
	
	public ChunkServer(){
		// set up chunks folder
		filePath = System.getProperty("user.dir");
		filePath += "\\chunks\\";
		if (!Files.exists(Paths.get(filePath)) || !Files.isDirectory(Paths.get(filePath))) {			
			try {
				Files.createDirectories(Paths.get(filePath));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		counter = new File(filePath).listFiles().length;
		
		// listen for incoming client connections
		try {
			ss = new ServerSocket(port);
			System.out.println("listening...");
			while (true) {
				new ChunkServerThread(ss.accept()).start();
			}
		} catch (IOException e) {
			System.out.println("Error establishing client connection");
			e.printStackTrace();
		} finally {
			try {
				ss.close();
			} catch (IOException e) {
				System.out.println("Error closing server socket");
				e.printStackTrace();
			}
		}

	}

	/**
	 * Receive and send data to a single client through input/output streams 
	 */
	class ChunkServerThread extends Thread {
		private DataOutputStream dos;
		private DataInputStream din;
		private Socket socket;

		public ChunkServerThread(Socket socket) {
			this.socket = socket;
		}

		public void run(){
			try {
				dos = new DataOutputStream(socket.getOutputStream());
				din = new DataInputStream(socket.getInputStream());
				
				// continuously receive create, read, or write commands from the client
				while(true){
					char command = din.readChar();
					if(command == 'c') sendChunkHandle();
					else if (command == 'r') 
						sendReadChunk();
					else if (command == 'w') receiveWriteChunk();	
				}
			} catch (IOException e) {
				System.out.println("Client connection closed");
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		private void sendChunkHandle() throws IOException {
			String chunkHandle = createChunk();
			dos.writeUTF(chunkHandle);
			dos.flush();
		}
		
		private void sendReadChunk() throws IOException {
			String chunkHandle = din.readUTF();
//			System.out.println("read chunk handle: " + chunkHandle);
			byte[] chunk = readChunk(chunkHandle, din.readInt(), din.readInt());
			dos.write(chunk);
			dos.flush();
		}
		
		private void receiveWriteChunk() throws IOException {
			String chunkHandle = din.readUTF();
			byte[] payload = new byte[din.readInt()];
			din.readFully(payload);
			int offset = din.readInt();
			dos.writeBoolean(writeChunk(chunkHandle, payload, offset));
			dos.flush();
		}
	}

	/**
	 * Each chunk corresponds to a file.
	 * Return the chunk handle of the last chunk in the file.
	 */
	public synchronized String createChunk() {
		counter++;
		return "chunks\\chunk" + counter;
	}

	/**
	 * Write the byte array to the chunk at the specified offset
	 * The byte array size should be no greater than 4KB
	 */
	public synchronized boolean writeChunk(String ChunkHandle, byte[] payload, int offset) {
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(ChunkHandle, "rws");
			raf.seek(offset);
			raf.write(payload, 0, payload.length);
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
	public synchronized byte[] readChunk(String ChunkHandle, int offset, int NumberOfBytes) {
		if (!Files.exists(Paths.get(ChunkHandle)) || Files.isDirectory(Paths.get(ChunkHandle))) return null;

		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(ChunkHandle, "rws");
			byte[] chunkArr = new byte[NumberOfBytes];
			raf.seek(offset);
			if(raf.read(chunkArr, 0, NumberOfBytes) == -1)
				System.out.println("Not enough bytes read");
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
