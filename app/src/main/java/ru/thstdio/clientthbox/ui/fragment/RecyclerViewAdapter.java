package ru.thstdio.clientthbox.ui.fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Map;

import ru.thstdio.clientthbox.R;
import ru.thstdio.clientthbox.fileutil.PDir;
import ru.thstdio.clientthbox.fileutil.PFile;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolders> {
    public static final int ICON_FILE = 1;
    public static final int ICON_FOLDER = 0;

    private List<PFile> itemList;
    private Context context;
    Map<Integer, Drawable> icon;
    RecyclerViewHolders.OnClickHolderItem onClickHolderItem;


    public RecyclerViewAdapter(Context context, List<PFile> itemList, Map<Integer, Drawable> icon,RecyclerViewHolders.OnClickHolderItem onClickHolderItem) {
        this.itemList = itemList;
        this.context = context;
        this.icon = icon;
        this.onClickHolderItem=onClickHolderItem;
    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, null);
        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolders holder, int position) {

        if (itemList.get(position) instanceof PDir) {
            holder.icon.setImageDrawable(icon.get(ICON_FOLDER));
            holder.isFolder=true;
        } else {

            holder.icon.setImageDrawable(icon.get(ICON_FILE));
        }
        holder.name.setText(itemList.get(position).name);
        holder.click=onClickHolderItem;
        holder.id=itemList.get(position).id;
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }
}