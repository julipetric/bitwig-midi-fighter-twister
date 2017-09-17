package com.hjaxel.page;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.MidiOut;
import com.bitwig.extension.controller.api.Transport;
import com.hjaxel.framework.MidiChannel;
import com.hjaxel.framework.MidiMessage;

/**
 * Created by axel on 2017-09-16.
 */
public abstract class MidiListener {

    private final ControllerHost host;
    private final MidiOut outPort;
    private final Transport transport;
    private final CursorTrack cursorTrack;

    public MidiListener(ControllerHost host) {
        this.host = host;
        this.outPort = host.getMidiOutPort(0);
        transport = host.createTransport();
        cursorTrack = host.createCursorTrack("2f9fce85-6a96-46a7-b8b4-ad097ee13f9d", "cursor-track", 0, 0, true);
    }

    protected final void print(ShortMidiMessage msg) {
        host.println(String.format("S[%s] C[%s] D1[%s] D2[%s]", msg.getStatusByte(), msg.getChannel(), msg.getData1(), msg.getData2()));
    }

    protected final void sendTurnOn(MidiChannel channel, int cc){
        midiOut().sendMidi(channel.value(), cc, 127);
    }

    protected final void sendTurnOff(MidiChannel channel, int cc){
        midiOut().sendMidi(channel.value(), cc, 0);
    }

    protected final void sendValue(MidiChannel channel, int cc, int value){
        if(value < 0 || value > 127){
            print(String.format("Invalid message not sent. c: %s, cc: %s, v: %s", channel, cc, value));
            return;
        }
        midiOut().sendMidi(channel.value(), cc, value);
    }

    protected final void print(String s){
        host().println(s);
    }

    protected CursorTrack cursorTrack() {
        return cursorTrack;
    }

    protected final Transport transport() {
        return transport;
    }

    protected final MidiOut midiOut() {
        return outPort;
    }

    protected final ControllerHost host() {
        return host;
    }

    public final void onMessage(ShortMidiMessage midiMessage) {
        MidiMessage message = new MidiMessage(MidiChannel.from(midiMessage.getChannel()), midiMessage.getData1(), midiMessage.getData2());
        print(message.toString());
        accept(message);
    }

    protected abstract boolean accept(MidiMessage midiMessage);

}
