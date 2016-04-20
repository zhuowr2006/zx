/*    */ package com.fortunes.zxcx.secure;
/*    */ 
/*    */ import java.util.Calendar;
/*    */ import java.util.UUID;
/*    */ 
/*    */ public class FDPacketHead
/*    */ {
/*    */   public byte[] srclen;
/*    */   public byte[] dstlen;
/*    */   public byte[] devid;
/*    */   public byte[] date;
/*    */   public byte[] version;
/*    */   public byte[] sig;
/*    */ 
/*    */   public FDPacketHead()
/*    */   {
/* 15 */     this.srclen = new byte[4];
/* 16 */     this.dstlen = new byte[4];
/*    */ 
/* 18 */     this.devid = new byte[16];
/* 19 */     UUID uuid = UUID.randomUUID();
/*    */ 
/* 26 */     System.arraycopy(FDUtility.UUID2Bin(uuid.toString()), 0, this.devid, 0, 16);
/*    */ 
/* 28 */     this.date = new byte[4];
/*    */ 
/* 30 */     Calendar cal = Calendar.getInstance();
/* 31 */     long tmpMills = cal.getTimeInMillis();
/* 32 */     int mills = (int)tmpMills;
/*    */ 
/* 34 */     byte[] tmp = FDUtility.convertIntToBe(mills);
/* 35 */     System.arraycopy(tmp, 0, this.date, 0, 4);
/*    */ 
/* 37 */     this.version = new byte[] { 1 };
/*    */ 
/* 39 */     this.sig = new byte[] { 77 };
/*    */   }
/*    */ }

/* Location:           D:\安全控件\FDSecureControl_2015_08_11\FDSecureControl_2015_08_11\Android\Andriod Distribution\FDSecureControl.jar
 * Qualified Name:     cn.com.fortunes.fdsecurecontrol.FDPacketHead
 * JD-Core Version:    0.6.2
 */