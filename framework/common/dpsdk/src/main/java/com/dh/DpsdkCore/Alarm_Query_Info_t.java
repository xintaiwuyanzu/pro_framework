package com.dh.DpsdkCore;

public class Alarm_Query_Info_t {

	public byte[]						szCameraId = new byte[dpsdk_constant_value.DPSDK_CORE_CHL_ID_LEN];			// ͨ��ID
	public long							uStartTime;									// ��ʼʱ��
	public long							uEndTime;									// ����ʱ��
	public int      					nAlarmType;									// ��������  dpsdk_alarm_type_e

	public Alarm_Query_Info_t()
	{

	}
}
