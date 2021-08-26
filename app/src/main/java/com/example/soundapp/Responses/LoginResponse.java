package com.example.soundapp.Responses;

public class LoginResponse {
   private String respCode;
   private String respDesc;
   private String token;
   private User user;

   public String getRespCode() {
      return respCode;
   }

   public void setRespCode(String respCode) {
      this.respCode = respCode;
   }

   public String getRespDesc() {
      return respDesc;
   }

   public void setRespDesc(String respDesc) {
      this.respDesc = respDesc;
   }

   public String getToken() {
      return token;
   }

   public void setToken(String token) {
      this.token = token;
   }

   public User getUser() {
      return user;
   }

   public void setUser(User user) {
      this.user = user;
   }
}
