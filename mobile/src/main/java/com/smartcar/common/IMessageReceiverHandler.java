package com.smartcar.common;

import com.smartcar.core.MessageId;

/**
 * Created by Mathew on 5/17/2015.
 */
public interface IMessageReceiverHandler {
    public void onMessageReceived(MessageId id, String message);
}
