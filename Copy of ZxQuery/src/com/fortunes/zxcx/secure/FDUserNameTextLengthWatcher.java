/*    */ package com.fortunes.zxcx.secure;
/*    */ 
/*    */ import android.content.Context;
/*    */ import android.text.Editable;
/*    */ import android.widget.Toast;
/*    */ 
/*    */ public class FDUserNameTextLengthWatcher extends FDTextLengthWatcher
/*    */ {
/*  8 */   private String maxMsg = "";
/*    */   private Context ctx;
/*    */ 
/*    */   public FDUserNameTextLengthWatcher(int maxLength)
/*    */   {
/* 13 */     super(maxLength);
/*    */   }
/*    */ 
/*    */   public FDUserNameTextLengthWatcher(int maxLength, String maxMsg, Context ctx)
/*    */   {
/* 18 */     super(maxLength);
/*    */ 
/* 20 */     this.maxMsg = maxMsg;
/* 21 */     this.ctx = ctx;
/*    */   }
/*    */ 
/*    */   public void afterTextChanged(Editable s)
/*    */   {
/* 26 */     while (calculateLength(s) > this.maxLength) {
/* 27 */       this.currentEnd -= 1;
/* 28 */       s.delete(this.currentEnd, this.currentEnd + 1);
/*    */ 
/* 30 */       if ((this.ctx != null) && (this.maxMsg.length() > 0))
/* 31 */         Toast.makeText(this.ctx, this.maxMsg, 0).show();
/*    */     }
/*    */   }
/*    */ }

/* Location:           D:\安全控件\FDSecureControl_2015_08_11\FDSecureControl_2015_08_11\Android\Andriod Distribution\FDSecureControl.jar
 * Qualified Name:     cn.com.fortunes.fdsecurecontrol.FDUserNameTextLengthWatcher
 * JD-Core Version:    0.6.2
 */