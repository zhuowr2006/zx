package com.fortunes.zxcx.keys;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;
/** *//** 
 * <p> 
 * RSA公钥/私钥/签名工具包 
 * </p> 
 * <p> 
 * <p> 
 * 字符串格式的密钥在未在特殊说明情况下都为BASE64编码格式<br/> 
 * 由于非对称加密速度极其缓慢，一般文件不使用它来加密而是使用对称加密，<br/> 
 * 非对称加密算法可以用来对对称加密的密钥加密，这样保证密钥的安全也就保证了数据的安全 
 * </p> 
 *  
 * @author  
 * @date  
 * @version 1.0 
 */  
public class RSAUtils {  
  
    /** *//** 
     * 加密算法RSA 
     */  
    public  final String KEY_ALGORITHM = "RSA";  
      
    /** *//** 
     * 签名算法 
     */  
    public  final String SIGNATURE_ALGORITHM = "MD5withRSA";  
  
    /** *//** 
     * 获取公钥的key 
     */  
    public static final String PUBLIC_KEY = "RSAPublicKey";  
    
    /** *//** 
     * 获取私钥的key 
     */  
    public static  final String PRIVATE_KEY = "RSAPrivateKey";  
      
    /** *//** 
     * RSA最大加密明文大小 
     */  
    private  final int MAX_ENCRYPT_BLOCK = 117;  
      
    /** *//** 
     * RSA最大解密密文大小 
     */  
    private  final int MAX_DECRYPT_BLOCK = 128;  
    
  
  
    /** *//** 
     * <p> 
     * 生成密钥对(公钥和私钥) 
     * </p> 
     *  
     * @return 
     * @throws Exception 
     */  
    public  Map<String, String> genKeyPair() throws Exception {  
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);  
        keyPairGen.initialize(1024);  
        KeyPair keyPair = keyPairGen.generateKeyPair();  
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        
        Map<String, String> keyMap = new HashMap<String, String>(2);
        Base64Utils base64Utils=new Base64Utils();
        String keyPublic= base64Utils.encode(publicKey.getEncoded());
        String keyPrivate= base64Utils.encode(privateKey.getEncoded());
        keyMap.put(PUBLIC_KEY, keyPublic);  
        keyMap.put(PRIVATE_KEY, keyPrivate); 
         
        return keyMap;  
    }  
      
    /** *//** 
     * <p> 
     * 用私钥对信息生成数字签名 
     * </p> 
     *  
     * @param data 已加密数据 
     * @param privateKey 私钥(BASE64编码) 
     *  
     * @return 
     * @throws Exception 
     */  
    public  String sign(byte[] data, String privateKey) throws Exception { 
    	Base64Utils base64Utils=new Base64Utils();
        byte[] keyBytes = base64Utils.decode(privateKey);  
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);  
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);  
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);  
        signature.initSign(privateK);  
        signature.update(data);  
        return base64Utils.encode(signature.sign());  
    }  
  
    /** *//** 
     * <p> 
     * 校验数字签名 
     * </p> 
     *  
     * @param data 已加密数据 
     * @param publicKey 公钥(BASE64编码) 
     * @param sign 数字签名 
     *  
     * @return 
     * @throws Exception 
     *  
     */  
    public  boolean verify(byte[] data, String publicKey, String sign)  
            throws Exception { 
    	Base64Utils base64Utils=new Base64Utils();
        byte[] keyBytes = base64Utils.decode(publicKey);  
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);  
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
        PublicKey publicK = keyFactory.generatePublic(keySpec);  
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);  
        signature.initVerify(publicK);  
        signature.update(data);  
        return signature.verify(base64Utils.decode(sign));  
    }  
  
    /** *//** 
     * <P> 
     * 私钥解密 
     * </p> 
     *  
     * @param encryptedData 已加密数据 
     * @param privateKey 私钥(BASE64编码) 
     * @return 
     * @throws Exception 
     */  
    public  byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey)  
            throws Exception { 
    	Base64Utils base64Utils=new Base64Utils();
        byte[] keyBytes = base64Utils.decode(privateKey);  
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);  
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);  
        Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding");  
        cipher.init(Cipher.DECRYPT_MODE, privateK);  
        int inputLen = encryptedData.length;  
        ByteArrayOutputStream out = new ByteArrayOutputStream();  
        int offSet = 0;  
        byte[] cache;  
        int i = 0;  
        // 对数据分段解密  
        while (inputLen - offSet > 0) {  
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {  
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);  
            } else {  
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);  
            }  
            out.write(cache, 0, cache.length);  
            i++;  
            offSet = i * MAX_DECRYPT_BLOCK;  
        }  
        byte[] decryptedData = out.toByteArray();  
        out.close();  
        return decryptedData;  
    }  
  
    /** *//** 
     * <p> 
     * 公钥解密 
     * </p> 
     *  
     * @param encryptedData 已加密数据 
     * @param publicKey 公钥(BASE64编码) 
     * @return 
     * @throws Exception 
     */  
    public  byte[] decryptByPublicKey(byte[] encryptedData, String publicKey)  
            throws Exception {  
    	Base64Utils base64Utils=new Base64Utils();
        byte[] keyBytes = base64Utils.decode(publicKey);  
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);  
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
        Key publicK = keyFactory.generatePublic(x509KeySpec);  
        Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding");  
        cipher.init(Cipher.DECRYPT_MODE, publicK);  
        int inputLen = encryptedData.length;  
        ByteArrayOutputStream out = new ByteArrayOutputStream();  
        int offSet = 0;  
        byte[] cache;  
        int i = 0;  
        // 对数据分段解密  
        while (inputLen - offSet > 0) {  
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {  
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);  
            } else {  
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);  
            }  
            out.write(cache, 0, cache.length);  
            i++;  
            offSet = i * MAX_DECRYPT_BLOCK;  
        }  
        byte[] decryptedData = out.toByteArray();  
        out.close();  
        return decryptedData;  
    }  
  
    /** *//** 
     * <p> 
     * 公钥加密 
     * </p> 
     *  
     * @param data 源数据 
     * @param publicKey 公钥(BASE64编码) 
     * @return 
     * @throws Exception 
     */  
    public  byte[] encryptByPublicKey(byte[] data, String publicKey)  
            throws Exception { 
    	Base64Utils base64Utils=new Base64Utils();
    	
    	BASE64Decoder decoder = new BASE64Decoder();
    	//加载公钥
        byte[] keyBytes =  base64Utils.decode(publicKey);  
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);  
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
        Key publicK = keyFactory.generatePublic(x509KeySpec);  
        // 对数据加密  
        Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding");  
        cipher.init(Cipher.ENCRYPT_MODE, publicK);  
        int inputLen = data.length;  
        ByteArrayOutputStream out = new ByteArrayOutputStream();  
        int offSet = 0;  
        byte[] cache;  
        int i = 0;  
        // 对数据分段加密  
        while (inputLen - offSet > 0) {  
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {  
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);  
            } else {  
                cache = cipher.doFinal(data, offSet, inputLen - offSet);  
            }  
            out.write(cache, 0, cache.length);  
            i++;  
            offSet = i * MAX_ENCRYPT_BLOCK;  
        }  
        byte[] encryptedData = out.toByteArray();  
        out.close();  
        return encryptedData;  
    }  
  
    /** *//** 
     * <p> 
     * 私钥加密 
     * </p> 
     *  
     * @param data 源数据 
     * @param privateKey 私钥(BASE64编码) 
     * @return 
     * @throws Exception 
     */  
    public  byte[] encryptByPrivateKey(byte[] data, String privateKey)  
            throws Exception { 
    	Base64Utils base64Utils=new Base64Utils();
        byte[] keyBytes = base64Utils.decode(privateKey);  
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);  
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);  
        Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding");  
        cipher.init(Cipher.ENCRYPT_MODE, privateK);  
        int inputLen = data.length;  
        ByteArrayOutputStream out = new ByteArrayOutputStream();  
        int offSet = 0;  
        byte[] cache;  
        int i = 0;  
        // 对数据分段加密  
        while (inputLen - offSet > 0) {  
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {  
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);  
            } else {  
                cache = cipher.doFinal(data, offSet, inputLen - offSet);  
            }  
            out.write(cache, 0, cache.length);  
            i++;  
            offSet = i * MAX_ENCRYPT_BLOCK;  
        }  
        byte[] encryptedData = out.toByteArray();  
        out.close();  
        return encryptedData;  
    } 
    
    /** *//** 
     * <p> 
     * 保存公钥和私钥 
     * </p> 
     *  
     * @param keyMap 密钥对 
     * @return 
     * @throws Exception 
     */ 
   public   void createKeysFile(String s,String path) throws IOException{
		
		File f = new File(path);
		if (!f.getParentFile().exists()) {
			if (!f.getParentFile().mkdirs()) {
			}
		}
		FileWriter fw = new FileWriter(f);
		fw.write(s);
		fw.flush();
		fw.close();
		
	}
   
   /** *//** 
    * <p> 
    * 读取公钥和私钥文件
    * </p> 
    *  
    * @param keyMap 密钥对 
    * @return 
    * @throws Exception 
    */ 
   
   public   String readFileByChars(String fileName) throws Exception {
       File file = new File(fileName);
       InputStream urlStream = new FileInputStream(file);
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(urlStream, "utf-8"));
		StringBuffer str = new StringBuffer();
		String total = "";
		while ((total = bufferedReader.readLine()) != null) {
			str.append(total);
		}
		return new String(str);
       
   }
    
   /*public String readFile(String fileName){
	   
//	   BufferedReader bf = new BufferedReader(new );
	   BufferedReader bf = new BufferedReader(new InputStreamReader(fileName));
	   bf.readLine();
	   
	    
	   return null;
   }*/
  
    /** *//** 
     * <p> 
     * 获取私钥 
     * </p> 
     *  
     * @param keyMap 密钥对 
     * @return 
     * @throws Exception 
     */  
    public  String getPrivateKeyFile(String s)  throws Exception { 
    	String keyPub="D:/keys/a/key.public";
    	String f=readFileByChars(s);
    	BASE64Decoder decoder = new BASE64Decoder();
		byte[] b = decoder.decodeBuffer(f);
		Base64Utils base64Utils=new Base64Utils();
		return new BASE64Encoder().encode(b);
//		return base64Utils.encodeFile(s);
         
    }  
    
    
    /** *//** 
     * <p> 
     * 获取公钥 
     * </p> 
     *  
     * @param keyMap 密钥对 
     * @return 
     * @throws Exception 
     */  
    public  String getPublicKeyFile(String s)  
            throws Exception {  
    	String keyPub="D:/keys/a/key.private";
    	String f=readFileByChars(s);
    	Base64Utils base64Utils=new Base64Utils();
//    	byte[] b = new sun.misc.BASE64Decoder().decodeBuffer(s);
    	
    	BASE64Decoder decoder = new BASE64Decoder();
		byte[] b = decoder.decodeBuffer(f);
		
		return base64Utils.encode(b); 
//    	return base64Utils.encodeFile(s);
    }  
    
   
    
    /** *//** 
     * <p> 
     * 获取私钥 
     * </p> 
     *  
     * @param keyMap 密钥对 
     * @return 
     * @throws Exception 
     */  
    public  String getPrivateKey(Map<String, Object> keyMap)  throws Exception { 
    	String keyPub="D:/keys/a/key.public";
        Key key = (Key) keyMap.get(PRIVATE_KEY);  
        Base64Utils base64Utils=new Base64Utils();
        return base64Utils.encode(key.getEncoded());  
    } 
    
  
    /** *//** 
     * <p> 
     * 获取公钥 
     * </p> 
     *  
     * @param keyMap 密钥对 
     * @return 
     * @throws Exception 
     */  
    public  String getPublicKey(Map<String, Object> keyMap)  
            throws Exception {  
        Key key = (Key) keyMap.get(PUBLIC_KEY);  
        Base64Utils base64Utils=new Base64Utils();
        return base64Utils.encode(key.getEncoded());  
    }  
  
} 


