package com.aseemsethi.bookappoauth.ui.main;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aseemsethi.bookappoauth.R;

public class securityFragment extends Fragment {

    private PageViewModel pageViewModel;
    final String TAG = "BookAppOauth: Sec";

    public static securityFragment newInstance(int index) {
        return new securityFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        pageViewModel.getLoggedin().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                Log.d(TAG, "onChanged Logged in: " + s);
                Toast.makeText(getActivity().getApplicationContext(),s,
                        Toast.LENGTH_LONG).show();
            }
        });
        return inflater.inflate(R.layout.security_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        Log.d(TAG, "onActivity Created");
        // TODO: Use the ViewModel
    }

}