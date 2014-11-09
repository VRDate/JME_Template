/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Android_Template;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.control.BetterCharacterControl;
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

/**
 *
 * @author practicing01
 */
public class BCCSceneObjectControl extends AbstractControl implements SceneObjectInterface, PhysicsTickListener {
    //Any local variables should be encapsulated by getters/setters so they
    //appear in the SDK properties window and can be edited.
    //Right-click a local variable to encapsulate it with getters and setters.

    public BetterCharacterControl betterCharacterControl;
    private float radius, height, mass, moveToTravelTime, moveToElapsedTime, moveToSpeed;
    private Vector3f forwardDir, leftDir, walkDirection, viewDirection, moveToDir, moveToLoc, moveToDest;
    private boolean isMoving = false, moveToStopOnTime;

    public BCCSceneObjectControl(float radius, float height, float mass) {
        walkDirection = new Vector3f(Vector3f.ZERO);
        viewDirection = new Vector3f(0f, 0f, 1f);
        this.radius = radius;
        this.height = height;
        this.mass = mass;
        betterCharacterControl = new BetterCharacterControl(radius, height, mass);
        Main.bulletAppState.getPhysicsSpace().add(betterCharacterControl);
    }

    public void attachControls() {
        spatial.addControl(betterCharacterControl);
    }

    public void setGravity(Vector3f gravity) {
        betterCharacterControl.setGravity(gravity);
    }

    public void setPhysicsDamping(float damping) {
        betterCharacterControl.setPhysicsDamping(damping);
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
        viewDirection = moveToDir;
    }

    @Override
    protected void controlUpdate(float tpf) {
        //TODO: add code that controls Spatial,
        //e.g. spatial.rotate(tpf,tpf,tpf)

    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }

    @Override
    public Control cloneForSpatial(Spatial spatial) {
        BCCSceneObjectControl control = new BCCSceneObjectControl(radius, height, mass);
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
    public void prePhysicsTick(PhysicsSpace space, float tpf) {
        if (isMoving == true) {

            forwardDir = spatial.getWorldRotation().mult(Vector3f.UNIT_Z);
            leftDir = spatial.getWorldRotation().mult(Vector3f.UNIT_X);

            walkDirection.set(0f, 0f, 0f);
            walkDirection.addLocal(forwardDir.mult(moveToSpeed));
            betterCharacterControl.setWalkDirection(walkDirection);
            betterCharacterControl.setViewDirection(viewDirection);

            moveToElapsedTime += tpf;

            if (moveToElapsedTime >= moveToTravelTime) {
                isMoving = false;
                if (moveToStopOnTime == true) {
                    betterCharacterControl.setWalkDirection(Vector3f.ZERO);
                }
                onMoveToComplete();
            }

            //System.out.println(moveToElapsedTime + " " + moveToTravelTime + " ");
        }
    }

    @Override
    public void physicsTick(PhysicsSpace space, float tpf) {
    }
}
