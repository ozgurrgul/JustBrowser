package com.browser.odm.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.browser.R;
import com.browser.odm.ODMDatabase;
import com.browser.odm.ODMItem;
import com.browser.odm.ODMPool;
import com.browser.odm.ODMUtils;
import com.browser.ui.dialog.JListDialog;
import com.browser.ui.widget.jlist.JLTV;
import com.browser.ui.widget.jlist.JLTV2;
import com.browser.ui.widget.jlist.JLBG;

import java.io.File;

/**
 * Created by ozgur on 07.07.2016.
 */
public class CompletedFragment extends BaseFragment {

    protected BroadcastReceiver finishedReceiver;
    protected BroadcastReceiver cancelledReceiver;
    protected BroadcastReceiver errorReceiver;
    private CompletedListAdapter completedListAdapter;

    @Override
    public void fragmentCreated() {

        completedListAdapter = new CompletedListAdapter();
        mRecyclerView.setAdapter(completedListAdapter);

        updateDataList();

        finishedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log("finishedReceiver onReceive: " + intent);
                updateDataList();
            }
        };

        cancelledReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log("cancelledReceiver onReceive: " + intent);
                updateDataList();
            }
        };

        errorReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log("errorReceiver onReceive: " + intent);
                updateDataList();
            }
        };


    }

    private class CompletedListAdapter extends RecyclerView.Adapter<RowHolder> {

        @Override
        public RowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RowHolder(mInflater.inflate(R.layout.dl_completed_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(RowHolder holder, int position) {
            final ODMItem item = dataList.get(position);

            if(!TextUtils.isEmpty(item.filename) && item.filename.length() > 25) {
                item.filename = item.filename.substring(0, 24) + "...";
            }

            /**/
            holder.mFileNameTextView.setText(item.filename);
            holder.mFileSizeTextView.setText(ODMUtils.getSizeString(item.filesize));

            /**/
            holder.mActionsImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showMenu(item);
                }
            });

            /**/
            holder.mWrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(!TextUtils.isEmpty(item.path)) {
                        ODMUtils.openFile(new File(item.path), mainActivity);
                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

    }

    private void showMenu(final ODMItem item) {

        JListDialog j = new JListDialog(mainActivity);
        j.setTitle(R.string.menu);
        j.setListItems(new String [] {
            getString(R.string.a_open),
            getString(R.string.a_delete),
            getString(R.string.a_delete_with_file)
        });
        j.setCancelable(true);
        j.setDialogClickListener(new JListDialog.DialogClickListener() {
            @Override
            public void onClick(String clickedItem, int position) {

                if(position == 0) {
                    ODMUtils.openFile(new File(item.path), mainActivity);
                }

                if(position == 1) {
                    mainActivity.odm.delete(item.did);
                    updateDataList();
                }

                if(position == 2) {
                    mainActivity.odm.delete(item.did);
                    new File(item.path).delete();
                    updateDataList();
                }
            }
        });
        j.show();

    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().registerReceiver(finishedReceiver, new IntentFilter(ODMPool.BROADCAST_ACTION_FINISHED));
        getActivity().registerReceiver(cancelledReceiver, new IntentFilter(ODMPool.BROADCAST_ACTION_CANCELLED));
        getActivity().registerReceiver(errorReceiver, new IntentFilter(ODMPool.BROADCAST_ACTION_ERROR));
    }

    @Override
    public void onPause() {
        super.onPause();

        getActivity().unregisterReceiver(finishedReceiver);
        getActivity().unregisterReceiver(cancelledReceiver);
        getActivity().unregisterReceiver(errorReceiver);
    }

    public void updateDataList() {
        dataList = ODMDatabase.getInstance(mainActivity).getCompletedDownloadList(999);

        if(completedListAdapter != null) {
            completedListAdapter.notifyDataSetChanged();
        }

        if(dataList.size() == 0) {
            mRecyclerView.setVisibility(View.GONE);
            mNoDataToSee.show();
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mNoDataToSee.hide();
        }
    }

    private static class RowHolder extends RecyclerView.ViewHolder {

        public JLTV mFileNameTextView;
        public JLTV2 mFileSizeTextView;
        public JLBG mWrapper;
        public ImageView mActionsImageView;

        public RowHolder(View itemView) {
            super(itemView);

            mFileNameTextView = (JLTV) itemView.findViewById(R.id.FileNameTextView);
            mFileSizeTextView = (JLTV2) itemView.findViewById(R.id.FileSizeTextView);
            mActionsImageView = (ImageView) itemView.findViewById(R.id.ActionsImageView);
            mWrapper = (JLBG) itemView.findViewById(R.id.Wrapper);
        }
    }
}
