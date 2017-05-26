package com.browser.ui.dialog;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.browser.R;
import com.browser.theme.themes.ThemeController;
import com.browser.theme.themes.ThemeListener;
import com.browser.theme.themes.ThemeModel;
import com.browser.ui.widget.JTV;
import com.browser.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * Created by ozgur on 21.06.2016.
 */
public class JDirChooserDialog extends JBaseDialog implements ThemeListener {

    private JTV mTitleTextView;
    private JTV mCurrentFolderNameTextView;
    private JDialogButton chooseButton;
    private JDialogButton upButton;
    private ListView mListView;
    private ListAdapter listAdapter;
    private LayoutInflater mInflater;
    private LinearLayout mWrapper;

    public void setDirectoryChooserListener(DirectoryChooserListener directoryChooserListener) {
        this.directoryChooserListener = directoryChooserListener;
    }

    public interface DirectoryChooserListener {
        void onDirChosen(String dir);
    }

    /**/
    private ArrayList<FileHolder> dirs = new ArrayList<>();
    private String mDir = "";

    /**/
    private DirectoryChooserListener directoryChooserListener;

    public JDirChooserDialog(Context context) {
        super(context);

        mInflater = LayoutInflater.from(context);

        /**/
        requestWindowFeature(Window.FEATURE_NO_TITLE);//before content
        setContentView(R.layout.j_dialog_dir_chooser);

        setWH(Utils.dpToPx(getContext(), 300), ViewGroup.LayoutParams.WRAP_CONTENT); //after setContentView

        mTitleTextView = (JTV) findViewById(R.id.TitleTextView);
        mCurrentFolderNameTextView = (JTV) findViewById(R.id.CurrentFolderNameTextView);
        chooseButton = (JDialogButton) findViewById(R.id.ChooseButton);
        upButton = (JDialogButton) findViewById(R.id.UpButton);
        mListView = (ListView) findViewById(R.id.ListView);
        mWrapper = (LinearLayout) findViewById(R.id.Wrapper);

        listAdapter = new ListAdapter();
        mListView.setAdapter(listAdapter);

        ThemeController.getInstance().register(this);
        changeTheme();

        try {
            setCurrentDir(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getCanonicalPath()
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCurrentDir(dirs.get(0).path);
            }
        });

        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(directoryChooserListener != null) {
                    directoryChooserListener.onDirChosen(mDir);
                    hide();
                }
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                setCurrentDir(dirs.get(i).path);
            }
        });
    }

    public void setCurrentDir(String e) {

        //check if root
        if(new File(e).getParentFile() == null) {
            return;
        }

        mDir = e;
        dirs.clear();
        dirs.add(new FileHolder("...", new File(e).getParentFile().getPath()));
        dirs.addAll(getDirectories(mDir));
        mCurrentFolderNameTextView.setText(mDir);
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void themeChanged() {
        changeTheme();
    }

    @Override
    public void changeTheme() {
        ThemeModel t = ThemeController.getInstance().getCurrentTheme();
        mWrapper.setBackgroundColor(t.dialogBg);
        mTitleTextView.setTextColor(t.dialogTitleColor);
        mTitleTextView.setBackgroundColor(t.dialogTitleBg);
    }

    private ArrayList<FileHolder> getDirectories(String dir) {
        ArrayList<FileHolder> dirs = new ArrayList<>();

        File dirFile = new File(dir);
        Log.d("Dirc", "dirFile: " + dirFile);

        File[] files = dirFile.listFiles();

        if(files == null) {
            return dirs;
        }

        for (File file : dirFile.listFiles()) {
            if ( file.isDirectory() ) {
                dirs.add(new FileHolder(file.getName(), file.getPath()));
            }
        }

        Log.d("DirC", "size: " + dirs.size());

        Collections.sort(dirs, new Comparator<FileHolder>() {

            public int compare(FileHolder o1, FileHolder o2) {
                return o1.name.compareTo(o2.name);
            }

        });

        return dirs;
    }

    private class ListAdapter extends BaseAdapter {

        public int getCount() {
            return dirs == null ? 0 : dirs.size();
        }

        @Override
        public FileHolder getItem(int i) {
            return dirs.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if(convertView == null) {
                convertView = mInflater.inflate(R.layout.j_list_dialog_item, parent, false);
            }

            final FileHolder f = getItem(position);

            JTV textView = (JTV) convertView.findViewById(R.id.TextView);
            textView.setText(" " + f.name);

            return convertView;
        }
    }

    private class FileHolder {

        public String name;
        public String path;

        public FileHolder (String name, String path) {
            this.name = name;
            this.path = path;
        }

    }

}
