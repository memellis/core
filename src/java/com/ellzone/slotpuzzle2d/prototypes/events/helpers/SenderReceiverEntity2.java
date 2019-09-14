package com.ellzone.slotpuzzle2d.prototypes.events.helpers;

import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.ellzone.slotpuzzle2d.prototypes.events.helpers.MessageType;

import java.text.MessageFormat;

public class SenderReceiverEntity2 implements Telegraph {
    MessageDispatcher messageDispatcher = MessageManager.getInstance();
    public SenderReceiverEntity2() {
    }

    @Override
    public boolean handleMessage(Telegram message) {
        if (message.message == MessageType.RemovedComponent.index) {
            System.out.println(MessageFormat.format("{0} removed component", this.getClass().getSimpleName()));
        }
        return true;
    }
}
