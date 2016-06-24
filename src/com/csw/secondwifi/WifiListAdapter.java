package com.csw.secondwifi;

import java.util.List;

import com.csw.tianlai_ui.R;





import android.content.Context;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WifiListAdapter extends BaseAdapter {

	private List<WifiInfo> wifiInfoList;
	private LayoutInflater mInflater;
	private Context context;
	private String ssidName;
	WifiAdmin wifiAdmin;
	String test;
	public WifiListAdapter(Context context, List<WifiInfo> wifiInfoList) {
		super();
		this.wifiInfoList = wifiInfoList;
		this.wifiAdmin = new WifiAdmin(context,test);
		this.mInflater = LayoutInflater.from(context);
		this.context = context;
		this.ssidName = wifiAdmin.getSSID();
	}

	public WifiListAdapter(Context context,WifiAdmin wifiAdmin, List<WifiInfo> wifiInfoList){
		super();
		this.wifiInfoList = wifiInfoList;
		this.wifiAdmin = wifiAdmin;
		this.mInflater = LayoutInflater.from(context);
		this.context = context;
		this.ssidName = wifiAdmin.getSSID();
	}
	public WifiListAdapter(Context context, List<WifiInfo> wifiInfoList,String test) {
		super();
		this.wifiInfoList = wifiInfoList;
		this.wifiAdmin = new WifiAdmin(context,test);
		this.mInflater = LayoutInflater.from(context);
		this.context = context;
		this.ssidName = wifiAdmin.getSSID();
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return wifiInfoList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return wifiInfoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = null;
		ViewHolder viewHolder = null;
		if (convertView == null || convertView.getTag() == null) {
			view = mInflater.inflate(R.layout.wifi_item, null);
			viewHolder = new ViewHolder(view);
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) convertView.getTag();
		}
		WifiInfo wifiInfo = (WifiInfo) getItem(position);

		if(position==Wifi_second_Activity.whichItemChange){
			if(Wifi_second_Activity.wifistateFlag==true){
				viewHolder.ssidConnectStateView.setText("已连接");
				viewHolder.wifiIconView.setVisibility(View.VISIBLE);
//				Wifi_second_Activity.wifistateFlag=false;
//				Wifi_second_Activity.wifiConnectSuccessFlag=false;
				
			}else {
				viewHolder.ssidConnectStateView.setText("正在连接中...");
				viewHolder.wifiIconView.setVisibility(View.GONE);
				
				if(Wifi_second_Activity.wifiConnectSuccessFlag==false){
					viewHolder.ssidConnectStateView.setText("连接失败");
					
					Wifi_second_Activity.wifiConnectSuccessFlag=true;
				}
			}
			
		}else{
			viewHolder.ssidConnectStateView.setText("");
			viewHolder.wifiIconView.setVisibility(View.GONE);
		}
		    
			viewHolder.wifiImageView.setImageDrawable(wifiInfo.getHotspotIcon());
			viewHolder.ssidNameView.setText(wifiInfo.getSsid());
		    
			  if (position == selectItem) {  
				    viewHolder.wifiImageView.setBackgroundResource(R.drawable.hotspot_icon);
	            }   else {  
	            	 viewHolder.wifiImageView.setBackgroundResource(R.drawable.hotspot_icon_focus);
	            }  
			  
			

		return view;

	}

	class ViewHolder {
		ImageView wifiImageView;
		TextView ssidNameView;
		TextView ssidConnectStateView;
		ImageView wifiIconView;

		public ViewHolder(View view) {
			this.wifiImageView = (ImageView) view.findViewById(R.id.wifiIcon);// 热点图标
			this.ssidNameView = (TextView) view.findViewById(R.id.wifiSsidText);// 热点名称
			this.ssidConnectStateView=(TextView)view.findViewById(R.id.wifiSsidStateText_second);//wifi连接状态
			this.wifiIconView=(ImageView)view.findViewById(R.id.wifiIconSelect);//热点选中

			
		}
	}

	
	public  void setSelectItem(int selectItem) {  
        this.selectItem = selectItem;  
   }  
   private int  selectItem=-1;  
}

