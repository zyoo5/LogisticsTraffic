package com.bt.zhangzy.logisticstraffic.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.data.Location;

import java.util.ArrayList;

/**
 * Created by ZhangZy on 2015/6/18.
 */
public class LocationListAdapter extends BaseAdapter {

    private static final String TAG = LocationListAdapter.class.getSimpleName();
    private ArrayList<View> listView = new ArrayList<View>();//用于保存view对象，此处是通过牺牲内存来保证界面的流畅性
    private ItemOnClickCallback itemOnClickCallback;
    private ArrayList<ArrayList<Location>> locationList;

    public LocationListAdapter(ArrayList<ArrayList<Location>> jsonArray) {
        if (jsonArray == null)
            return;
        listView = new ArrayList<View>(jsonArray.size());
        locationList = jsonArray;

    }

    @Override
    public int getCount() {
        return locationList == null ? 0 : locationList.size();
    }

    @Override
    public Object getItem(int position) {
        return position < locationList.size() ? locationList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    char lastChar;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (position < listView.size() && listView.get(position) != null) {
            convertView = listView.get(position);
            holder = (ViewHolder) convertView.getTag();
        } else {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_list_item, null);
            holder.top = (TextView) convertView.findViewById(R.id.location_list_item_char);
            holder.title = (TextView) convertView.findViewById(R.id.location_list_item_title);
            holder.title.setOnClickListener(holder.listener);
            holder.ly = (LinearLayout) convertView.findViewById(R.id.location_list_item_ly);
            convertView.setTag(holder);
            listView.add(convertView);
        }

        if (position < locationList.size()) {
            ArrayList<Location> tmpArray = locationList.get(position);
//            holder.locationArray = tmpArray;
            if (tmpArray != null && !tmpArray.isEmpty()) {
                Location location = tmpArray.get(0);
                holder.setData(location.getProvinceName(), tmpArray);
                char topChar = location.getFistLatter();
                if (topChar != lastChar) {
                    holder.setTopChar(String.valueOf(topChar).toUpperCase());
                    lastChar = topChar;
                } else {
                    holder.setTopChar(null);
                }
//                holder.setTitle(tmpArray.get(0).getProvinceName());
//                holder.clearItem();

            }
        }

        return convertView;
    }

    public void setItemOnClickCallback(ItemOnClickCallback itemOnClickCallback) {
        this.itemOnClickCallback = itemOnClickCallback;
    }

    private Handler visableHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.obj != null && msg.obj instanceof LinearLayout) {
                LinearLayout view = (LinearLayout) msg.obj;
                view.setVisibility(view.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                if (view.getChildCount() == 0 && view.getVisibility() == View.VISIBLE) {
                    if (view.getTag() != null) {
                        ViewHolder holder = (ViewHolder) view.getTag();
                        holder.visibleAllItem();
                    }
                }
                notifyDataSetChanged();
            }
            return false;
        }
    });

    class ViewHolder {
        TextView top;
        TextView title;
        LinearLayout ly;
        ArrayList<Location> locationArray;

        /**
         * 设置数据
         *
         * @param titleStr
         * @param list
         */
        void setData(String titleStr, ArrayList<Location> list) {
            if (titleStr == null || TextUtils.isEmpty(titleStr))
                return;
            if (title.getText().equals(titleStr))
                return;
            title.setText(titleStr);
            title.setBackgroundColor(title.getResources().getColor(R.color.main_bg_color));
            locationArray = list;
            if (ly == null)
                return;
            ly.removeAllViews();
            ly.setTag(this);
        }

        private void setTopChar(String topChar) {
            if (topChar == null) {
                top.setVisibility(View.GONE);
            } else {
//            char topchar = Tools.getFirstLetter(titleStr.charAt(0));
                top.setText(topChar);
                top.setVisibility(View.VISIBLE);
            }
        }


        /**
         * 添加数据 异步操作能更好的优化性能
         */
        void visibleAllItem() {
            if (locationArray == null || locationArray.isEmpty())
                return;
            for (Location location : locationArray) {
                addItem(location, ly.getContext());
            }
        }

        private void addItem(Location location, Context context) {
            if (ly == null)
                return;
            TextView tx = new TextView(context);
//            FrameLayout ly_tx = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.location_list_item_smail, null);
//            TextView tx = (TextView) ly_tx.findViewById(R.id.location_list_item_tx);
            tx.setText(location.getCityName());
            tx.setTag(location);
            tx.setTextSize(14);
            tx.setTextColor(context.getResources().getColor(R.color.black));
            tx.setBackgroundColor(context.getResources().getColor(R.color.def_line));
            tx.setPadding(80, 10, 0, 10);
            tx.setGravity(Gravity.CENTER_VERTICAL);
//            tx.setTag(index);
            tx.setOnClickListener(listener);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.topMargin = 5;
            layoutParams.leftMargin = 3;
            layoutParams.rightMargin = 3;
            tx.setLayoutParams(layoutParams);
            ly.addView(tx);
        }

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == title) {
                    if (ly != null) {
                        Message msg = Message.obtain();
                        msg.obj = ly;
                        visableHandler.sendMessage(msg);
//                        ly.setVisibility(ly.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
//                        notifyDataSetChanged();
                    }
                } else if (v != null && v.getTag() != null) {
                    if (itemOnClickCallback != null) {
                        itemOnClickCallback.onClickItem((Location) v.getTag());
                    }
                }
            }
        };
    }

    public interface ItemOnClickCallback {
        void onClickItem(Location location);

    }
}
