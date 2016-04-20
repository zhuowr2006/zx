/*    */ package com.fortunes.zxcx.secure;
/*    */ 
/*    */ public class FDSpinnerModel
/*    */ {
/*    */   public String key;
/*    */   public String value;
/*    */ 
/*    */   public FDSpinnerModel()
/*    */   {
/*  8 */     this.key = "";
/*  9 */     this.value = "";
/*    */   }
/*    */ 
/*    */   public FDSpinnerModel(String skey, String svalue) {
/* 13 */     this.key = skey;
/* 14 */     this.value = svalue;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 19 */     return this.value;
/*    */   }
/*    */ 
/*    */   public String getKey() {
/* 23 */     return this.key;
/*    */   }
/*    */ 
/*    */   public String getValue() {
/* 27 */     return this.value;
/*    */   }
/*    */ 
/*    */   public FDSpinnerModel(String sValue)
/*    */   {
/* 32 */     String[] tmp = sValue.split("\\|");
/*    */ 
/* 34 */     this.key = tmp[1];
/* 35 */     this.value = tmp[0];
/*    */   }
/*    */ }

/* Location:           D:\瀹夊叏鎺т欢\FDSecureControl_2015_08_11\FDSecureControl_2015_08_11\Android\Andriod Distribution\FDSecureControl.jar
 * Qualified Name:     cn.com.fortunes.fdsecurecontrol.FDSpinnerModel
 * JD-Core Version:    0.6.2
 */