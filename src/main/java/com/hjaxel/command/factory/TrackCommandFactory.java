/*
 *     Bitwig Extension for Midi Fighter Twister
 *     Copyright (C) 2017 Axel Hjälm
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>
 */

package com.hjaxel.command.factory;

import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.SettableColorValue;
import com.bitwig.extension.controller.api.Track;
import com.bitwig.extension.controller.api.TrackBank;
import com.hjaxel.UserSettings;
import com.hjaxel.command.BitwigCommand;
import com.hjaxel.command.track.*;
import com.hjaxel.framework.ColorMap;
import com.hjaxel.framework.MidiFighterTwister;
import com.hjaxel.navigation.CursorNavigator;

import java.util.function.Consumer;

public class TrackCommandFactory {

    private final CursorNavigator trackNavigation;
    private final CursorTrack track;
    private final TrackBank trackBank;
    private MidiFighterTwister twister;
    private final ColorMap colorMap;

    public TrackCommandFactory(CursorTrack track, TrackBank trackBank, UserSettings settings, MidiFighterTwister twister) {
        this.track = track;
        this.track.color().markInterested();
        this.trackBank = trackBank;
        this.twister = twister;
        trackNavigation = new CursorNavigator(track, settings);
        colorMap = new ColorMap();
    }

    public PanCommand pan(double value){
        return new PanCommand(track, value);
    }

    public BitwigCommand volume(int trackNo, int delta, double scale){
        return () -> {
            Track item = trackBank.getItemAt(trackNo);
            item.volume().inc(delta, scale);
        };
    }

    public BitwigCommand volume(int trackNo, double value){
        return () -> {

            Track item = trackBank.getItemAt(trackNo);

            item.volume().set(value, 128);
        };
    }

    public VolumeCommand volume(double value){
        return new VolumeCommand(track, value);
    }

    public MuteCommand mute(){
        return new MuteCommand(track);
    }

    public BitwigCommand solo() {
        return new SoloCommand(track);
    }

    public BitwigCommand panReset() {
        return new ResetPanCommand(track);
    }

    public BitwigCommand scroll(int direction) {
        return () ->{
                trackNavigation.onChange(64 + direction);
                ColorMap.TwisterColor twisterColor = colorMap.get(track.color().red(), track.color().green(), track.color().blue());
                twister.color(0, twisterColor.twisterValue);
                twister.color(1, twisterColor.twisterValue);
                twister.color(2, twisterColor.twisterValue);
                twister.color(32, twisterColor.twisterValue);
                twister.color(33, twisterColor.twisterValue);
                twister.color(34, twisterColor.twisterValue);
        };
    }

    public BitwigCommand send(int sendNo, int velocity, Consumer<String> c) {
        return new SendCommand(track, sendNo, velocity, c);
    }

    public BitwigCommand next() {
        return trackBank::scrollPageForwards;
    }

    public BitwigCommand previous() {
        return trackBank::scrollPageBackwards;
    }

    public void color(int direction) {
        SettableColorValue color = track.color();
        ColorMap.TwisterColor twisterColor = colorMap.get(color.red(), color.green(), color.blue());
        ColorMap.TwisterColor newColor = colorMap.get(twisterColor.twisterValue + 1);
        track.color().set(newColor.red, newColor.green, newColor.blue);
    }
}
