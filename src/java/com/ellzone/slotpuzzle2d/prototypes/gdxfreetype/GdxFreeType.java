package com.ellzone.slotpuzzle2d.prototypes.gdxfreetype;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;

public class GdxFreeType extends SPPrototype {

    private Stage stage;

    @Override
    public void create () {
        stage = new Stage(new ScreenViewport());
        int Help_Guides = 12;
        int row_height = Gdx.graphics.getWidth() / 12;
        int col_width = Gdx.graphics.getWidth() / 12;
        addBackgroundGuide(Help_Guides);

        Label.LabelStyle label1Style = new Label.LabelStyle();
        BitmapFont myFont = new BitmapFont(Gdx.files.internal("bitmapfont/Amble-Regular-26.fnt"));
        label1Style.font = myFont;
        label1Style.fontColor = Color.RED;

        Label label1 = new Label("Title (BitmapFont)",label1Style);
        label1.setSize(Gdx.graphics.getWidth(),row_height);
        label1.setPosition(0,Gdx.graphics.getHeight()-row_height*2);
        label1.setAlignment(Align.center);
        stage.addActor(label1);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("truetypefont/Amble-Light.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 30;
        parameter.borderWidth = 1;
        parameter.color = Color.YELLOW;
        parameter.shadowOffsetX = 3;
        parameter.shadowOffsetY = 3;
        parameter.shadowColor = new Color(0, 0.5f, 0, 0.75f);
        BitmapFont font24 = generator.generateFont(parameter); // font size 24 pixels
        generator.dispose();

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font24;

        Label label2 = new Label("True Type Font (.ttf) - Gdx FreeType",labelStyle);
        label2.setSize(Gdx.graphics.getWidth()/Help_Guides*5,row_height);
        label2.setPosition(col_width*2,Gdx.graphics.getHeight()-row_height*4);
        stage.addActor(label2);

        Skin mySkin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));

        Label label3 = new Label("This is a Label (skin) on  5 columns ",mySkin,"black");
        label3.setSize(Gdx.graphics.getWidth()/Help_Guides,row_height);
        label3.setPosition(col_width*2,Gdx.graphics.getHeight()-row_height*6);
        stage.addActor(label3);

        Label label4 = new Label("This is a Label (skin) with a 5 columns width but WITH wrap",mySkin,"black");
        label4.setSize(Gdx.graphics.getWidth()/Help_Guides*5,row_height);
        label4.setPosition(col_width*2,Gdx.graphics.getHeight()-row_height*7);
        label4.setWrap(true);
        stage.addActor(label4);
    }

    public void addBackgroundGuide(int columns){
        Texture texture = new Texture(Gdx.files.internal("background.jpg"));
        texture.setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.MirroredRepeat);

        TextureRegion textureRegion = new TextureRegion(texture);
        textureRegion.setRegion(0,0,texture.getWidth()*columns,texture.getWidth()*columns);
        Image background = new Image(textureRegion);
        background.setSize(Gdx.graphics.getWidth(),Gdx.graphics.getWidth());
        background.setPosition(0,Gdx.graphics.getHeight()-background.getHeight());
        stage.addActor(background);
    }

    @Override
    public void render () {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
    }
}
