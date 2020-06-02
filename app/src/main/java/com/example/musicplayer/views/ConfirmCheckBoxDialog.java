package com.example.musicplayer.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.musicplayer.R;

public class ConfirmCheckBoxDialog extends Dialog {

    private OnDialogActionClickListener mClickListener = null;
    private CheckBox mCheckBox;
    private View mCancel;
    private View mConfirm;

    public ConfirmCheckBoxDialog(@NonNull Context context) {
        this(context, 0);
    }

    public ConfirmCheckBoxDialog(@NonNull Context context, int themeResId) {
        this(context, true, null);
    }

    protected ConfirmCheckBoxDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_checkbox_confirm);
        initView();
        initListener();
    }

    private void initListener() {
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    boolean checked=mCheckBox.isChecked();
                    mClickListener.onConfirmClick(checked);
                    dismiss();
                }

            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onCancelClick();
                    dismiss();
                }

            }
        });
    }

    private void initView() {
        mCancel = this.findViewById(R.id.dialog_checkbox_cancel);
        mConfirm = this.findViewById(R.id.dialog_checkbox_confirm);
        mCheckBox = this.findViewById(R.id.dialog_checkbox);
    }

    public void setOnDialogActionClickListener(OnDialogActionClickListener listener) {
        this.mClickListener = listener;
    }

    public interface OnDialogActionClickListener {
        void onCancelClick();

        void onConfirmClick(boolean isCheck);
    }
}
