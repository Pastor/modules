package ru.phi.modules.osm;

import org.junit.Test;
import ru.phi.modules.osm.generated.Nd;
import ru.phi.modules.osm.generated.Node;
import ru.phi.modules.osm.generated.Tag;
import ru.phi.modules.osm.generated.Way;

import java.util.List;

import static java.text.MessageFormat.format;
import static org.junit.Assert.assertNotNull;

public final class ResourceTest {
    @Test
    public void getWay() throws Exception {
        for (long id : ways) {
            processId(id);
        }
    }

    private static void processId(long id) throws java.io.IOException, javax.xml.bind.JAXBException {
        final Way way = Resource.get(Resource.Type.WAY, id, Way.class);
        assertNotNull(way);
        final List<Tag> tags = Resource.filter(way.getRest(), Tag.class);
        for (Tag tag : tags) {
            System.out.println(format("k = {0}, v = {1}", tag.getK(), tag.getV()));
        }
        final List<Nd> nodes = Resource.filter(way.getRest(), Nd.class);
        for (Nd nd : nodes) {
            final Node node = Resource.get(Resource.Type.NODE, nd.getRef().longValue(), Node.class);
            assertNotNull(node);
            System.out.println(format("point(pastor| {0,number,#.########}| {1,number,#.########})|", node.getLat(), node.getLon())
                    .replaceAll(",", ".").replaceAll("\\|", ","));
        }
    }

    private static final long[] ways = {
            37567552,
            55914640,
            55914408,
            55914840
    };
}