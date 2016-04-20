/*     */ package com.fortunes.zxcx.secure;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.ByteOrder;
/*     */ import java.nio.charset.Charset;
/*     */ import java.security.InvalidAlgorithmParameterException;
/*     */ import java.security.InvalidKeyException;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.util.Map;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import java.util.zip.Deflater;
/*     */ import javax.crypto.BadPaddingException;
/*     */ import javax.crypto.Cipher;
/*     */ import javax.crypto.IllegalBlockSizeException;
/*     */ import javax.crypto.NoSuchPaddingException;
/*     */ import javax.crypto.spec.IvParameterSpec;
/*     */ import javax.crypto.spec.SecretKeySpec;
/*     */ 
/*     */ public class FDUtility
/*     */ {
/*     */   public static byte[] makePacket(FDPacketHead head, Map<String, String> fields)
/*     */   {
/*  24 */     ByteBuffer buffer = ByteBuffer.allocate(2048);
/*  25 */     buffer.order(ByteOrder.BIG_ENDIAN);
/*     */ 
/*  27 */     short fieldAmount = (short)fields.size();
/*     */ 
/*  29 */     byte[] tmp = convertShortToBe(fieldAmount);
/*  30 */     buffer.put(tmp);
/*     */ 
/*  32 */     tmp = new byte[] { 0, 0, 1 };
/*  33 */     buffer.put(tmp);
/*     */ 
/*  35 */     for (String key : fields.keySet()) {
/*  36 */       String value = (String)fields.get(key);
/*  37 */       tmp = convertStringToBE(value);
/*  38 */       short len = (short)tmp.length;
/*  39 */       buffer.put(convertShortToBe(len));
/*  40 */       buffer.put(tmp);
/*     */     }
/*  42 */     buffer.flip();
/*     */ 
/*  44 */     byte[] data = null;
/*     */ 
/*  46 */     int buffferLength = buffer.limit();
/*  47 */     int modValue = buffferLength % 16;
/*  48 */     System.arraycopy(convertIntToBe(buffferLength), 0, head.srclen, 0, 4);
/*     */ 
/*  50 */     if (modValue != 0) {
/*  51 */       ByteBuffer paddingBuffer = ByteBuffer.allocate(16);
/*  52 */       paddingBuffer.order(ByteOrder.BIG_ENDIAN);
/*  53 */       for (int i = 0; i < 16 - modValue; i++) {
/*  54 */         paddingBuffer.put(new byte[1]);
/*     */       }
/*  56 */       paddingBuffer.flip();
/*  57 */       data = new byte[buffer.limit() + paddingBuffer.limit()];
/*  58 */       buffer.get(data, 0, buffer.limit());
/*  59 */       paddingBuffer.get(data, buffer.limit(), paddingBuffer.limit());
/*     */     }
/*     */     else {
/*  62 */       data = new byte[buffer.limit()];
/*  63 */       buffer.get(data);
/*     */     }
/*     */ 
/*  67 */     ByteBuffer keyBuffer = ByteBuffer.allocate(16);
/*  68 */     keyBuffer.order(ByteOrder.BIG_ENDIAN);
/*  69 */     for (int i = 0; i < 4; i++) {
/*  70 */       keyBuffer.put(head.date);
/*     */     }
/*  72 */     keyBuffer.flip();
/*     */ 
/*  74 */     byte[] workKeyData = new byte[keyBuffer.limit()];
/*  75 */     keyBuffer.get(workKeyData);
/*     */ 
/*  77 */     byte[] encryptKeyData = encrypt(workKeyData, head.devid);
/*  78 */     byte[] encryptData = encrypt(data, encryptKeyData);
/*     */ 
/*  80 */     byte[] rtnData = null;
/*     */     try {
/*  82 */       byte[] deflateData = Deflate(encryptData);
/*  83 */       System.arraycopy(convertIntToBe(deflateData.length), 0, head.dstlen, 0, 4);
/*     */ 
/*  85 */       ByteBuffer packetBuffer = ByteBuffer.allocate(4096);
/*  86 */       packetBuffer.order(ByteOrder.BIG_ENDIAN);
/*  87 */       packetBuffer.put(head.srclen);
/*  88 */       packetBuffer.put(head.dstlen);
/*  89 */       packetBuffer.put(head.devid);
/*  90 */       packetBuffer.put(head.date);
/*  91 */       packetBuffer.put(head.version);
/*  92 */       packetBuffer.put(head.sig);
/*  93 */       packetBuffer.put(deflateData);
/*     */ 
/*  95 */       packetBuffer.flip();
/*  96 */       rtnData = new byte[packetBuffer.limit()];
/*  97 */       packetBuffer.get(rtnData);
/*     */     }
/*     */     catch (IOException e) {
/* 100 */       e.printStackTrace();
/*     */     }
/* 102 */     return rtnData;
/*     */   }
/*     */ 
/*     */   public static byte[] encrypt(byte[] content, byte[] password) {
/* 106 */     byte[] result = null;
/*     */     try {
/* 108 */       SecretKeySpec key = new SecretKeySpec(password, "AES");
/* 109 */       Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
/* 110 */       byte[] ivByte = new byte[16];
/* 111 */       IvParameterSpec iv = new IvParameterSpec(ivByte);
/* 112 */       cipher.init(1, key, iv);
/* 113 */       result = cipher.doFinal(content);
/*     */     }
/*     */     catch (NoSuchAlgorithmException e) {
/* 116 */       e.printStackTrace();
/*     */     } catch (NoSuchPaddingException e) {
/* 118 */       e.printStackTrace();
/*     */     } catch (InvalidKeyException e) {
/* 120 */       e.printStackTrace();
/*     */     } catch (IllegalBlockSizeException e) {
/* 122 */       e.printStackTrace();
/*     */     } catch (BadPaddingException e) {
/* 124 */       e.printStackTrace();
/*     */     } catch (InvalidAlgorithmParameterException e) {
/* 126 */       e.printStackTrace();
/*     */     }
/* 128 */     return result;
/*     */   }
/*     */ 
/*     */   public static byte[] Deflate(byte[] source) throws IOException
/*     */   {
/* 133 */     Deflater deflater = new Deflater(-1);
/* 134 */     ByteArrayOutputStream stream = null;
/* 135 */     byte[] result = null;
/*     */     try {
/* 137 */       deflater.setInput(source);
/* 138 */       deflater.finish();
/* 139 */       stream = new ByteArrayOutputStream(source.length);
/* 140 */       byte[] buffer = new byte[1024];
/* 141 */       while (!deflater.finished())
/*     */       {
/* 143 */         int compressed = deflater.deflate(buffer);
/* 144 */         stream.write(buffer, 0, compressed);
/*     */       }
/* 146 */       stream.close();
/* 147 */       result = stream.toByteArray();
/* 148 */       stream = null;
/*     */     } finally {
/* 150 */       deflater.end();
/* 151 */       if (stream != null) {
/* 152 */         stream.close();
/*     */       }
/*     */     }
/* 155 */     return result;
/*     */   }
/*     */ 
/*     */   public static byte[] convertStringToBE(String str) {
/* 159 */     String name = str;
/* 160 */     if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
/* 161 */       ByteBuffer buffer = ByteBuffer.wrap(name.getBytes(Charset.forName("GBK")));
/* 162 */       buffer.order(ByteOrder.BIG_ENDIAN);
/* 163 */       buffer.flip();
/* 164 */       return buffer.array();
/*     */     }
/* 166 */     return name.getBytes(Charset.forName("GBK"));
/*     */   }
/*     */ 
/*     */   public static byte[] convertIntToBe(int number)
/*     */   {
/* 171 */     ByteBuffer buffer = ByteBuffer.allocate(40);
/* 172 */     buffer.order(ByteOrder.BIG_ENDIAN);
/*     */ 
/* 174 */     buffer.putInt(number);
/* 175 */     buffer.flip();
/* 176 */     byte[] rtnData = new byte[buffer.limit()];
/* 177 */     buffer.get(rtnData);
/* 178 */     return rtnData;
/*     */   }
/*     */ 
/*     */   public static byte[] convertShortToBe(short number) {
/* 182 */     ByteBuffer buffer = ByteBuffer.allocate(40);
/* 183 */     buffer.order(ByteOrder.BIG_ENDIAN);
/*     */ 
/* 185 */     buffer.putShort(number);
/* 186 */     buffer.flip();
/* 187 */     byte[] rtnData = new byte[buffer.limit()];
/* 188 */     buffer.get(rtnData);
/* 189 */     return rtnData;
/*     */   }
/*     */ 
/*     */   public static boolean isEmpty(String value) {
/* 193 */     String tmp = value.trim();
/* 194 */     if (tmp.length() == 0) {
/* 195 */       return true;
/*     */     }
/* 197 */     return false;
/*     */   }
/*     */ 
/*     */   public static byte[] UUID2Bin(String uuid)
/*     */   {
/* 203 */     byte ch = 0;
/* 204 */     int i = 0; int j = 0;
/* 205 */     byte[] data = new byte[16];
/*     */ 
/* 207 */     for (int ii = 0; ii < uuid.length(); ii++)
/*     */     {
/* 209 */       byte byteCh = (byte)uuid.charAt(ii);
/* 210 */       if ((byteCh >= 97) && (byteCh <= 102))
/* 211 */         byteCh = (byte)(byteCh - 32);
/* 212 */       if (((byteCh >= 65) && (byteCh <= 70)) || ((byteCh >= 48) && (byteCh <= 57)))
/*     */       {
/* 214 */         ch = (byte)(ch << (j << 2) | (byteCh > 57 ? byteCh - 55 : byteCh - 48));
/* 215 */         j++; if (j > 1)
/*     */         {
/* 217 */           data[(i++)] = ch;
/* 218 */           j = 0;
/* 219 */           ch = 0;
/*     */         }
/*     */       }
/*     */     }
/* 223 */     return data;
/*     */   }
/*     */ 
/*     */   public static boolean stringMatch(String regex, String mobiles) {
/* 227 */     Pattern p = Pattern.compile(regex);
/* 228 */     Matcher m = p.matcher(mobiles);
/* 229 */     return m.matches();
/*     */   }
/*     */ }

/* Location:           D:\安全控件\FDSecureControl_2015_08_11\FDSecureControl_2015_08_11\Android\Andriod Distribution\FDSecureControl.jar
 * Qualified Name:     cn.com.fortunes.fdsecurecontrol.FDUtility
 * JD-Core Version:    0.6.2
 */