package com.anaqaphone.services;


import com.anaqaphone.models.ProductDataModel;
import com.anaqaphone.models.PlaceGeocodeData;
import com.anaqaphone.models.PlaceMapDetailsData;
import com.anaqaphone.models.SettingModel;
import com.anaqaphone.models.Slider_Model;
import com.anaqaphone.models.UserModel;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface Service {


    @GET("place/findplacefromtext/json")
    Call<PlaceMapDetailsData> searchOnMap(@Query(value = "inputtype") String inputtype,
                                          @Query(value = "input") String input,
                                          @Query(value = "fields") String fields,
                                          @Query(value = "language") String language,
                                          @Query(value = "key") String key
    );

    @GET("geocode/json")
    Call<PlaceGeocodeData> getGeoData(@Query(value = "latlng") String latlng,
                                      @Query(value = "language") String language,
                                      @Query(value = "key") String key);


    @FormUrlEncoded
    @POST("api/login")
    Call<UserModel> login(@Field("phone_code") String phone_code,
                          @Field("phone") String phone

    );

    @FormUrlEncoded
    @POST("api/register")
    Call<UserModel> signUpWithoutImage(
            @Field("name") String name,
            @Field("phone_code") String phone_code,
            @Field("phone") String phone,
            @Field("email") String email
    );

    @Multipart
    @POST("api/register")
    Call<UserModel> signUpWithImage(@Part("name") RequestBody name,
                                    @Part("email") RequestBody email,
                                    @Part("phone_code") RequestBody phone_code,
                                    @Part("phone") RequestBody phone,
                                    @Part MultipartBody.Part logo


    );

    @GET("api/setting")
    Call<SettingModel> getSetting(
            @Header("lang") String lang

    );

    @GET("api/slider")
    Call<Slider_Model> get_slider();


    @GET("api/product")
    Call<ProductDataModel> Product_detials(@Query("product_id") int product_id);

    @GET("api/offers")
    Call<ProductDataModel> getOffersProducts(@Query("pagination") String pagination,
                                             @Query("user_id") int user_id);

}