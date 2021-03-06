package com.bt.zhangzy.logisticstraffic.adapter;

import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.data.Product;
import com.bt.zhangzy.tools.ViewUtils;

import java.util.List;
import java.util.ListIterator;

/**
 * Created by ZhangZy on 2015/6/9.
 */
public class HomeListAdapter extends BaseAdapter {
    private static final String TAG = HomeListAdapter.class.getSimpleName();

//    ArrayList<ViewHolder> listView = new ArrayList<ViewHolder>();

    private OnClickItemListener itemListener;
    private List<Product> list;

    public HomeListAdapter(List<Product> list) {
        this.list = list;
    }

    public void addList(List<Product> list) {
        if (list == null || list.isEmpty()) {
            Log.i(TAG, "addList is empty");
            return;
        }
        Log.d(TAG, "before list size=" + list.size());
        //增加数据对比
        Product last_p;//= this.list.get(this.list.size() - 1);
        Product last_np;
        ListIterator<Product> iterator;
        for (int k = this.list.size() - 1; k >= 0; k--) {
            last_p = this.list.get(k);
            if (list.isEmpty()) {
                Log.d(TAG, "新列表中的数据 为空 跳出");
                break;
            }
            Log.d(TAG, "对比数据" + last_p.getID());
            iterator = list.listIterator();
            while (iterator.hasNext()) {
                last_np = iterator.next();
                Log.d(TAG, "-->" + last_np.getID());
                if (last_np.equals(last_p)) {
                    Log.d(TAG, "删除新列表中的数据" + last_np.getID());
                    iterator.remove();
                    break;
                }
            }
        }

        if (list.isEmpty()) {
            Log.d(TAG, "新列表中的数据 没有新数据");
        } else {
            this.list.addAll(list);
            Log.d(TAG, "新列表中的数据 添加数量" + list.size());
        }


//        int removeSize = 0;
//        for (int j = list.size() - 1; j >= 0; j--) {
//            last_np = list.get(j);
////                Log.d(TAG, "addList check data k=" + k + ",j=" + j);
//            if (last_np.equals(last_p)) {
//                removeSize = j;
//                Log.d(TAG, "check one :" + last_np.getID());
//                break;
//            }
//        }
//        if (removeSize > 0) {
//            if (removeSize >= list.size() - 1) {
//                Log.d(TAG, "no new data!!!");
//                return;
//            }
//            for (int k = removeSize + 1; k < list.size(); k++) {
//                this.list.add(list.get(k));
//                Log.d(TAG, "add new data :" + k);
//            }
//
//
//        } else {
//            this.list.addAll(list);
//            Log.d(TAG, "all new data!");
//        }

//        notifyDataSetChanged();
    }

    public void setOnClickItemListener(OnClickItemListener listener) {
        this.itemListener = listener;
    }


    @Override
    public int getCount() {
        return list == null || list.isEmpty() ? 0 : list.size();
    }

    @Override
    public Product getItem(int position) {
        return position < list.size() ? list.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
//            final int[] fakeImgId = {R.drawable.fake_1, R.drawable.fake_2, R.drawable.fake_3, R.drawable.fake_4, R.drawable.fake_5, R.drawable.fake_6, R.drawable.fake_7, R.drawable.fake_8, R.drawable.fake_9, R.drawable.fake_10};
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_list_item, null);
            holder = new ViewHolder();
            convertView.setTag(holder);

            holder.img = (ImageView) convertView.findViewById(R.id.item_img);
            holder.button = (ImageButton) convertView.findViewById(R.id.list_item_phone);
            holder.textView = (TextView) convertView.findViewById(R.id.list_item_name_tx);
            holder.levelBar = (RatingBar) convertView.findViewById(R.id.list_item_lv_rating);
            holder.vipImg = (ImageView) convertView.findViewById(R.id.list_item_vip_img);
            holder.layout = convertView.findViewById(R.id.list_item_ly);
            holder.call_times_tx = (TextView) convertView.findViewById(R.id.list_item_call_times_tx);
            holder.times_tx = (TextView) convertView.findViewById(R.id.list_item_times_count_tx);
            holder.dir_tx = (TextView) convertView.findViewById(R.id.list_item_dir_tx);


            holder.button.setOnClickListener(holder.listener);
            holder.layout.setOnClickListener(holder.listener);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.position = position;
        Product product = getItem(position);
        if (product != null) {
            Resources resources = convertView.getResources();
            ViewUtils.setImageUrl(holder.img, product.getIconImgUrl());
            ViewUtils.setText(holder.textView, product.getName());
            String callTimes = product.getCallTimes();
            callTimes = TextUtils.isEmpty(callTimes) ? "0" : callTimes;
            ViewUtils.setText(holder.call_times_tx, String.format(resources.getString(R.string.item_call_times_template), callTimes));
            String times = product.getTimes();
            times = TextUtils.isEmpty(times) ? "0" : times;
            ViewUtils.setText(holder.times_tx, String.format(resources.getString(R.string.item_view_times_template), times));

            //线路设置
            String describe = product.getDescribe();
            if (!TextUtils.isEmpty(describe)) {
                String[] split = describe.split(",");
                if (split.length > 0) {
                    describe = split[0];
                    describe = describe.replace("/", "——");
                    ViewUtils.setText(holder.dir_tx, describe);
                }
            }

            if (holder.levelBar != null) {
                if (product.getLevel() <= 0) {
                    holder.levelBar.setVisibility(View.GONE);
                } else {
                    holder.levelBar.setRating(product.getLevel());
                }
            }
            if (holder.vipImg != null) {
                holder.vipImg.setVisibility(product.isVip() ? View.VISIBLE : View.INVISIBLE);
            }

        }
        return convertView;
    }


    class ViewHolder {
        int position;
        ImageView img;
        TextView textView;
        TextView call_times_tx, times_tx;
        TextView dir_tx;
        ImageButton button;
        View layout;
        RatingBar levelBar;
        ImageView vipImg;
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemListener != null) {
                    itemListener.onItemClick(v, position);
                }
            }
        };
    }

    public interface OnClickItemListener {
        public void onItemClick(View v, int position);
    }
}
