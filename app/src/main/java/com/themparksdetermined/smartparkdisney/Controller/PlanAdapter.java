package com.themparksdetermined.smartparkdisney.Controller;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.themparksdetermined.smartparkdisney.Model.PlanItem;
import com.themparksdetermined.smartparkdisney.R;

import java.util.List;

/**
 * Created by Crist on 8/7/2017.
 */

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.holder> {

    private List<PlanItem> listData;
    private LayoutInflater inflater;
    private AssetManager manager;
    private Context context;

    public PlanAdapter(List<PlanItem> listData, Context c){
        this.inflater = LayoutInflater.from(c);
        this.listData = listData;
        this.context = c;
    }

    @Override
    public PlanAdapter.holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.planitem, parent, false);
        manager = view.getContext().getAssets();
        return new holder(view);
    }

    @Override
    public void onBindViewHolder(final PlanAdapter.holder holder, int position) {
        final PlanItem item = listData.get(position);
        Typeface mouse = Typeface.createFromAsset(manager,"font/mouse.ttf");

        holder.title.setText(item.getTitle());
        holder.title.setTypeface(mouse);
        holder.img.setImageResource(item.getImg());
        holder.description.setText(item.getDescription());
        holder.description.setTypeface(mouse);
        holder.time.setText(item.getTime());
        holder.time.setTypeface(mouse);
        holder.time.setTextColor(0xFF4caf50);

        if(position == 0){
            holder.layout.setPadding(20,20,20,20);
            holder.title.setTextSize(36);
            holder.description.setTextSize(30);
            holder.card.setCardElevation(20);
        }

    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public String remove(int position){
        String name = listData.get(position).getTitle();
        listData.remove(position);
        return name;
    }

    public void update(List<PlanItem> items){
        listData.clear();
        listData.addAll(items);
        notifyDataSetChanged();
    }

    class holder extends RecyclerView.ViewHolder{

        private TextView title;
        private ImageView img;
        private TextView description;
        private TextView time;
        private LinearLayout layout;
        private CardView card;

        public holder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.planTitle);
            img = (ImageView) itemView.findViewById(R.id.planImg);
            description = (TextView) itemView.findViewById(R.id.planDescription);
            time = (TextView) itemView.findViewById(R.id.planTime);
            layout = (LinearLayout) itemView.findViewById(R.id.planLayout);
            card = (CardView) itemView.findViewById(R.id.planCard);
        }
    }
}
