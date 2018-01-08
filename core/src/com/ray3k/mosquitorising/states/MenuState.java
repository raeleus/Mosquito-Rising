/*
 * The MIT License
 *
 * Copyright 2017 Raymond Buckley.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.ray3k.mosquitorising.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ray3k.mosquitorising.Core;
import com.ray3k.mosquitorising.State;

public class MenuState extends State {
    private Stage stage;
    private Skin skin;
    private Table root;

    public MenuState(Core core) {
        super(core);
    }
    
    @Override
    public void start() {
        skin = Core.assetManager.get(Core.DATA_PATH + "/ui/mosquito-rising.json", Skin.class);
        stage = new Stage(new ScreenViewport());
        
        Gdx.input.setInputProcessor(stage);
        
        createMenu();
    }
    
    private void createMenu() {
        root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
        
        Image image = new Image(skin, "title");
        image.setScaling(Scaling.none);
        root.add(image).colspan(2).expand();
        
        root.defaults().space(30.0f).expand().top();
        root.row();
        TextButton textButtton = new TextButton("Play", skin);
        root.add(textButtton);
        
        textButtton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Core.assetManager.get(Core.DATA_PATH + "/sfx/beep.wav", Sound.class).play(.25f);
                showAIdialog();
            }
        });
        
        textButtton = new TextButton("Quit", skin);
        root.add(textButtton);
        
        textButtton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Core.assetManager.get(Core.DATA_PATH + "/sfx/beep.wav", Sound.class).play(.25f);
                Gdx.app.exit();
            }
        });
    }
    
    @Override
    public void draw(SpriteBatch spriteBatch, float delta) {
        Gdx.gl.glClearColor(66 / 255.0f, 27 / 255.0f, 18 / 255.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    @Override
    public void act(float delta) {
        stage.act(delta);
    }

    @Override
    public void dispose() {
        
    }

    @Override
    public void stop() {
        stage.dispose();
    }
    
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    
    public void showAIdialog() {
        final Slider slider = new Slider(0f, 1f, .01f, false, skin);
        
        Dialog dialog = new Dialog("Choose AI Behaviors", skin) {
            @Override
            protected void result(Object object) {
                if ((Boolean) object) {
                    GameState.chance = slider.getValue();
                    Core.stateManager.loadState("game");
                }
            }
            
        };
        
        dialog.getContentTable().defaults().space(20.0f);
        Label label = new Label("Stationary", skin);
        dialog.getContentTable().add(label);
        
        slider.setValue(GameState.chance);
        dialog.getContentTable().add(slider).growX();
        
        label = new Label("Chasing", skin);
        dialog.getContentTable().add(label);
        
        dialog.button("OK", true).button("Cancel", false);
        dialog.key(Keys.ENTER, true).key(Keys.ESCAPE, false);
        
        dialog.show(stage);
        dialog.setSize(600.0f, 150.0f);
        dialog.setPosition(Gdx.graphics.getWidth() / 2.0f, Gdx.graphics.getHeight() / 2.0f, Align.center);
    }
}