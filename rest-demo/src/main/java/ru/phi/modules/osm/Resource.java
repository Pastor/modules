package ru.phi.modules.osm;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.phi.modules.osm.generated.Osm;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public final class Resource {
    private static final String host = "api.openstreetmap.org";
    private static final String path = "api/0.6/";
    private static final OkHttpClient client = new OkHttpClient();

    @SuppressWarnings({"unchecked"})
    public static <E> E get(Type type, long id, Class<E> clazz) throws IOException, JAXBException {
        final HttpUrl.Builder urlBuilder = new HttpUrl.Builder();
        urlBuilder.scheme("http");
        urlBuilder.host(host);
        urlBuilder.addPathSegments(path);
        urlBuilder.addPathSegment(type.name().toLowerCase());
        urlBuilder.addPathSegment("" + id);
        final Request request = new Request.Builder()
                .get()
                .url(urlBuilder.build())
                .build();
        final Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            try (final Reader reader = response.body().charStream()) {
                final Osm osm = (Osm) unmarshaller.unmarshal(reader);
                for (Object object : osm.getBoundOrUserOrPreferences()) {
                    if (object.getClass().equals(clazz))
                        return (E) object;
                }
            }
        }
        return null;
    }

    @SuppressWarnings({"unchecked"})
    public static <E> List<E> filter(List<Object> objects, Class<E> clazz) {
        return objects.stream()
                .filter(object -> object.getClass().equals(clazz))
                .map(object -> (E) object)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    public enum Type {
        NODE,
        WAY,
        RELATION
    }

    private static final JAXBContext context;
    private static final Unmarshaller unmarshaller;

    static {
        try {
            context = JAXBContext.newInstance(Osm.class);
            unmarshaller = context.createUnmarshaller();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}
