package freetimelabs.fxcellent.reactor.flux;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.BeforeClass;
import org.junit.Test;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

public class FxFluxFromTest
{


    private static final Phaser p = new Phaser(2);
    private static final ExecutorService SERVICE = Executors.newSingleThreadExecutor();
    private Scheduler thread = Schedulers.immediate();

    @BeforeClass
    public static void initApplication() throws TimeoutException, InterruptedException
    {
        SERVICE.submit(() -> Application.launch(TestApp.class));
        p.awaitAdvanceInterruptibly(p.arrive(), 3, TimeUnit.SECONDS);
    }

    private void setStage(Consumer<Stage> stageConsumer) throws TimeoutException, InterruptedException
    {
        Phaser barrier = new Phaser(2);
        Platform.runLater(() ->
        {
            stageConsumer.accept(TestApp.primaryStage());
            barrier.arrive();
        });
        barrier.awaitAdvanceInterruptibly(barrier.arrive(), 3, TimeUnit.SECONDS);

    }

    @Test
    public void testNodeEvent() throws TimeoutException, InterruptedException
    {
        AtomicReference<Node> actual = new AtomicReference<>();
        setStage(stage ->
        {
            Pane pane = new Pane();
            actual.set(pane);
            stage.setScene(new Scene(pane));
        });

        AtomicReference<Event> event = new AtomicReference<>();
        Node pane = actual.get();
        FxFluxFrom.nodeEvent(pane, ActionEvent.ANY)
                  .publishOn(thread)
                  .subscribe(e ->
                  {
                      event.set(e);

                  });
        ActionEvent e = new ActionEvent();
        actual.get()
              .fireEvent(e);
        assertThat(event.get()
                        .getSource()).isEqualTo(pane);

    }

    public static final class TestApp extends Application
    {
        private static final AtomicReference<TestApp> TEST_APP = new AtomicReference<>();
        private Stage stage;

        public TestApp()
        {
            synchronized (TEST_APP)
            {
                TEST_APP.getAndSet(this);
            }
        }

        public static Stage primaryStage()
        {
            return TEST_APP.get().stage;
        }

        @Override
        public void start(Stage primaryStage) throws Exception
        {
            stage = primaryStage;
            p.arrive();
        }
    }
}
