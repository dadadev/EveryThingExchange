package com.dadabit.everythingexchange.ui.adapter;


import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dadabit.everythingexchange.R;
import com.dadabit.everythingexchange.model.vo.ChatMessageItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;

    private List<ChatMessageItem> mMessageList;


    public MessagesAdapter(Context context) {
        Log.d("@@@", "MessagesAdapter.create");
        this.context = context;
    }


    public void setChatItems(List<ChatMessageItem> chatItems) {

        mMessageList = chatItems;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {

        return mMessageList.get(position).getType();

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        switch (viewType){

            case ChatMessageItem.SENDER:

                return new ViewHolderSender(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.view_chat_message_sender, parent, false));

            case ChatMessageItem.RECIPIENT:

                return new ViewHolderRecipient(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.view_chat_message_recipient, parent, false));


            case ChatMessageItem.DATE:

                return new ViewHolderDate(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.view_chat_message_date, parent, false));


            case ChatMessageItem.AVATAR_RECIPIENT:

                return new ViewHolderRecipientAvatar(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.view_chat_message_avatar_recipient, parent, false));

            case ChatMessageItem.AVATAR_SENDER:

                return new ViewHolderSenderAvatar(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.view_chat_message_avatar_sender, parent, false));


            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()){

            case ChatMessageItem.SENDER:

                ViewHolderSender holderSender = (ViewHolderSender) holder;

                holderSender.tvMessage.setText(mMessageList.get(position).getMessage());

                holderSender.tvTime.setText(mMessageList.get(position).getTime());


                break;

            case ChatMessageItem.RECIPIENT:

                ViewHolderRecipient holderRecipient = (ViewHolderRecipient) holder;

                holderRecipient.tvMessage.setText(mMessageList.get(position).getMessage());

                holderRecipient.tvTime.setText(mMessageList.get(position).getTime());

                break;


            case ChatMessageItem.DATE:

                ViewHolderDate holderDate = (ViewHolderDate) holder;

                holderDate.tvDate.setText(mMessageList.get(position).getTime());

                break;


            case ChatMessageItem.AVATAR_RECIPIENT:

                ViewHolderRecipientAvatar recipientAvatar = (ViewHolderRecipientAvatar) holder;

                Glide.with(context)
                        .load(mMessageList.get(position).getMessage())
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(recipientAvatar.imageView);

                recipientAvatar.tvName.setText(mMessageList.get(position).getTime());

                break;

            case ChatMessageItem.AVATAR_SENDER:

                ViewHolderSenderAvatar senderAvatar = (ViewHolderSenderAvatar) holder;

                Glide.with(context)
                        .load(mMessageList.get(position).getMessage())
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(senderAvatar.imageView);

                senderAvatar.tvName.setText(mMessageList.get(position).getTime());

                break;


        }

    }



    @Override
    public int getItemCount() {
        return mMessageList == null ? 0 : mMessageList.size();
    }




    public class ViewHolderSender extends RecyclerView.ViewHolder{

        @BindView(R.id.chat_message_sender_layout) LinearLayout layout;
        @BindView(R.id.chat_message_sender_textViewMessage) TextView tvMessage;
        @BindView(R.id.chat_message_sender_textViewTime) TextView tvTime;

        ViewHolderSender(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class ViewHolderRecipient extends RecyclerView.ViewHolder{

        @BindView(R.id.chat_message_recipient_layout) LinearLayout layout;
        @BindView(R.id.chat_message_recipient_textViewMessage) TextView tvMessage;
        @BindView(R.id.chat_message_recipient_textViewTime) TextView tvTime;


        ViewHolderRecipient(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class ViewHolderDate extends RecyclerView.ViewHolder{

        @BindView(R.id.chat_message_date_textViewTime) TextView tvDate;

        ViewHolderDate(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class ViewHolderSenderAvatar extends RecyclerView.ViewHolder{

        @BindView(R.id.chat_message_avatar_sender_imageView)
        ImageView imageView;
        @BindView(R.id.chat_message_avatar_sender_textView)
        TextView tvName;


        ViewHolderSenderAvatar(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class ViewHolderRecipientAvatar extends RecyclerView.ViewHolder{

        @BindView(R.id.chat_message_avatar_recipient_imageView)
        ImageView imageView;
        @BindView(R.id.chat_message_avatar_recipient_textView)
        TextView tvName;

        ViewHolderRecipientAvatar(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
