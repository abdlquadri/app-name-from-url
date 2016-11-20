package ng.abdlquadri;

import java.io.IOException;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.LoggerFormat;
import io.vertx.ext.web.handler.LoggerHandler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Created by abdlquadri on 11/19/16.
 */
public class ApplicationNameVerticle extends AbstractVerticle {

    private Router router;

    @Override
    public void start(Future<Void> startFuture) throws Exception {

//    document.querySelectorAll('div[class="id-app-title"]') gplay

//    document.querySelectorAll('h1[itemprop="name"]') appstore


        router = Router.router(vertx);


        router.route().handler(BodyHandler.create());
        router.route().handler(LoggerHandler.create(LoggerFormat.DEFAULT));

        router.post("/api").handler(routingContext -> {
            JsonObject app = routingContext.getBodyAsJson();

            routingContext.request().response().end(getAppName(app));
        });

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(config().getInteger("http.port"), result -> {
                    if (result.succeeded())
                        startFuture.complete();
                    else
                        startFuture.fail(result.cause());
                });

    }

    private String getAppName(JsonObject app) {
        Document doc = null;
        try {
            doc = Jsoup.connect(app.getString("url")).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String appName;
        switch (app.getString("provider")) {
            case "gplay":
                appName = doc.select("div[class=\"id-app-title\"]").first().ownText();
                break;
            case "appstore":
                appName = doc.select("h1[itemprop=\"name\"]").first().ownText();
                break;
            case "steam":
                //Steam seem un-uniform
                appName = doc.select("div[class=\"apphub_AppName\"]").first().ownText();
                break;
            default:
                appName = "";
        }
        return appName;
    }
}
