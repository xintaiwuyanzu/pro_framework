package com.dh.DpsdkCore;

public class Single_Alarm_Info_t {
	public int					nAlarmType;									// ��������  dpsdk_alarm_type_e
	public int					nEventType;									// �¼�״̬ dpsdk_event_type_e
	public byte[]				szDevId = new byte[dpsdk_constant_value.DPSDK_CORE_DEV_ID_LEN];				// �����豸ID
	public int					uChannel;									// ����ͨ��
	public long					uAlarmTime;									// ����ʱ��
	public int          		nDealWith;									// ������� dpsdk_alarm_dealwith_e

	public Single_Alarm_Info_t()
	{
	}
}
