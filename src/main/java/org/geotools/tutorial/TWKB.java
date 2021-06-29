package org.geotools.tutorial;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.OutStream;
import org.locationtech.jts.io.OutputStreamOutStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TWKB {
	private  ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
	private OutStream byteArrayOutStream = new OutputStreamOutStream(byteArrayOS);

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// System.out.println(toHex(b));
		TWKB twkb=new TWKB();
		byte[] b=twkb.writevarint(100);
		int result=twkb.readvarint(b);
		System.out.println(b.length);
	}

	public byte[] writevarint(int n) {
		byte[] bytes = new byte[5];
		int idx = 0;

		while (true) {
			if ((n & ~0x7F) == 0) { // 除开最后7位全部为0，表示无信息
				bytes[idx] = (byte) n;
				break;
			} else { // 除低7位，不全部为0
				bytes[idx++] = ((byte) ((n & 0x7F) | 0x80)); // 高1位置补1，低7位按位与得到实际值
				n >>>= 7;
			}
		}
		return bytes;

	}

	public int readvarint(byte[] bytes) {
		int ret = 0;

		int offset = 0;

		for (int i = 0; i < bytes.length; i++, offset += 7){
			byte n = bytes[i];

			if ((n & 0x80) != 0x80) {
				ret |= (n << offset);
				break;
			}else{
				ret |= ((n & 0x7f) << offset);
			}

		}

		return ret;
	}

	public String toHex(byte[] bytes) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			byte b = bytes[i];
			buf.append(toHexDigit((b >> 4) & 0x0F));
			buf.append(toHexDigit(b & 0x0F));
		}
		return buf.toString();
	}

	private static char toHexDigit(int n) {
		if (n < 0 || n > 15)
			throw new IllegalArgumentException("Nibble value out of range: " + n);
		if (n <= 9)
			return (char) ('0' + n);
		return (char) ('A' + (n - 10));
	}

	private int zigzag_encode(int n) {
		return (n << 1) ^ (n >> 31);
	}

	private int zigzag_decode(int n) {
		return (n >>> 1) ^ -(n & 1);
	}
	 public byte[] write(Geometry geom)
	  {
	    try {
	      byteArrayOS.reset();
	      write(geom, byteArrayOutStream);
	    } catch (IOException ex) {
	      throw new RuntimeException("Unexpected IO exception: " + ex.getMessage());
	    }
	    return byteArrayOS.toByteArray();
	 }
	 public void write(Geometry geom, OutStream os) throws IOException {
		 if(geom instanceof Point) {
			 writePoint((Point) geom,os);
		 }
	 }
	 public void writePoint(Point geom,OutStream os) {
		 
	 }
}
