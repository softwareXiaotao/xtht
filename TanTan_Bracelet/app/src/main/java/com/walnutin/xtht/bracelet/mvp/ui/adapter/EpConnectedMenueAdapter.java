package com.walnutin.xtht.bracelet.mvp.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.walnutin.xtht.bracelet.R;
import com.walnutin.xtht.bracelet.mvp.model.api.cache.CommonCache;
import com.walnutin.xtht.bracelet.mvp.model.entity.EpMenue;

import java.util.List;

/**
 * Created by Leiht on 2017/6/15.
 */

public class EpConnectedMenueAdapter extends RecyclerView.Adapter<EpConnectedMenueAdapter.MenueViewHolder> implements View.OnClickListener {
    List<EpMenue> epMenues = null;

    private Context mContext;

    private OnItemClickListener mOnItemClickListener = null;

    public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public EpConnectedMenueAdapter(List<EpMenue> epMenues, Context context) {
        this.epMenues = epMenues;
        this.mContext = context;
    }

    @Override
    public EpConnectedMenueAdapter.MenueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(
                mContext).inflate(R.layout.ep_menue_item, null,
                false);
        EpConnectedMenueAdapter.MenueViewHolder holder = new EpConnectedMenueAdapter.MenueViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(EpConnectedMenueAdapter.MenueViewHolder holder, int position) {
        holder.itemView.setTag(position);
        holder.iv.setImageResource(R.mipmap.laidianshibie);
        EpMenue epMenue = epMenues.get(position);
        holder.tv.setText(epMenue.getName());
        if(epMenue.getIcon().contains("laidianshibie.png")) {
            holder.iv.setImageResource(R.mipmap.laidianshibie);
        }else if(epMenue.getIcon().contains("richangnaozhong.png")) {
            holder.iv.setImageResource(R.mipmap.richangnaozhong);
        }else if(epMenue.getIcon().contains("xiaoxi.png")) {
            holder.iv.setImageResource(R.mipmap.xiaoxi);
        }else if(epMenue.getIcon().contains("wenti.png")) {
            holder.iv.setImageResource(R.mipmap.wenti);
        }else if(epMenue.getIcon().contains("gujiangengxin.png")) {
            holder.iv.setImageResource(R.mipmap.gujiangengxin);
        }
    }

    @Override
    public int getItemCount() {
        return epMenues.size();
    }

    @Override
    public void onClick(View view) {
        if(mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(view, (Integer) view.getTag());
        }
    }

    class MenueViewHolder extends RecyclerView.ViewHolder {
        ImageView iv;
        TextView tv;
        View itemView;


        public MenueViewHolder(View view) {
            super(view);
            itemView = view;
            iv = (ImageView) view.findViewById(R.id.ep_menue_icon);
            tv = (TextView) view.findViewById(R.id.ep_menue_name);
        }
    }
}
