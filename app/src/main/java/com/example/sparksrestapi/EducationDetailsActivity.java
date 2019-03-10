package com.example.sparksrestapi;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sparksrestapi.EducationDetailsObjects.EducationDetailsInput;
import com.example.sparksrestapi.EducationDetailsObjects.EducationDetailsOutput;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EducationDetailsActivity extends AppCompatActivity {
    private static final int GET_FROM_GALLERY=1;
    EditText degreeBox,organisationBox,locationBox;
    Spinner startYear,endYear;
    Button addCertiBtn,saveBtn;
    String encodedImage;
    Uri selectedImage;
    int loginID,postEDetailsID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education_details);

        initialize();

        setSpinners();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String degreeText,organisationText,locationText,startYearText,endYearText;

                startYearText=startYear.getSelectedItem().toString();
                endYearText=endYear.getSelectedItem().toString();
                degreeText=degreeBox.getText().toString();
                organisationText=organisationBox.getText().toString();
                locationText=locationBox.getText().toString();

                if(startYearText.equals("") || endYearText.equals("") || degreeText.equals("") || organisationText.equals("")
                || locationText.equals("")){
                    Toast.makeText(getApplicationContext(),"All fields are mandatory",Toast.LENGTH_SHORT).show();
                    return;
                }

                EducationDetailsInput edi=new EducationDetailsInput(startYearText,degreeText,organisationText,locationText,endYearText);

                if(!getIntent().getBooleanExtra("toUpdate",false)){

                    Call<EducationDetailsOutput> saveEducationDetailsCall = RetrofitClient.getInstance().getApi().saveEDetails(loginID, edi);

                    saveEducationDetailsCall.enqueue(new Callback<EducationDetailsOutput>() {

                        @Override
                        public void onResponse(Call<EducationDetailsOutput> call, Response<EducationDetailsOutput> response) {

                            if (response.body().getData() == null) {
                                Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            postEDetailsID=response.body().getData().getId();
                            SharedPreferences sp=getSharedPreferences("MyPref",MODE_PRIVATE);
                            SharedPreferences.Editor editor=sp.edit();
                            editor.putInt("postEduID",postEDetailsID);
                            editor.apply();

                            if(encodedImage!=null) {

                                File file=new File(getRealPathFromURI(selectedImage));
                                RequestBody fbody = RequestBody.create(MediaType.parse("image/*"), file);
                                RequestBody id = RequestBody.create(MediaType.parse("text/plain"), loginID+"");

                                Call<StatusMessageObject> saveCertiCall=RetrofitClient.getInstance().getApi().saveCerti(fbody,id);

                                saveCertiCall.enqueue(new Callback<StatusMessageObject>() {

                                    @Override
                                    public void onResponse(Call<StatusMessageObject> call, Response<StatusMessageObject> response) {
                                        if (response.body() != null)
                                            Toast.makeText(getApplicationContext(), "AMoL: "+response.body().getStatus_message(), Toast.LENGTH_SHORT).show();
                                        else
                                            Toast.makeText(getApplicationContext(), "Couldn't save certificate...", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(Call<StatusMessageObject> call, Throwable t) {
                                        Toast.makeText(getApplicationContext(), "Errorrr "+t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }else{
                                Toast.makeText(getApplicationContext(),"No image!!!",Toast.LENGTH_LONG).show();
                            }


                            if (getCallingActivity() != null && !getCallingActivity().getClassName().equals(PersonalDetailsActivity.class.getName())) {
                                Intent i=new Intent();
                                setResult(Activity.RESULT_OK,i);
                                finish();
                            }

                            Intent i = new Intent(getApplicationContext(), ProfessionalDetailsActivity.class);
                            i.putExtra("userID", loginID);
                            i.putExtra("toUpdate",false);
                            startActivity(i);
                            finish();

                        }

                        @Override
                        public void onFailure(Call<EducationDetailsOutput> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    });

                }else{

                    Call<EducationDetailsOutput> updateEDeatilsCall=RetrofitClient.getInstance().getApi().updateEDetails(loginID,edi);

                    updateEDeatilsCall.enqueue(new Callback<EducationDetailsOutput>() {

                        @Override
                        public void onResponse(Call<EducationDetailsOutput> call, Response<EducationDetailsOutput> response) {
                            Toast.makeText(getApplicationContext(),"Updated Successfully!",Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getApplicationContext(), InfoDisplayActivity.class);
                            i.putExtra("userID", loginID);
                            startActivity(i);
                            finish();

                        }

                        @Override
                        public void onFailure(Call<EducationDetailsOutput> call, Throwable t) {
                            Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_SHORT).show();
                        }

                    });
                }
            }
        });

        addCertiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*if(GET_FROM_GALLERY==0){
                    Toast.makeText(getApplicationContext(),"You cannot add more than one certificate",Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(),"To edit, click on uploaded certificate",Toast.LENGTH_LONG).show();
                    return;
                }*/

                Intent intent=new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);

                startActivityForResult(intent, GET_FROM_GALLERY);

            }
        });
    }

    protected void onResume() {
        super.onResume();
        initialize();
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    private void initialize(){
        degreeBox=findViewById(R.id.degreeBox);
        organisationBox=findViewById(R.id.organisationBox);
        locationBox=findViewById(R.id.locationBox2);
        startYear=findViewById(R.id.startYear);
        endYear=findViewById(R.id.endYear);
        addCertiBtn=findViewById(R.id.addCertificateBtn);
        saveBtn=findViewById(R.id.submitBtn2);

        loginID=getIntent().getIntExtra("userID",-1);
    }

    private void setSpinners(){
        String[] years1=new String[21];

        for(int i=0;i<=20;i++){
            years1[i]=(i+1999)+"";
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startYear.setAdapter(adapter);

        String[] years2=new String[21];

        for(int i=0;i<=20;i++){
            years2[i]=(i+2004)+"";
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years2);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        endYear.setAdapter(adapter);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            selectedImage = data.getData();

            LinearLayout ll=findViewById(R.id.certificateSet);

            ImageView iv=new ImageView(getApplicationContext());
            iv.setImageURI(selectedImage);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv.setPadding(10,10,10,10);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    startActivityForResult(intent, GET_FROM_GALLERY);
                }
            });

            ll.addView(iv);

            ViewGroup.LayoutParams params=iv.getLayoutParams();

            if(params!=null) {
                params.width = 250;
                params.height = 300;
            }

            iv.setLayoutParams(params);

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] byteArrayImage = baos.toByteArray();
                encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);

                //GET_FROM_GALLERY=0;
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
