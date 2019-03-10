package com.example.sparksrestapi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sparksrestapi.LoginSignupObjects.LoginAndSignup;
import com.example.sparksrestapi.PersonalDetailsObjects.PersonalDetailsInput;
import com.example.sparksrestapi.PersonalDetailsObjects.PersonalDetailsOutput;
import com.example.sparksrestapi.PersonalDetailsObjects.ProfilePicInput;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.security.AccessController.getContext;

public class PersonalDetailsActivity extends AppCompatActivity {
    private int loginID,postPDetailsID;
    private String emailID, encodedImage,currentSkillSet;
    private CircleImageView profilePic;
    private Button saveBtn,addSkillBtn;
    private EditText fullName,location,contact,link,skills,email;

    SharedPreferences sharedPreferences;

    private static final int GET_FROM_GALLERY=1;
    private static final String MY_PREFERENCES="MyPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initialize();



        addSkillBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String enteredSkill=skills.getText().toString();

                if(enteredSkill.equals("")){
                    Toast.makeText(getApplicationContext(),"Enter a Skill",Toast.LENGTH_SHORT).show();
                    return;
                }

                storeSkill(enteredSkill);

                displaySkill(enteredSkill);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullNameText, contactText, linkText, locationText;

                fullNameText = fullName.getText().toString();
                contactText = contact.getText().toString();
                linkText = link.getText().toString();
                locationText = location.getText().toString();

                if (fullNameText.equals("") || contactText.equals("") || linkText.equals("") || locationText.equals("") || currentSkillSet.equals("")) {

                    Toast.makeText(getApplicationContext(), "Fill all fields", Toast.LENGTH_SHORT).show();
                    return;

                }

                PersonalDetailsInput pdi = new PersonalDetailsInput(currentSkillSet, contactText, fullNameText, linkText, locationText, emailID);

                SharedPreferences.Editor editor=sharedPreferences.edit();

                editor.remove("Skills");

                editor.apply();

                if (!getIntent().getBooleanExtra("toUpdate",false)){

                    Call<PersonalDetailsOutput> savePersonalDetailsCall = RetrofitClient.getInstance().getApi().savePDetails(loginID, pdi);

                    savePersonalDetailsCall.enqueue(new Callback<PersonalDetailsOutput>() {

                        @Override
                        public void onResponse(Call<PersonalDetailsOutput> call, Response<PersonalDetailsOutput> response) {

                            if(response.body().getData()==null){

                                Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
                                return;

                            }

                            postPDetailsID=response.body().getData().getId();

                            if(encodedImage!=null) {
                                ProfilePicInput ppi = new ProfilePicInput(encodedImage, loginID);

                                Call<StatusMessageObject> saveProPicCall = RetrofitClient.getInstance().getApi().saveProPic(ppi);

                                saveProPicCall.enqueue(new Callback<StatusMessageObject>() {

                                    @Override
                                    public void onResponse(Call<StatusMessageObject> call, Response<StatusMessageObject> response) {
                                        //Toast.makeText(getApplicationContext(), response.body().getStatus_message(), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(Call<StatusMessageObject> call, Throwable t) {
                                        Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                });
                            }

                            Intent intent=new Intent(getApplicationContext(),EducationDetailsActivity.class);
                            intent.putExtra("toUpdate",false);
                            intent.putExtra("userID",loginID);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onFailure(Call<PersonalDetailsOutput> call, Throwable t) {
                            Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });

                }else{
                    Call<PersonalDetailsOutput> updatePDetailsCall=RetrofitClient.getInstance().getApi().updatePDetails(loginID,pdi);

                    updatePDetailsCall.enqueue(new Callback<PersonalDetailsOutput>() {

                        @Override
                        public void onResponse(Call<PersonalDetailsOutput> call, Response<PersonalDetailsOutput> response) {

                            Toast.makeText(getApplicationContext(),"Updated Successfully!",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), InfoDisplayActivity.class);
                            intent.putExtra("userID", loginID);
                            startActivity(intent);
                            finish();

                        }

                        @Override
                        public void onFailure(Call<PersonalDetailsOutput> call, Throwable t) {
                            Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage(v);
            }
        });
    }

    public void storeSkill(String enteredSkill){

        sharedPreferences=getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        currentSkillSet=sharedPreferences.getString("Skills",null);

        if(currentSkillSet!=null) {
            currentSkillSet = currentSkillSet + "," + enteredSkill;
        }else{
            currentSkillSet = enteredSkill;
        }

        editor.putString("Skills",currentSkillSet);
        editor.apply();
    }

    public void displaySkill(String enteredSkill){
        LinearLayout linearLayout=findViewById(R.id.skillSet);
        Button btn=new Button(getApplicationContext());
        linearLayout.addView(btn);

        btn.setText(enteredSkill);

        ViewGroup.LayoutParams params=btn.getLayoutParams();

        if(params!=null) {
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }

        btn.setLayoutParams(params);

        skills.setText("");

        Toast.makeText(getApplicationContext(),"Added!",Toast.LENGTH_SHORT).show();
    }

    public void initialize(){
        Bundle bundle=getIntent().getExtras();

        if(bundle!=null){
            emailID=bundle.getString("email");
            loginID=bundle.getInt("userID");
        }

        fullName=findViewById(R.id.fullNameBox);
        location=findViewById(R.id.locationBox);
        link=findViewById(R.id.linksBox);
        contact=findViewById(R.id.mobileBox);
        skills=findViewById(R.id.skillsBox);
        email=findViewById(R.id.emailBox3);
        profilePic=findViewById(R.id.profilePic);


        email.setText(emailID);
        email.setFocusable(false);

        saveBtn=findViewById(R.id.submitBtn);
        addSkillBtn=findViewById(R.id.addSkillBtn);

    }

    public void uploadImage(View v){
        Intent intent=new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, GET_FROM_GALLERY);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            profilePic.setImageURI(selectedImage);

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] byteArrayImage = baos.toByteArray();
                encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
