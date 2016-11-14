package com.example.doctorsbuilding.nav.Question;

import android.content.Intent;

/**
 * Created by hossein on 11/8/2016.
 */
public enum ReplyType {
    selection(0),
    text(1);

    private final int replyType;
    private static final String name = ReplyType.class.getName();

    ReplyType(int replyType) {
        this.replyType = replyType;
    }

    public int getReplyType() {
        return this.replyType;
    }

    public void attachTo(Intent intent) {
        intent.putExtra(name, ordinal());
    }

    public static ReplyType detachFrom(Intent intent) {
        if (!intent.hasExtra(name)) throw new IllegalStateException();
        return values()[intent.getIntExtra(name, -1)];
    }
}
