package koob.networking

import groovy.util.logging.Slf4j
import io.moquette.broker.Server;
import io.moquette.interception.AbstractInterceptHandler;
import io.moquette.interception.InterceptHandler;
import io.moquette.interception.messages.InterceptPublishMessage;
import io.moquette.broker.config.ClasspathResourceLoader;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.config.IResourceLoader;
import io.moquette.broker.config.ResourceLoaderConfig;
import static java.nio.charset.StandardCharsets.UTF_8;

class MqttBrokerService {

    final Server mqttBroker = new Server()

    void start(){
        IResourceLoader classpathLoader = new ClasspathResourceLoader()
        final IConfig classPathConfig = new ResourceLoaderConfig(classpathLoader)
        List<? extends InterceptHandler> userHandlers = Collections.singletonList(new PublisherListener())
        mqttBroker.startServer(classPathConfig, userHandlers)
    }

    void stop(){
        mqttBroker.stopServer()
    }

    class PublisherListener extends AbstractInterceptHandler {

        @Override
        public String getID() {
            return "EmbeddedLauncherPublishListener";
        }

        @Override
        public void onPublish(InterceptPublishMessage msg) {
            String message = msg.getPayload().toString(UTF_8)
            log.info " "
            log.info ("MqttBroker.onPublish >> topic: " + msg.getTopicName() + " content: $message")
        }
    }


}
