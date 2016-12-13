package net.doepner.baghchal.resources;

import net.doepner.baghchal.model.Piece;

import java.awt.image.BufferedImage;

/**
 * Loads images from classpath
 */
public interface Images {

    enum ImageId {BACKGROUND, CONGRATS }

    BufferedImage getImage(Piece piece);

    BufferedImage getImage(ImageId imageId);

}
