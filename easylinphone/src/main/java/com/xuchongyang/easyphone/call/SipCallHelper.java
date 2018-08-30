package com.xuchongyang.easyphone.call;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.xuchongyang.easyphone.EasyLinphone;
import com.xuchongyang.easyphone.callback.RegistrationCallback;
import com.xuchongyang.easyphone.service.LinphoneService;

public class SipCallHelper {

    private static final String TAG = "SipCallHelper";
    private String callNumber;

    private static class SingletonHolder {
        /***单例对象实例*/
        static final SipCallHelper INSTANCE = new SipCallHelper();
    }

    public static SipCallHelper getInstance() {
        return SipCallHelper.SingletonHolder.INSTANCE;
    }

    private void init(Context context, SipConfigEntity config){
        //设置参数
        SipCallConfig.SIP_USERNAME = config.getUserid();
        SipCallConfig.SIP_PASSWORD = config.getPassword();
        SipCallConfig.SIP_DOMAIN = config.getDomain();
        SipCallConfig.SIP_PROXY = config.getProxy();
    }

    public void initSipService(final Context context){
        if (!LinphoneService.isReady()) {
            EasyLinphone.startService(context);
            EasyLinphone.addCallback(new RegistrationCallback() {
                @Override
                public void registrationOk() {
                    super.registrationOk();
                    Log.e(TAG, "registrationOk: ");
                    Toast.makeText(context, "登录成功！", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void registrationFailed() {
                    super.registrationFailed();
                    Log.e(TAG, "registrationFailed: ");
                    Toast.makeText(context, "登录失败！", Toast.LENGTH_SHORT).show();
                }
            }, null);
        }
    }

    private void startSipEngine(Context context){
        EasyLinphone.setAccount(SipCallConfig.SIP_USERNAME, SipCallConfig.SIP_PASSWORD, SipCallConfig.SIP_PROXY);
        EasyLinphone.login();
    }

    public void initSip(SipConfigEntity config,Activity mActivity){
        init(mActivity, config);
        //初始化service
        initSipService(mActivity);
        //初始化引擎
        startSipEngine(mActivity);
    }

    public void makeVoiceCall(Context context,String phoneNumber,SipCallListener listener){
        makeVoiceCall(context,phoneNumber,-1,listener);
    }

    public void makeVoiceCall(Context context,String phoneNumber,long recordId,SipCallListener listener){
        if(phoneNumber == null || phoneNumber.isEmpty()){
            Log.e(TAG,"failed to makeVoiceCall number: '" + phoneNumber + "'");
            return;
        }

        callNumber = phoneNumber;
        SipCallActivity.setSipCallListener(listener);

        Intent i = new Intent();
        i.setClass(context, SipCallActivity.class);
        i.putExtra(SipCallConfig.EXTRAT_SIP_CALLID,recordId);
        i.putExtra(SipCallConfig.EXTRAT_SIP_CALLNUM,phoneNumber);
        context.startActivity(i);
    }

    public String getCallNumber() {
        return callNumber;
    }
}
