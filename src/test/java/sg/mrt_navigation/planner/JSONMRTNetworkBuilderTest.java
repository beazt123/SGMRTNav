package sg.mrt_navigation.planner;

import org.junit.Test;

import static org.junit.Assert.*;

public class JSONMRTNetworkBuilderTest {

    @Test
    public void initialize() {
        new JSONMRTNetworkBuilder();
    }

    @Test
    public void buildAGraph() {
        JSONMRTNetworkBuilder b = new JSONMRTNetworkBuilder();
        b.build();
    }

    @Test
    public void find() {
        JSONMRTNetworkBuilder b = new JSONMRTNetworkBuilder();
        b.build();
    }


}