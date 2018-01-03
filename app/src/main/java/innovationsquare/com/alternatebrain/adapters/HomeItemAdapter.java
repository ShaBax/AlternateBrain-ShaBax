package innovationsquare.com.alternatebrain.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import innovationsquare.com.alternatebrain.R;
import innovationsquare.com.alternatebrain.activities.SingleImageActivity;
import innovationsquare.com.alternatebrain.models.Image;
import innovationsquare.com.alternatebrain.utils.GsonFactory;

/**
 * Created by macbookpro on 10/12/2017.
 */

public class HomeItemAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    Context con;
    public List<Image> imageList;


    public HomeItemAdapter(Context context, List<Image> images2) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageList = images2;
        con = context;
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView image;
        Image item = imageList.get(position);
        final Image myItem = item;
        final ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.home_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.nameEdtx.setText(myItem.place_name);
        holder.dateEtx.setText(myItem.bill_date);
        holder.amountEdt.setText(myItem.amount);


        holder.eyeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(con, SingleImageActivity.class);
                intent.putExtra("imageStr", GsonFactory.getInstance().getGson().toJson(myItem));
                con.startActivity(intent);
            }
        });

        holder.checkBox.setChecked(myItem.isSelected);
        holder.checkBox.setTag(position);
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer pos = (Integer) holder.checkBox.getTag();
                if (imageList.get(pos).isSelected) {
                    imageList.get(pos).isSelected = false;
                } else {
                    imageList.get(pos).isSelected = true;
                }
            }
        });

        return convertView;
    }

    public String getSelectedId() {
        List<Integer> list = new ArrayList<>();
        for (Image image : imageList) {
            if (image.isSelected) {
                list.add(image.id);
            }
        }

        String data = TextUtils.join(",", list);
        return data;
    }

    public void selectAllByValue(boolean selectAll) {
        for (Image image : imageList) {
            image.isSelected = selectAll;
        }
        notifyDataSetChanged();
    }

    public void clearAllItems(){
        if(imageList != null) {
            imageList.clear();
            notifyDataSetChanged();
        }
    }

    static class ViewHolder {
        public CheckBox checkBox;
        public ImageView eyeImage;
        public TextView dateEtx, amountEdt, nameEdtx;

        public ViewHolder(View view) {
            checkBox = (CheckBox) view.findViewById(R.id.checkbox);
            eyeImage = (ImageView) view.findViewById(R.id.eyeImage);
            dateEtx = (TextView) view.findViewById(R.id.dateEtx);
            amountEdt = (TextView) view.findViewById(R.id.amountTxt);
            nameEdtx = (TextView) view.findViewById(R.id.nameEdt);
        }
    }
}
