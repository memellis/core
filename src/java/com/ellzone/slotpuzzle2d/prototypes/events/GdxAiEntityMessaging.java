package com.ellzone.slotpuzzle2d.prototypes.events;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;

import static com.ellzone.slotpuzzle2d.SlotPuzzleConstants.VIRTUAL_HEIGHT;
import static com.ellzone.slotpuzzle2d.SlotPuzzleConstants.VIRTUAL_WIDTH;

public class GdxAiEntityMessaging extends SPPrototype {
    private FitViewport viewport;
    private SenderReceiverEntity1 senderReceiverEntity1;
    private SenderReceiverEntity2 senderReceiverEntity2;
    private MessageManager messageManager;

    public void create() {
        OrthographicCamera camera = setupCamera();
        setUpEntities();
        setUpScreen(camera);
        setUpMessages();
    }

    private void setUpEntities() {
        senderReceiverEntity1 = new SenderReceiverEntity1();
        senderReceiverEntity2 = new SenderReceiverEntity2();
    }

    private void setUpMessages() {
        messageManager = MessageManager.getInstance();
        messageManager.addListeners(senderReceiverEntity1,
                                    MessageType.AddedCompoenent.index,
                                    MessageType.RemovedComponent.index);
        messageManager.addListeners(senderReceiverEntity2,
                                    MessageType.AddedCompoenent.index,
                                    MessageType.RemovedComponent.index);
    }

    private OrthographicCamera setupCamera() {
        OrthographicCamera camera = new OrthographicCamera(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
        camera.update();
        return camera;
    }

    private void setUpScreen(OrthographicCamera camera) {
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        update(Gdx.graphics.getDeltaTime());
    }

    private void update(float deltaTime) {
        if (Gdx.input.isKeyPressed(Input.Keys.A))
            messageManager.dispatchMessage(MessageType.AddedCompoenent.index);
        if (Gdx.input.isKeyPressed(Input.Keys.R))
            messageManager.dispatchMessage(MessageType.RemovedComponent.index);
    }
}