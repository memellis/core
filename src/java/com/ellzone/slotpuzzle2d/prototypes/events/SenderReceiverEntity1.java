package com.ellzone.slotpuzzle2d.prototypes.events;

import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;

import java.text.MessageFormat;

public class SenderReceiverEntity1 implements Telegraph {
    MessageDispatcher messageDispatcher = MessageManager.getInstance();
    public SenderReceiverEntity1() {
    }

    @Override
    public boolean handleMessage(Telegram message) {
        if (message.message == MessageType.AddedCompoenent.index) {
            System.out.println(MessageFormat.format("{0} added component",this.getClass().getSimpleName()));
            messageDispatcher.dispatchMessage(MessageType.RemovedComponent.index);
            return true;
        }
        return false;
    }
}
