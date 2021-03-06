package ru.phi.modules.osm;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.*;
import ru.phi.modules.osm.generated.*;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

import static java.text.MessageFormat.format;

public final class SquarePolygon {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();


    public static void main(String[] args) throws Exception {
//        writeRel(181326);
        readRel(181326);
    }

    private static void readRel(long id) throws IOException {
        final String content = Files.toString(new File(format("{0}.json", id)), Charsets.UTF_8);
        final Area area = GSON.fromJson(content, Area.class);
        writeGeoJson(id, area);
        Map<Location, Line> starts = new HashMap<>();
        Map<Location, Line> ends = new HashMap<>();
        for (Line line : area.lines) {
            starts.put(line.locations.getFirst(), line);
            ends.put(line.locations.getLast(), line);
        }

        Line it = starts.values().iterator().next();
        starts.remove(it.locations.getFirst());
        LinkedList<Location> locations = new LinkedList<>();
        while (it != null) {
            starts.remove(it.locations.getFirst());
            ends.remove(it.locations.getLast());
            if (locations.size() == 0) {
                locations.addAll(it.locations);
            }
            Line fetcher = it;
            it = ends.get(fetcher.locations.getFirst());
            if (it != null) {
                locations.addAll(it.locations);
            } else {
                it = starts.get(fetcher.locations.getLast());
                if (it == null) {
//
                } else {
                    //
                }
            }
        }
        System.out.println(area);
    }

    private static void writeGeoJson(long id, Area area) throws IOException {
        JsonObject object = new JsonObject();
        object.add("type", new JsonPrimitive("FeatureCollection"));
        final JsonArray array = new JsonArray();

        for (Line line : area.lines) {
            final JsonObject element = new JsonObject();
            element.addProperty("type", "Feature");
            element.add("properties", new JsonObject());
            final JsonObject geometry = new JsonObject();
            geometry.addProperty("type", "Polygon");
            final JsonArray coordinates = new JsonArray();
            final JsonArray ways = new JsonArray();
            for (Location location : line.locations) {
                final JsonArray elements = new JsonArray();
                elements.add(new JsonPrimitive(location.longitude));
                elements.add(new JsonPrimitive(location.latitude));
                ways.add(elements);
            }
            coordinates.add(ways);
            geometry.add("coordinates", coordinates);
            element.add("geometry", geometry);
            array.add(element);
        }
        object.add("features", array);
        Files.write(object.toString(), new File(format("{0}.geojson", id)), Charsets.UTF_8);
    }

    private static void writeRel(long id) throws Exception {
        final Relation rel = Resource.get(Resource.Type.RELATION, id, Relation.class);
        final Area area = create(rel);
        final String content = GSON.toJson(area);
        Files.write(content, new File(format("{0}.json", id)), Charsets.UTF_8);
    }

    private static final class Location {
        public final float latitude;
        public final float longitude;

        Location(float latitude, float longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Location location = (Location) o;
            return Float.compare(location.latitude, latitude) == 0 &&
                    Float.compare(location.longitude, longitude) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(latitude, longitude);
        }
    }

    public static final class Line {
        final LinkedList<Location> locations = new LinkedList<>();
    }

    public static final class Area {
        final LinkedList<Line> lines = new LinkedList<>();
    }

    public static Line create(Way way) throws IOException, JAXBException {
        if (way == null)
            return null;
        final Line ret = new Line();
        final List<Nd> nodes = Resource.filter(way.getRest(), Nd.class);
        for (Nd nd : nodes) {
            final Node node = Resource.get(Resource.Type.NODE, nd.getRef().longValue(), Node.class);
            if (node != null) {
                ret.locations.add(new Location(node.getLat(), node.getLon()));
            }
        }
        return ret;
    }

    public static Area create(Relation rel) throws IOException, JAXBException {
        if (rel == null)
            return null;
        final List<Member> members = Resource.filter(rel.getTagOrMember(), Member.class);
        if (members.size() == 0)
            return null;
        final Area ret = new Area();
        for (Member member : members) {
            final BigInteger ref = member.getRef();
            final Resource.Type type = Resource.Type.valueOf(member.getType().toUpperCase());
            if (type == Resource.Type.WAY) {
                final Way way = Resource.get(type, ref.longValue(), Way.class);
                ret.lines.add(create(way));
            }
        }
        return ret;
    }
}
