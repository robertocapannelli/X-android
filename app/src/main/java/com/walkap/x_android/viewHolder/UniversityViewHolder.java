package com.walkap.x_android.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.walkap.x_android.R;

public class UniversityViewHolder extends RecyclerView.ViewHolder {
    public TextView mTitleView;
    public TextView mCityView;
    //public TextView addressView;

    public UniversityViewHolder( View itemView ){
        super(itemView);

        mTitleView = (TextView) itemView.findViewById(R.id.post_title);
        mCityView = (TextView) itemView.findViewById(R.id.post_city);
        //addressView = (TextView) itemView.findViewById(R.id.post_address);

    }

    public void setName(String name){
        mTitleView.setText(name);
    }

    public void setCity(String city){
        mCityView.setText(city);
    }

}
