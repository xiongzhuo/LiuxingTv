package activity.liuxing.tv.com.tvliuxing.activity.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import activity.liuxing.tv.com.tvliuxing.R;

/**
 * Created by Administrator on 2017/5/4.
 */

public class DataAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> list;
    private int clickPosition = -1;

    public DataAdapter(Context mContext, List<String> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public int getCount() {
        if (list != null && list.size() > 0) {
            return list.size();
        } else {
            return 0;
        }
    }

    public int getClickPosition() {
        return clickPosition;
    }

    public void setClickPosition(int clickPosition) {
        this.clickPosition = clickPosition;
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.item_entry, null);
//        final TextView tv = ((TextView) view
//                .findViewById(R.id.tv_item_classify));
//        tv.setText(list.get(position));
//        tv.setBackgroundResource(R.drawable.bg_button_focused);
//        if (position == clickPosition) {
//            tv.setBackgroundColor(mContext.getResources().getColor(R.color.purple));
//        }
        return view;
    }
}
