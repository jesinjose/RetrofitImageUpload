package com.example.pathfromuri.Webservice;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CommonFunction {
    Context context;

    public CommonFunction(Context context) {
        this.context = context;
    }
    public Retrofit createRetrofitObjectWithHeader(String URL) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .readTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    Request.Builder newRequest = request.newBuilder()
                            .header("ApiKey", "");
                    return chain.proceed(newRequest.build());
                }).build();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create());
        return builder.build();
    }
}
