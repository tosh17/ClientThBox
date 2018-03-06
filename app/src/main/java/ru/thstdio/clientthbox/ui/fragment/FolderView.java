package ru.thstdio.clientthbox.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import ru.thstdio.clientthbox.R;
import ru.thstdio.clientthbox.bus.BusProvider;
import ru.thstdio.clientthbox.bus.event.FolderLoadEvent;
import ru.thstdio.clientthbox.bus.event.RemoveFileEvent;
import ru.thstdio.clientthbox.connect.ConnectThBox;
import ru.thstdio.clientthbox.fileutil.FileType;
import ru.thstdio.clientthbox.fileutil.PDir;
import ru.thstdio.clientthbox.fileutil.PFile;
import ru.thstdio.clientthbox.user.ParserJson;

/**
 * Created by shcherbakov on 03.02.2018.
 */

public class FolderView extends Fragment implements RecyclerViewHolders.OnClickHolderItem {

    private static final int COLUMNS_COUNT = 3;
    @BindView(R.id.recycler_view)
    RecyclerView rView;

    @BindDrawable(R.drawable.ic_file)
    Drawable icFile;
    @BindDrawable(R.drawable.ic_file_folder)
    Drawable icFolder;
    @BindDrawable(R.drawable.ic_file_txt)
    Drawable icFileTxt;
    @BindDrawable(R.drawable.ic_file_movies)
    Drawable icFileMovies;
    @BindDrawable(R.drawable.ic_file_audio)
    Drawable icFileAudio;
    @BindDrawable(R.drawable.ic_file_pic)
    Drawable icFilePic;
    @BindDrawable(R.drawable.ic_file_arc)
    Drawable icFileArc;

    Map<FileType, Drawable> icon = new HashMap<FileType, Drawable>();
    PDir root;
    List<PFile> fileItem;
    Set<Long> checkedFile = new HashSet<>();
    RecyclerViewAdapter rcAdapter;
    public FragmentCallback callback;

    public void setUpFolder() {
        sendRequest(root.parent);
    }

    public interface FragmentCallback {
        public void setFragmentTitle(PDir title);
    }

    public static FolderView newInstance(FragmentCallback callback) {

        FolderView fragment = new FolderView();
        fragment.callback = callback;
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
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_folder_view, container, false);
        ButterKnife.bind(this, rootView);
        GridLayoutManager lLayout = new GridLayoutManager(getContext(), COLUMNS_COUNT);
        rView.setHasFixedSize(true);
        rView.setLayoutManager(lLayout);
        icon.put(FileType.None, icFile);
        icon.put(FileType.Folder, icFolder);
        icon.put(FileType.Txt, icFileTxt);
        icon.put(FileType.Video, icFileMovies);
        icon.put(FileType.Image, icFilePic);
        icon.put(FileType.Audio, icFileAudio);
        icon.put(FileType.Arc, icFileArc);
        return rootView;
    }

    @Subscribe
    public void onGetFolder(@NonNull FolderLoadEvent event) {
        try {
            root = ParserJson.parseFolder(event.folderStr);
            createAdapter();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        rView.setAdapter(rcAdapter);
    }

    @Subscribe
    public void onRemoveFiles(@NonNull RemoveFileEvent event) {
        List<PFile> toRemove = new LinkedList<>();
        for (PFile f : fileItem) if (checkedFile.contains(f.id)) toRemove.add(f);
        fileItem.removeAll(toRemove);
        checkedFile.clear();
        rcAdapter = new RecyclerViewAdapter(getContext(), fileItem, icon, this);
        rView.setAdapter(rcAdapter);
    }

    public void createAdapter() {
        if (root != null) {
            if (fileItem == null) fileItem = new ArrayList<>();
            else fileItem.clear();
            callback.setFragmentTitle(root);

            for (PDir d : root.dirs) fileItem.add(d);
            for (PFile f : root.files) fileItem.add(f);
            rcAdapter = new RecyclerViewAdapter(getContext(), fileItem, icon, this);
        }
    }

    @Override
    public void onClickHolderItem(long id, String name, boolean isFolder) {
        if (isFolder) {
            sendRequest(id);
            checkedFile.clear();
        } else loadFile(id, name);
        Toast.makeText(getContext(), "Clicked ID = " + id, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCheckHolderItem(long id, boolean isChecked) {
        if (isChecked) checkedFile.add(id);
        else checkedFile.remove(id);
    }

    private void loadFile(long id, String name) {
        Intent intent = new Intent(getContext(), ConnectThBox.class);
        intent.putExtra(ConnectThBox.KEY_COMMAND, ConnectThBox.COMMAND_DOWNLOAD_FILE);
        intent.putExtra(ConnectThBox.KEY_FILE_ID, id);
        intent.putExtra(ConnectThBox.KEY_FILE_NAME, name);
        getActivity().startService(intent);
    }

    public void sendRequest(long id) {
        Intent intent = new Intent(getContext(), ConnectThBox.class);
        intent.putExtra(ConnectThBox.KEY_COMMAND, ConnectThBox.COMMAND_GET_FOLDER);
        intent.putExtra(ConnectThBox.KEY_FOLDER_ID, id);
        getActivity().startService(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.folder_view_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_folder:
                createFolder();
                break;
            case R.id.action_del:
                deleteCheckFiles();
                break;
        }

        return super.onOptionsItemSelected(item);

    }

    private void deleteCheckFiles() {
        Intent intent = new Intent(getContext(), ConnectThBox.class);
        intent.putExtra(ConnectThBox.KEY_COMMAND, ConnectThBox.COMMAND_DELETE_FILE);
        long[] ids = new long[checkedFile.size()];
        int i = 0;
        for (Long l : checkedFile) ids[i++] = l;
        intent.putExtra(ConnectThBox.KEY_FILE_ID, ids);
        getActivity().startService(intent);
    }

    void createFolder() {
        AlertDialog.Builder ad = new AlertDialog.Builder(getContext());
        final EditText nameFolder = new EditText(getContext());
        ad.setView(nameFolder);
        ad.setMessage(getString(R.string.dialog_create_folder)); // сообщение
        ad.setPositiveButton(getString(R.string.dialog_create_folder_button_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String text = nameFolder.getText().toString();
                Intent intent = new Intent(getContext(), ConnectThBox.class);
                intent.putExtra(ConnectThBox.KEY_COMMAND, ConnectThBox.COMMAND_CREATE_FOLDER);
                intent.putExtra(ConnectThBox.KEY_STR, text);
                intent.putExtra(ConnectThBox.KEY_FOLDER_ID, root.id);
                getActivity().startService(intent);
            }
        }).setNegativeButton(getString(R.string.dialog_create_folder_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        ad.show();


    }
}
