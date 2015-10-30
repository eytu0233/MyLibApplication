package edu.ncku.ui.recentActivity;

import java.util.HashMap;
import java.util.Map;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import edu.ncku.R;
import edu.ncku.R.array;
import edu.ncku.R.id;
import edu.ncku.R.layout;
import edu.ncku.util.ListViewInfoAdapter;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SyncStateContract.Constants;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

public class RecentActivityFragment extends Fragment {

	private static final String DEBUG_FLAG = RecentActivityFragment.class
			.getName();
	
	private static final String URL = "http://140.116.207.87/";

	private static String[] imgURLs = {
		URL +"images/cycle/cycle01.jpg",
		URL + "images/cycle/cycle02.jpg",
		URL + "images/cycle/cycle03.jpg",};
	
	private static Map<String, String> imgSuperLink = new HashMap<String, String>();
	
	private static Gallery gallery;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_recent_activity,
				container, false);
		
		imgSuperLink.put(imgURLs[0], URL + "service/promotion/bazaar/");
		imgSuperLink.put(imgURLs[1], "http://www.lib.ncku.edu.tw/www2008/media/exhibit/2015/summer/index.html");
		imgSuperLink.put(imgURLs[2], "http://libvote.nlpi.edu.tw/vote_detail.php?Key=81");

		gallery = (Gallery) rootView.findViewById(R.id.gallery);
		gallery.setAdapter(new ImageAdapter(getActivity()));
		gallery.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.d(DEBUG_FLAG, "position : " + position % imgURLs.length);
				Uri uri=Uri.parse(imgSuperLink.get(imgURLs[position % imgURLs.length])); 
				Intent i=new Intent(Intent.ACTION_VIEW,uri); 
				startActivity(i); 
			}
		});
		gallery.post(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				gallery.setSelected(true);
				gallery.setSelection(Integer.MAX_VALUE / 2);
			}
			
		});
		

		return rootView;
	}

	private static class ImageAdapter extends BaseAdapter {

		private LayoutInflater inflater;

		private DisplayImageOptions options;
		
		private ImageLoader imageLoader;

		ImageAdapter(Context context) {
			inflater = LayoutInflater.from(context);

			options = new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.ic_stub)
					.showImageForEmptyUri(R.drawable.ic_empty)
					.showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
					.cacheOnDisk(true).considerExifParams(true)
					.bitmapConfig(Bitmap.Config.RGB_565)
					.displayer(new RoundedBitmapDisplayer(20)).build();
			
			imageLoader = ImageLoader.getInstance();
			imageLoader.init(ImageLoaderConfiguration.createDefault(inflater
					.getContext()));
		}

		@Override
		public int getCount() {
			return Integer.MAX_VALUE;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position % imgURLs.length;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView = (ImageView) convertView;
			if (imageView == null) {
				imageView = (ImageView) inflater.inflate(
						R.layout.item_gallery_image, parent, false);
			}			
			
			int width = gallery.getWidth() * 2 / 3, height = gallery.getHeight() * 2 / 3;
			
			imageView.setAdjustViewBounds(true);
			imageView.setLayoutParams(new Gallery.LayoutParams(width, height));

			imageLoader.displayImage(imgURLs[position % imgURLs.length], imageView, options);
			return imageView;
		}
	}

}
