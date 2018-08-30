package com.xuchongyang.easyphone.call;

public interface SipCallListener {
    void callFailed(long callId);
    void callEnd(long callId, long starTime, long endTime);
}
