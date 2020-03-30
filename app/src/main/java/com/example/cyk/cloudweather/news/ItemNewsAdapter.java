package com.example.cyk.cloudweather.news;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.cyk.cloudweather.R;
import com.example.cyk.cloudweather.bean.NewsBean;
import com.example.cyk.cloudweather.util.ResolutionUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class ItemNewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<NewsBean.Bean> objects = new ArrayList<NewsBean.Bean>();

    private Context context;

    public ItemNewsAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<NewsBean.Bean> objects) {
        this.objects = objects;
    }

    public void addData(List<NewsBean.Bean> newObjects) {
        objects.addAll(newObjects);
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_item_type_one, parent, false);
            return new ItemNewsHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.footer, parent, false);
            return new FooterHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemNewsHolder) {
            final NewsBean.Bean bean = objects.get(position);
            if (bean == null) {
                return;
            }
            int widthPixels=context.getResources().getDisplayMetrics().widthPixels;
            int width = widthPixels;
            int height = ResolutionUtil.dipToPx(context, 200);
            Picasso.get()
                    .load(bean.getImgsrc())
                    .error(R.drawable.img_error)
                    .resize(width,height)
                    .placeholder(R.drawable.loads)
                    .centerCrop()
                    .into(((ItemNewsHolder) holder).ivNewsImg);
//            Glide.with(context)
//                    .load(bean.getImgsrc())
//                    .error(R.drawable.img_error)
//                    .placeholder(R.drawable.loads)
//                    .override(width,height)
//                    .centerCrop()
//                    .into(((ItemNewsHolder) holder).ivNewsImg);

//            if (position == 0) {
//                ((ItemNewsHolder) holder).tvNewsDigest.setVisibility(View.GONE);
//                ((ItemNewsHolder) holder).tvNewsTitle.setText("图片：" + bean.getTitle());
//            } else {
//                ((ItemNewsHolder) holder).tvNewsTitle.setText(bean.getTitle());
//                ((ItemNewsHolder) holder).tvNewsDigest.setText(bean.getMtime() + " : " + bean.getDigest());
//                ((ItemNewsHolder) holder).cvNews.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(context, NewDetailActivity.class);
//                        intent.putExtra("url", bean.getUrl());
//                        intent.putExtra("title", bean.getSource());
//                        context.startActivity(intent);
//                    }
//                });
//            }

            ((ItemNewsHolder) holder).tvNewsTitle.setText(bean.getTitle());
            ((ItemNewsHolder) holder).tvNewsSource.setText(bean.getSource());
            ((ItemNewsHolder) holder).recylerview_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, NewDetailActivity.class);
                    intent.putExtra("url", bean.getUrl());
                    intent.putExtra("title", bean.getTitle());
                    context.startActivity(intent);
                }
            });
        }
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    protected class ItemNewsHolder extends RecyclerView.ViewHolder {
        private ImageView ivNewsImg;
        private TextView tvNewsTitle;
        private TextView tvNewsDigest;
        private TextView tvNewsSource;
        private TextView tvNewsTime;
        private CardView cvNews;
        private LinearLayout recylerview_ll;

        public ItemNewsHolder(View view) {
            super(view);
//            ivNewsImg = (ImageView) view.findViewById(R.id.iv_news_img);
//            tvNewsTitle = (TextView) view.findViewById(R.id.tv_news_title);
//            tvNewsDigest = (TextView) view.findViewById(R.id.tv_news_digest);
//            cvNews = (CardView) view.findViewById(R.id.cv_news);

            ivNewsImg = (ImageView) view.findViewById(R.id.news_img);
            tvNewsTitle = (TextView) view.findViewById(R.id.news_title);
            tvNewsSource = (TextView) view.findViewById(R.id.news_source);
            tvNewsTime  = (TextView) view.findViewById(R.id.news__time);
            recylerview_ll = (LinearLayout) view.findViewById(R.id.recylerview_ll);
        }
    }

    protected class FooterHolder extends RecyclerView.ViewHolder {

        public FooterHolder(View itemView) {
            super(itemView);
        }
    }
}
