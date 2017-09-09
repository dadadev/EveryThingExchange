package com.dadabit.everythingexchange.ui.presenter.chat;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dadabit.everythingexchange.R;
import com.dadabit.everythingexchange.model.SingleChatActivityRepo;
import com.dadabit.everythingexchange.model.vo.ChatMessageItem;
import com.dadabit.everythingexchange.model.vo.User;
import com.dadabit.everythingexchange.ui.adapter.MessagesAdapter;
import com.dadabit.everythingexchange.ui.presenter.BasePresenter;
import com.dadabit.everythingexchange.utils.ChatItemsManager;
import com.dadabit.everythingexchange.utils.Constants;
import com.dadabit.everythingexchange.utils.Dialogs;
import com.dadabit.everythingexchange.utils.Utils;

import java.util.Calendar;
import java.util.List;


public class SingleChatActivityPresenter extends BasePresenter<SingleChatActivityView> implements View.OnClickListener{


    private SingleChatActivityRepo mRepository;

    private MessagesAdapter mAdapter;

    private Handler uiHandler;


    public SingleChatActivityPresenter() {
        Log.d("@@@", "SingleChatActivityPresenter.create");
        mRepository = new SingleChatActivityRepo();

    }


    @Override
    public void attachView(SingleChatActivityView singleChatActivityView) {
        super.attachView(singleChatActivityView);
        Log.d("@@@", "SingleChatActivityPresenter.attachView");

        uiHandler = new Handler();

        setHandler(new Handler(getLooper()));

        if (mRepository.getExchange() != null){

            setContent();

            setAdapter();

            mRepository.attachListener(changesListener);

            initInput();

            if (mRepository.getExchange().getStatus() == Constants.EXCHANGE_STATUS_ENDED){

                Dialogs.getAlertDialog(getView().getActivityContext(),
                        mRepository.getExchange().getThing2_ownerName()+" has ended the exchange.\nDo you confirm it?\n(It removes your thing from the system)",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                getHandler().post(new Runnable() {
                                    @Override
                                    public void run() {

                                        mRepository.removeExchange();

                                    }
                                });

                            }
                        },
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                getHandler().post(new Runnable() {
                                    @Override
                                    public void run() {

                                        mRepository.cancelExchange(false);

                                    }
                                });
                            }
                        }).show();

            }

        }
    }

    private ChatItemsManager.NewMessageCallback changesListener = new ChatItemsManager.NewMessageCallback() {
        @Override
        public void onMessagesLoad(List<ChatMessageItem> messages) {

            if (messages == null || messages.isEmpty()){
                loadData();
            } else {
                mRepository.setChatItems(messages);
            }


            if (isViewAttached() && mAdapter != null){

                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        getView().getProgressBar().setVisibility(View.GONE);

                        mAdapter.setChatItems(mRepository.getChatItems());
                        getView().getRecyclerView().scrollToPosition(mAdapter.getItemCount()-1);
                    }
                });

            }

        }

        @Override
        public void onNewMessage(ChatMessageItem message) {
            Log.d("@@@", "SingleChatActivityPresenter.onNewMessage.message: "+message.getMessage());

            if (isViewAttached()){

                mAdapter.notifyItemInserted(mAdapter.getItemCount()-1);
                getView().getRecyclerView().scrollToPosition(mAdapter.getItemCount()-1);

            }
        }

        @Override
        public void onDateChanged(final long newDate) {
            Log.d("@@@", "SingleChatActivityPresenter.onDateChanged");

            mRepository.getExchange().setEndDate(newDate);

            final String date = String.format("Exchange date: %s",
                    Utils.timestampToString(newDate));

            Log.d("@@@", "SingleChatActivityPresenter.changeChatEndDate "+ date);

            uiHandler.post(new Runnable() {
                @Override
                public void run() {

                    if (isViewAttached()){
                        getView().animateDateChange(date);
                    }
                }
            });


        }

        @Override
        public void onChatRemoved() {

            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (isViewAttached()){

                        getView().backButtonPressed();

                    }

                }
            });

        }
    };



    private void setContent() {

        Glide.with(getView().getActivityContext())
                .load(mRepository.getExchange().getThing2_ownerImg())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(getView().getCompanionImageView());

        Glide.with(getView().getActivityContext())
                .load(mRepository.getExchange().getThing1_img())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(getView().getCardThing1ImageView());


        Glide.with(getView().getActivityContext())
                .load(mRepository.getExchange().getThing2_img())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(getView().getCardThing2ImageView());


        getView().getDateTextSwitcher()
                .setText(String.format("Exchange date: %s",
                        Utils.timestampToString(mRepository.getExchange().getEndDate())));



        getView().getCompanionImageView().setOnClickListener(this);
        getView().getCardThing1ImageView().setOnClickListener(this);
        getView().getCardThing2ImageView().setOnClickListener(this);

        getView().getFab().setOnClickListener(this);

    }

    private void setAdapter() {

        getView().getRecyclerView().setLayoutManager(
                new LinearLayoutManager(getView().getActivityContext(),
                        LinearLayoutManager.VERTICAL, false));

        getView().getRecyclerView().setAdapter(
                mAdapter = new MessagesAdapter(getView().getAppContext()));

        if (mRepository.getChatItems() == null || mRepository.getChatItems().isEmpty()){

            loadData();

            getView().getProgressBar().setVisibility(View.VISIBLE);

        } else {
            mAdapter.setChatItems(mRepository.getChatItems());
            getView().getRecyclerView().scrollToPosition(mAdapter.getItemCount()-1);
        }


    }

    private int counter = 0;
    private void loadData() {

        if (counter == 10){

            getView().getProgressBar().setVisibility(View.GONE);


        } else {
            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    counter++;

                    mRepository.loadChatItems();
                }
            }, 5 * 1000);
        }
    }


    private void initInput() {

        getView().getButton().setOnClickListener(this);
        getView().getButton().setEnabled(false);
        getView().getButton().setClickable(false);
        getView().getButton().setColorFilter(Color.argb(255, 224, 224, 224));


        getView().getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() > 0){

                    getView().getButton().setEnabled(true);
                    getView().getButton().setClickable(true);
                    getView().getButton().clearColorFilter();


                } else {

                    getView().getButton().setEnabled(false);
                    getView().getButton().setClickable(false);
                    getView().getButton().setColorFilter(Color.argb(255, 224, 224, 224));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }



    @Override
    public void detachView() {
        super.detachView();
        uiHandler = null;
        mRepository.detachListener();
        mRepository.cleanChatCounter();
    }

    public void detachRepository() {

        if (mRepository != null){
            mRepository = null;
        }
    }


    @Override
    public void onClick(View v) {


        switch (v.getId()){

            case R.id.single_chat_fab_sendButton:

                mRepository.sendMessage(getView().getEditText().getText().toString());

                getView().getEditText().getText().clear();

                break;

            case R.id.single_chat_toolbar_ivThing1:

                Dialogs.getUrlImageDialog(
                        getView().getActivityContext(),
                        mRepository.getExchange().getThing1_img())
                        .show();


                break;

            case R.id.single_chat_toolbar_ivThing2:

                Dialogs.getUrlImageDialog(
                        getView().getActivityContext(),
                        mRepository.getExchange().getThing2_img())
                        .show();

                break;


            case R.id.single_chat_companionImg:

                getView().startPersonInfoActivity(
                        new User(
                                mRepository.getExchange().getThing2_ownerId(),
                                "",
                                mRepository.getExchange().getThing2_ownerName(),
                                mRepository.getExchange().getThing2_ownerImg(),
                                "",
                                ""));

                break;

            case R.id.single_chat_fab:

                getView().getAppBarLayout().setExpanded(false, true);

                getView().getEditText().requestFocus();

                InputMethodManager imm = (InputMethodManager)
                        getView().getActivityContext().getSystemService(Context.INPUT_METHOD_SERVICE);

                imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (isViewAttached()){

                            getView().getRecyclerView().smoothScrollToPosition(
                                    getView().getRecyclerView().getAdapter().getItemCount()-1);
                        }

                    }
                }, 300);


                break;
        }

    }


    public void onOptionItemSelected(int menuItemId) {

        setHandler(new Handler(getLooper()));


        switch (menuItemId){

            case R.id.menu_chats_change_date:

                Dialogs.showDateDialog(
                        getView().getActivityContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                                Calendar chosenDate = Calendar.getInstance();
                                chosenDate.set(year, month, dayOfMonth);

                                final long newDate = chosenDate.getTimeInMillis();

                                getHandler().post(new Runnable() {
                                    @Override
                                    public void run() {

                                        mRepository.changeDate(newDate);

                                    }
                                });
                            }
                        });

                break;

            case R.id.menu_chats_cancel_exchange:


                Dialogs.getAlertDialog(getView().getActivityContext(),
                        "Are you sure you want to cancel exchange?",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                getHandler().post(new Runnable() {
                                    @Override
                                    public void run() {

                                        mRepository.cancelExchange(true);

                                    }
                                });

                            }
                        }, null).show();

                break;

            case R.id.menu_chats_end_exchange:


                Dialogs.getAlertDialog(getView().getActivityContext(),
                        "End this exchange? \n\n(It removes your thing from the system)",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                getHandler().post(new Runnable() {
                                    @Override
                                    public void run() {

                                        mRepository.endExchange();

                                    }
                                });

                            }
                        }, null).show();

                break;

        }

    }
}
