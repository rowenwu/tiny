package UnitTests;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.Ref;

import com.chunkserver.ChunkServer;
import com.client.Client;

/**
 * A utility used by the UnitTests
 * @author Shahram Ghandeharizadeh
 *
 */

public class TestReadAndWrite {
	
//	public static ChunkServer cs = new ChunkServer();
	public static Client client = new Client();
	public static final int ChunkSize = 4096;
	
	/**
	 * Create and write chunk(s) of a physical file.
	 * The default full chunk size is 4K. Note that the last chunk of the file may not have the size 4K.
	 * The sequence of chunk handles are returned, which are stored in a static map.
	 */
	public String[] createFile(File f) {
		try {
			RandomAccessFile raf = new RandomAccessFile(f.getAbsolutePath(), "rw");
			raf.seek(0);
			long size = f.length();
			int num = (int)Math.ceil((double)size / ChunkSize);
			String[] ChunkHandles = new String[num];
			String handle = null;
			for(int i = 0; i < num; i++){
				handle = client.createChunk();
				byte[] chunkArr;
				ChunkHandles[i] = handle;
				if(i != num - 1){
					chunkArr = new byte[ChunkSize];
					raf.read(chunkArr, 0, ChunkSize);
				}else{
					chunkArr = new byte[(int)size % ChunkSize];
					raf.read(chunkArr, 0, (int)size % ChunkSize);
				}
//				raf.read(chunkArr, 0, chunkSize);
				boolean isWritten = client.writeChunk(handle, chunkArr, 0);
				if(isWritten == false){
					throw new IOException("Cannot write a chunk to the chunk server!");
				}
			}
			raf.close();
			return ChunkHandles;
		} catch (IOException ie){
			return null;
		}
	}

}
