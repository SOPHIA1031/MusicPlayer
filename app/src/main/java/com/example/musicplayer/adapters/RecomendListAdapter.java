package com.example.musicplayer.adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.fragments.RecommendFragment;
import com.example.musicplayer.utils.LogUtil;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.List;

//连接数据与展示View的中介，通知RecylerView绘制多少View，每条View怎么绘制，View内部事件如何响应
public class RecomendListAdapter extends RecyclerView.Adapter<RecomendListAdapter.InnerHolder> {
    private List<Album> mData = new ArrayList<>();
    private onRecItemClickListener mListener = null;
    private String TAG = "RecommendList:";

    @NonNull
    @Override
    public RecomendListAdapter.InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 创建每一个item
        View itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend, parent, false);
        return new InnerHolder(itemview);
    }

    @Override
    public void onBindViewHolder(@NonNull RecomendListAdapter.InnerHolder holder, int position) {
        //设置数据
        holder.itemView.setTag(position);  //每一个位置的item设置一个tag
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // v.getTag()获得被点击的view的索引
                if (mListener != null) {
                    int clickPos = (int) v.getTag();
                    mListener.onItemClick(clickPos, mData.get(clickPos));//RecommendFragment中重写
                }
            }
        });
        holder.setData(mData.get(position));  //根据index设置数据
    }

    @Override
    public int getItemCount() {
        // 返回显示的个数
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    // 每次添加数据都很更新
    public void setData(List<Album> albumList) {
        if (mData != null) {
            mData.clear();
            mData.addAll(albumList);
        }
        //更新UI
        notifyDataSetChanged();
    }

    public void setOnRecItemClickListener(onRecItemClickListener listener) {
        this.mListener = listener;
    }


    public interface onRecItemClickListener {
        void onItemClick(int index, Album album);
    }

    // 内部类
    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(View itemView) {
            super(itemView);
        }

        public void setData(Album album) {
            //找到各个控件，设置数据
            ImageView albumCoverPic = itemView.findViewById(R.id.album_cover); //专辑封面
            TextView albumTitle = itemView.findViewById(R.id.album_title); //专辑标题
            TextView albumDesc = itemView.findViewById(R.id.album_description);//专辑描述
            TextView albumPlayCount = itemView.findViewById(R.id.album_play_count);//播放次数
            TextView albumContentSize = itemView.findViewById(R.id.album_content_size); //专辑中内容数量

            if (album.getAlbumIntro().equals("")) {
                albumDesc.setText("暂无简介");
            } else {
                albumDesc.setText(album.getAlbumIntro());
            }
            albumTitle.setText(album.getAlbumTitle()); //调用sdk中的方法
            albumPlayCount.setText(album.getPlayCount() + "");
            albumContentSize.setText(album.getIncludeTrackCount() + "");
            String coverUrlLarge = album.getCoverUrlLarge();
            if (!TextUtils.isEmpty(coverUrlLarge)) {
                Picasso.with(itemView.getContext()).load(coverUrlLarge).into(albumCoverPic);
            }else{
                albumCoverPic.setImageResource(R.mipmap.logo);
            }
        }

    }

}
