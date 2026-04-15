package opendoja.host;

import java.awt.*;

public record ExternalFrameLayout(
        boolean enabled,
        double scale,
        Rectangle screenArea,
        Rectangle drawArea,
        Rectangle topBar,
        Rectangle bottomBar,
        Rectangle leftConnector,
        Rectangle rightConnector,
        Rectangle statusArea,
        Rectangle softKeyArea,
        Dimension preferredSize
) {
}
