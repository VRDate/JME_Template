/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Android_Template;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import de.lessvoid.nifty.Nifty;

/**
 *
 * @author practicing01
 */
public class GameplayState extends AbstractAppState {

    private static SimpleApplication app;
    private Camera cam;
    private Node rootNode;
    private AssetManager assetManager;
    private AppStateManager stateManager;
    private AppSettings settings;
    private BulletAppState bulletAppState;
    private InputManager inputManager;
    private Nifty nifty;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        //TODO: initialize your AppState, e.g. attach spatials to rootNode
        //this is called on the OpenGL thread after the AppState has been attached

        this.app = (SimpleApplication) app;
        this.stateManager = stateManager;
        this.cam = this.app.getCamera();
        this.rootNode = this.app.getRootNode();
        this.assetManager = this.app.getAssetManager();
        this.settings = app.getContext().getSettings();
        this.bulletAppState = app.getStateManager().getState(BulletAppState.class);
        this.inputManager = app.getInputManager();
        
        //Debug.
        //bulletAppState.getPhysicsSpace().enableDebug(assetManager);
        //this.app.getFlyByCamera().setDragToRotate(true);
        //this.app.getInputManager().setCursorVisible(true);
        
        bulletAppState.getPhysicsSpace().setAccuracy(0.01f);

        mapKeys();
        
    }

    @Override
    public void update(float tpf) {
        //TODO: implement behavior during runtime
        //System.out.println("loc" + cam.getLocation() + "dir" + cam.getDirection());
    }

    @Override
    public void cleanup() {
        super.cleanup();
        //TODO: clean up what you initialized in the initialize method,
        //e.g. remove all spatials from rootNode
        //this is called on the OpenGL thread after the AppState has been detached
    }
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            System.out.println(name + " " + keyPressed);
        }
    };

    public void mapKeys() {
        app.getFlyByCamera().setEnabled(false);
        app.getInputManager().addMapping("Touch", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        app.getInputManager().addListener(actionListener, "Touch");
    }
    
}
