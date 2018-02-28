/**
 * 2018年2月9日  下午4:29:00
 * soleil
 */
package nio.study;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author soleil
 * @date 2018年2月9日
 * @time 下午4:29:00
 */
public class ChannelDemo {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		
		FileInputStream fin = new FileInputStream("pom.xml");
		FileChannel fc = fin.getChannel();
		
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		
		while(fc.read(buffer)  > 0)
			System.out.println(buffer.toString());

	}

}
