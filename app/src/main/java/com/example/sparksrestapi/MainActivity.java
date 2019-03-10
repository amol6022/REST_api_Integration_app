package com.example.sparksrestapi;

import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements loginFragment.OnFragmentInteractionListener,signupFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabLayout tabLayout=findViewById(R.id.tabLayout);
        //tabLayout.addTab(tabLayout.newTab().setText("Sign Up"));
        //tabLayout.addTab(tabLayout.newTab().setText("Login"));

        ViewPager viewPager=findViewById(R.id.pager);

        PagerAdapter pagerAdapter=new PagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        checkServer();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void checkServer(){
        Call<ServerStatus> serverTestCall= RetrofitClient.getInstance().getApi().checkServerStatus();
        serverTestCall.enqueue(new Callback<ServerStatus>() {
            @Override
            public void onResponse(Call<ServerStatus> call, Response<ServerStatus> response) {
                Toast.makeText(MainActivity.this,"Server Status: "+response.body().getStatus(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ServerStatus> call, Throwable t) {
                Toast.makeText(MainActivity.this,"Server Down: "+t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
