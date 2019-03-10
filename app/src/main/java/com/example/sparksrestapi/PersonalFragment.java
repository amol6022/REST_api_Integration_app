package com.example.sparksrestapi;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sparksrestapi.PersonalDetailsObjects.PersonalDetailsOutput;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PersonalFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PersonalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PersonalFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TextView nameBox,contactBox,emailBox,linkBox,locationBox;
    private LinearLayout ll;
    SharedPreferences sp;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public PersonalFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PersonalFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PersonalFragment newInstance(String param1, String param2) {
        PersonalFragment fragment = new PersonalFragment();
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
        View v= inflater.inflate(R.layout.fragment_personal, container, false);

        return v;
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


        nameBox=view.findViewById(R.id.fullNameBox3);
        contactBox=view.findViewById(R.id.contactBox);
        emailBox=view.findViewById(R.id.emailBox4);
        locationBox=view.findViewById(R.id.locationBox3);
        linkBox=view.findViewById(R.id.linksBox3);
        ll=view.findViewById(R.id.skillLayout);

        sp=getActivity().getSharedPreferences("MyPref",Context.MODE_PRIVATE);

        Button editbutton=view.findViewById(R.id.editPersonal);
        editbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getContext(),PersonalDetailsActivity.class);
                i.putExtra("toUpdate",true);
                i.putExtra("userID",sp.getInt("loginID",-1));
                i.putExtra("email",sp.getString("email","No Email"));
                startActivity(i);
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
    public void onResume() {
        super.onResume();
        getPersonalDetails();
    }

    private void getPersonalDetails(){
        Call<PersonalDetailsOutput> personalDetailsOutputCall=RetrofitClient.getInstance().getApi().getPDetails(sp.getInt("loginID",-1));

        personalDetailsOutputCall.enqueue(new Callback<PersonalDetailsOutput>() {

            @Override
            public void onResponse(Call<PersonalDetailsOutput> call, Response<PersonalDetailsOutput> response) {

                if(response.body().getData()!=null) {

                    nameBox.setText(response.body().getData().getName());
                    contactBox.setText(response.body().getData().getMobile_no());
                    emailBox.setText(sp.getString("email","No Email"));
                    locationBox.setText(response.body().getData().getLocation());
                    linkBox.setText(response.body().getData().getLinks());

                    String[] skills = response.body().getData().getSkills().split(",");

                    for (String str : skills) {
                        TextView tt = new TextView(getContext());
                        tt.setText(str);
                        tt.setTextSize(20);
                        tt.setPadding(5, 5, 5, 5);
                        ll.addView(tt);
                        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) tt.getLayoutParams();
                        lp.setMargins(10, 10, 10, 10);

                    }

                }

            }

            @Override
            public void onFailure(Call<PersonalDetailsOutput> call, Throwable t) {
                Toast.makeText(getContext(),t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ll.removeAllViews();
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
