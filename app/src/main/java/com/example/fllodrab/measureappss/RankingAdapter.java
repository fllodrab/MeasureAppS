package com.example.fllodrab.measureappss;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


/**
 * Created by FllodraB.
 */
public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.MyViewHolder> {

    private List<MyApp> appList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView year;
        public ImageView icon;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            icon = (ImageView) view.findViewById(R.id.icon);
            year = (TextView) view.findViewById(R.id.year);
        }
    }


    public RankingAdapter(List<MyApp> appList) {
        this.appList = appList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ranking_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MyApp app = appList.get(position);
        holder.title.setText(app.getName());
        holder.icon.setImageDrawable(app.getImgItem());
        holder.year.setText("1");
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }
}
