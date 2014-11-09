/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Android_Template;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;
import java.util.concurrent.Callable;

/**
 *
 * @author practicing01
 */
public class SceneObjectControl extends AbstractControl implements SceneObjectInterface {
    //Any local variables should be encapsulated by getters/setters so they
    //appear in the SDK properties window and can be edited.
    //Right-click a local variable to encapsulate it with getters and setters.

    private float moveToTravelTime, moveToElapsedTime, moveToSpeed, inderp, remainingDist;
    private Vector3f moveToDir, moveToLoc, moveToDest;
    private boolean isMoving = false, moveToStopOnTime;

    @Override
    protected void controlUpdate(float tpf) {
        //TODO: add code that controls Spatial,
        //e.g. spatial.rotate(tpf,tpf,tpf);
        if (isMoving == true) {

            inderp = tpf * moveToSpeed;
            remainingDist = spatial.getLocalTranslation().distance(moveToDest);
            
            Main.app.enqueue(new Callable() {
                public Object call() throws Exception {
                    spatial.setLocalTranslation(spatial.getWorldTranslation().interpolate(moveToDest, inderp / remainingDist));
                    return null;
                }
            });
            
            moveToElapsedTime += tpf;

            if (moveToElapsedTime >= moveToTravelTime) {
                isMoving = false;
                if (moveToStopOnTime == true) {
                }
                onMoveToComplete();
            }

            //System.out.println(moveToElapsedTime + " " + moveToTravelTime + " ");
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }

    public Control cloneForSpatial(Spatial spatial) {
        SceneObjectControl control = new SceneObjectControl();
        //TODO: copy parameters to new Control
        return control;
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule in = im.getCapsule(this);
        //TODO: load properties of this Control, e.g.
        //this.value = in.readFloat("name", defaultValue);
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule out = ex.getCapsule(this);
        //TODO: save properties of this Control, e.g.
        //out.write(this.value, "name", defaultValue);
    }

    @Override
    public void onMoveToComplete() {
    }

    @Override
    public void moveTo(Vector3f dest, float speed, boolean stopOnCompletion) {
        moveToSpeed = speed;
        moveToDest = dest;
        moveToLoc = spatial.getWorldTranslation();
        moveToDir = dest.subtract(moveToLoc);
        moveToDir.normalizeLocal();
        moveToTravelTime = moveToDest.distance(moveToLoc) / speed;
        moveToElapsedTime = 0;
        moveToStopOnTime = stopOnCompletion;
        isMoving = true;
    }
}
