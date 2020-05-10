package com.example.musicplayer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.fragments.RecommendFragment;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;

import java.util.ArrayList;
import java.util.List;

public class RecomendListAdapter extends RecyclerView.Adapter<RecomendListAdapter.InnerHolder> {
    private  List<Album> mData= new ArrayList<>();
    private onRecItemClickListener mListener=null;
    @NonNull
    @Override
    public RecomendListAdapter.InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 创建每一个item
        View itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend,parent,false);
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
                if(mListener!=null){
                    int clickPos = (int) v.getTag();
                    mListener.onItemClick(clickPos,mData.get(clickPos));
                }
            }
        });
        holder.setData(mData.get(position));
    }

    @Override
    public int getItemCount() {
        // 返回显示的个数
        if(mData!=null){
            return mData.size();
        }
        return 0;
    }

    // 每次添加数据都很更新
    public void setData(List<Album> albumList){
        if(mData!=null){
            mData.clear();
            mData.addAll(albumList);
        }
        notifyDataSetChanged();//更新UI
    }


    // 内部类
    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(View itemView){
            super(itemView);
        }

        public void setData(Album album) {
            //找到各个控件，设置数据
            ImageView albumCoverPic=itemView.findViewById(R.id.album_cover); //专辑封面
            TextView albumTitle =itemView.findViewById(R.id.album_title); //专辑标题
            TextView albumDesc =itemView.findViewById(R.id.album_description);//专辑描述
            TextView albumPlayCount =itemView.findViewById(R.id.album_play_count);//播放次数
            TextView albumContentSize =itemView.findViewById(R.id.album_content_size); //专辑中内容数量

            albumTitle.setText(album.getAlbumTitle()); //调用sdk中的方法
            albumDesc.setText(album.getAlbumIntro());
            albumPlayCount.setText(album.getPlayCount()+"");
            albumContentSize.setText(album.getIncludeTrackCount()+"");
            Picasso.with(itemView.getContext()).load(album.getCoverUrlLarge()).into(albumCoverPic);
        }

    }
    public interface onRecItemClickListener{
        void onItemClick(int index,Album album);
    }

    public void setOnRecItemClickListener(onRecItemClickListener listener){
        this.mListener=listener;
    }

}
