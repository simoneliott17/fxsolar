package GuiFX;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.geometry.Point3D;

import java.net.URL;

public class Test extends Application {

    private static final float WIDTH = 1400;
    private static final float HEIGHT = 800;

    private final DoubleProperty angleX = new SimpleDoubleProperty(0);
    private final DoubleProperty angleY = new SimpleDoubleProperty(0);
    private final DoubleProperty angleZ = new SimpleDoubleProperty(0);
    private final DoubleProperty EarthAxis = new SimpleDoubleProperty(0);

    private double anchorX, anchorY;
    private double anchorAngleX = 0;
    private double anchorAngleY = 0;

    private final Sphere sphere = new Sphere(50);
    private final Sphere sphere2 = new Sphere(10);
    private final Sphere sphereMoon = new Sphere(10);
    private final Sphere sphereMoonGhost = new Sphere(1);
    Group world = new Group();
    Group Moon = new Group();
    Group MoonGhost = new Group();


    @Override
    public void start(Stage primaryStage) {
        Camera camera = new PerspectiveCamera(true);
        camera.setNearClip(1);
        camera.setFarClip(10000);
        camera.translateZProperty().set(-1000);

        world.getChildren().add(prepareSun());
        world.getChildren().add(prepareEarth());

        sphereMoonGhost.setTranslateX(20);
        Moon.getChildren().add(prepareMoon());
        MoonGhost.getChildren().add(sphereMoonGhost);

        Slider slider = prepareSlider();
        world.translateZProperty().bind(slider.valueProperty());
        Moon.translateZProperty().bind(slider.valueProperty());

        URL resource = getClass().getResource("travis.mp3");
        Media media = new Media(resource.toString());

        MediaPlayer player = new MediaPlayer(media);
        //player.play();

        Group root = new Group();
        root.getChildren().add(prepareImageView());
        root.getChildren().add(MoonGhost);
        root.getChildren().add(world);
        root.getChildren().add(Moon);
        root.getChildren().add(slider);

        Scene scene = new Scene(root, WIDTH, HEIGHT, true);
        scene.setFill(Color.SILVER);
        scene.setCamera(camera);
        //scene.getStylesheets().add("flatterAdd.css");
        //initMouseControl(world, scene, primaryStage);

        primaryStage.setTitle("Galaxy");
        primaryStage.setScene(scene);
        primaryStage.show();

        prepareAnimation();
    
/*
    Thread thread =  new Thread() {
     
    	@Override
        public void run() {
        	MoonCoords();                                                                     
        }
    };    
    thread.start();
*/

    }

    private void prepareAnimation() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                //rotation around own axis
                sphere.rotateProperty().set(sphere.getRotate() + 1);
                sphere2.rotateProperty().set(sphere2.getRotate() + 0.2);
                sphereMoon.rotateProperty().set(sphereMoon.getRotate() + 1);

                Rotate xRotate;
                Rotate zRotate;
                Rotate yRotate;
                world.getTransforms().addAll(
                        xRotate = new Rotate(0, Rotate.X_AXIS),
                        yRotate = new Rotate(0, Rotate.Y_AXIS),
                        zRotate = new Rotate(0, Rotate.Z_AXIS)

                );

                Rotate xRotateMoon;
                Rotate yRotateMoon;
                Moon.getTransforms().addAll(
                        xRotateMoon = new Rotate(0, Rotate.X_AXIS),
                        yRotateMoon = new Rotate(0, Rotate.Y_AXIS)
                );
                yRotateMoon.pivotYProperty().bind(EarthAxis);

                xRotate.angleProperty().bind(angleX);
                yRotate.angleProperty().bind(angleY);
                zRotate.angleProperty().bind(angleZ);
                xRotateMoon.angleProperty().bind(angleX);
                yRotateMoon.angleProperty().bind(angleY);

                //angle defines the way of rotation
                //angleX.set(1);
                angleZ.set(1);

                /**This doesnt work, was an experiment
                 ** Axis is not changing, so it can not be updated
                 */
                EarthAxis.set(sphere2.getTranslateX());
            }
        };
        timer.start();
    }

    //Background
    private ImageView prepareImageView() {
        Image image = new Image(Test.class.getResourceAsStream("galaxy.jpeg"));
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.getTransforms().add(new Translate(-image.getWidth() / 2 , -image.getHeight() / 2 , 800));
        return imageView;
    }

    //Slider for Zoom
    private Slider prepareSlider() {
        Slider slider = new Slider();
        slider.setMax(800);
        slider.setMin(-400);
        slider.setPrefWidth(300d);
        slider.setLayoutX(-150);
        slider.setLayoutY(200);
        slider.setShowTickLabels(true);
        slider.setTranslateZ(5);
        slider.setStyle("-fx-base: black");
        return slider;
    }

    private Node prepareSun() {
        //images
        PhongMaterial earthMaterial = new PhongMaterial();
        earthMaterial.setDiffuseMap(new Image(getClass().getResourceAsStream("images/sun.jpeg")));

        //set Rotation
        sphere.setRotationAxis(Rotate.Y_AXIS);
        sphere.setMaterial(earthMaterial);
        return sphere;
    }

    private Node prepareEarth() {
        //images
        PhongMaterial earthMaterial = new PhongMaterial();
        earthMaterial.setDiffuseMap(new Image(getClass().getResourceAsStream("images/earth/earth.normal.jpeg")));
        earthMaterial.setSelfIlluminationMap(new Image(getClass().getResourceAsStream("images/earth/dark.jpeg")));
        earthMaterial.setSpecularMap(new Image(getClass().getResourceAsStream("images/earth/shine.jpeg")));
        earthMaterial.setBumpMap(new Image(getClass().getResourceAsStream("images/earth/blue.jpeg")));

        sphere2.setRotationAxis(Rotate.Y_AXIS);
        sphere2.setMaterial(earthMaterial);
        //set Location
        sphere2.setTranslateX(300);
        return sphere2;
    }

    private Node prepareMoon() {
        //images
        PhongMaterial earthMaterial = new PhongMaterial();
        earthMaterial.setDiffuseMap(new Image(getClass().getResourceAsStream("images/moon.jpeg")));

        sphereMoon.setRotationAxis(Rotate.Y_AXIS);
        sphereMoon.setMaterial(earthMaterial);
        sphereMoon.setTranslateX(280);
        return sphereMoon;
    }
 /* 
  private void MoonCoords(){
  	  while(true){
  	  	  sphereMoon.setTranslateX(sphere2.getTranslateX()+sphereMoonGhost.getTranslateX());
  	  	  sphereMoon.setTranslateY(sphere2.getTranslateY()+sphereMoonGhost.getTranslateY());
  	  	  sphereMoon.setTranslateZ(sphere2.getTranslateZ()+sphereMoonGhost.getTranslateZ());
  	  }
  }

  private void initMouseControl(Group group, Scene scene, Stage stage) {
    Rotate xRotate;
    Rotate yRotate;
    group.getTransforms().addAll(
        xRotate = new Rotate(0, Rotate.X_AXIS),
        yRotate = new Rotate(0, Rotate.Y_AXIS)
    );
    xRotate.angleProperty().bind(angleX);
    yRotate.angleProperty().bind(angleY);

    scene.setOnMousePressed(event -> {
      anchorX = event.getSceneX();
      anchorY = event.getSceneY();
      anchorAngleX = angleX.get();
      anchorAngleY = angleY.get();
    });

    scene.setOnMouseDragged(event -> {
      angleX.set(anchorAngleX - (anchorY - event.getSceneY()));
      angleY.set(anchorAngleY + anchorX - event.getSceneX());
    });

    stage.addEventHandler(ScrollEvent.SCROLL, event -> {
      double delta = event.getDeltaY();
      group.translateZProperty().set(group.getTranslateZ() + delta);
    });
  }
*/

}