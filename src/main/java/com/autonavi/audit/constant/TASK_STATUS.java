package com.autonavi.audit.constant;

public enum TASK_STATUS {
	// 0 未分配 1已领取(待采集) 2冻结 3已保存(待提交) 4已提交(待审核) 5完成(已审核) 6已分配 7 未找到 8 超时 9申诉
	UNALLOT(0), RECEIVE(1), FREEZE(2), SAVE(3), SUBMIT(4), FINISH(5), ALLOT(6), NOT_FOUND(
			7), TIME_OUT(8), APPEAL(9);

	private int code;
	
	public int getCode() {
		return code;
	}

	private TASK_STATUS(int code) {
		this.code = code;
	}

}
