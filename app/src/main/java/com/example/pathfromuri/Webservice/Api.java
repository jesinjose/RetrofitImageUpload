package com.example.pathfromuri.Webservice;


import com.example.pathfromuri.Model.CommonModel;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Api {

    @Multipart
    @POST("addComplaintsPic")
    Call<CommonModel> Upload(@Part MultipartBody.Part image);


}
