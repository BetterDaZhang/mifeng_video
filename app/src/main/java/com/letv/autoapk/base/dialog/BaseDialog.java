package com.letv.autoapk.base.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.letv.autoapk.common.utils.Logger;

/**
 * Besides DialogFragment, BaseDialog integrate CocoQuery/ButterKniff/Loader, a better callback with activity.
 *
 * @param <T>
 */
public abstract class BaseDialog extends DialogFragment  {

    /**
     * Dialog message
     */
    private static final String ARG_TITLE = "title";

    /**
     * Dialog message
     */
    private static final String ARG_MESSAGE = "message";

    /**
     * Request code
     */
    private static final String ARG_REQUEST_CODE = "requestCode";


    protected Context context;
    protected View v;


    private boolean result = false;

    /**
     * Is this fragment usable from the UI-thread
     *
     * @return true if usable, false otherwise
     */
    protected boolean isUsable() {
        return getActivity() != null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        setStyle(DialogFragment.STYLE_NO_FRAME, getStyle());
        if (getArguments() != null) {
            result = getArguments().getInt(BaseDialog.ARG_REQUEST_CODE) > 0;
        }
    }

    public int getStyle() {
        return android.R.style.Theme_Dialog;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        v = inflater.inflate(layoutId(), container, false);
        try {
            setupUI(v, savedInstanceState);
        } catch (Exception e) {
        	Logger.log(e);
        }catch (OutOfMemoryError e) {
        	e.printStackTrace();
        }
        return v;
    }

    public static void show(FragmentManager fm, DialogFragment d) {

        try {
        		FragmentTransaction ft = fm.beginTransaction();
    			Fragment prev = fm.findFragmentByTag("dialog");
    			if (prev != null) {
    			    ft.remove(prev);
    			}
    			if(!d.isAdded()){
        			d.show(ft, "dialog");
    			}
    			
		} catch (Exception e) {
			Logger.log(e);
		}
    }


	public static void showForResult(FragmentManager fm,
                                     DialogFragment d, int requestCode) {

        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        
        if (d.getArguments() == null) {
            d.setArguments(BaseDialog.createArguments(null, null, requestCode));
        } else {
            d.getArguments().putInt(BaseDialog.ARG_REQUEST_CODE, requestCode);
        }
        d.show(ft, null);
    }


    public abstract int layoutId();

    protected abstract void setupUI(View view, Bundle bundle) throws Exception;

    /**
     * Create bundle with standard arguments
     *
     * @param title
     * @param message
     * @param requestCode
     * @return bundle
     */
    protected static Bundle createArguments(String title,
                                            String message, int requestCode) {
        Bundle arguments = new Bundle();
        arguments.putInt(BaseDialog.ARG_REQUEST_CODE, requestCode);
        arguments.putString(BaseDialog.ARG_TITLE, title);
        arguments.putString(BaseDialog.ARG_MESSAGE, message);
        return arguments;
    }

    /**
     * Call back to the activity with the dialog result
     *
     * @param resultCode
     */
    protected void onResult(int resultCode) {
        if (result) {
            ((DialogResultListener) getActivity()).onDialogResult(
                    getArguments().getInt(BaseDialog.ARG_REQUEST_CODE),
                    resultCode, getArguments());
        }
    }

    /**
     * Get title
     *
     * @return title
     */
    protected String getTitle() {
        return getArguments().getString(BaseDialog.ARG_TITLE);
    }

    /**
     * Get message
     *
     * @return mesage
     */
    protected String getMessage() {
        return getArguments().getString(BaseDialog.ARG_MESSAGE);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        onResult(Activity.RESULT_CANCELED);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme());
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        onResult(Activity.RESULT_OK);
    }
/*
    public static void show(final FragmentManager fm, final DialogFragment d,
                            final Bundle arg) {
        d.setArguments(arg);
        BaseDialog.show(fm, d);
    }*/

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    protected final <E extends View> E view(int resourceId) {
        return (E) v.findViewById(resourceId);
    }


}
