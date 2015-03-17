package godlesz.de.golemdeit_news2.rss;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import godlesz.de.golemdeit_news2.ApplicationHelper;
import godlesz.de.golemdeit_news2.R;
import godlesz.de.golemdeit_news2.util.VolleySingleton;

public class RssAdapter extends BaseAdapter {

    private final List<RssItem> items;
    private final Context context;

    public RssAdapter(Context context, List<RssItem> items) {
        this.items = items;
        this.context = context;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.rss_item, null);
            holder = new ViewHolder();
            holder.thumbnail = (NetworkImageView) convertView.findViewById(R.id.thumbnail);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.teaser = (TextView) convertView.findViewById(R.id.teaser);
            holder.dateInfo = (TextView) convertView.findViewById(R.id.dateInfo);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        RssItem item = items.get(position);

        holder.thumbnail.setDefaultImageResId(R.drawable.ic_image_placeolder);
        holder.thumbnail.setImageUrl(item.getThumbnailUrl(), VolleySingleton.getInstance().getImageLoader());
        holder.title.setText(item.getTitleFormatted());
        holder.teaser.setText(item.getDescription());
        holder.dateInfo.setText(item.getPubDateFormatted());


        if(ApplicationHelper.is_night_view() == true){
            holder.title.setTextColor(Color.WHITE);
            holder.teaser.setTextColor(Color.WHITE);
            holder.dateInfo.setTextColor(Color.WHITE);
        } else {
            holder.title.setTextColor(Color.BLACK);
            holder.teaser.setTextColor(Color.BLACK);
            holder.dateInfo.setTextColor(Color.BLACK);

        }
        return convertView;
    }


    static class ViewHolder {
        NetworkImageView thumbnail;
        TextView title;
        TextView teaser;
        TextView dateInfo;
    }

}