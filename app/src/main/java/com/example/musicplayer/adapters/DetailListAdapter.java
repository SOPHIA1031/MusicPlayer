package com.example.musicplayer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.fragments.HistoryFragment;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


//适配器
public class DetailListAdapter extends RecyclerView.Adapter<DetailListAdapter.innerHolder> {
    private List<Track> detailData = new ArrayList<>();
    //格式化时间
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat mDurationFormat = new SimpleDateFormat("mm:ss");
    private ItemClickListener itemClickListener = null;
    private ItemLongClickListener mItemLongClickListener=null;

    @NonNull
    @Override
    //显示UI
    public DetailListAdapter.innerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail, parent, false);
        return new innerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailListAdapter.innerHolder holder, int position) {
        //找到对应控件
        final View itemView = holder.itemView;
        TextView detailItemTitle = itemView.findViewById(R.id.detail_item_title);//歌的标题
        TextView detailItemCount = itemView.findViewById(R.id.detail_item_count);//歌的播放量
        TextView detailItemTime = itemView.findViewById(R.id.detail_item_time); //时长
        TextView detailItemDate = itemView.findViewById(R.id.detail_item_date); //日期

        //设置数据
        Track track = detailData.get(position);
        detailItemTitle.setText(track.getTrackTitle());
        detailItemCount.setText(track.getPlayCount() + "");
        String duration = mDurationFormat.format(track.getDuration() * 1000);
        detailItemTime.setText(duration);
        String updateTime = mDateFormat.format(track.getUpdatedAt());
        detailItemDate.setText(updateTime);

        //设置item点击事件
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    // 需要设置列表和位置
                    itemClickListener.onItemClick(detailData, position);
                }
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mItemLongClickListener != null) {
                    mItemLongClickListener.onItemLongClick(track);
                }

                return true;
            }
        });

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

    public void setItemClickListener(ItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void setItemLongClickListener(ItemLongClickListener listener) {
        this.mItemLongClickListener=listener;
    }

    public interface ItemClickListener {
        void onItemClick(List<Track> detailData, int position);
    }

    public interface ItemLongClickListener {
        void onItemLongClick(Track track);
    }

    public class innerHolder extends RecyclerView.ViewHolder {
        public innerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
