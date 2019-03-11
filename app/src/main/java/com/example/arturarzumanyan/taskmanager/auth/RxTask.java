package com.example.arturarzumanyan.taskmanager.auth;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import io.reactivex.SingleEmitter;

public class RxTask<T> implements OnSuccessListener<T>, OnFailureListener {

    private final SingleEmitter<? super T> emitter;

    private RxTask(SingleEmitter<? super T> observer) {
        this.emitter = observer;
    }

    public static <T> void assignOnTask(SingleEmitter<? super T> observer, Task<T> task) {
        RxTask handler = new RxTask(observer);
        task.addOnSuccessListener(handler);
        task.addOnFailureListener(handler);
    }

    @Override
    public void onSuccess(T res) {
        if (!emitter.isDisposed()) {
            emitter.onSuccess(res);
        }
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        if (!emitter.isDisposed()) {
            emitter.onError(e);
        }
    }
}
