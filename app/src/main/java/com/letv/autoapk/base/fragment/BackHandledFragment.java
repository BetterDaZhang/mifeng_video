package com.letv.autoapk.base.fragment;

import com.letv.autoapk.base.activity.BackHandledInterface;

import android.os.Bundle;
import android.support.v4.app.Fragment;


public abstract class BackHandledFragment extends BaseTitleFragment {

	protected BackHandledInterface mBackHandledInterface;
	public abstract boolean onBackPressed();
	public abstract void onBackProgerss();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(!(getActivity() instanceof BackHandledInterface)){
			throw new ClassCastException("Back Activity must implement BackHandledInterface");
		}else{
			this.mBackHandledInterface = (BackHandledInterface)getActivity();
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		mBackHandledInterface.setSelectedFragment(this);
	}
	
}
