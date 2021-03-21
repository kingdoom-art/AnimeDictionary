package com.example.animedictionary.tools.rx;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.SingleTransformer;

public final class Transformer {
    @SuppressWarnings("unchecked")
    public static <TResult> SingleTransformer<TResult, TResult> actionBasicScheduler() {
        return (SingleTransformer<TResult, TResult>) ACTION_BASIC_SCHEDULER;
    }

    private static final SingleTransformer<?, ?> ACTION_BASIC_SCHEDULER = single ->
        single.observeOn(AndroidSchedulers.mainThread());


    private Transformer() {
    }
}