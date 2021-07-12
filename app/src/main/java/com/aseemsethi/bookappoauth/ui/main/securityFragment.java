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
import android.widget.TextView;
import android.widget.Toast;

import com.aseemsethi.bookappoauth.R;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class securityFragment extends Fragment {

    private PageViewModel pageViewModel;
    final String TAG = "BookAppOauth: Sec";
    MqttHelper mqttHelper;
    View root;

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
        try {
            startMqtt();
        } catch (MqttException e) {
            e.printStackTrace();
        }
        root = inflater.inflate(R.layout.security_fragment, container, false);
        return root;
    }

    private void startMqtt() throws MqttException {
        mqttHelper = new MqttHelper(getActivity().getApplicationContext());
        mqttHelper.mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                String msg = mqttMessage.toString();
                Log.d(TAG, "11 MQTT Msg recvd: " + msg);
                String[] arrOfStr = msg.split(":", 4);
                Log.d(TAG, "MQTT Msg recvd:" + arrOfStr[0] + " : " + arrOfStr[1]);
                if ((arrOfStr[1].trim()).equals("4ffe1a")) {
                    Log.d(TAG, "MQTT Msg recvd from: 4ffe1a");
                    TextView v1 = (TextView) root.findViewById(R.id.sensorValue1);
                    v1.setText(arrOfStr[2]);
                } else if ((arrOfStr[1].trim()).equals("f6e01a")) {
                    Log.d(TAG, "MQTT Msg recvd from: f6e01a");
                    TextView v1 = (TextView) root.findViewById(R.id.sensorValue2);
                    v1.setText(arrOfStr[2]);
                }
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                Log.d(TAG, "msg delivered");
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        Log.d(TAG, "onActivity Created");
        // TODO: Use the ViewModel
    }

}