package life.qbic.portal.portlet;

import life.qbic.projectbrowser.components.LevelComponent;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link LevelComponent}.
 */
public class LevelComponentTest {

    @Test
    public void SampleTypeIsBlacklisted() {
        assertTrue(LevelComponent.isSampleTypeBlacklisted("Q_NGS_NANOPORE_SINGLE_SAMPLE_RUN"));
        assertTrue(LevelComponent.isSampleTypeBlacklisted("Q_MS_RUN"));
    }

    @Test
    public void SampleTypeIsNotBlacklisted() {
        assertFalse(LevelComponent.isSampleTypeBlacklisted("Q_NOT_BLACKLISTED_TYPE"));
    }
}
