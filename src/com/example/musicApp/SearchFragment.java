package com.example.musicApp;


import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.turbomanage.httpclient.AsyncCallback;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;
import com.turbomanage.httpclient.android.AndroidHttpClient;
//import org.apache.http.NameValuePair;
//import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Nick_dnepr on 13.05.2015.
 */
public class SearchFragment extends Fragment implements MediaPlayer.OnPreparedListener {
    private String requestString;
    private String parsedJson;
    private List<Info> list = new ArrayList<>();
    private Gson gson = new GsonBuilder().create();
    private MyAdapter adapter;
    private String streamUrl;
    private String downloadUrl;
    private MusicDataBase dataBase;
    private int index;
    private FragmentTab downloadFragment;
    private MainActivity activity;
    SettingsAndPlaylist serviceInfo;
    ListView listView;
    //    String url = ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_fragment_layout, container, false);

        return view;

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (MainActivity) getActivity();
        dataBase = activity.getDataBase();
        serviceInfo=activity.serviceInfo;
        downloadFragment = (FragmentTab) getFragmentManager().findFragmentByTag("download");
        listView = (ListView) getView().findViewById(R.id.listView);
        adapter = new MyAdapter(getActivity(), list, false);
        listView.setAdapter(adapter);




        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final ImageView downloadButton = (ImageView) view.findViewById(R.id.downloadButton);

                downloadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DataBaseTask task = new DataBaseTask();
//                dataBase.addInfo(list.get(index));
                        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//                        dataBase.addInfo(list.get(index));
                        downloadButton.setImageResource(R.drawable.ic_get_app_black_48dp);
                        Log.i("download", "started downloading");
                    }
                });
                streamUrl = list.get(position).getStream_url() + "?client_id=b45b1aa10f1ac2941910a7f0d10f8e28";
//                downloadUrl = list.get(position).getDownload_url();

                index = position;
                Log.i("url", list.get(position).getStream_url());


                serviceInfo.setPlayList(list);
                serviceInfo.setPosition(position);

                Intent intent = new Intent(getActivity(), MyService.class);
                intent.putExtra("command", list.get(position).getStream_url() + "?client_id=b45b1aa10f1ac2941910a7f0d10f8e28");
                intent.putExtra("dataBase", serviceInfo);
                activity.startService(intent);
                Log.i("tag", list.get(position).getStream_url());
                Log.i("tag", list.toString());


                activity.needToRefresh = true;


            }
        });

//        if (serviceInfo.getPlayList()!=null){
//            list=serviceInfo.getPlayList();
//            Log.i("searchFragment", "called onResume, called listSetter");
//            Log.i("searchFragment", serviceInfo.getPlayList().toString());
//            adapter.notifyDataSetChanged();
//        }



        final EditText request = (EditText) getView().findViewById(R.id.request);
        Button b = (Button) getView().findViewById(R.id.search_button);


        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestString = request.getText().toString();
                makeRequest();
                Log.i("request", "request sended");


            }


        });

    }


    private void makeRequest() {
        AndroidHttpClient httpClient = new AndroidHttpClient("http://api.soundcloud.com");
        ParameterMap params = httpClient.newParams()
                .add("q", requestString)
                .add("limit", "50")
                .add("client_id", "b45b1aa10f1ac2941910a7f0d10f8e28");
        httpClient.setMaxRetries(3);
        httpClient.get("/tracks.json", params, new AsyncCallback() {
            public void onComplete(HttpResponse httpResponse) {
                String s = httpResponse.getBodyAsString();
                Type listType = new TypeToken<ArrayList<Info>>() {
                }.getType();
                list.clear();
                List<Info> infoList = gson.fromJson(s, listType);
                list.addAll(infoList);

                Log.i("112", list.toString());
                serviceInfo.setPlayList(list);
                adapter.notifyDataSetChanged();

            }

            public void onError(Exception e) {

                Log.i("111", "request error", e);
            }
        });
    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.i("sss", "ready");
        mp.start();
    }

    private class DataBaseTask extends AsyncTask<Void, Integer, String> {


        @Override
        protected String doInBackground(Void... params) {
            dataBase.addInfo(list.get(index));

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i("info", "async final");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("info", "starting async");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("searchFragment", "called onResume");

        if (serviceInfo.getPlayList()!=null){
            list=serviceInfo.getPlayList();
            adapter=new MyAdapter(getActivity(), list, false);
            listView.setAdapter(adapter);
            Log.i("searchFragment", "called onResume, called listSetter");
            Log.i("searchFragment", serviceInfo.getPlayList().toString());

        }
        adapter.notifyDataSetChanged();
        listView.refreshDrawableState();
    }

    public List<Info> getList(){
        return list;
    }
}
