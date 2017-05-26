package com.browser.odm.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.browser.R;
import com.browser.odm.ODMDatabase;
import com.browser.odm.ODMItem;
import com.browser.odm.ODMPool;
import com.browser.odm.ODMUtils;
import com.browser.odm.ODMWorker;
import com.browser.ui.dialog.JListDialog;
import com.browser.ui.widget.jlist.JLTV;
import com.browser.ui.widget.jlist.JLTV2;

import java.io.File;

/**
 * Created by ozgur on 07.07.2016.
 */
public class RunningFragment extends BaseFragment {

    /* broadcast receivers */
    protected BroadcastReceiver progressReceiver;
    protected BroadcastReceiver startedReceiver;
    protected BroadcastReceiver finishedReceiver;
    protected BroadcastReceiver cancelledReceiver;
    protected BroadcastReceiver pausedReceiver;
    protected BroadcastReceiver errorReceiver;
    private RunningListAdapter runningListAdapter;

    @Override
    public void fragmentCreated() {
        runningListAdapter = new RunningListAdapter();
        mRecyclerView.setAdapter(runningListAdapter);

        updateDataList();

         /**/
        startedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log("startedReceiver onReceive: " + intent);
                updateDataList();
            }
        };

        progressReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log("progressReceiver onReceive: " + intent);
                updateDataListProgress(intent);
            }
        };

        finishedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log("finishedReceiver onReceive: " + intent);
                snack(R.string.download_finished);
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

        pausedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log("pausedReceiver onReceive: " + intent);
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

    private ODMItem getODMItemByDid(String did) {
        ODMItem odmItem = null;
        for (ODMItem o: dataList) {
            if(o.did.equals(did)) {
                odmItem = o;
                break;
            }
        }
        return odmItem;
    }

    private class RunningListAdapter extends RecyclerView.Adapter<RowHolder> {

        @Override
        public RowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RowHolder(mInflater.inflate(R.layout.dl_running_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(RowHolder holder, int position) {
            final ODMItem item = dataList.get(position);

            final String state = item.state;
            final String did = item.state;

            int dltotal = item.dltotal;
            int filesize = item.filesize;

            /**/
            holder.mFileNameTextView.setText(item.filename /*+ " | " + item.state*/);
            holder.mFileSizeTextView.setText(ODMUtils.getSizeString(dltotal) + "/" + ODMUtils.getSizeString(filesize));

            if(ODMWorker.WORKER_RUNNING.equals(state)) {
                holder.mPauseOrResumeImageView.setVisibility(View.VISIBLE);
                holder.mPauseOrResumeImageView.setImageResource(R.drawable.ic_pause);
            }

            else if(ODMWorker.WORKER_PAUSED.equals(state)) {
                holder.mPauseOrResumeImageView.setVisibility(View.VISIBLE);
                holder.mPauseOrResumeImageView.setImageResource(R.drawable.ic_play);
            }

            else {
                holder.mPauseOrResumeImageView.setVisibility(View.GONE);
            }

            holder.mPauseOrResumeImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Log("mPauseOrResumeImageView click, STATE: " + state);

                    //if it's running, pause it
                    if(ODMWorker.WORKER_RUNNING.equals(state)) {
                        //TODO: alert if it does not support range dl

                        mainActivity.odm.pause(did);
                        updateDataList();
                    }

                    //if it's pausing, resume it
                    if(ODMWorker.WORKER_PAUSED.equals(state)) {
                        mainActivity.odm.resume(did);
                    }
                }
            });

            /**/
            holder.mActionsImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showMenu(item);
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
                getString(R.string.a_cancel),
                getString(R.string.a_delete)
        });
        j.setCancelable(true);
        j.setDialogClickListener(new JListDialog.DialogClickListener() {
            @Override
            public void onClick(String clickedItem, int position) {
                if(position == 0) {
                    mainActivity.odm.cancel(item.did);
                    updateDataList();
                }

                if(position == 1) {
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

        getActivity().registerReceiver(startedReceiver, new IntentFilter(ODMPool.BROADCAST_ACTION_STARTED));
        getActivity().registerReceiver(progressReceiver, new IntentFilter(ODMPool.BROADCAST_ACTION_PROGRESS));
        getActivity().registerReceiver(finishedReceiver, new IntentFilter(ODMPool.BROADCAST_ACTION_FINISHED));
        getActivity().registerReceiver(cancelledReceiver, new IntentFilter(ODMPool.BROADCAST_ACTION_CANCELLED));
        getActivity().registerReceiver(pausedReceiver, new IntentFilter(ODMPool.BROADCAST_ACTION_PAUSED));
        getActivity().registerReceiver(errorReceiver, new IntentFilter(ODMPool.BROADCAST_ACTION_ERROR));
    }

    @Override
    public void onPause() {
        super.onPause();

        getActivity().unregisterReceiver(startedReceiver);
        getActivity().unregisterReceiver(progressReceiver);
        getActivity().unregisterReceiver(finishedReceiver);
        getActivity().unregisterReceiver(cancelledReceiver);
        getActivity().unregisterReceiver(pausedReceiver);
        getActivity().unregisterReceiver(errorReceiver);
    }

    public void updateDataList() {
        //TODO: use async
        dataList = ODMDatabase.getInstance(mainActivity).getRunningDownloadList();

        if(runningListAdapter != null) {
            runningListAdapter.notifyDataSetChanged();
        }

        if(dataList.size() == 0) {
            mRecyclerView.setVisibility(View.GONE);
            mNoDataToSee.show();
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mNoDataToSee.hide();
        }
    }

    private void updateDataListProgress(Intent intent) {
        String did = intent.getStringExtra("did");
        ODMItem odmItem = getODMItemByDid(did);
        //TODO: update all list?

        if(odmItem != null) {
            odmItem.progress = intent.getIntExtra("progress", 0);
            odmItem.dltotal = intent.getIntExtra("dltotal", 0);
            runningListAdapter.notifyItemChanged(dataList.indexOf(odmItem));
        }
    }

    private static class RowHolder extends RecyclerView.ViewHolder {

        public JLTV mFileNameTextView;
        public JLTV2 mFileSizeTextView;
        public ImageView mPauseOrResumeImageView;
        public ImageView mActionsImageView;

        public RowHolder(View itemView) {
            super(itemView);

            mFileNameTextView = (JLTV) itemView.findViewById(R.id.FileNameTextView);
            mFileSizeTextView = (JLTV2) itemView.findViewById(R.id.FileSizeTextView);
            mPauseOrResumeImageView = (ImageView) itemView.findViewById(R.id.PauseOrResumeImageView);
            mActionsImageView = (ImageView) itemView.findViewById(R.id.ActionsImageView);
        }
    }
}
