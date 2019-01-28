import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Assert;
import java.io.IOException;

public class MainAPITest {

    private static final String LUKE_SKYWALKER_URL = "https://swapi.co/api/people/1/";
    private static final String HOME_PLANET_LUKE = "Tatooine";
    private static final String PLANET_POPULATION_LUKE = "200000";
    private static final String TEST_MOVIE_NAME = "Attack of the Clones";

    public static void main(String[] args) throws IOException {

        Gson gson = new Gson();

        People lukeSkywalker = gson.fromJson(apiConnector(LUKE_SKYWALKER_URL).toString(), People.class);
        String lukesHomeworldUrl = lukeSkywalker.getHomeworld();

        Planet lukesHome = gson.fromJson(apiConnector(lukesHomeworldUrl).toString(), Planet.class);
        Assert.assertEquals(HOME_PLANET_LUKE, lukesHome.getName());
        System.out.println("Success! Luke's home is " + lukesHome.getName());

        Assert.assertEquals(PLANET_POPULATION_LUKE, lukesHome.getPopulation());
        System.out.println("Tatooine's population is " + lukesHome.getPopulation());

        String firstFilmUrl = lukesHome.films.get(0).toString();

        Film theFilm = gson.fromJson(apiConnector(firstFilmUrl).toString(), Film.class);
        Assert.assertEquals(TEST_MOVIE_NAME, theFilm.getTitle());
        System.out.println("The film is " + theFilm.getTitle());

        System.out.println("Is Luke Skywalker acting in "
                + theFilm.getTitle() + " movie: "
                + theFilm.characters.contains(LUKE_SKYWALKER_URL));

        System.out.println("Is Tatooine shown in "
                + theFilm.getTitle() + " movie: "
                + theFilm.planets.contains(lukesHomeworldUrl));
    }

    static JsonNode apiConnector (String url) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        HttpResponse response = client.execute(request);
        String responseBody = IOUtils.toString(response.getEntity().getContent());

        return objectMapper.readTree(responseBody);
    }
}