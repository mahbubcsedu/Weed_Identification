package com.mpss.weed.id.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mpss.weed.id.R;


public class HomeImageAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
     
    public HomeImageAdapter(Context c,String []mText1, Integer []mThumb) {
    	mContext = c;
    	
    	mText=mText1;
    	mThumbIds=mThumb;
        mInflater = LayoutInflater.from(c);
        
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
         
        /*//If We want to show only images
        //Try commented code
        //Ref : http://developer.android.com/resources/tutorials/views/hello-gridview.html
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mThumbIds[position]);
        return imageView;*/
                     
        ViewHolder holder;
        if (convertView == null) {             
           convertView = mInflater.inflate(R.layout.grid_row_view, null);
           holder = new ViewHolder();
           holder.ImgThumb = (ImageView) convertView.findViewById(R.id.imgThumb);
           holder.ImhText  = (TextView) convertView.findViewById(R.id.imgText);
            
           convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
         
        holder.ImgThumb.setImageResource(mThumbIds[position]);
        holder.ImhText.setText(mText[position]);
                     
        return convertView;        
    } 

    private class ViewHolder {
       ImageView ImgThumb;
       TextView ImhText;
    }

    // references to our images
    private Integer[] mThumbIds; 
    
    private String[] mText ;
}
