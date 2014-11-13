/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Android_Template;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetEventListener;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import java.util.concurrent.Callable;

/**
 *
 * @author practicing01 & others
 */
public class SplashesState extends AbstractAppState {

    private static SimpleApplication app;
    private Camera cam;
    private Node rootNode;
    private AssetManager assetManager;
    private AppStateManager stateManager;
    private AppSettings settings;
    private BulletAppState bulletAppState;
    private int sceneTraversalPhase, splashPhase = -1;
    private Spatial birdNode, birdStartNode, birdEndNode, camLocNode, camLookAtNode;
    private float elapsedTime = 0, tpfOffset, delayTime = -1;
    private boolean splashing = false, canSplash = false;
    private AudioNode mourningDoveSFX;
    private AssetEventListener assListener;

    @Override
    public void update(float tpf) {
        if (splashing == true) {
            elapsedTime += tpf;
            if (elapsedTime >= delayTime) {
                splash();
            }
        } else {
            if (canSplash == true && delayTime != -1) {
                splashing = true;
                app.getAssetManager().removeAssetEventListener(assListener);
            }
        }
    }

    @Override
    public void cleanup() {
    }

    @Override
    public void initialize(AppStateManager stateManager,
            Application app) {

        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        this.stateManager = stateManager;
        this.cam = this.app.getCamera();
        this.rootNode = this.app.getRootNode();
        this.assetManager = this.app.getAssetManager();
        this.settings = app.getContext().getSettings();
        this.bulletAppState = app.getStateManager().getState(BulletAppState.class);

        bulletAppState.getPhysicsSpace().enableDebug(assetManager);
        this.app.getFlyByCamera().setDragToRotate(true);
        this.app.getInputManager().setCursorVisible(true);

        assListener = new AssetEventListener() {
            public void assetLoaded(AssetKey key) {
                //System.out.println("ass loaded " + key);//Common/MatDefs/Misc/Unshaded.frag
                if (key.getName().equals("Scenes/Splashes/Scene_MourningDoveSoft.j3o") == true) {//"Scenes/Splashes/Scene_MourningDoveSoft.j3o"
                    canSplash = true;
                }
            }

            public void assetRequested(AssetKey key) {
                //System.out.println("ass requested " + key);
            }

            public void assetDependencyNotFound(AssetKey parentKey, AssetKey dependentAssetKey) {
            }
        };

        app.getAssetManager().addAssetEventListener(assListener);

        cam.setFrustumPerspective(45f, (float) cam.getWidth() / cam.getHeight(), 0.01f, 1000f);

        mourningDoveSFX = new AudioNode(assetManager, "Sounds/mourningdove.ogg", false);

        Node splashScene = (Node) assetManager.loadModel("Scenes/Splashes/Scene_MourningDoveSoft.j3o");
        rootNode.attachChild(splashScene);

        SceneGraphVisitor visitor = new SceneGraphVisitor() {
            @Override
            public void visit(Spatial spat) {
                if (sceneTraversalPhase == 0) {
                    if (spat.getName().equals("CameraNode")) {
                        camLocNode = spat;
                    } else if (spat.getName().equals("CamLookAtNode")) {
                        camLookAtNode = spat;
                    } else if (spat.getName().equals("BirdNode")) {
                        birdNode = spat;
                    } else if (spat.getName().equals("BirdStartNode")) {
                        birdStartNode = spat;
                    } else if (spat.getName().equals("BirdEndNode")) {
                        birdEndNode = spat;
                    } else if (spat.getName().equals("Bird-ogremesh")) {
                        AnimControl animControl = spat.getControl(AnimControl.class);
                        AnimChannel animChannel = animControl.createChannel();
                        animChannel.setAnim("Fly");
                    }
                }
            }
        };

        sceneTraversalPhase = 0;
        rootNode.depthFirstTraversal(visitor);

        cam.setLocation(camLocNode.getWorldTranslation());
        cam.lookAt(camLookAtNode.getWorldTranslation(), Vector3f.UNIT_Y);

        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText Text = new BitmapText(guiFont);
        Text.setSize(guiFont.getCharSet().getRenderedSize());
        Text.move(settings.getWidth() / 2, Text.getLineHeight(), 0);
        Text.setText("MourningDoveSoft");
        this.app.getGuiNode().attachChild(Text);

        Vector3f extent = ((BoundingBox) birdNode.getWorldBound()).getExtent(null);
        float radius = extent.x;

        if (extent.x < extent.z) {
            radius = extent.z;
        }

        /*BCCSceneObjectControl physicsSceneObjectControl = new BCCSceneObjectControl(radius, extent.y, 1f);
         birdNode.addControl(physicsSceneObjectControl);
         physicsSceneObjectControl.attachControls();
         physicsSceneObjectControl.setGravity(new Vector3f(0f, -0.00001f, 0f));
         //physicsSceneObjectControl.setPhysicsDamping(0f);
         bulletAppState.getPhysicsSpace().addTickListener(physicsSceneObjectControl);
         physicsSceneObjectControl.moveTo(birdEndNode.getWorldTranslation(), 1f, true);*/

        /*RBSceneObjectControl physicsSceneObjectControl = new RBSceneObjectControl(new BoxCollisionShape(birdNode.getWorldScale()), 1f);
         birdNode.addControl(physicsSceneObjectControl);
         physicsSceneObjectControl.attachControls();
         physicsSceneObjectControl.setGravity(new Vector3f(0f, -0.00001f, 0f));
         bulletAppState.getPhysicsSpace().addTickListener(physicsSceneObjectControl);

         physicsSceneObjectControl.moveTo(birdEndNode.getWorldTranslation(), 1f, true);*/

        SceneObjectControl sceneObjectControl = new SceneObjectControl();
        birdNode.addControl(sceneObjectControl);

        delayTime = 0f;
    }

    public void splashMDS() {
        mourningDoveSFX.play();
        birdNode.getControl(SceneObjectControl.class).moveTo(birdEndNode.getWorldTranslation(), 2f, true);
    }

    public void splashOGA() {

        AudioNode oga = new AudioNode(assetManager, "Sounds/OGA.ogg", false);
        oga.play();
        Box box = new Box(settings.getWidth() / 2, settings.getHeight() / 2, 1f);
        Spatial wall = new Geometry("Box", box);
        Material mat_brick = new Material(
                assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_brick.setTexture("ColorMap",
                assetManager.loadTexture("Textures/oga.png"));
        wall.setMaterial(mat_brick);

        wall.setLocalTranslation(settings.getWidth() / 2,
                settings.getHeight() / 2, 0);

        app.getGuiNode().attachChild(wall);

    }

    public void splashJME() {

        AudioNode jme = new AudioNode(assetManager, "Sounds/jme.ogg", false);
        jme.play();
        Box box = new Box(settings.getWidth() / 2, settings.getHeight() / 2, 1f);
        Spatial wall = new Geometry("Box", box);
        Material mat_brick = new Material(
                assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_brick.setTexture("ColorMap",
                assetManager.loadTexture("Textures/Monkey.jpg"));
        wall.setMaterial(mat_brick);

        wall.setLocalTranslation(settings.getWidth() / 2,
                settings.getHeight() / 2, 0);

        app.getGuiNode().attachChild(wall);

    }

    public void splash() {

        splashPhase++;

        if (splashPhase == 0) {

            //clear scene
            app.enqueue(new Callable() {
                public Object call() throws Exception {
                    splashMDS();
                    elapsedTime = 0;
                    delayTime = 5f;
                    return null;
                }
            });

        } else if (splashPhase == 1) {

            //clear scene
            app.enqueue(new Callable() {
                public Object call() throws Exception {
                    Main.Clear_Scene(app, rootNode, bulletAppState);
                    splashOGA();
                    elapsedTime = 0;
                    delayTime = 5f;
                    return null;
                }
            });

        } else if (splashPhase == 2) {

            //clear scene
            app.enqueue(new Callable() {
                public Object call() throws Exception {
                    Main.Clear_Scene(app, rootNode, bulletAppState);
                    splashJME();
                    elapsedTime = 0;
                    delayTime = 3f;
                    return null;
                }
            });

        } else if (splashPhase >= 3) {

            //clear scene
            app.enqueue(new Callable() {
                public Object call() throws Exception {
                    Main.Clear_Scene(app, rootNode, bulletAppState);
                    return null;
                }
            });

            splashing = false;
            
            app.getAssetManager().removeAssetEventListener(assListener);

            stateManager.detach(this);

            MainMenu mm = new MainMenu();

            stateManager.attach(mm);

            Main.appstate_delete(this);

        }

    }

}
