package ru.phi.modules.osm;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import lombok.experimental.UtilityClass;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import ru.phi.modules.osm.generated.Nd;
import ru.phi.modules.osm.generated.Node;
import ru.phi.modules.osm.generated.Way;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import static java.text.MessageFormat.format;
import static ru.phi.modules.osm.LoadObjects.gson;

@UtilityClass
public final class UpdateObjects {
    private static final OkHttpClient client = new OkHttpClient();

    public static void main(String[] args) throws IOException, JAXBException {
        final LoadObjects.Element[] elements = load();
        for (int i = 0, elementsLength = elements.length; i < elementsLength; i++) {
            final LoadObjects.Element element = elements[i];
            update(element);
            element.polygon = polygon(element.longitude, element.latitude);
            System.out.println(format("Element {0}: processed", i));
        }
        final String content = gson.toJson(elements);
        Files.write(content, new File("u_objects.json"), Charsets.UTF_8);
    }

    private static void update(LoadObjects.Element element) throws IOException {
        final HttpUrl url = HttpUrl.parse("http://176.112.215.104/osis/ReadOSI")
                .newBuilder()
                .addQueryParameter("id", element.id)
                .build();
        final Request request = new Request.Builder().url(url).get().build();
        final Call call = client.newCall(request);
        final Response execute = call.execute();
        final String content = execute.body().string();
        if (execute.isSuccessful()) {
            final LoadObjects.Element update = gson.fromJson(content, LoadObjects.Element.class);
            element.latitude = update.latitude;
            element.longitude = update.longitude;
        }
    }

    private static LoadObjects.Location[] polygon(double longitude, double latitude) throws IOException, JAXBException {
        final HttpUrl url = HttpUrl
                .parse("http://www.openstreetmap.org/geocoder/search_osm_nominatim_reverse")
                .newBuilder()
                .addQueryParameter("lat", format("{0,number,#.########}", latitude).replaceAll(",", "."))
                .addQueryParameter("lon", format("{0,number,#.########}", longitude).replaceAll(",", "."))
                .build();
        final Request request = new Request.Builder().url(url).get().build();
        final Call call = client.newCall(request);
        final Response execute = call.execute();
        final String content = execute.body().string();
        if (execute.isSuccessful()) {
            final Document document = Jsoup.parse(content);
            final Elements elements = document.select("a");
            for (org.jsoup.nodes.Element element : elements) {
                final Attributes attributes = element.attributes();
                for (Attribute attribute : attributes) {
                    if (attribute.getKey().equalsIgnoreCase("data-id")) {
                        final String id = attribute.getValue();
                        final Way way = Resource.get(Resource.Type.WAY, Long.parseLong(id), Way.class);
                        if (way == null)
                            return new LoadObjects.Location[0];
                        final List<Nd> nodes = Resource.filter(way.getRest(), Nd.class);
                        final LoadObjects.Location[] locations = new LoadObjects.Location[nodes.size()];
                        for (int i = 0; i < nodes.size(); i++) {
                            Nd nd = nodes.get(i);
                            final Node node = Resource.get(Resource.Type.NODE, nd.getRef().longValue(), Node.class);
                            if (node != null) {
                                locations[i] = new LoadObjects.Location();
                                locations[i].latitude = node.getLat();
                                locations[i].longitude = node.getLon();
                            }
                        }
                        return locations;
                    }
                }
            }
        }
        System.out.println(content);
        return new LoadObjects.Location[0];
    }

    private static LoadObjects.Element[] load() throws IOException {
        final HttpUrl url = HttpUrl.parse("http://176.112.215.104/osis/ReadOSIs");
        final Request request = new Request.Builder().url(url).get().build();
        final Call call = client.newCall(request);
        final Response execute = call.execute();
        final ResponseBody body = execute.body();
        if (execute.isSuccessful()) {
            try (Reader reader = body.charStream()) {
                return gson.fromJson(reader, LoadObjects.Element[].class);
            }
        }
        System.out.println(body.string());
        return new LoadObjects.Element[0];
    }

}
