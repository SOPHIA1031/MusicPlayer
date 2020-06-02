package com.example.musicplayer.api;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.musicplayer.base.BaseApplication;
import com.example.musicplayer.utils.Constants;
import com.example.musicplayer.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

public class HistoryDao implements IHistoryDao {

    private static final String TAG = "HistoryDao";
    private final MusicDBHelper mDbHelper;
    private IHistoryCallback mCallback=null;

    public HistoryDao(){
        mDbHelper=new MusicDBHelper(BaseApplication.getAppContext());

    }
    @Override
    public void setCallback(IHistoryCallback callback) {
        this.mCallback=callback;

    }

    @Override
    public void addHistory(Track track) {
        SQLiteDatabase db=null;
        boolean isSuccess=false;

        try {
            db=mDbHelper.getWritableDatabase();
            db.beginTransaction();
            ContentValues values=new ContentValues();
            //封装数据
            values.put(Constants.HISTORY_TRACK_ID,track.getDataId());
            values.put(Constants.HISTORY_TITLE,track.getTrackTitle());
            values.put(Constants.HISTORY_PLAY_COUNT,track.getPlayCount());
            values.put(Constants.HISTORY_DURATION,track.getDuration());
            values.put(Constants.HISTORY_UPDATE_TIME,track.getUpdatedAt());
            values.put(Constants.HISTORY_COVER,track.getCoverUrlLarge());

            //插入数据
            db.insert(Constants.HISTORY_TB_NAME,null,values);
            db.setTransactionSuccessful();
            isSuccess=true;
        } catch (Exception e) {
            isSuccess=false;
            e.printStackTrace();
        }finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
            mCallback.onHistoryAdd(isSuccess);
        }

    }

    @Override
    public void delHistory(Track track) {
        SQLiteDatabase db = null;
        boolean isDelSuccess=false;
        try {
            db = mDbHelper.getWritableDatabase();
            db.beginTransaction();
            int delete = db.delete(Constants.HISTORY_TB_NAME, Constants.HISTORY_ID+ "=?", new String[]{track.getDataId() + ""});
            LogUtil.d(TAG, "delete -- > " + delete);
            db.setTransactionSuccessful();
            isDelSuccess=true;

        } catch (Exception e) {
            e.printStackTrace();
            isDelSuccess=false;
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
            if (mCallback != null) {
                mCallback.onHistoryDel(isDelSuccess);
            }
        }

    }

    @Override
    public void clearHistory() {
        SQLiteDatabase db = null;
        boolean isDelSuccess=false;
        try {
            db = mDbHelper.getWritableDatabase();
            db.beginTransaction();
            db.delete(Constants.HISTORY_TB_NAME,null,null);
            db.setTransactionSuccessful();
            isDelSuccess=true;

        } catch (Exception e) {
            e.printStackTrace();
            isDelSuccess=false;
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
            if (mCallback != null) {
                mCallback.onHistoryClean(isDelSuccess);
            }
        }

    }

    @Override
    public void listHistory() {
        //从数据表中查出所有的历史纪录
        SQLiteDatabase db=null;

        try {
            db=mDbHelper.getReadableDatabase();
            db.beginTransaction();
            Cursor cursor=db.query(Constants.HISTORY_TB_NAME,null,null,null,null,null,"_id desc");
            List<Track> histories=new ArrayList<>();
            while (cursor.moveToNext()) {
                Track track=new Track();
                int trackId=cursor.getInt(cursor.getColumnIndex(Constants.HISTORY_TRACK_ID));
                track.setDataId(trackId);
                String title=cursor.getString(cursor.getColumnIndex(Constants.HISTORY_TITLE));
                track.setTrackTitle(title);
                int playCount=cursor.getInt(cursor.getColumnIndex(Constants.HISTORY_PLAY_COUNT));
                track.setPlayCount(playCount);
                int duration=cursor.getInt(cursor.getColumnIndex(Constants.HISTORY_COVER));
                track.setDuration(duration);
                long updateTime=cursor.getLong(cursor.getColumnIndex(Constants.HISTORY_UPDATE_TIME));
                track.setUpdatedAt(updateTime);
                String cover=cursor.getString(cursor.getColumnIndex(Constants.HISTORY_COVER));
                track.setCoverUrlLarge(cover);
                track.setCoverUrlMiddle(cover);
                track.setCoverUrlSmall(cover);
                histories.add(track);
            }
            db.setTransactionSuccessful();
            //通知出去
            if (mCallback != null) {
                mCallback.onHistoryLoaded(histories);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }finally {

            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }

    }
}
