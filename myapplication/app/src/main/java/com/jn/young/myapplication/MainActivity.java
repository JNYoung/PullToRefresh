package com.jn.young.myapplication;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jn.young.pulltorefresh.PtrFrame;
import com.jn.young.pulltorefresh.utils.PtrObserver;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<String> mDatas;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final PtrFrame frame = (PtrFrame) findViewById(R.id.ptrframe);
        frame.setObserver(new PtrObserver() {
            @Override
            public boolean canPull() {
                return true;
            }

            @Override
            public boolean stopFetchOps() {
                return false;
            }

            @Override
            public void onReset() {

            }

            @Override
            public void onRefreshing() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        frame.onFetchComplete("刷新成功");
                    }
                }, 1000);
            }

            @Override
            public void onPullToRefresh() {

            }

        });
        initData();
        mRecyclerView = (RecyclerView)findViewById(R.id.id_recyclerview);
        mRecyclerView.setAdapter(new HomeAdapter());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.refresh_without_anim).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frame.setRefresh(false, false);
            }
        });

        findViewById(R.id.refresh_with_anim).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frame.setRefresh(true, false);
            }
        });
        TempDoubleDragHeader header = new TempDoubleDragHeader(this);
        frame.setHeader(header);
    }

    protected void initData() {
        mDatas = new ArrayList<String>();
        for (int i = 'A'; i < 'z'; i++) {
            mDatas.add("" + (char) i);
        }
    }


    class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                    MainActivity.this).inflate(R.layout.item_home, parent,
                    false));
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.tv.setText(mDatas.get(position));
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView tv;

            public MyViewHolder(View view) {
                super(view);
                tv = (TextView) view.findViewById(R.id.id_num);
            }
        }
    }
}

