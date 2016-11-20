package ng.abdlquadri;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by abdlquadri on 11/20/16.
 */
@RunWith(VertxUnitRunner.class)
public class ApplicationNameTest {

    private Vertx vertx;
    private EventBus eventBus;
    int PORT = 8000;
    String SERVER = "127.0.0.1";

    @Before
    public void before(TestContext context) {

        vertx = Vertx.vertx();
        eventBus = vertx.eventBus();

        JsonObject config = new JsonObject().put("http.port", PORT).put("http.server", SERVER);

        DeploymentOptions deploymentOptions = new DeploymentOptions().setConfig(config);
        vertx.deployVerticle(ApplicationNameVerticle.class.getCanonicalName(), deploymentOptions, context.asyncAssertSuccess());


    }

    @Test
    public void testGPLAYApplicationName(TestContext context) {
        Async async = context.async();

        JsonObject app = new JsonObject()
                .put("url", "https://play.google.com/store/apps/details?id=co.vero.app&hl=en")
                .put("provider", "gplay");

        HttpClient client = vertx.createHttpClient();
        client.post(PORT, SERVER, "/api", response -> {

            response.bodyHandler(buffer -> {
                context.assertEquals(200, response.statusCode());
                context.assertEquals("Vero - True Social", buffer.toString());
                client.close();
                async.complete();
            });


        }).end(app.encode());

    }

    @Test
    public void testAPPSTOREApplicationName(TestContext context) {
        Async async = context.async();

        JsonObject app = new JsonObject()
                .put("url", "https://itunes.apple.com/us/app/vero-true-social/id971055041?mt=8")
                .put("provider", "appstore");

        HttpClient client = vertx.createHttpClient();
        client.post(PORT, SERVER, "/api", response -> {

            response.bodyHandler(buffer -> {
                context.assertEquals(200, response.statusCode());
                context.assertEquals("Vero - True Social", buffer.toString());
                client.close();
                async.complete();
            });


        }).end(app.encode());

    }

    @Test
    public void testSTEAMApplicationName(TestContext context) {
        Async async = context.async();

        JsonObject app = new JsonObject()
                .put("url", "http://store.steampowered.com/app/550930/")
                .put("provider", "steam");

        HttpClient client = vertx.createHttpClient();
        client.post(PORT, SERVER, "/api", response -> {

            response.bodyHandler(buffer -> {
                context.assertEquals(200, response.statusCode());
                context.assertEquals("Rocket Craze 3D", buffer.toString());
                client.close();
                async.complete();
            });


        }).end(app.encode());

    }

    @After
    public void close(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }
}
