package com.example.jsh.word.test;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jsh.word.R;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private String[] mDataset1;
    private String[] mDataset2;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewGroup mParent;
        public TextView mTextView1;
        public TextView mTextView2;
        public TextView mTextView3;
        public ViewHolder(ViewGroup vg) {
            super(vg);
            mParent = vg;
            mTextView1 = (TextView)vg.getChildAt(0);
            mTextView2 = (TextView)vg.getChildAt(1);
            mTextView3 = (TextView)vg.getChildAt(2);
        }
    }

    public CustomAdapter(String[] myDataset1, String[] myDataset2) {
        mDataset1 = myDataset1;
        mDataset2 = myDataset2;
    }

    @Override
    public CustomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_custom_view, parent, false);
        ViewHolder vh = new ViewHolder((ViewGroup)((LinearLayout)v));
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextView1.setText(""+(position+1));
        holder.mTextView2.setText(mDataset1[position]);
        holder.mTextView3.setText(mDataset2[position]);
    }

    @Override
    public int getItemCount() {
        return mDataset1.length;
    }

}
