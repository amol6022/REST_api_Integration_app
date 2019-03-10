package com.example.sparksrestapi;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sparksrestapi.PersonalDetailsObjects.PersonalDetailsOutput;
import com.example.sparksrestapi.ProfessionalDetailsObjects.ProfessionalDetailsOutput;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.net.URI;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InfoDisplayActivity extends AppCompatActivity implements PersonalFragment.OnFragmentInteractionListener,EducationFragment.OnFragmentInteractionListener,ProfessionalFragment.OnFragmentInteractionListener{
    int id;
    TabLayout tabLayout;
    ViewPager viewPager;
    Button logoutButton;
    CircleImageView profilePic;

    TextView nameBox,organisationBox,designationBox,linkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_display);

        initialize();
    }

    public void onFragmentInteraction(Uri uri) {

    }

    public void initialize(){
        id=getIntent().getIntExtra("userID",-1);

        tabLayout=findViewById(R.id.tabLayout2);

        //tabLayout.addTab(tabLayout.newTab().setText("Personal"));
        //tabLayout.addTab(tabLayout.newTab().setText("Education"));
        //tabLayout.addTab(tabLayout.newTab().setText("Profession"));

        viewPager=findViewById(R.id.pager2);

        PagerAdapter2 pagerAdapter=new PagerAdapter2(getSupportFragmentManager());

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        profilePic=findViewById(R.id.profilePic2);
        nameBox=findViewById(R.id.fullNameBox2);
        organisationBox=findViewById(R.id.organisationBox3);
        designationBox=findViewById(R.id.designationBox2);
        linkBox=findViewById(R.id.linksBox2);

        logoutButton=findViewById(R.id.logoutBtn);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Logged Out!",Toast.LENGTH_SHORT).show();
                Intent i=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    public void setInfo(){

        Call<PersonalDetailsOutput> getPDetailsCall=RetrofitClient.getInstance().getApi().getPDetails(id);

        getPDetailsCall.enqueue(new Callback<PersonalDetailsOutput>() {
            @Override
            public void onResponse(Call<PersonalDetailsOutput> call, Response<PersonalDetailsOutput> response) {
                nameBox.setText(response.body().getData().getName());
                linkBox.setText(response.body().getData().getLinks());

                String imageUrl="http://139.59.65.145:9090/user/personaldetail/profilepic/";
                Uri uri=Uri.parse(imageUrl+String.valueOf(id));

                RequestCreator rc=Picasso.with(getApplicationContext()).load(uri);

                if(rc!=null){
                    rc.into(profilePic);
                }else{
                    profilePic.setBackgroundResource(R.drawable.usericon);
                }
            }

            @Override
            public void onFailure(Call<PersonalDetailsOutput> call, Throwable t) {
                Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        Call<ProfessionalDetailsOutput> getProDetailsCall=RetrofitClient.getInstance().getApi().getProDetails(id);

        getProDetailsCall.enqueue(new Callback<ProfessionalDetailsOutput>() {
            @Override
            public void onResponse(Call<ProfessionalDetailsOutput> call, Response<ProfessionalDetailsOutput> response) {
                organisationBox.setText(response.body().getData().getOrganisation());
                designationBox.setText(response.body().getData().getDesignation());
            }

            @Override
            public void onFailure(Call<ProfessionalDetailsOutput> call, Throwable t) {
                Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setInfo();
    }
}
