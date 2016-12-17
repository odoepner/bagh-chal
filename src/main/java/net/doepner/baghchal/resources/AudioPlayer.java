package net.doepner.baghchal.resources;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.net.URL;

import static javax.sound.sampled.AudioSystem.getAudioInputStream;
import static javax.sound.sampled.AudioSystem.getLine;
import static javax.sound.sampled.LineEvent.Type.STOP;

/**
 *
 */
public class AudioPlayer {

    private Clip lastClip = null;

    public void play(URL url) {
        if (lastClip != null) {
//            lastClip.stop();
            lastClip = null;
        }
        try (AudioInputStream stream = getAudioInputStream(url)) {

            final Clip clip = (Clip) getLine(new Info(Clip.class, stream.getFormat()));

            clip.open(stream);
            clip.start();

            clip.addLineListener(event -> {
                if (STOP.equals(event.getType())) {
                    clip.close();
                }
            });

            lastClip = clip;

        } catch (UnsupportedAudioFileException | LineUnavailableException e) {
            throw new IllegalArgumentException(e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}