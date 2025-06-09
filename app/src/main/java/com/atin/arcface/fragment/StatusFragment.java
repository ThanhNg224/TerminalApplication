package com.atin.arcface.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.atin.arcface.R;
import com.atin.arcface.activity.Application;
import com.atin.arcface.common.CompanyConstantParam;
import com.atin.arcface.common.Constants;
import com.atin.arcface.common.VolleySingleton;
import com.atin.arcface.model.SynchStatusViewModel;
import com.atin.arcface.util.BaseUtil;
import com.atin.arcface.util.StringUtils;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class StatusFragment extends Fragment {
    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.

    private static final String TAG = "StatusFragment";
    private ImageView imgIntenet, imgDefaultStatus;
    private SynchStatusViewModel viewModel;
    private View vSyncStatus;
    private Thread threadInternetStatus, threadCloudStatus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.status_layout, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        imgIntenet = view.findViewById(R.id.intenetStatus);
        imgDefaultStatus = view.findViewById(R.id.cloudDefaultStatus);
        vSyncStatus = view.findViewById(R.id.cloudSyncStatus);

        //Disable ssl
        BaseUtil.handleSSLHandshake();

//        String domain = getDomain();
//        threadInternetStatus = new Thread(() -> internetStatus());
//        threadCloudStatus = new Thread(() -> cloudApiStatus(domain + "/hello"));
//
//        threadInternetStatus.start();
//        threadCloudStatus.start();
//
//        viewModel = new ViewModelProvider(requireActivity()).get(SynchStatusViewModel.class);
//        viewModel.getStatus().observe(requireActivity(), item -> {
//            boolean syncStatus = item;
//            if(syncStatus){
//                //imgDefaultStatus.setVisibility(View.GONE);
//                vSyncStatus.setVisibility(View.VISIBLE);
//            }else{
//                //imgDefaultStatus.setVisibility(View.VISIBLE);
//                vSyncStatus.setVisibility(View.GONE);
//            }
//        });
    }

    @Override
    public void onStop() {
        super.onStop();

        try{
            threadInternetStatus.interrupt();
            threadCloudStatus.interrupt();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private String getDomain(){
        SharedPreferences pref = getContext().getSharedPreferences(Constants.SHARE_PREFERENCE, MODE_PRIVATE);
        String prefServerDomain = pref.getString( Constants.PREF_BUSINESS_SERVER_HOST, CompanyConstantParam.getInstance().getParam(Constants.MOI_TRUONG).getServerUrlApi());
        String httpPrefix = "";
        if(prefServerDomain.contains(Constants.HTTP) || prefServerDomain.contains(Constants.HTTPS)){
            httpPrefix = "";
        }else{
            httpPrefix = Constants.HTTPS;
            if(BaseUtil.regexIpv4(prefServerDomain)){
                httpPrefix = Constants.HTTP;
            }
        }
        return httpPrefix + prefServerDomain;
    }

    private void internetStatus(){
        while(true){
            try{
                ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                if(cm == null){
                    continue;
                }

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if(activeNetwork != null && activeNetwork.isConnected()){
                    getActivity().runOnUiThread(() -> imgIntenet.setImageResource(R.drawable.internet_on));
                }else{
                    getActivity().runOnUiThread(() -> imgIntenet.setImageResource(R.drawable.internet_off));
                }

                Thread.sleep(1000);
            }catch (InterruptedException ex){
                break;
            }
        }
    }

    private void cloudApiStatus(String url){
        while(true){
            try{
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, new JSONObject(),
                    response -> {
                        getActivity().runOnUiThread(() -> imgDefaultStatus.setImageResource(R.drawable.cloud_done));
                    },
                    error -> {
                        getActivity().runOnUiThread(() -> imgDefaultStatus.setImageResource(R.drawable.cloud_off));
                        String cause = StringUtils.getThrowCause(error);
                        Log.e(TAG, cause);
                    }) {
                };
                VolleySingleton.getInstance(getContext()).addToRequestQueue(request);
                Thread.sleep(10*1000);
            }catch (InterruptedException ex){
                break;
            }
        }
    }
}