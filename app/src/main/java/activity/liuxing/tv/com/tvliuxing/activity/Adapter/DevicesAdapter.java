package activity.liuxing.tv.com.tvliuxing.activity.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import activity.liuxing.tv.com.tvliuxing.R;
import activity.liuxing.tv.com.tvliuxing.activity.entity.UserDevList;

public class DevicesAdapter extends BaseAdapter {
    private Context mContext;
    private List<UserDevList> list;
    private int clickPosition = -1;

    public DevicesAdapter(Context mContext, List<UserDevList> list) {
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
        view = inflater.inflate(R.layout.item_classify, null);
        final TextView tv = ((TextView) view
                .findViewById(R.id.tv_item_classify));
        final TextView tvOnLine = ((TextView) view
                .findViewById(R.id.tv_on_line));
        final TextView listItemText = ((TextView) view
                .findViewById(R.id.list_item_text));
        listItemText.setText(list.get(position).getDevice_nickname());
        if (list.get(position).getIson() == 1) {
            Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.on_line);
            /// 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, 25, 25);
            tvOnLine.setText("在线");
            tvOnLine.setCompoundDrawables(drawable, null, null, null);
        } else {
            Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.off_line);
            /// 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, 25, 25);
            tvOnLine.setText("离线");
            tvOnLine.setCompoundDrawables(drawable, null, null, null);
        }
//        tv.setBackgroundResource(R.drawable.bg_button_focused);
        if (position == clickPosition) {
            tv.setVisibility(View.VISIBLE);
//            tv.setBackgroundColor(mContext.getResources().getColor(R.color.purple));
        } else {
            tv.setVisibility(View.GONE);
        }
        return view;
    }

    public void setList(List<UserDevList> list) {
        this.list = list;
        notifyDataSetChanged();
    }
}
