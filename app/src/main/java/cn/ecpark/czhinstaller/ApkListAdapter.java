package cn.ecpark.czhinstaller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * @className: ApkListAdapter
 * @classDescription: Apk列表适配器
 * @author: swallow
 * @createTime: 2015/10/29
 */
public class ApkListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private ArrayList<ItemData> mData;

    public ApkListAdapter(Context context, ArrayList<ItemData> data){
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mData = data;
    }

    @Override
    public int getCount() {
        return mData == null?0:mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
            convertView = mInflater.inflate(R.layout.item_main_apk_list, null);
            holder = new ViewHolder();
            holder.tvName = (TextView) convertView.findViewById(R.id.item_main_apk_list_name_tv);
            holder.tvUrl = (TextView) convertView.findViewById(R.id.item_main_apk_list_url_tv);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvName.setText(mData.get(position).name);
        holder.tvUrl.setText(mData.get(position).url);
        return convertView;
    }

    public static class ItemData{
        public String name = "none";
        public String url = "none";
    }

    public static class ViewHolder{
        public TextView tvName;
        public TextView tvUrl;
    }
}
