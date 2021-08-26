package com.example.soundapp.Responses;

import com.example.soundapp.Responses.LoginRequest;
import com.example.soundapp.Responses.LoginResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserService {


    @FormUrlEncoded
      @POST("/register")
    Call<ResponseBody> createUser(
            @Field("name") String name,
            @Field("email") String email,
            @Field("password") String password,
            @Field("deviceId") String deviceId

    );

    @POST("/login")
    Call<LoginResponse> userLogin (@Body LoginRequest loginRequest);
}
