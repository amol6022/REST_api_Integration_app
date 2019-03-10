package com.example.sparksrestapi;

import com.example.sparksrestapi.EducationDetailsObjects.EducationDetailsInput;
import com.example.sparksrestapi.EducationDetailsObjects.EducationDetailsOutput;
import com.example.sparksrestapi.LoginSignupObjects.LoginAndSignup;
import com.example.sparksrestapi.PersonalDetailsObjects.PersonalDetailsInput;
import com.example.sparksrestapi.PersonalDetailsObjects.PersonalDetailsOutput;
import com.example.sparksrestapi.PersonalDetailsObjects.ProfilePicInput;
import com.example.sparksrestapi.ProfessionalDetailsObjects.ProfessionalDetailsInput;
import com.example.sparksrestapi.ProfessionalDetailsObjects.ProfessionalDetailsOutput;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface Api {

    @GET("test")
    Call<ServerStatus> checkServerStatus();

    @POST("user/login")
    Call<LoginAndSignup> loginUser(@Body EmailAndPassword enp);

    @POST("user/signup")
    Call<LoginAndSignup> signupUser(@Body EmailAndPassword enp);

    @POST("user/personaldetail/{id}")
    Call<PersonalDetailsOutput> savePDetails(@Path("id")int id, @Body PersonalDetailsInput pdi);

    @POST("user/personaldetail/pp/post")
    Call<StatusMessageObject> saveProPic(@Body ProfilePicInput ppi);

    @POST("user/educationdetail/{id}")
    Call<EducationDetailsOutput> saveEDetails(@Path("id") int id, @Body EducationDetailsInput edi);

    @Multipart
    @POST("user/educationdetail/certificate")
    Call<StatusMessageObject> saveCerti(@Part("photo\"; filename=\"pp.png\" ")RequestBody file, @Part("uid") RequestBody id);

    @POST("user/professionaldetail/{id}")
    Call<ProfessionalDetailsOutput> saveProDetails(@Path("id")int id, @Body ProfessionalDetailsInput pdi);

    @GET("user/personaldetail/{id}")
    Call<PersonalDetailsOutput> getPDetails(@Path("id") int id);

    @GET("user/professionaldetail/{id}")
    Call<ProfessionalDetailsOutput> getProDetails(@Path("id") int id);

    @GET("user/educationdetail/{id}")
    Call<EducationDetailsOutput> getEDetails(@Path("id") int id);

    @PUT("user/personaldetail/{id}")
    Call<PersonalDetailsOutput> updatePDetails(@Path("id")int id,@Body PersonalDetailsInput pdi);

    @PUT("user/educationdetail/{id}")
    Call<EducationDetailsOutput> updateEDetails(@Path("id")int id,@Body EducationDetailsInput pdi);

    @PUT("user/professionaldetail/{id}")
    Call<ProfessionalDetailsOutput> updateProDetails(@Path("id")int id,@Body ProfessionalDetailsInput pdi);

    @DELETE("user/educationdetail/{id}")
    Call<StatusMessageObject> deleteEDetails(@Path("id") int id);

    @DELETE("user/professionaldetail/{id}")
    Call<StatusMessageObject> deleteProDetails(@Path("id") int id);
}
