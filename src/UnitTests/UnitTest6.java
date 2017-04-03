package UnitTests;

import com.client.Client;

public class UnitTest6 {
	public static void main(String[] args) throws Exception {
		Client client = new Client();
		String str = "ab";
        String handle = client.createChunk();
        if(handle == null){
        	System.out.println("Unit test r result: fail!");
        	return;
        }
        boolean isSuccess = client.writeChunk(handle, str.getBytes(), 0);
        if(isSuccess == true){
        	System.out.println("Unit test 6 write result: success!");
        }else{
        	System.out.println("Unit test 6 write result: fail!");
        }
        byte[] b = client.readChunk(handle, 0, str.getBytes().length);
        if(str.equals(new String(b))){
        	System.out.println("Unit test 6 read result: success!");
	    }else{
	    	System.out.println("Unit test 6 read result: fail!");
	    }
	}
}
