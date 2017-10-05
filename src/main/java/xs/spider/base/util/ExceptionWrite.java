package xs.spider.base.util;

import java.io.*;

public class ExceptionWrite extends PrintWriter {
	public ExceptionWrite() {
		super(new StringWriter());
	}

	public ExceptionWrite(int initialSize) {
		super(new StringWriter(initialSize));
	}

	public String getString() {
		flush();
		return ((StringWriter) this.out).toString();
	}

	@Override
	public String toString() {
		return getString();
	}

	/**
	 * 返回错误堆栈信息的字符串
	 * @param e
	 * @return
	 */
//	public static String get(Exception e) {
//		ExceptionWrite spw = new ExceptionWrite();
//		e.printStackTrace(spw);
//		return spw.getString();
//	}
	/**
	 * 将异常信息转化成字符串
	 * @param t
	 * @return
	 * @throws IOException
	 */
	public static String get(Throwable t) {
		if(t == null)
			return null;
		ByteArrayOutputStream baos = null;
		try{
			baos = new ByteArrayOutputStream();
			t.printStackTrace(new PrintStream(baos));
		}finally{
			if (null != baos) try {
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return baos.toString();
	}
}
