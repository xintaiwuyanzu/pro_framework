package com.dh.DpsdkCore;

public class Send_OSDInfo_t {

	public byte[]						szDevId = new byte[dpsdk_constant_value.DPSDK_CORE_DEV_ID_LEN];			// ͨ��ID
	public byte[]						szMessage = new byte[dpsdk_constant_value.DPSDK_CORE_OSDTEMPLAT_CONTENT_LEN];			// ͨ��ID

	public Send_OSDInfo_t()
	{

	}

}
