package com.example.cyk.cloudweather.news;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cyk.cloudweather.R;
import com.example.cyk.cloudweather.bean.HeadlineNewsBean;
import com.example.cyk.cloudweather.bean.NewsBean;
import com.example.cyk.cloudweather.util.ResolutionUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class ItemNewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<NewsBean> objects = new ArrayList<NewsBean>();

    private Context context;

    public ItemNewsAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<NewsBean> objects) {
        this.objects = objects;
    }

    public void addData(List<NewsBean> newObjects) {
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

    /**
     * @param parent:ViewGroup是将保存您要创建的单元格的父级视图。因此，ViewGroup父级是此处的RecyclerView（它将保存您的单元格）。
     *              在布局膨胀过程中使用了父对象，因此您可以看到它传递给膨胀调用。
     * @param viewType:如果列表中的单元格类型不同，则viewType很有用。例如，如果您具有标题单元格和详细信息单元格。您可以使用viewType
     *                来确保为这两种类型的单元格中的每一个都填充正确的布局文件。
     *
     * @return
     */
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
            final NewsBean data = objects.get(position);
            if (data == null) {
                return;
            }
            int widthPixels=context.getResources().getDisplayMetrics().widthPixels;
            int width = widthPixels;
            int height = ResolutionUtil.dipToPx(context, 200);
            Picasso.get()
                    .load(data.getThumbnail_pic_s())
                    .error(R.drawable.img_error)
                    .resize(width,height)
                    .placeholder(R.drawable.loads)
                    .centerCrop()
                    .into(((ItemNewsHolder) holder).ivNewsImg);
            ((ItemNewsHolder) holder).tvNewsTitle.setText(data.getTitle());
            ((ItemNewsHolder) holder).tvNewsSource.setText(data.getAuthor_name());
            ((ItemNewsHolder) holder).recylerview_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, NewDetailActivity.class);
                    intent.putExtra("url", data.getUrl());
                    intent.putExtra("title", data.getTitle());
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
        private TextView tvNewsSource;
        private TextView tvNewsTime;
        private LinearLayout recylerview_ll;

        public ItemNewsHolder(View view) {
            super(view);

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
