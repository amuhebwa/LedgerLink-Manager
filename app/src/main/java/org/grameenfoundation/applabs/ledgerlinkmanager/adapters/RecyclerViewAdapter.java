package org.grameenfoundation.applabs.ledgerlinkmanager.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.grameenfoundation.applabs.ledgerlinkmanager.R;
import org.grameenfoundation.applabs.ledgerlinkmanager.models.VslaInfo;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    ArrayList<VslaInfo> vslaInfo;
    OnItemClickListener mItemClickListener;

    public RecyclerViewAdapter(ArrayList<VslaInfo> vslaInfo) {
        this.vslaInfo = vslaInfo;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        VslaInfo dataSet = vslaInfo.get(position);
        holder.vslaName.setText(dataSet.getGroupName());
        holder.physicalAddress.setText(dataSet.getPhysicalAddress());
        holder.responsiblePerson.setText(dataSet.getMemberName());
        holder.uploadDataIcon.setImageResource(dataSet.getUploadDataIcon());


    }

    @Override
    public int getItemCount() {
        return vslaInfo.size();
    }

    /**  Class to load the UI components */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView vslaName;
        public TextView physicalAddress;
        public TextView responsiblePerson;
        private ImageView uploadDataIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            vslaName = (TextView) itemView.findViewById(R.id._vslaName);
            physicalAddress = (TextView) itemView.findViewById(R.id._physicalAddress);
            responsiblePerson = (TextView) itemView.findViewById(R.id._responsiblePerson);
            uploadDataIcon = (ImageView) itemView.findViewById(R.id.uploadDataIcon);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClickListener(v, getPosition());
            }
        }
    }

    /**
     * Interface
     */
    public interface OnItemClickListener {
        void onItemClickListener(View view, int position);
    }
    /** Set on click listener */
    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
}
