package com.example.backgroundimage.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.thn.backgroundimage.R;


public class DialogUtils extends DialogFragment {
    private Activity mActivity;

    private String dialogTitle, dialogText, positiveText, negativeText, viewIdText;
    private TextView txtDialogTitle, txtDialogText;

    public static interface OnCompleteListener {
        public abstract void onComplete(Boolean isOkPressed, String viewIdText);
    }

    private OnCompleteListener mListener;
    private OnCompleteListener onCompleteListener;

    public static DialogUtils newInstance(String dialogTitle, String dialogText, String yes, String no, String viewIdText) {
        Bundle args = new Bundle();
        args.putString(AppConstant.BUNDLE_KEY_TITLE, dialogTitle);
        args.putString(AppConstant.BUNDLE_KEY_MESSAGE, dialogText);
        args.putString(AppConstant.BUNDLE_KEY_YES, yes);
        args.putString(AppConstant.BUNDLE_KEY_NO, no);
        args.putString(AppConstant.BUNDLE_KEY_VIEW_ID, viewIdText);
        DialogUtils fragment = new DialogUtils();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = activity;

        try {
            if (activity instanceof OnCompleteListener)
                this.mListener = (OnCompleteListener) activity;
        } catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString());
        }

    }


    public void setOnCompleteListener(OnCompleteListener onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(mActivity).inflate(R.layout.fragment_custom_dialog, null);

        initVar();
        initView(rootView);
        initFunctionality();

        return new androidx.appcompat.app.AlertDialog.Builder(mActivity)
                .setView(rootView)
                .setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onComplete(true, viewIdText);
                        if (onCompleteListener != null) {
                            onCompleteListener.onComplete(true, viewIdText);
                        }
                    }
                })
                .setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (dialogInterface != null) {
                            mListener.onComplete(false, viewIdText);
                            if (onCompleteListener != null) {
                                onCompleteListener.onComplete(false, viewIdText);
                            }
                        }
                    }
                })
                .create();
    }

    public void initVar() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            dialogTitle = getArguments().getString(AppConstant.BUNDLE_KEY_TITLE);
            dialogText = getArguments().getString(AppConstant.BUNDLE_KEY_MESSAGE);
            positiveText = getArguments().getString(AppConstant.BUNDLE_KEY_YES);
            negativeText = getArguments().getString(AppConstant.BUNDLE_KEY_NO);
            viewIdText = getArguments().getString(AppConstant.BUNDLE_KEY_VIEW_ID);
        }
    }

    public void initView(View rootView) {
        txtDialogTitle = (TextView) rootView.findViewById(R.id.dialog_title);
        txtDialogText = (TextView) rootView.findViewById(R.id.dialog_text);
    }

    public void initFunctionality() {
        txtDialogTitle.setText(dialogTitle);
        txtDialogText.setText(dialogText);
    }

}