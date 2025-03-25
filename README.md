# TileBeanEngine

![TileBeanEngine Logo](/assets/gfx/tilebeanengine_logo.png)

TileBeanEngine is a basic Java game engine designed for learning.
This engine is intended to be a slight step up in difficulty compared to what one might learn in school, and is closer to a real-world engine experience.

## Features

* Well-commented code, explaining the game engine design and structure
* Several demos built into the engine (Just run the *.jar!)
* Basic entity-component system (ECS)
* Allows component inheritance for design flexibility
* Basic Tiled *.tmx support
* Collision between colliders and tilemaps
* Useful, basic built-in components:
  * Collider
  * Sprite (displays images or animations)
  * TimerInstance / TimerManager
  * Tween (interpolated movement/rotation/scaling/etc.)

## Examples

```
// Load an animation.
TextureAsset anim = new TextureAsset("gfx/character/idle.anim");
anim.load();
TextureAssetHandle anim_handle = TileBeanEngine.assets.add(anim);

// Create a new game object.
Object2D obj = new Object2D();
Object2DHandle obj_handle = TileBeanEngine.world.add(obj);

// Add a Sprite component. Sprites can display images and animations.
Sprite sprite = new Sprite();
sprite.setGraphics(anim_handle);
sprite.play();
TileBeanEngine.world.addComponent(obj_handle, sprite);
```

```
// Create a new game object.
Object2D obj = new Object2D();
Object2DHandle obj_handle = TileBeanEngine.world.add(obj);

// Create a timer manager, which can manage one or more timers.
TimerManager timer_manager = new TimerManager();
TileBeanEngine.world.addComponent(obj_handle, timer_manager);

// Start a 2 second timer that repeats infinitely (-1 repeats).
timer_manager.start("timer", 2.0f, -1, false);

// Later, each frame...
TimerManager timer_manager = (TimerManager)TileBeanEngine.world.getComponent(obj_handle, TimerManager.class.hashCode());
TimerInstance timer = timer_manager.get("timer");
if (timer.isFinished()) {
	// Perform an action
	timer.clearFinished();
}
```

```
// Create two new game objects.
Object2D obj = new Object2D();
Object2DHandle obj_handle = TileBeanEngine.world.add(obj);
Object2D obj2 = new Object2D();
Object2DHandle obj2_handle = TileBeanEngine.world.add(obj2);

// Add a Collider component to each. The Collider class has convenience methods to easily make box colliders and circle colliders.
Collider c = Collider.makeBoxCollider(32, 32); // 32x32 box
TileBeanEngine.world.addComponent(obj_handle, c);
Collider c2 = Collider.makeCircleCollider(32, 16); // Circle with 16 points and radius 32
TileBeanEngine.world.addComponent(obj_handle, c);

// Later, each frame...
// Retrieve the colliders
Collider collider = (Collider)TileBeanEngine.world.getComponent(obj_handle, Collider.class.hashCode());
Collider collider2 = (Collider)TileBeanEngine.world.getComponent(obj2_handle, Collider.class.hashCode());
// Detect collision
CollisionInfo info = Collision.detect(collider, collider2); // You can examine a CollisionInfo without resolving a collision, or pass it to Collision.resolve to resolve it.
// Resolve collision
Collision.resolve(info);
```