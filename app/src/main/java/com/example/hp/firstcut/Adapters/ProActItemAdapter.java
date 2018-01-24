package com.example.hp.firstcut.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hp.firstcut.R;

import java.util.ArrayList;

import static com.example.hp.firstcut.ProjectActivity.fab;

/**
 * Created by HP on 1/24/2018.
 */

public class ProActItemAdapter extends RecyclerView.Adapter<ProActItemAdapter.ViewHolder>
{
    private int listItemLayout;
    private ArrayList<ProjectActivityAdapter> list1;
    public ProActItemAdapter.MyClickListener2 myClickListener1;
    public ProActItemAdapter(int listlayout, ArrayList<ProjectActivityAdapter> tl)
    {
        this.listItemLayout=listlayout;
        this.list1=tl;
    }
    @Override
    public ProActItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(listItemLayout, parent, false);
        final ProActItemAdapter.ViewHolder myViewHolder = new ProActItemAdapter.ViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(ProActItemAdapter.ViewHolder holder, int position)
    {

        holder.textView.setText(list1.get(position).getActivities());
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
                    myClickListener1.onItemClick(getAdapterPosition(), view);

                }
            });


        }

    }
    public interface MyClickListener2
    {
        void onItemClick(int position, View v);
    }
    public void setOnItemClickListener2(ProActItemAdapter.MyClickListener2 myClickListener) {
        this.myClickListener1 =  myClickListener;
    }


}
