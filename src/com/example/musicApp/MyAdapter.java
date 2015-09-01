package com.example.musicApp;

import android.content.ClipData;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick_dnepr on 18.05.2015.
 */
public class MyAdapter extends ArrayAdapter<Info> {


    private boolean listBoolean;
    public MyAdapter(Context context, List<Info> objects, boolean  listBoolean) {
        super(context, 0, objects);
        this.listBoolean=listBoolean;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(R.layout.item, parent, false);
        }

        Info item = getItem(position);

        TextView title = (TextView)convertView.findViewById(R.id.title);
        TextView likes = (TextView)convertView.findViewById(R.id.likes_string);
        TextView duration = (TextView)convertView.findViewById(R.id.time_string);
        TextView avtor = (TextView)convertView.findViewById(R.id.avtor);
        ImageView downloadButton = (ImageView)convertView.findViewById(R.id.downloadButton);


        if(listBoolean){
            downloadButton.setImageResource(R.drawable.ic_highlight_off_white_48dp);
        }
//        Log.i("adapterInfo", item.getTitle());
//        Log.i("adapterInfo", item.getLikes_count()+"");
//        Log.i("adapterInfo", item.getDuration()+"");
//        Log.i("adapterInfo", item.getUser().getUsername());


        title.setText(item.getTitle());
        likes.setText(String.valueOf(item.getLikes_count()));
        duration.setText(generateString(item.getDuration()));
        avtor.setText(item.getUser().getUsername());

        return convertView;
    }

    private String generateString(int duration) {
        String answer;
        answer = duration/1000/60 + ":"+ duration/1000%60;
        return answer;
    }
}
