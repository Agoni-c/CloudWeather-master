package com.example.cyk.cloudweather.video;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cyk.cloudweather.R;
import com.example.cyk.cloudweather.bean.TodayContentBean;
import com.example.cyk.cloudweather.video.presenter.IVideoPresenter;
import com.example.cyk.cloudweather.video.presenter.VideoPresenter;
import com.example.cyk.cloudweather.video.view.IVideoView;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class VideoFragment extends Fragment  implements IVideoView {

    private IVideoPresenter iVideoPresenter;
    private RecyclerView rv_video;
    private ItemVideoAdapter itemVideoAdapter;
    private SwipeRefreshLayout srl_video;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video,container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        iVideoPresenter = new VideoPresenter(this);
        rv_video = view.findViewById(R.id.rv_video);
        srl_video = view.findViewById(R.id.srl_video);
        srl_video.setColorSchemeColors(Color.parseColor("#ffce3d3a"));
        iVideoPresenter.loadVideo();
        srl_video.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                iVideoPresenter.loadVideo();
            }
        });
        itemVideoAdapter = new ItemVideoAdapter(getActivity());
    }

    @Override
    public void showVideo(List<TodayContentBean> todayContentBeans, List<String> videoList) {
        itemVideoAdapter.setData(todayContentBeans, videoList);
        rv_video.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));
        rv_video.setAdapter(itemVideoAdapter);
    }

    @Override
    public void hideDialog() {
        srl_video.setRefreshing(false);
    }

    @Override
    public void showDialog() {
        srl_video.setRefreshing(true);
    }

    @Override
    public void showErrorMsg(Throwable throwable) {
        Toast.makeText(getContext(), "加载出错:"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
    }
}