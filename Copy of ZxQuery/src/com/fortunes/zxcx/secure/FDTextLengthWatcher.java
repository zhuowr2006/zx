/*    */ package com.fortunes.zxcx.secure;
/*    */ 
/*    */ import android.text.Editable;
/*    */ import android.text.TextWatcher;
/*    */ 
/*    */ public class FDTextLengthWatcher
/*    */   implements TextWatcher
/*    */ {
/*    */   protected int maxLength;
/*  8 */   protected int currentEnd = 0;
/*    */ 
/* 10 */   public FDTextLengthWatcher(int maxLength) { setMaxLength(maxLength); }
/*    */ 
/*    */   public final void setMaxLength(int maxLength) {
/* 13 */     if (maxLength >= 0)
/* 14 */       this.maxLength = maxLength;
/*    */     else
/* 16 */       this.maxLength = 0;
/*    */   }
/*    */ 
/*    */   public int getMaxLength() {
/* 20 */     return this.maxLength;
/*    */   }
/*    */ 
/*    */   public void beforeTextChanged(CharSequence s, int start, int count, int after)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void onTextChanged(CharSequence s, int start, int before, int count) {
/* 28 */     this.currentEnd = (start + count);
/*    */   }
/*    */ 
/*    */   public void afterTextChanged(Editable s) {
/* 32 */     while (calculateLength(s) > this.maxLength) {
/* 33 */       this.currentEnd -= 1;
/* 34 */       s.delete(this.currentEnd, this.currentEnd + 1);
/*    */     }
/*    */   }
/*    */ 
/* 38 */   protected int calculateLength(CharSequence c) { int len = 0;
/* 39 */     int l = c.length();
/* 40 */     for (int i = 0; i < l; i++) {
/* 41 */       char tmp = c.charAt(i);
/* 42 */       if ((tmp >= ' ') && (tmp <= '~'))
/* 43 */         len++;
/*    */       else {
/* 45 */         len += 2;
/*    */       }
/*    */     }
/* 48 */     return len;
/*    */   }
/*    */ }

/* Location:           D:\安全控件\FDSecureControl_2015_08_11\FDSecureControl_2015_08_11\Android\Andriod Distribution\FDSecureControl.jar
 * Qualified Name:     cn.com.fortunes.fdsecurecontrol.FDTextLengthWatcher
 * JD-Core Version:    0.6.2
 */