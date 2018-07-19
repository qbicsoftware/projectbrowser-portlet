package life.qbic.projectbrowser.samplegraph;

import java.util.List;

import com.vaadin.annotations.JavaScript;
import com.vaadin.ui.AbstractJavaScriptComponent;

@JavaScript({"vaadin://js/d3.v4.min.js", "graph_connector.js", "vaadin://js/dagre.min.js", "vaadin://js/d3-scale-chromatic.v1.min.js"})
public class ProjectGraph extends AbstractJavaScriptComponent {

  @Override
  public ProjectGraphState getState() {
    return (ProjectGraphState) super.getState();
  }

  public void setProject(final List<SampleSummary> list) {
    getState().setProject(list);
  }
  
  public ProjectGraph(final GraphPage layout, String imagePath) {
    getState().setImagePath(imagePath);

    registerRpc(new NodeClickRpc() {
//      public void onNodeClick(List<String> nodeEntries) {
//        layout.showSamples(nodeEntries);
//      }
      
      public void onCircleClick(String label, List<String> sampleCodes) {
        layout.showDatasetsForSamples(label, sampleCodes);
      }
    });
  }
}
