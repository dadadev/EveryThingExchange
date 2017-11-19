package com.dadabit.everythingexchange.ui.adapter;


import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dadabit.everythingexchange.R;
import com.dadabit.everythingexchange.model.vo.ChatItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder>{

    private Context context;
    private List<ChatItem> chatItems;
    @Nullable private ChatsClickCallback mCallback;
    private boolean isEmpty;


    public void setChatItems(final List<ChatItem> newChatItems){
        Log.d("@@@", "ChatsAdapter.setChatItems");
        if (chatItems == null){
            chatItems = newChatItems;
            notifyItemRangeInserted(0, newChatItems.size());
        } else {
            Log.d("@@@", "ChatsAdapter.setChatItems.change");

            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return chatItems.size();
                }

                @Override
                public int getNewListSize() {
                    return newChatItems.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return chatItems.get(oldItemPosition).getExchangeId() ==
                            newChatItems.get(newItemPosition).getExchangeId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return chatItems.get(oldItemPosition).getLastUpdate() ==
                            newChatItems.get(newItemPosition).getLastUpdate();
                }
            });

            chatItems = newChatItems;
//            result.dispatchUpdatesTo(this);
            notifyDataSetChanged();


        }

    }


    public ChatsAdapter(Context context,
                        @Nullable ChatsClickCallback mCallback) {
        this.context = context;
        this.mCallback = mCallback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.view_card_chat_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (isEmpty){

            holder.ivUserPic.setVisibility(View.INVISIBLE);
            holder.ivThing1.setVisibility(View.GONE);
            holder.ivThing2.setVisibility(View.GONE);
            holder.iconExchange.setVisibility(View.GONE);

            holder.tvName.setText("NO CHATS ITEMS");

        } else {

            Glide.with(context)
                    .load(chatItems.get(position).getCompanionImg())
                    .thumbnail(0.5f)
                    .crossFade()
                    .into(holder.ivUserPic);

            Glide.with(context)
                    .load(chatItems.get(position).getMyThingImg())
                    .thumbnail(0.5f)
                    .crossFade()
                    .into(holder.ivThing1);

            Glide.with(context)
                    .load(chatItems.get(position).getCompanionThingImg())
                    .thumbnail(0.5f)
                    .crossFade()
                    .into(holder.ivThing2);

            holder.tvName.setText(chatItems.get(position).getCompanionName());


            if (chatItems.get(position).getNewMessages() > 0){
                holder.tvNewMessages.setText(String.valueOf(chatItems.get(position).getNewMessages()));
                holder.tvNewMessages.setVisibility(View.VISIBLE);
            } else {
                holder.tvNewMessages.setVisibility(View.GONE);
            }
        }


    }

    @Override
    public int getItemCount() {
        if (chatItems == null || chatItems.size() ==0 ){
            isEmpty = true;
            return 1;
        } else {
            isEmpty = false;
            return chatItems.size();
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public @BindView(R.id.card_chatItem_imageView) ImageView ivUserPic;
        public @BindView(R.id.card_chatItem_iv_thing1) ImageView ivThing1;
        public @BindView(R.id.card_chatItem_iv_thing2) ImageView ivThing2;
        @BindView(R.id.card_chatItem_tvName) TextView tvName;
        @BindView(R.id.card_chatItem_tvCount) TextView tvNewMessages;
        @BindView(R.id.card_chatItem_ic_exchange) ImageView iconExchange;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            assert mCallback != null;
            mCallback.onClick(
                    getLayoutPosition(),
                    chatItems.get(getLayoutPosition()).getExchangeId());
        }
    }

    public interface ChatsClickCallback {

        void onClick(int position, int id);
    }

}
