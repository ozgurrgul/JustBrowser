package com.browser.dial;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

public class DialViewAdapter extends RecyclerView.Adapter<DialViewAdapter.MyViewHolder> implements DraggableItemAdapter<DialViewAdapter.MyViewHolder> {

    private DialView dialView;
    private DialItems dialItems;

    /* data */
    private boolean isEditing = false;

    public static class MyViewHolder extends AbstractDraggableItemViewHolder {

        public DialViewUrlItem dialViewUrlItem;

        public MyViewHolder(View v) {
            super(v);
            dialViewUrlItem = (DialViewUrlItem) v;
        }
    }

    public DialViewAdapter(DialView dialView) {
        this.dialView = dialView;
        setHasStableIds(true);
        dialItems = dialView.uiController.getDialItems();
    }

    @Override
    public long getItemId(int position) {
        return dialItems.get(position).getId();
    }

    @Override
    public int getItemViewType(int position) {

        if(dialItems.get(position).isPlusButton()) {
            return DialItem.PLUS;
        }

        return DialItem.URL;
    }

    @Override
    public int getItemCount() {
        return dialItems.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
         return new MyViewHolder(new DialViewUrlItem(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        final DialItem itemData = dialItems.get(position);
        holder.dialViewUrlItem.setItemData(itemData);

        if(isEditing && !itemData.isPlusButton()) {
            holder.dialViewUrlItem.showRemoveButton();
        }

        if(!isEditing && !itemData.isPlusButton()) {
            holder.dialViewUrlItem.hideRemoveButton();
        }

        holder.dialViewUrlItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isEditing) {
                    return;
                }

                if(itemData.isPlusButton()) {
                    //open add url stuff
                    dialView.uiController.getDialogManager().addSpeedDial(null, null, null);
                } else {

                    dialView.uiController.getTab().fresh();
                    dialView.uiController.loadUrl(itemData.getUrl(), true);
                }
            }
        });

        holder.dialViewUrlItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                //show remove icons and "close" button in bottom
                if(!isEditing) {
                    dialView.uiController.enterDialEditMode();
                }

                return true;
            }
        });

        holder.dialViewUrlItem.mRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialItems.delete(itemData);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {

        if(isPlusButton(fromPosition) || isPlusButton(toPosition))
            return;

        if (fromPosition == toPosition) {
            return;
        }

        DialItem touching = dialItems.get(fromPosition);
        DialItem target = dialItems.get(toPosition);

        dialItems.swap(touching, target);

        notifyDataSetChanged();
    }

    private void Log(String e) {
        Log.d(getClass().getSimpleName(), e);
    }

    @Override
    public boolean onCheckCanStartDrag(MyViewHolder holder, int position, int x, int y) {

        Log("onCheckCanStartDrag");

        if(isPlusButton(position)) {// it's plus icon, do not allow move
            return false;
        }

        return true;
    }

    private boolean isPlusButton(int pos) {
        return dialItems.get(pos).isPlusButton();
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(MyViewHolder holder, int position) {
        // no drag-sortable range specified
        return null;
    }

    public void enterDialEditMode() {
        isEditing = true;
        dialView.mRecyclerViewDragDropManager.setLongPressTimeout(DialView.LONG_PRESS_TIMEOUT_WHEN_EDITING);
        notifyDataSetChanged();
    }

    public void exitDialEditMode() {
        isEditing = false;
        dialView.mRecyclerViewDragDropManager.setLongPressTimeout(DialView.LONG_PRESS_TIMEOUT);
        notifyDataSetChanged();
    }

    @Override
    public boolean onCheckCanDrop(int draggingPosition, int dropPosition) {
        return true;
    }


    public boolean onBackPressed() {

        if(isEditing) {
            dialView.uiController.exitDialEditMode();
            return true;
        }

        return false;
    }

}