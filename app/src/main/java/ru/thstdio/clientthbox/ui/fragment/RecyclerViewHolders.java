package ru.thstdio.clientthbox.ui.fragment;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ru.thstdio.clientthbox.R;

/**
 * Created by shcherbakov on 03.02.2018.
 */

public class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView name;
    public ImageView icon;
    public boolean isFolder=false;
    public long id;
    OnClickHolderItem click;

    public interface OnClickHolderItem {
        void onClickHolderItem(long id, String text, boolean isFolder);
    }

    public RecyclerViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        name = (TextView)itemView.findViewById(R.id.name_file);
        icon = (ImageView)itemView.findViewById(R.id.ic_file);
    }

    @Override
    public void onClick(View view) {
       click.onClickHolderItem(id,name.getText().toString(),isFolder);

    }
}