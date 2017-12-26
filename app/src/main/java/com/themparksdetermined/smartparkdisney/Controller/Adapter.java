package com.themparksdetermined.smartparkdisney.Controller;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;

import com.themparksdetermined.smartparkdisney.Model.ListItem;
import com.themparksdetermined.smartparkdisney.R;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Crist on 8/2/2017.
 */

public class Adapter extends RecyclerView.Adapter<Adapter.holder>  {

    private List<ListItem> listData;
    private LayoutInflater inflater;
    private AssetManager manager;
    private Context context;

    public Adapter(List<ListItem> listData, Context c){
        this.inflater = LayoutInflater.from(c);
        this.listData = listData;
        this.context = c;
    }

    @Override
    public holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.listitem, parent, false);
        manager = view.getContext().getAssets();
        return new holder(view);
    }

    @Override
    public void onBindViewHolder(holder holder, int position) {
        Typeface mouseFont = Typeface.createFromAsset(manager,"font/mouse.ttf");
        final ListItem item = listData.get(position);
        final String name = item.getNameOfRide();
        final LinearLayout layout = holder.layout;
        holder.nameOfRide.setText(item.getNameOfRide());
        holder.nameOfRide.setTypeface(mouseFont);

        holder.waitTime.setText(item.getWaitTime() + " minute wait");
        holder.waitTime.setTypeface(mouseFont);

        holder.rideImg.setImageResource(item.getRideImg());

        holder.status.setText(item.getStatus());
        holder.status.setTypeface(mouseFont);

        if(item.isOpen()){
            holder.status.setTextColor(0xFF4caf50);
        }
        else{
            holder.status.setTextColor(0xFFc62828);
        }
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public void update(List<ListItem> items, boolean listClosed, String sortBy, boolean ascending){

        switch(sortBy){
            case "Name":
                if(ascending) {
                    Collections.sort(items, new Comparator<ListItem>() {
                        @Override
                        public int compare(ListItem o1, ListItem o2) {
                            if(!o1.isOpen() && o2.isOpen()) return 1;
                            if(!o2.isOpen() && o1.isOpen()) return -1;
                            return o1.getNameOfRide().compareToIgnoreCase(o2.getNameOfRide());
                        }
                    });
                }else{
                    Collections.sort(items, new Comparator<ListItem>() {
                        @Override
                        public int compare(ListItem o1, ListItem o2) {
                            if(!o1.isOpen() && o2.isOpen()) return 1;
                            if(!o2.isOpen() && o1.isOpen()) return -1;
                            return o2.getNameOfRide().compareToIgnoreCase(o1.getNameOfRide());
                        }
                    });
                }
                break;
            case "Wait":
                if(ascending) {
                    Collections.sort(items, new Comparator<ListItem>() {
                        @Override
                        public int compare(ListItem o1, ListItem o2) {
                            if(!o1.isOpen() && o1.isOpen()) return 1;
                            if(!o2.isOpen() && o2.isOpen()) return -1;
                            int wait1 = Integer.parseInt(o1.getWaitTime());
                            int wait2 = Integer.parseInt(o2.getWaitTime());

                            return wait2 - wait1;

                        }
                    });
                }else{
                    Collections.sort(items, new Comparator<ListItem>() {
                        @Override
                        public int compare(ListItem o1, ListItem o2) {
                            if(!o1.isOpen() && o1.isOpen()) return 1;
                            if(!o2.isOpen() && o2.isOpen()) return -1;
                            int wait1 = Integer.parseInt(o1.getWaitTime());
                            int wait2 = Integer.parseInt(o2.getWaitTime());

                            return wait1 - wait2;
                        }
                    });
                }
                break;
        }
        if(items!= null && items.size() > 0){
            listData.clear();
            if(!listClosed) {
                for(int i = 0; i < items.size(); i++) {
                    String stat = items.get(i).getStatus();
                    if (stat.equals("Open")) {
                        listData.add(items.get(i));
                    }
                }
            }else{
                listData.addAll(items);
            }
            notifyDataSetChanged();
        }
    }
    class holder extends RecyclerView.ViewHolder{
        private TextView nameOfRide;
        private TextView waitTime;
        private ImageView rideImg;
        private TextView status;
        private LinearLayout layout;
        private boolean open;

        public holder(View itemView){
            super(itemView);

            nameOfRide = (TextView) itemView.findViewById(R.id.name);
            waitTime   = (TextView) itemView.findViewById(R.id.wait);
            status     = (TextView) itemView.findViewById(R.id.status);
            rideImg    = (ImageView)itemView.findViewById(R.id.icon);
            layout     = (LinearLayout) itemView.findViewById(R.id.cont_item_root);
        }
    }
}
