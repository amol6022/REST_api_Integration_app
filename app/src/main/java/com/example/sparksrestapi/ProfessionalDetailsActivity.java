package com.example.sparksrestapi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sparksrestapi.ProfessionalDetailsObjects.ProfessionalDetailsInput;
import com.example.sparksrestapi.ProfessionalDetailsObjects.ProfessionalDetailsOutput;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfessionalDetailsActivity extends AppCompatActivity {
    EditText organisationBox,designationBox;
    Spinner sMonth,sYear,eMonth,eYear;
    CheckBox checkBox;
    Button saveBtn;
    int loginID,postProDetailsID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professional_details);

        initialize();

        setSpinners();

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout ll=findViewById(R.id.endLayout);
                TextView tv=findViewById(R.id.endTitle);
                tv.setVisibility(View.GONE);
                ll.setVisibility(View.GONE);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (organisationBox.getText() == null || designationBox.getText() == null) {
                    Toast.makeText(getApplicationContext(), "Fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                String organisationText = organisationBox.getText().toString();
                String designationText = designationBox.getText().toString();
                String startMonth = sMonth.getSelectedItem().toString();
                String startYear = sYear.getSelectedItem().toString();

                String endMonth,endYear,endDate;

                if(!checkBox.isChecked()) {
                    endMonth = eMonth.getSelectedItem().toString();
                    endYear = eYear.getSelectedItem().toString();
                    endDate=endMonth + "-" + endYear;
                }else{
                    endDate="Currently Working";
                }

                ProfessionalDetailsInput pdi = new ProfessionalDetailsInput(
                        startMonth + "-" + startYear,
                        endDate,
                        organisationText,
                        designationText);

                if (!getIntent().getBooleanExtra("toUpdate",false)) {

                    Call<ProfessionalDetailsOutput> saveProDetailsCall = RetrofitClient.getInstance().getApi().saveProDetails(loginID, pdi);

                    saveProDetailsCall.enqueue(new Callback<ProfessionalDetailsOutput>() {

                        @Override
                        public void onResponse(Call<ProfessionalDetailsOutput> call, Response<ProfessionalDetailsOutput> response) {
                            postProDetailsID=response.body().getData().getId();
                            SharedPreferences sp=getSharedPreferences("MyPref",MODE_PRIVATE);
                            SharedPreferences.Editor editor=sp.edit();
                            editor.putInt("postProID",postProDetailsID);
                            editor.apply();
                            //Toast.makeText(getApplicationContext(), response.body().getData().getDesignation(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<ProfessionalDetailsOutput> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    Intent i = new Intent(getApplicationContext(), InfoDisplayActivity.class);
                    i.putExtra("userID", loginID);
                    startActivity(i);
                    finish();

                }else{

                    Call<ProfessionalDetailsOutput> updateProDetailsCall=RetrofitClient.getInstance().getApi().updateProDetails(loginID,pdi);

                    updateProDetailsCall.enqueue(new Callback<ProfessionalDetailsOutput>() {
                        @Override
                        public void onResponse(Call<ProfessionalDetailsOutput> call, Response<ProfessionalDetailsOutput> response) {
                            Toast.makeText(getApplicationContext(),"Updated Successfully!",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<ProfessionalDetailsOutput> call, Throwable t) {
                            Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });

                    Intent i = new Intent(getApplicationContext(), InfoDisplayActivity.class);
                    i.putExtra("userID", loginID);
                    startActivity(i);
                    finish();
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        initialize();
    }

    private void initialize(){
        organisationBox=findViewById(R.id.organizationBox2);
        designationBox=findViewById(R.id.designationBox);
        saveBtn=findViewById(R.id.submitBtn3);
        checkBox=findViewById(R.id.currentlyCheckbox);
        sMonth=findViewById(R.id.startMonth);
        sYear=findViewById(R.id.startYear2);
        eMonth=findViewById(R.id.endMonth);
        eYear=findViewById(R.id.endYear2);

        loginID=getIntent().getIntExtra("userID",-1);
    }

    private void setSpinners(){
        String[] months={"January","February","March","April","May","June","July","August","September","October","November","December"};

        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,months);
        sMonth.setAdapter(adapter);
        sMonth.setSelection(0);
        eMonth.setAdapter(adapter);
        eMonth.setSelection(0);

        String[] years=new String[14];

        for(int i=0;i<14;i++){
            years[i]=i+2006+"";
        }

        adapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,years);
        sYear.setAdapter(adapter);
        sYear.setSelection(0);
        eYear.setAdapter(adapter);
        eYear.setSelection(0);
    }
}
