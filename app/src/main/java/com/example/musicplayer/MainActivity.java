package com.example.musicplayer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.category.Category;
import com.ximalaya.ting.android.opensdk.model.category.CategoryList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG="MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Map<String, String> map = new HashMap<>();
        CommonRequest.getCategories(map,new IDataCallBack<CategoryList>(){
            @Override
            public void onSuccess(@Nullable CategoryList categoryList) {
                List<Category> categories= categoryList.getCategories();
                if(categories!= null){
                    int size=categories.size();
                    Log.d(TAG,"categories size ---"+size);
                    for (Category category : categories) {
                        Log.d(TAG,"category ->"+category.getCategoryName());
                    }
                }
            }

            @Override
            public void onError(int code, String message) {
                Log.e(TAG,"ERROR CODE"+code+"ERROR MESSAGE"+message);
            }
        });

    }

}
