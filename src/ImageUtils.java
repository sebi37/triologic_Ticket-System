import java.awt.Image;

public class ImageUtils {
    public static final int MAX_WIDTH = 200;
    public static final int MAX_HEIGHT = 200;

    public static Image getScaledImage(Image srcImg, int maxWidth, int maxHeight) {
        int width = srcImg.getWidth(null);
        int height = srcImg.getHeight(null);
        double aspectRatio = (double) width / height;

        if (width > maxWidth) {
            width = maxWidth;
            height = (int) (width / aspectRatio);
        }

        if (height > maxHeight) {
            height = maxHeight;
            width = (int) (height * aspectRatio);
        }

        return srcImg.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }
}