package life.qbic.projectbrowser.samplegraph;

import java.util.List;

import com.vaadin.shared.communication.ServerRpc;

import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.Sample;

public interface NodeClickRpc extends ServerRpc {
//  public void onNodeClick(List<String> entries);
  public void onCircleClick(String label, List<String> sampleCodes);
}