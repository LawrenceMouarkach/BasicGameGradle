package com.lawrencemouarkach;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.shadow.BasicShadowRenderer;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;

public class Main extends SimpleApplication {
    private BulletAppState bulletAppState = new BulletAppState();
    private Node player;
    private BetterCharacterControl playerControl;
    private AnimControl control;
    private AnimChannel channel;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        stateManager.attach(bulletAppState);
        initialiseWorld();
    }

    private void initialiseWorld() {
        Texture texture = assetManager.loadTexture("sky.JPG");
        Spatial sky = SkyFactory.createSky(assetManager, texture, SkyFactory.EnvMapType.EquirectMap);

        setupViewPort();

        Geometry floatingBoxSpatial = createFloatingBox();

        Geometry worldSpatial = createWorld();

        addToRootNode(sky, floatingBoxSpatial, worldSpatial);
    }

    private void addToRootNode(Spatial sky, Geometry floatingBoxSpatial, Geometry worldSpatial) {
        rootNode.attachChild(sky);
        rootNode.attachChild(floatingBoxSpatial);
        rootNode.attachChild(worldSpatial);
        initPlayerNode();
        initPivotNode();
    }

    private Geometry createFloatingBox() {
        Box floatingBox = new Box(1, 1, 1);
        Material floatingBoxMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Geometry floatingBoxSpatial = new Geometry("Box", floatingBox);
        floatingBoxMaterial.setColor("Color", ColorRGBA.randomColor());
        floatingBoxSpatial.setMaterial(floatingBoxMaterial);
        floatingBoxSpatial.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        return floatingBoxSpatial;
    }

    private Geometry createWorld() {
        Box worldGround = new Box(100, -5, 100);
        Material worldMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        worldMaterial.setColor("Color", new ColorRGBA(0.0f, 0.4f, 0.0f, 0.9f));
        Geometry worldSpatial = new Geometry("Shut up", worldGround);
        worldSpatial.setMaterial(worldMaterial);
        worldSpatial.setShadowMode(RenderQueue.ShadowMode.Receive);
        worldSpatial.setLocalTranslation(0, -15, 0);
        return worldSpatial;
    }

    private void setupViewPort() {
        BasicShadowRenderer basicShadowRenderer = new BasicShadowRenderer(assetManager, 10000);
        basicShadowRenderer.setDirection(new Vector3f(.3f, -0.5f, -0.5f));
        viewPort.addProcessor(basicShadowRenderer);
        setDisplayFps(false);
    }

    private void initPlayerNode() {
        Spatial playerSpatial = assetManager.loadModel("character/human/male/ogre/male.scene");
        player = (Node) playerSpatial; // You can set the model directly to the player. (We just wanted to explicitly show that it's a spatial.)
        Node playerNode = new Node(); // You can create a new node to wrap your player to adjust the location. (This allows you to solve issues with character sinking into floor, etc.)
        playerNode.attachChild(player); // add it to the wrapper
        player.move(0, 3.5f, 0); // adjust position to ensure collisions occur correctly.
        player.setLocalScale(0.5f); // optionally adjust scale of model
// setup animation:
        player.addControl(new AnimControl());
        control = player.getControl(AnimControl.class);
//        control.addListener(this);
        channel = control.createChannel();
//        channel.setAnim("stand");
        playerControl = new BetterCharacterControl(1.5f, 12f, 2f); // construct character. (If your character bounces, try increasing height and
        // weight.)
        playerNode.addControl(playerControl); // attach to wrapper
// set basic physical properties:
        playerControl.setJumpForce(new Vector3f(0, 5f, 0));
        playerControl.setGravity(new Vector3f(0, 1f, 0));
        playerControl.warp(new Vector3f(0, 0, 0)); // warp character into landscape at particular location
// add to physics state
        bulletAppState.getPhysicsSpace().add(playerControl);
        bulletAppState.getPhysicsSpace().addAll(playerNode);
    }

    private void initPivotNode() {
        Box boxRed = new Box(1,1,1);
        Geometry red = new Geometry("Box", boxRed);
        red.setLocalTranslation(new Vector3f(1,3,1));
        Material mat2 = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setColor("Color", ColorRGBA.Red);
        red.setMaterial(mat2);

        Node pivot = new Node("pivot");
        rootNode.attachChild(pivot);
        pivot.attachChild(red);
        pivot.rotate(0.4f, 0.4f, 0.0f);

    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }


}
