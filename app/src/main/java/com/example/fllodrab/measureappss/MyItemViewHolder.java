package com.example.fllodrab.measureappss;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by FllodraB.
 */
public class MyItemViewHolder extends RecyclerView.ViewHolder {

    final TextView tvItem;

    public MyItemViewHolder(View itemView) {
        super(itemView);

        tvItem = (TextView) itemView.findViewById(R.id.tvItem);
    }
}
