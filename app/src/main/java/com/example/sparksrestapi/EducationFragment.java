package com.example.sparksrestapi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sparksrestapi.EducationDetailsObjects.EducationDetailsOutput;
import com.example.sparksrestapi.PersonalDetailsObjects.PersonalDetailsOutput;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class EducationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextView degreeBox,organisationBox,locationBox,startYearBox,endYearBox;
    ImageView iv;
    int loginID,postEduID;
    Button editbutton,deletebutton;
    SharedPreferences sp;
    LinearLayout ll;
    Boolean toUpdate;
    private OnFragmentInteractionListener mListener;

    public EducationFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static EducationFragment newInstance(String param1, String param2) {
        EducationFragment fragment = new EducationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_education, container, false);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        degreeBox=view.findViewById(R.id.degreeBox2);
        organisationBox=view.findViewById(R.id.organisationBox4);
        locationBox=view.findViewById(R.id.locationBox4);
        startYearBox=view.findViewById(R.id.startYear3);
        endYearBox=view.findViewById(R.id.endYear3);
        iv=view.findViewById(R.id.certificatePic);

        editbutton=view.findViewById(R.id.editEducation);
        ll=view.findViewById(R.id.eduLayout);
        deletebutton=view.findViewById(R.id.deleteEducation);

        getEducationDetails();

        editbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getContext(),EducationDetailsActivity.class);

                i.putExtra("toUpdate",toUpdate);
                i.putExtra("userID",sp.getInt("loginID",-1));
                i.putExtra("email",sp.getString("email","No Email"));

                startActivityForResult(i,1);
            }
        });


        deletebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<StatusMessageObject> deleteEduDetailsCall=RetrofitClient.getInstance().getApi().deleteEDetails(postEduID);

                deleteEduDetailsCall.enqueue(new Callback<StatusMessageObject>() {
                    @Override
                    public void onResponse(Call<StatusMessageObject> call, Response<StatusMessageObject> response) {
                         ll.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<StatusMessageObject> call, Throwable t) {

                    }
                });
            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && resultCode== Activity.RESULT_OK){
            getEducationDetails();
        }
    }

    private void getEducationDetails(){
        sp=getActivity().getSharedPreferences("MyPref",Context.MODE_PRIVATE);

        loginID=sp.getInt("loginID",-1);
        postEduID=sp.getInt("postEduID",-1);
        //Toast.makeText(getContext(),"Edu LoginID: "+loginID,Toast.LENGTH_SHORT).show();

        Call<EducationDetailsOutput> educationDetailsOutputCall=RetrofitClient.getInstance().getApi().getEDetails(loginID);

        educationDetailsOutputCall.enqueue(new Callback<EducationDetailsOutput>() {

            @Override
            public void onResponse(Call<EducationDetailsOutput> call, Response<EducationDetailsOutput> response) {

                if(response.body().getData()!=null) {
                    toUpdate=true;

                    degreeBox.setText(response.body().getData().getDegree());
                    organisationBox.setText(response.body().getData().getOrganisation());
                    locationBox.setText(response.body().getData().getLocation());
                    startYearBox.setText(response.body().getData().getStart_year());
                    endYearBox.setText(response.body().getData().getEnd_year());

                    String imageUrl = "http://139.59.65.145:9090/user/educationdetail/certificate/";

                    Uri uri=Uri.parse(imageUrl+String.valueOf(loginID));

                    RequestCreator r = Picasso.with(getActivity().getApplicationContext()).load(uri);

                    if (r != null) {
                        r.into(iv);
                        //Toast.makeText(getActivity().getApplicationContext(),"Yessss image",Toast.LENGTH_LONG).show();

                    }else{
                        Toast.makeText(getActivity().getApplicationContext(),"Noooo image",Toast.LENGTH_LONG).show();
                    }

                    ll.setVisibility(View.VISIBLE);
                    editbutton.setBackgroundResource(R.drawable.editicon);
                }

            }

            @Override
            public void onFailure(Call<EducationDetailsOutput> call, Throwable t) {
                Toast.makeText(getContext(),t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
