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
    public void SampleTypeIsRaw() {
        assertTrue(LevelComponent.isSampleTypeForRawData("Q_NGS_NANOPORE_SINGLE_SAMPLE_RUN"));
        assertTrue(LevelComponent.isSampleTypeForRawData("Q_MS_RUN"));
    }

    @Test
    public void SampleTypeIsNotRaw() {
        assertFalse(LevelComponent.isSampleTypeForRawData("Q_NOT_BLACKLISTED_TYPE"));
    }


    @Test
    public void SampleTypeIsProcessed() {
        assertTrue(LevelComponent.isSampleTypeForProcessedData("Q_WF_MS_MAXQUANT_RUN"));
        assertTrue(LevelComponent.isSampleTypeForProcessedData("Q_WF_MS_LIGANDOMICS_ID_RUN"));
    }

    @Test
    public void SampleTypeIsNotProcessed() {
        assertFalse(LevelComponent.isSampleTypeForProcessedData("Q_MS_RUN"));
    }
}
