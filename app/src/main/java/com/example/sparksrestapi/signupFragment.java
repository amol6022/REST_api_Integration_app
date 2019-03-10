package com.example.sparksrestapi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sparksrestapi.LoginSignupObjects.LoginAndSignup;

import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link signupFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link signupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class signupFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private EditText email,password;
    private Button signupBtn;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public signupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment signupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static signupFragment newInstance(String param1, String param2) {
        signupFragment fragment = new signupFragment();
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
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_signup, container, false);

        email=v.findViewById(R.id.emailBox);
        password=v.findViewById(R.id.passwordBox);
        signupBtn=v.findViewById(R.id.signupBtn);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText()!=null && password.getText()!=null){
                    EmailAndPassword enp=new EmailAndPassword(email.getText().toString(),password.getText().toString());
                    Call<LoginAndSignup> signupCall=RetrofitClient.getInstance().getApi().signupUser(enp);

                    signupCall.enqueue(new Callback<LoginAndSignup>() {
                        @Override
                        public void onResponse(Call<LoginAndSignup> call, Response<LoginAndSignup> response) {
                            if(response.body().getData()!=null){
                                email.setText("");
                                password.setText("");
                                Toast.makeText(getContext(),"Successful signup, Login to Proceed...",Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<LoginAndSignup> call, Throwable t) {
                            Toast.makeText(getContext(),"Couldn't Signup: "+t.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Toast.makeText(getContext(),"Fill all fields",Toast.LENGTH_SHORT).show();
                }
            }
        });

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(final Context context) {
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
