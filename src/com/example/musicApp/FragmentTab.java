package com.example.musicApp;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick_dnepr on 13.05.2015.
 */
public class FragmentTab extends Fragment {


    private MusicDataBase dataBase;
    private ArrayList<Info> list;
    private MyAdapter adapter;
    private MainActivity activity;
    private int position;
    private SettingsAndPlaylist serviceInfo;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.download_fragment_layout, container, false);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (MainActivity) getActivity();
        dataBase = activity.getDataBase();
        serviceInfo=activity.serviceInfo;
        list = dataBase.getInfo();
        ListView listView = (ListView) getView().findViewById(R.id.downloadList);
        adapter = new MyAdapter(getActivity(), list, true);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final Info info = list.get(position);

                ImageView downloadButton = (ImageView)view.findViewById(R.id.downloadButton);
                downloadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dataBase.deleteInfo(info);
                        list.remove(position);
                        refreshList();
                        Intent pauseIntent = new Intent(getActivity(), MyService.class);
                        pauseIntent.putExtra("command", "pause");
                        getActivity().startService(pauseIntent);
                    }
                });

                Intent pauseIntent = new Intent(getActivity(), MyService.class);
                pauseIntent.putExtra("command", "pause");
                getActivity().startService(pauseIntent);

                Intent serviceIntent = new Intent(getActivity(), MyService.class);
                serviceIntent.putExtra("command", info.getPath_to_file());
                getActivity().startService(serviceIntent);
                serviceInfo.setPlayList(list);
                serviceInfo.setPosition(position);



                ListTask task = new ListTask();
                FragmentTab.this.position=position;
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }
        });


    }


    public void refreshList() {
        list.clear();
        list.addAll(dataBase.getInfo());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (activity.needToRefresh) {
            refreshList();
            activity.needToRefresh = false;
        }
    }




    private class ListTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            Intent listIntent = new Intent("listSender");
            listIntent.putExtra("list", list);
            listIntent.putExtra("position", position);
            getActivity().sendBroadcast(listIntent);
            return null;
        }
    }
}
