package com.fortunes.zxcx.keys;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;


public class AESEND {

	/**
	 * 加密
	 * 
	 * @param content 需要加密的内容
	 * 
	 * @param password 加密密码
	 * 
	 * @return
	 */
	public static String iv = "1234567890123456";

	public static String encrypt(String content, String password)
			throws Exception {

		byte[] enCodeFormat = new BASE64Decoder().decodeBuffer(password);// 对秘钥进行解码
		SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		byte[] byteContent = content.getBytes("utf-8");
		cipher.init(Cipher.ENCRYPT_MODE, key,
				new IvParameterSpec(iv.getBytes()));
		byte[] result = cipher.doFinal(byteContent);
		return new BASE64Encoder().encode(result); // 对加密的结果进行base64编码

	}

	/**
	 * 注意：解密的时候要传入byte数组 解密
	 * 
	 * @param content 待解密内容
	 * 
	 * @param password 解密密钥
	 * 
	 * @return
	 */
	public static String decrypt(String contentStr, String password)
			throws Exception {

		byte[] content = new BASE64Decoder().decodeBuffer(contentStr);// 对密文先解码
		byte[] enCodeFormat = new BASE64Decoder().decodeBuffer(password);// 对秘钥进行解码
		SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, key,
				new IvParameterSpec(iv.getBytes()));
		byte[] result = cipher.doFinal(content);
		return new String(result, "utf-8"); // 解密
	}

	/**
	 * 将16进制转换为二进制
	 * 
	 * @param hexStr
	 * @return
	 */
	public static byte[] parseHexStr2Byte(String hexStr) {
		if (hexStr.length() < 1)
			return null;
		byte[] result = new byte[hexStr.length() / 2];
		for (int i = 0; i < hexStr.length() / 2; i++) {
			int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2),
					16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
	}
}