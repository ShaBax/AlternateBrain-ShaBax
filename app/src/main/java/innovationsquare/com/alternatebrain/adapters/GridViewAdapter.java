package innovationsquare.com.alternatebrain.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

import innovationsquare.com.alternatebrain.R;
import innovationsquare.com.alternatebrain.activities.SingleImageActivity;
import innovationsquare.com.alternatebrain.models.Image;
import innovationsquare.com.alternatebrain.utils.URL;

public class GridViewAdapter extends ArrayAdapter<Image> {

    private Context context;
    private int layoutResourceId;
    private List<Image> data = new ArrayList<>();

    public GridViewAdapter(Context context, int layoutResourceId, List<Image> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;
        Image item = data.get(position);
        final Image myItem = item;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.image = row.findViewById(R.id.image);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

//        Picasso.with(context)
//                .load(URL.IMAGE_BASE+item.attachment)
//                .fit()
//                .noFade()
//                .placeholder(R.drawable.loader)
//                .tag(context)
//                .into(holder.image);

        Glide.with(context)
                .load(URL.IMAGE_BASE+item.attachment)
                .centerCrop()
                .placeholder(R.drawable.loader)
                .into(holder.image);


        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SingleImageActivity.class);
                intent.putExtra("imageKey", myItem.attachment);
                context.startActivity(intent);
            }
        });
        return row;
    }

    static class ViewHolder {
        ImageView image;
    }
}