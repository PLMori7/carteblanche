package ca.polymtl.inf4410.tp2.shared;

import java.io.Serializable;

public class Operation implements Serializable {
	private String mType;
	private int mOperand;
	private int mResult;
	private boolean mDone;

	public Operation(String nameParam, int operandParam){
		mType = nameParam;
		mOperand = operandParam;
		mDone = false;
	}

	public String getType() {
		return mType;
	}

	public int getOperand() {
		return mOperand;
	}

	public int getResult() {
		return mResult;
	}

	public Boolean isFinished() {
		return mDone;
	}
}