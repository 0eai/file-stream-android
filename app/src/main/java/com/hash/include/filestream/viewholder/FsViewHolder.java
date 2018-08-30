package com.hash.include.filestream.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hash.include.filestream.R;

public class FsViewHolder extends RecyclerView.ViewHolder {
    public LinearLayout itemLayout;
    public ImageView icon;
    public TextView name;
    public TextView size;
    public ImageButton download;

    public FsViewHolder(View v) {
        super(v);
        itemLayout = v.findViewById(R.id.item_layout);
        icon = v.findViewById(R.id.icon);
        name = v.findViewById(R.id.name);
        size = v.findViewById(R.id.size);
        download = v.findViewById(R.id.download);
    }
}
