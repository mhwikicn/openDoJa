package opendoja.probes;

import com.nttdocomo.ui.graphics3d.Primitive;
import opendoja.g3d.Software3DContext;
import opendoja.g3d.SoftwareTexture;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public final class PreciseTextureCoordinatesProbe {
    private PreciseTextureCoordinatesProbe() {
    }

    public static void main(String[] args) {
        DemoLog.enableInfoLogging();
        BufferedImage target = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = target.createGraphics();
        try {
            Software3DContext context = new Software3DContext();
            context.setUiParallelView(32, 32);
            context.renderUiPrimitive(
                    graphics,
                    target,
                    0,
                    0,
                    32,
                    32,
                    Primitive.PRIMITIVE_TRIANGLES,
                    Primitive.TEXTURE_COORD_PER_VERTEX,
                    1,
                    new int[]{
                            -14, 14, 0,
                            14, 14, 0,
                            0, -14, 0
                    },
                    null,
                    new int[]{
                            0, 0,
                            0, 0,
                            0, 0
                    },
                    makeTexture(),
                    new float[]{
                            1.2f, 0f,
                            1.2f, 0f,
                            1.2f, 0f
                    },
                    Software3DContext.identity(),
                    0,
                    1f,
                    false,
                    0f,
                    0f,
                    true,
                    true,
                    true
            );
        } finally {
            graphics.dispose();
        }
        int pixel = target.getRGB(16, 16);
        if ((pixel & 0x00FFFFFF) != 0x00FFFFFF) {
            throw new IllegalStateException(String.format(
                    "precise texture coordinates were not used: pixel=%08x", pixel));
        }
        DemoLog.info(PreciseTextureCoordinatesProbe.class, String.format("pixel=%08x", pixel));
    }

    private static SoftwareTexture makeTexture() {
        return SoftwareTexture.fromIndexed(2, 1, new int[]{0xFF000000, 0xFFFFFFFF}, new byte[]{0, 1}, true);
    }
}
