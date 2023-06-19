import javax.swing.*;
import java.awt.*;

public interface Theme {
    Color getPrimaryColor();
    Color getSecondaryColor();
    Color getAccentColor();
    Color getTextColor();
    Color getBackgroundColor();
    ImageIcon getThemeIcon();
}