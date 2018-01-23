package com.example.hp.firstcut.Adapters;

/**
 * Created by HP on 1/23/2018.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hp.firstcut.R;

import java.util.ArrayList;

/**
 * Created by HP on 12/29/2017.
 */

public class ProItemAdapter extends RecyclerView.Adapter<ProItemAdapter.ViewHolder>
{
    private int listItemLayout;
    private ArrayList<Project> list1;
    public MyClickListener myClickListener;
    public ProItemAdapter(int listlayout, ArrayList<Project> tl)
    {
        this.listItemLayout=listlayout;
        this.list1=tl;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(listItemLayout, parent, false);
        final ViewHolder myViewHolder = new ViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {

        holder.textView.setText(list1.get(position).getPro_name());
    }

    @Override
    public int getItemCount()
    {
        return list1==null?0:list1.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder
    {

        TextView textView;
        public ViewHolder(View view)
        {
            super(view);
            textView=(TextView)view.findViewById(R.id.proname);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    myClickListener.onItemClick(getAdapterPosition(), view);
                }
            });


        }

    }
    public interface MyClickListener
    {
        void onItemClick(int position, View v);
    }
    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }





}