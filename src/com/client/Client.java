package com.client;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.chunkserver.ChunkServer;
import com.interfaces.ClientInterface;

/**
 * implementation of interfaces at the client side
 * @author Shahram Ghandeharizadeh
 *
 */
public class Client implements ClientInterface {
	private static int port = 9999;
	private static String hostName = "localhost";
	private Socket socket;
	DataOutputStream dos;
	DataInputStream din;

	/**
	 * Initialize the client
	 */
	public Client(){
		try {
			socket = new Socket(hostName, port);
			dos = new DataOutputStream(socket.getOutputStream());
			din = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	public String createChunk() {
		try {
			dos.writeChar('c');
			dos.flush();
			String chunkHandle = din.readUTF();
			return new String(chunkHandle);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return null;
	}

	public boolean writeChunk(String ChunkHandle, byte[] payload, int offset) {
		if(offset + payload.length > ChunkServer.ChunkSize) {
			System.out.println("The chunk write should be within the range of the file, invalid chunk write!");
			return false;
		}
		try {
			// send the write command, chunk handle, payload size, and payload to the server 
			// read and return the boolean sent back
			dos.writeChar('w');
			dos.writeUTF(ChunkHandle);
			dos.writeInt(payload.length);
			dos.write(payload);
			dos.writeInt(offset);
			dos.flush();
			return din.readBoolean();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return false;
	}

	public byte[] readChunk(String ChunkHandle, int offset, int NumberOfBytes) {
		if(NumberOfBytes + offset > ChunkServer.ChunkSize) {
			System.out.println("The chunk read should be within the range of the file, invalid chunk read!");
			return null;
		}
		try {
			// send the read command, chunk handle, offset, and number of bytes to the server
			// read the response into a byte array
			dos.writeChar('r');
			dos.writeUTF(ChunkHandle);
			dos.writeInt(offset); 
			dos.writeInt(NumberOfBytes); 
			dos.flush();
			byte[] byteArr = new byte[NumberOfBytes];
			din.readFully(byteArr);
			return byteArr;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
	}
	

}
