package ru.thstdio.clientthbox.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import ru.thstdio.clientthbox.R;
import ru.thstdio.clientthbox.bus.BusProvider;
import ru.thstdio.clientthbox.bus.event.FolderLoadEvent;
import ru.thstdio.clientthbox.connect.ConnectThBox;
import ru.thstdio.clientthbox.fileutil.PDir;
import ru.thstdio.clientthbox.fileutil.PFile;
import ru.thstdio.clientthbox.ui.MainActivity;
import ru.thstdio.clientthbox.user.ParserJson;

/**
 * Created by shcherbakov on 03.02.2018.
 */

public class FolderView extends Fragment implements RecyclerViewHolders.OnClickHolderItem {

    @BindView(R.id.recycler_view)
    RecyclerView rView;

    @BindDrawable(R.drawable.ic_file)
    Drawable icFile;
    @BindDrawable(R.drawable.ic_file_folder)
    Drawable icFolder;

    Map<Integer,Drawable> icon=new HashMap<Integer,Drawable>();
    PDir root;
    List<PFile> fileItem;

    RecyclerViewAdapter rcAdapter;
    public FragmentCallback callback;

    public void setUpFolder() {
        sendRequest(root.parent);
    }

    public interface FragmentCallback {
        public void setFragmentTitle(String title,boolean isHumburger);
    }
    public static FolderView newInstance(FragmentCallback callback) {

        FolderView fragment = new FolderView();
        fragment.callback=callback;
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sendRequest(0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_folder_view, container, false);
        ButterKnife.bind(this, rootView);
        GridLayoutManager lLayout = new GridLayoutManager(getContext(), 4);
        rView.setHasFixedSize(true);
        rView.setLayoutManager(lLayout);
        icon.put(RecyclerViewAdapter.ICON_FILE,icFile);
        icon.put(RecyclerViewAdapter.ICON_FOLDER,icFolder);
        return rootView;
    }

    @Subscribe
    public void onGetFolder(@NonNull FolderLoadEvent event) {
        try {
            root= ParserJson.parseFolder(event.folderStr);
            createAdapter();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        rView.setAdapter(rcAdapter);
    }

     public void createAdapter(){
        if(root!=null){
            if(fileItem==null)fileItem=new ArrayList<>(); else fileItem.clear();
            if(root.id!=0)callback.setFragmentTitle(root.name,false);
            else callback.setFragmentTitle("",true);
            for(PDir d:root.dirs) fileItem.add(d);
            for(PFile f:root.files) fileItem.add(f);
            rcAdapter = new RecyclerViewAdapter(getContext(), fileItem,icon,this);

        }
     }

    @Override
    public void onClickHolderItem(long id,boolean isFolder) {
        if(isFolder)sendRequest(id);
        Toast.makeText(getContext(), "Clicked ID = " + id, Toast.LENGTH_SHORT).show();
    }
    public void sendRequest(long id){
        Intent intent =new Intent(getContext(),ConnectThBox.class);
        intent.putExtra(ConnectThBox.KEY_COMMAND,ConnectThBox.COMMAND_GET_FOLDER);
        intent.putExtra(ConnectThBox.KEY_FOLDER_ID,id);
        getActivity().startService(intent);
    }

}
