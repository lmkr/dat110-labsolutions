package no.hvl.dat110.util;

/**
 * exercise/demo purpose in dat110
 * @author tdoy
 *
 */

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash { 
	
	
	public static BigInteger hashOf(String entity) {		
		
		BigInteger hashint = null;
		
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");  	// we use MD5 with 128 bits digest
			
			byte[] digest = md.digest(entity.getBytes("utf8"));
			
			String hashvalue = toHex(digest); 						// we use hexadecimal to have a compact form
			
			hashint = new BigInteger(hashvalue, 16);

			
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			
			e.printStackTrace();
		}
		
		return hashint;
	}
	
	public static BigInteger addressSize() {
		
		int digestlen = 0;
		try {
			digestlen = MessageDigest.getInstance("MD5").getDigestLength();			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		int mbit = digestlen*8;
		BigInteger modulos = new BigInteger("2");
		modulos = modulos.pow(mbit);				// 2^mbit
		
		return modulos;
	}
	
	public static String toHex(byte[] digest) {
		StringBuilder strbuilder = new StringBuilder();
		for(byte b : digest) {
			strbuilder.append(String.format("%02x", b&0xff));
		}
		return strbuilder.toString();
	}
	
//	public static void main(String[] args) throws UnknownHostException {
//	
//		System.out.println(Hash.hashOf("process1"));
//		System.out.println(Hash.addressSize());
//		BigInteger diff = Hash.addressSize().subtract(Hash.hashOf("process1"));
//		System.out.println(diff);
//		
//		BigInteger id6 = new BigInteger("5").mod(Hash.addressSize());
//		BigInteger lower6 = Hash.addressSize().subtract(new BigInteger("4"));
//		BigInteger upper6 = new BigInteger("2").mod(Hash.addressSize());
//		
//		System.out.println(id6);
//		System.out.println(lower6);
//		System.out.println(upper6);
//	}

}
