package com.example.sparksrestapi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sparksrestapi.EducationDetailsObjects.EducationDetailsOutput;
import com.example.sparksrestapi.ProfessionalDetailsObjects.ProfessionalDetailsOutput;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfessionalFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfessionalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfessionalFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextView organisationBox,designationBox,start,end;
    Button editbutton,deletebutton;
    SharedPreferences sp;
    LinearLayout ll;
    Boolean toUpdate;
    int loginID,postProID;

    private OnFragmentInteractionListener mListener;

    public ProfessionalFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfessionalFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfessionalFragment newInstance(String param1, String param2) {
        ProfessionalFragment fragment = new ProfessionalFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_professional, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        organisationBox=view.findViewById(R.id.organisationBox5);
        designationBox=view.findViewById(R.id.designationBox3);
        start=view.findViewById(R.id.startTime);
        end=view.findViewById(R.id.endTime);
        ll=view.findViewById(R.id.proLayout);

        sp=getActivity().getSharedPreferences("MyPref",Context.MODE_PRIVATE);

        loginID=sp.getInt("loginID",-1);
        postProID=sp.getInt("postProID",-1);

        editbutton=view.findViewById(R.id.editProfession);
        deletebutton=view.findViewById(R.id.deleteProfession);

        getProfessionalDetails();

        editbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getContext(),ProfessionalDetailsActivity.class);

                i.putExtra("toUpdate", toUpdate);
                i.putExtra("userID",sp.getInt("loginID",-1));
                i.putExtra("email",sp.getString("email","No Email"));

                startActivityForResult(i,0);

            }
        });

        deletebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<StatusMessageObject> deleteProDetailsCall=RetrofitClient.getInstance().getApi().deleteProDetails(postProID);
                deleteProDetailsCall.enqueue(new Callback<StatusMessageObject>() {
                    @Override
                    public void onResponse(Call<StatusMessageObject> call, Response<StatusMessageObject> response) {
                           ll.setVisibility(View.GONE);
//                        organisationBox.setText("");
//                        designationBox.setText("");
//                        start.setText("");
//                        end.setText("");
//
//                        editbutton.setBackgroundResource(R.drawable.addicon);
//                        Toast.makeText(getContext(),"To re-enter details, click on \"add\" icon",Toast.LENGTH_LONG).show();
//                        toUpdate=false;
                    }

                    @Override
                    public void onFailure(Call<StatusMessageObject> call, Throwable t) {

                    }
                });
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==0){
            getProfessionalDetails();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void getProfessionalDetails(){

        Call<ProfessionalDetailsOutput> professionalDetailsCall=RetrofitClient.getInstance().getApi().getProDetails(loginID);

        professionalDetailsCall.enqueue(new Callback<ProfessionalDetailsOutput>() {

            @Override
            public void onResponse(Call<ProfessionalDetailsOutput> call, Response<ProfessionalDetailsOutput> response) {

                if(response.body()!=null) {
                    toUpdate=true;
                    organisationBox.setText(response.body().getData().getOrganisation());
                    designationBox.setText(response.body().getData().getDesignation());
                    start.setText(response.body().getData().getStart_date());
                    end.setText(response.body().getData().getEnd_date());
                    ll.setVisibility(View.VISIBLE);
                    editbutton.setBackgroundResource(R.drawable.editicon);
                }

            }

            @Override
            public void onFailure(Call<ProfessionalDetailsOutput> call, Throwable t) {
                Toast.makeText(getContext(),t.getMessage(),Toast.LENGTH_SHORT).show();
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
