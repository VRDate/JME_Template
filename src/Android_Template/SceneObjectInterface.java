/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Android_Template;

import com.jme3.math.Vector3f;

/**
 *
 * @author practicing01
 */
public interface SceneObjectInterface {
    void onMoveToComplete();
    void moveTo(Vector3f dest, float speed, boolean stopOnCompletion);
}
