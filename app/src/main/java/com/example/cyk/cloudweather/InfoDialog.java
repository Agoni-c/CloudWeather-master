package com.example.cyk.cloudweather;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class InfoDialog extends Dialog {
    private InfoDialog(Context context , int themeResId){
        super(context, themeResId);
    }

    public static class Builder{
        private View mLayout;
        private TextView mContent;
        private Button mButton;

        private View.OnClickListener mButtonClickListener;

        private InfoDialog mDialog;

        public Builder(Context context){
            mDialog = new InfoDialog(context,R.style.Theme_AppCompat_Dialog);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //加载布局文件
            mLayout = inflater.inflate(R.layout.dialog,null,false);
            //添加布局文件到Dialog
            mDialog.addContentView(mLayout,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            mContent = mLayout.findViewById(R.id.dialog_content);
            mButton = mLayout.findViewById(R.id.dialog_button);
        }

        //设置content
        public Builder setContent(String content){
            mContent.setText(content);
            return this;
        }

        //设置按钮文字和监听
        public Builder setButton(String text, View.OnClickListener listener){
            mButton.setText(text);
            mButtonClickListener = listener;
            return this;
        }

        public InfoDialog create(){
            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                    mButtonClickListener.onClick(v);
                }
            });
            mDialog.setContentView(mLayout);
            mDialog.setCancelable(true);                //用户可以点击后退键关闭 Dialog
            mDialog.setCanceledOnTouchOutside(false);   //用户不可以点击外部来关闭 Dialog
            return mDialog;
        }
    }
}
