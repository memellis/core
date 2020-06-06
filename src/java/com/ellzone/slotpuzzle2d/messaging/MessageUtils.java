package com.ellzone.slotpuzzle2d.messaging;

import com.badlogic.gdx.ai.msg.MessageManager;
import com.ellzone.slotpuzzle2d.physics.contact.AnimatedReelsManager;

import static com.ellzone.slotpuzzle2d.messaging.MessageType.ReelSinkReelsLeftToFall;
import static com.ellzone.slotpuzzle2d.messaging.MessageType.ReelsLeftToFall;
import static com.ellzone.slotpuzzle2d.messaging.MessageType.SwapReelsAboveMe;

public class MessageUtils {
    public static MessageManager setUpMessageManager(AnimatedReelsManager animatedReelsManager) {
        MessageManager messageManager = MessageManager.getInstance();
        messageManager.addListeners(
                animatedReelsManager,
                SwapReelsAboveMe.index,
                ReelsLeftToFall.index,
                ReelSinkReelsLeftToFall.index);
        return messageManager;
    }
}
