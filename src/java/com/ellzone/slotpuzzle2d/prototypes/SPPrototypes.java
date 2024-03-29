/*
 Copyright 2011 See AUTHORS file.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.ellzone.slotpuzzle2d.prototypes;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.ellzone.slotpuzzle2d.prototypes.animation.AnimateBombViaSpriteSheet;
import com.ellzone.slotpuzzle2d.prototypes.animation.AnimatedBomb;
import com.ellzone.slotpuzzle2d.prototypes.animation.AnimatedCoin;
import com.ellzone.slotpuzzle2d.prototypes.animation.AnimatedReelPrototype;
import com.ellzone.slotpuzzle2d.prototypes.artemis.ArtemisOdbQuickStart;
import com.ellzone.slotpuzzle2d.prototypes.artemis.RenderReelsUsingArtemis;
import com.ellzone.slotpuzzle2d.prototypes.artemis.TweenUsingArtemis;
import com.ellzone.slotpuzzle2d.prototypes.ashley.RenderLightButtons;
import com.ellzone.slotpuzzle2d.prototypes.ashley.RenderMiniSllotMachineLoadedFromALevel;
import com.ellzone.slotpuzzle2d.prototypes.ashley.RenderReels;
import com.ellzone.slotpuzzle2d.prototypes.ashley.RenderSystemTest;
import com.ellzone.slotpuzzle2d.prototypes.audio.PlayAudio;
import com.ellzone.slotpuzzle2d.prototypes.audio.PlayMusic;
import com.ellzone.slotpuzzle2d.prototypes.basic2d.Basic2D;
import com.ellzone.slotpuzzle2d.prototypes.bezier.Bezier1;
import com.ellzone.slotpuzzle2d.prototypes.bezier.Bezier2;
import com.ellzone.slotpuzzle2d.prototypes.bezier.Bezier3;
import com.ellzone.slotpuzzle2d.prototypes.box2d.Box2DFallingReels;
import com.ellzone.slotpuzzle2d.prototypes.box2d.Box2DFallingSpinningReelsWithCatchBox;
import com.ellzone.slotpuzzle2d.prototypes.box2d.Box2DFallingTest;
import com.ellzone.slotpuzzle2d.prototypes.box2d.Box2DLights;
import com.ellzone.slotpuzzle2d.prototypes.box2d.Box2dFallingReelsWithCatchBox;
import com.ellzone.slotpuzzle2d.prototypes.box2d.ButtonLight;
import com.ellzone.slotpuzzle2d.prototypes.box2d.ButtonLightUsingLightButton;
import com.ellzone.slotpuzzle2d.prototypes.box2d.ButtonLightUsingLightButtonViaFrameBuffer;
import com.ellzone.slotpuzzle2d.prototypes.box2d.CollidingFallingReels;
import com.ellzone.slotpuzzle2d.prototypes.box2d.EdgeShapes;
import com.ellzone.slotpuzzle2d.prototypes.box2d.EdgeShapesBigCatchBox;
import com.ellzone.slotpuzzle2d.prototypes.box2d.Light;
import com.ellzone.slotpuzzle2d.prototypes.box2d.collisions.Box2DBoxesFallingFromSlotPuzzleMatrices;
import com.ellzone.slotpuzzle2d.prototypes.box2d.collisions.Box2dToggleActiveBoxBodies;
import com.ellzone.slotpuzzle2d.prototypes.box2d.effects.Box2dParticleEffects;
import com.ellzone.slotpuzzle2d.prototypes.camera.UnProjectCamera;
import com.ellzone.slotpuzzle2d.prototypes.cylinder.Render3DCylinder1;
import com.ellzone.slotpuzzle2d.prototypes.cylinder.Render3DCylinder2;
import com.ellzone.slotpuzzle2d.prototypes.cylinder.Render3DCylinder3;
import com.ellzone.slotpuzzle2d.prototypes.cylinder.Render3DCylinder4;
import com.ellzone.slotpuzzle2d.prototypes.cylinder.Render3DCylinder5;
import com.ellzone.slotpuzzle2d.prototypes.cylinder.Render3DCylinder6;
import com.ellzone.slotpuzzle2d.prototypes.cylinder.Render3DCylinderModelAsWireframe;
import com.ellzone.slotpuzzle2d.prototypes.cylinder.Render3DCylinderWithOptionalGrid;
import com.ellzone.slotpuzzle2d.prototypes.events.GdxAiEntityMessaging;
import com.ellzone.slotpuzzle2d.prototypes.framebuffer.FrameBufferParticleEffectSample;
import com.ellzone.slotpuzzle2d.prototypes.gdxfreetype.GdxFreeType;
import com.ellzone.slotpuzzle2d.prototypes.icons.DownloadMamuIcons;
import com.ellzone.slotpuzzle2d.prototypes.level.SuperSlotMachine.SuperSlotMachineECS;
import com.ellzone.slotpuzzle2d.prototypes.level.bombreel.BombReel;
import com.ellzone.slotpuzzle2d.prototypes.level.bombreel.ExplosionDemo;
import com.ellzone.slotpuzzle2d.prototypes.level.bombreel.ParticleEffectExplosion;
import com.ellzone.slotpuzzle2d.prototypes.level.hiddenpattern.HiddenPatternFallingReelsAnimatedReelsManager;
import com.ellzone.slotpuzzle2d.prototypes.level.hiddenpattern.HiddenPatternWithFallingReels;
import com.ellzone.slotpuzzle2d.prototypes.level.minislotmachine.MiniSlotMachineLevelFallingReels;
import com.ellzone.slotpuzzle2d.prototypes.level.minislotmachine.MiniSlotMachineLevelPrototype;
import com.ellzone.slotpuzzle2d.prototypes.level.minislotmachine.MiniSlotMachineLevelPrototypeSimpleScenario;
import com.ellzone.slotpuzzle2d.prototypes.level.minislotmachine.MiniSlotMachineLevelPrototypeWithLevelCreator;
import com.ellzone.slotpuzzle2d.prototypes.level.minislotmachine.SpinningSlots;
import com.ellzone.slotpuzzle2d.prototypes.level.minislotmachine.SpinningSlotsRotateHandleSprite;
import com.ellzone.slotpuzzle2d.prototypes.level.minislotmachine.SpinningSlotsRotateHandleSpriteUsingTweenEngine;
import com.ellzone.slotpuzzle2d.prototypes.level.minislotmachine.SpinningSlotsSetSizeOfSlots;
import com.ellzone.slotpuzzle2d.prototypes.level.minislotmachine.SpinningSlotsUsingSlotHandleSprite;
import com.ellzone.slotpuzzle2d.prototypes.level.minislotmachine.SpinningSlotsWithHoldButtons;
import com.ellzone.slotpuzzle2d.prototypes.level.minislotmachine.SpinningSlotsWithMatchesWin;
import com.ellzone.slotpuzzle2d.prototypes.level.minislotmachine.SpinningSlotsWithMatchesWinFlashes;
import com.ellzone.slotpuzzle2d.prototypes.level.minislotmachine.SpinningSlotsWithMatchesWinFlashesLoadedLevel;
import com.ellzone.slotpuzzle2d.prototypes.level.minislotmachine.SpinningSlotsWithThreeTilesDisplayed;
import com.ellzone.slotpuzzle2d.prototypes.map.SmoothScrollingWorldMap;
import com.ellzone.slotpuzzle2d.prototypes.map.SubPixelPerfectSmoothScrolling;
import com.ellzone.slotpuzzle2d.prototypes.map.WorldMapDynamicDoors;
import com.ellzone.slotpuzzle2d.prototypes.map.WorldMapLevelSelect;
import com.ellzone.slotpuzzle2d.prototypes.map.WorldMapLevelSelectAndReturn;
import com.ellzone.slotpuzzle2d.prototypes.menu.SlotPuzzleGame;
import com.ellzone.slotpuzzle2d.prototypes.particle.Particle1;
import com.ellzone.slotpuzzle2d.prototypes.particle.Particle1ExtendingParticleTemplate;
import com.ellzone.slotpuzzle2d.prototypes.particle.Particle2;
import com.ellzone.slotpuzzle2d.prototypes.particle.Particle2ExtendingParticleTemplate;
import com.ellzone.slotpuzzle2d.prototypes.particle.Particle3;
import com.ellzone.slotpuzzle2d.prototypes.particle.Particle3ExtendingParticleTemplate;
import com.ellzone.slotpuzzle2d.prototypes.particle.Particle4;
import com.ellzone.slotpuzzle2d.prototypes.particle.Particle4ExtendingParticleTemplate;
import com.ellzone.slotpuzzle2d.prototypes.particle.Particle5;
import com.ellzone.slotpuzzle2d.prototypes.particle.Particle5ExtendingParticleTemplate;
import com.ellzone.slotpuzzle2d.prototypes.particle.Particle6;
import com.ellzone.slotpuzzle2d.prototypes.particle.Particle6ExtendingParticleTemplate;
import com.ellzone.slotpuzzle2d.prototypes.scrollingsign.ScrollSignWithMultipleMessages;
import com.ellzone.slotpuzzle2d.prototypes.scrollingsign.ScrollingSign;
import com.ellzone.slotpuzzle2d.prototypes.scrollingsign.ScrollingSignDynamicSignChange;
import com.ellzone.slotpuzzle2d.prototypes.speechbubble.SpeechBubble;
import com.ellzone.slotpuzzle2d.prototypes.spin.SpinReelMetaSpinScreen;
import com.ellzone.slotpuzzle2d.prototypes.spin.SpinReelTileUsingBox2d;
import com.ellzone.slotpuzzle2d.prototypes.spin.SpinScreen;
import com.ellzone.slotpuzzle2d.prototypes.spin.SpinWheelUsingSpinWheelForSlotPuzzle;
import com.ellzone.slotpuzzle2d.prototypes.spin.SpinWheelWithoutStage;
import com.ellzone.slotpuzzle2d.prototypes.spin.SpinWheelWithoutStageChangeSize;
import com.ellzone.slotpuzzle2d.prototypes.starfield.Starfield;
import com.ellzone.slotpuzzle2d.prototypes.starfield.StarfieldUsingStarfieldObject;
import com.ellzone.slotpuzzle2d.prototypes.tween.DelayFlash;
import com.ellzone.slotpuzzle2d.prototypes.tween.Dynamic;
import com.ellzone.slotpuzzle2d.prototypes.tween.Flash;
import com.ellzone.slotpuzzle2d.prototypes.tween.GameOverPopUp;
import com.ellzone.slotpuzzle2d.prototypes.tween.IntroSequence;
import com.ellzone.slotpuzzle2d.prototypes.tween.IntroSequenceSpinningReelsWithBombSet;
import com.ellzone.slotpuzzle2d.prototypes.tween.IntroSequenceWithSpinningReels;
import com.ellzone.slotpuzzle2d.prototypes.tween.LevelOverPopUpUsingLevelPopUp;
import com.ellzone.slotpuzzle2d.prototypes.tween.LevelPopUp;
import com.ellzone.slotpuzzle2d.prototypes.tween.LevelPopUpUsingLevelPopUp;
import com.ellzone.slotpuzzle2d.prototypes.tween.ReelLetterTilePlay;
import com.ellzone.slotpuzzle2d.prototypes.tween.TileInputSelect;
import com.ellzone.slotpuzzle2d.prototypes.tween.Veil;
import com.ellzone.slotpuzzle2d.prototypes.tween.WayPoints1;
import com.ellzone.slotpuzzle2d.prototypes.tween.WayPoints2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SPPrototypes {
    public static final List<Class<? extends SPPrototype>> tests =
			new ArrayList<Class<? extends SPPrototype>>((Collection<? extends Class<? extends SPPrototype>>) Arrays.asList(
        AnimatedBomb.class,
		AnimatedCoin.class,
		AnimateBombViaSpriteSheet.class,
    	ArtemisOdbQuickStart.class,
		AnimatedReelPrototype.class,
        Basic2D.class,
        Bezier1.class,
        Bezier2.class,
        Bezier3.class,
		Box2DLights.class,
		ButtonLight.class,
		ButtonLightUsingLightButton.class,
        ButtonLightUsingLightButtonViaFrameBuffer.class,
        CollidingFallingReels.class,
		BombReel.class,
        Box2DBoxesFallingFromSlotPuzzleMatrices.class,
        Box2dToggleActiveBoxBodies.class,
        Box2DFallingTest.class,
        Box2DFallingReels.class,
        Box2dFallingReelsWithCatchBox.class,
        Box2DFallingSpinningReelsWithCatchBox.class,
		Box2dParticleEffects.class,
		DownloadMamuIcons.class,
        EdgeShapes.class,
        EdgeShapesBigCatchBox.class,
		ExplosionDemo.class,
		FrameBufferParticleEffectSample.class,
        GdxAiEntityMessaging.class,
        GdxFreeType.class,
        Light.class,
        SmoothScrollingWorldMap.class,
        SubPixelPerfectSmoothScrolling.class,
        WorldMapLevelSelect.class,
        WorldMapLevelSelectAndReturn.class,
        WorldMapDynamicDoors.class,
        MiniSlotMachineLevelPrototype.class,
        MiniSlotMachineLevelPrototypeSimpleScenario.class,
        MiniSlotMachineLevelPrototypeWithLevelCreator.class,
        MiniSlotMachineLevelFallingReels.class,
        HiddenPatternWithFallingReels.class,
        HiddenPatternFallingReelsAnimatedReelsManager.class,
		Particle1.class,
        Particle2.class,
        Particle3.class,
        Particle4.class,
        Particle5.class,
        Particle6.class,
		ParticleEffectExplosion.class,
		Particle1ExtendingParticleTemplate.class,																										   
		Particle2ExtendingParticleTemplate.class,					
		Particle3ExtendingParticleTemplate.class,																																														   //Dynamic.class,																						   
	    Particle4ExtendingParticleTemplate.class,																										   
		Particle5ExtendingParticleTemplate.class,
		Particle6ExtendingParticleTemplate.class,
        PlayAudio.class,
        PlayMusic.class,
        Render3DCylinder1.class,
        Render3DCylinder2.class,
        Render3DCylinder3.class,
        Render3DCylinder4.class,
        Render3DCylinder5.class,
        Render3DCylinder6.class,
        Render3DCylinderModelAsWireframe.class,
        Render3DCylinderWithOptionalGrid.class,
        RenderLightButtons.class,
        RenderMiniSllotMachineLoadedFromALevel.class,
        RenderReels.class,
        RenderSystemTest.class,
        ScrollingSign.class,
        ScrollSignWithMultipleMessages.class,
        ScrollingSignDynamicSignChange.class,
		SuperSlotMachineECS.class,
		Dynamic.class,																			   
		Flash.class,
        DelayFlash.class,
        GameOverPopUp.class,
        IntroSequence.class,
        IntroSequenceWithSpinningReels.class,
		IntroSequenceSpinningReelsWithBombSet.class,
        LevelOverPopUpUsingLevelPopUp.class,
        LevelPopUp.class,
        LevelPopUpUsingLevelPopUp.class,
		ReelLetterTilePlay.class,
        TileInputSelect.class,
		UnProjectCamera.class,
        Veil.class,
        WayPoints1.class,
        WayPoints2.class,
        RenderReelsUsingArtemis.class,
	    SpeechBubble.class,
		SpinReelTileUsingBox2d.class,
	    SpinningSlots.class,
		SpinningSlotsRotateHandleSprite.class,
		SpinningSlotsRotateHandleSpriteUsingTweenEngine.class,
		SpinningSlotsSetSizeOfSlots.class,
		SpinningSlotsUsingSlotHandleSprite.class,
		SpinningSlotsWithThreeTilesDisplayed.class,
        SpinningSlotsWithHoldButtons.class,
        SpinningSlotsWithMatchesWin.class,
        SpinningSlotsWithMatchesWinFlashes.class,
        SpinningSlotsWithMatchesWinFlashesLoadedLevel.class,
		SpinScreen.class,
		SpinWheelWithoutStage.class,
		SpinWheelWithoutStageChangeSize.class,
		SpinWheelUsingSpinWheelForSlotPuzzle.class,
		SpinReelMetaSpinScreen.class,
		SlotPuzzleGame.class,
        Starfield.class,
        StarfieldUsingStarfieldObject.class,
		TweenUsingArtemis.class
    ));

    public static List<String> getNames () {
        List<String> names = new ArrayList<String>(tests.size());
        for (Class clazz : tests)
            names.add(clazz.getSimpleName());
        Collections.sort(names);
        return names;
    }

    private static Class<? extends SPPrototype> forName(String name) {
        for (Class clazz : tests)
            if (clazz.getSimpleName().equals(name)) return clazz;
        return null;
    }

    public static SPPrototype newSPPrototype(String testName) throws InstantiationException, IllegalAccessException {
        try {
            return ClassReflection.newInstance(forName(testName));
        } catch (ReflectionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
