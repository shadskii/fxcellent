# fxcellent
fxcellent is a JavaFX integration library. The purpose of this is to provide support for integrating JavaFX with other frameworks. The following libraries are supported currently:
* [Project Reactor](http://projectreactor.io/)
* [Spring Framework](https://projects.spring.io/spring-framework/)

### Spring Integration 
fxcellent-spring allows for better integration of the Spring Framework and JavaFX. By leveraging the dependency injection power of spring we are able to reduce coupling between .fxml files and their controllers. This allows for greater flexibility in moving code around and reduces the overall size of controllers.

JavaFX components are loaded as spring beans during application initialization.
```java
public class ExampleApplication extends Application
{
    private static ClassPathResource FXML = new ClassPathResource("MainScreen.fxml");

    public static void main(String[] args)
    {
        launch(args);
    }

    private Scene mainScene;
    private ConfigurableApplicationContext ctx;

    @Override
    public void init() throws Exception
    {
        FXMLLoader loader = new FXMLLoader(FXML.getURL());
        mainScene = new Scene(loader.load());
        SpringApplication application = new SpringApplication(ExampleApplicationConfig.class);
        application.addInitializers(SpringFXLoader.loadFX(mainScene));
        ctx = application.run();
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        primaryStage.setTitle("Example application");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }
}
```

Controllers now can inject JavaFX components via `@Autowired`
```java
    @Autowired 
    private Button button1;

    @Autowired
    private Label middleText;
```

`intialize` is now replaced with a `@PostConstruct`
```java
  @FXML
  public void intialize(){}
  
  // Is replaced with
  @PostConstruct
  void intialize(){}
```

### Reactor Integration
fxcellent-reactor provides an easy to use and fluent API for leveraging reactor for JavaFX event handling.

```java
private Button btn;

FxFluxFrom.nodeActionEvent(btn)
          .publishOn(anotherScheduler)
          .map(ActionEvent::getSource)
          .subscribe(System.out::println);

```
