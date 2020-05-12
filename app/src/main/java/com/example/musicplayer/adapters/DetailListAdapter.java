package com.example.musicplayer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.Duration;

//适配器
public class DetailListAdapter extends RecyclerView.Adapter<DetailListAdapter.innerHolder> {
    private List<Track> detailData=new ArrayList<>();
    private SimpleDateFormat mDateFormat=new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat mDurationFormat =new SimpleDateFormat("mm:ss");
    @NonNull
    @Override
    //显示UI
    public DetailListAdapter.innerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail,parent,false);
        return new innerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailListAdapter.innerHolder holder, int position) {
        //找到对应控件
        View itemView =holder.itemView;
        TextView detailItemTitle =itemView.findViewById(R.id.detail_item_title);//歌的标题
        TextView detailItemCount =itemView.findViewById(R.id.detail_item_count);//歌的播放量
        TextView detailItemTime =itemView.findViewById(R.id.detail_item_time); //时长
        TextView detailItemDate =itemView.findViewById(R.id.detail_item_date); //日期

        //设置数据
        Track track=detailData.get(position);
        detailItemTitle.setText(track.getTrackTitle());
        detailItemCount.setText(track.getPlayCount()+"");
        String duration =mDurationFormat.format(track.getDuration()*1000);
        detailItemTime.setText(duration);
        String updateTime=mDateFormat.format(track.getUpdatedAt());
        detailItemDate.setText(updateTime);
    }

    @Override
    public int getItemCount() {
        return detailData.size();
    }

    public void setData(List<Track> tracks) {
        detailData.clear();
        detailData.addAll(tracks); //添加数据
        notifyDataSetChanged();//更新UI
    }


    public class innerHolder extends RecyclerView.ViewHolder {
        public innerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
