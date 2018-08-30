package com.xuchongyang.easyphone.call;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.linphone.core.LinphoneCall;
import com.xuchongyang.easyphone.EasyLinphone;
import com.xuchongyang.easyphone.R;
import com.xuchongyang.easyphone.callback.PhoneCallback;

public class SipCallActivity extends Activity {

	private static final String TAG = SipCallActivity.class.getCanonicalName();
	
	private TextView mTvInfo;
	private TextView mTvRemote;
	private ImageView mBtHangUp;
	private ImageView btn_handsfree;

	private boolean spearkerPhoneOn = false;
	private String callNumber;
	private long callRecordId;
	private boolean isConnected = false;
	private long startTime;
	private long endTime;

	private static SipCallListener sipCallListener;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sipcall);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
			callRecordId = extras.getLong(SipCallConfig.EXTRAT_SIP_CALLID);
			callNumber = extras.getString(SipCallConfig.EXTRAT_SIP_CALLNUM);
			if(callNumber == null || callNumber.isEmpty()){
				Toast.makeText(this,"拨打失败：号码不存在",Toast.LENGTH_LONG).show();
				return;
			}
        }else{
			Toast.makeText(this,"拨打失败：号码不存在",Toast.LENGTH_LONG).show();
			return;
		}

        mTvInfo = (TextView)findViewById(R.id.call_screen_textView_info);
        mTvRemote = (TextView)findViewById(R.id.callscreen_textView_remote);
        mBtHangUp = (ImageView)findViewById(R.id.callscreen_button_hangup);
		btn_handsfree = (ImageView)findViewById(R.id.btn_handsfree);

		//建立监听
		initCallListener();
		//拨打电话
		EasyLinphone.callTo(callNumber, false);

        mBtHangUp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EasyLinphone.hangUp();
				finish();
			}
		});
		btn_handsfree.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EasyLinphone.toggleSpeaker(!EasyLinphone.getLC().isSpeakerEnabled());
				if(EasyLinphone.getLC().isSpeakerEnabled()){
					btn_handsfree.setImageResource(R.mipmap.ic_mianti02);
				}else{
					btn_handsfree.setImageResource(R.mipmap.ic_mianti01);
				}
			}
		});
        mTvRemote.setText(callNumber);

        findViewById(R.id.menu_back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EasyLinphone.hangUp();
				finish();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG,"onResume()");
	}
	
	@Override
	protected void onDestroy() {
		Log.d(TAG,"onDestroy()");

		if(sipCallListener != null){
			if(!isConnected){
				sipCallListener.callFailed(callRecordId);
			}else{
				if(endTime <= 0){
					endTime = System.currentTimeMillis();
				}
				sipCallListener.callEnd(callRecordId,startTime,endTime);
			}
		}
		isConnected = false;
		sipCallListener = null;

		super.onDestroy();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	public static void setSipCallListener(SipCallListener sipCallListener) {
		SipCallActivity.sipCallListener = sipCallListener;
	}

	private void initCallListener(){
		EasyLinphone.addCallback(null, new PhoneCallback() {
			@Override
			public void incomingCall(LinphoneCall linphoneCall) {
				super.incomingCall(linphoneCall);
				// 开启铃声免提
				EasyLinphone.toggleSpeaker(true);
				mTvInfo.setText("来电");
			}

			@Override
			public void outgoingInit() {
				super.outgoingInit();
				mTvInfo.setText("拨号中");
			}

			@Override
			public void callConnected() {
				super.callConnected();
				// 视频通话默认免提，语音通话默认非免提
				EasyLinphone.toggleSpeaker(EasyLinphone.getVideoEnabled());
				// 所有通话默认非静音
				EasyLinphone.toggleMicro(false);
				mTvInfo.setText("通话中");
				isConnected = true;
				startTime = System.currentTimeMillis();
			}

			@Override
			public void callEnd() {
				super.callEnd();
				mTvInfo.setText("通话结束");
				endTime = System.currentTimeMillis();
			}
		});
	}
}
