package com.letv.autoapk.ui.mobilelive;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.letv.autoapk.R;
import com.letv.autoapk.base.activity.BaseActivity;
import com.letv.autoapk.base.activity.ContainerActivity;
import com.letv.autoapk.base.activity.PlayVideoActivity;
import com.letv.autoapk.base.adapter.BaseAdapter;
import com.letv.autoapk.common.net.LruImageCache;
import com.letv.autoapk.open.OpenShareActivity;
import com.letv.autoapk.widgets.NetImageView;

public class MobileLiveAdapter extends BaseAdapter implements OnClickListener {

    private List<MobileLiveInfo> list;
    private Context context;

    public MobileLiveAdapter(BaseActivity mActivity, List<MobileLiveInfo> liveVideoInfos) {
        context = mActivity;
        list = liveVideoInfos;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }
    
    public void setLiveVideoInfos(List<MobileLiveInfo> liveVideoInfos){
        this.list= liveVideoInfos; 
    }

    @Override
    public MobileLiveInfo getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    protected View getMyView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.live_mobile_item, null);
            holder = new Holder();
            holder.cover = (NetImageView) convertView.findViewById(R.id.live_cover);
            holder.cover.setDefaultImageResId(R.drawable.live_default_bg);
            holder.cover.setErrorImageResId(R.drawable.live_default_bg);
            holder.title = (TextView) convertView.findViewById(R.id.livetitle);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.brief = (TextView) convertView.findViewById(R.id.time);
            holder.icon = (ImageView) convertView.findViewById(R.id.head_icon);
            holder.isliving = convertView.findViewById(R.id.livingnow);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        MobileLiveInfo info = list.get(position);
        Object object = holder.name.getTag();
        if (object == null || info.liveUrl.equals(object) == false) {
            holder.name.setTag(info.liveUrl);
            holder.name.setText(info.userName);
            ImageListener listener = ImageLoader.getImageListener(holder.icon, R.drawable.mine_default_head_img, R.drawable.mine_default_head_img);
            LruImageCache.getImageLoader(context.getApplicationContext()).get(info.headPic, listener);
            holder.cover.setTag(info);
            holder.cover.setOnClickListener(this);
        }
        holder.title.setText(info.liveTitle);
        holder.brief.setText(info.beginTime);
        holder.cover.setCoverUrl(info.coverPic, context);
        if (info.isOnlie.equals("0")) {
            holder.isliving.setVisibility(View.GONE);
        } else {
            holder.isliving.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    class Holder {
        NetImageView cover;
        TextView title;
        TextView name;
        TextView brief;
        ImageView icon;
        View isliving;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, PlayVideoActivity.class);
        Bundle liveBundle2 = new Bundle();
        liveBundle2.putString("liveUrl", ((MobileLiveInfo) v.getTag()).liveUrl);
        liveBundle2.putString("anchorHeadImg", ((MobileLiveInfo) v.getTag()).headPic);
        liveBundle2.putString("anchorName", ((MobileLiveInfo) v.getTag()).userName);
        liveBundle2.putString("coverUrl", ((MobileLiveInfo) v.getTag()).coverPic);
        liveBundle2.putString("shareUrl", ((MobileLiveInfo) v.getTag()).shareUrl);
        liveBundle2.putString(PlayVideoActivity.FRAGMENTNAME, MobileLiveVideoFragment.class.getName());
        intent.putExtras(liveBundle2);
        context.startActivity(intent);
    }
}
