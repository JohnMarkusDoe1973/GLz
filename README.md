# GLz

## What is it?

GLz is a tiny 3D engine written in Java.

It is based on LWJGL and OpenGL GL11.
GLz uses a simple immediate mode rendering pipeline and is intended to be intuitive and easy to modify.

## Features

GLz currently supports the following features:

```
GL11-based rendering,
textured mesh rendering,
custom mesh loading,
basic GameObject system,
position, rotation, and scale transforms,
per-object update logic,
first-person-style movement,
mouse look,
arrow-key camera rotation,
basic AABB collision detection, (define collision...)
OpenGL lighting,
fog,
bitmap font rendering,
loading screen,
help screen,
FPS counter,
fullscreen toggle.
```

## Controls

Inside GLz:

```
W - move forward
S - move backward
A - move left
D - move right
Mouse - look around
Arrow keys - rotate camera
Left Control - slow movement
F1 - toggle help screen
Tab - toggle FPS counter
F11 - toggle fullscreen
Escape - quit
```

## Meshes

GLz currently loads a simple custom mesh format.
Each line in a mesh file contains one vertex:

```
x y z u v
```

Vertices are read in groups of three and rendered as triangles. (!)

An example mesh may look something like this:

```
-1 -1 0 0 0
 1 -1 0 1 0
 1  1 0 1 1
```

## Dependencies

```
Java,
LWJGL 3.3.4,
GLFW,
OpenGL 1.1,
STB image loading,
Gradle
```

## Building

GLz includes a Gradle build configuration.

Build the project with:

```
gradle build
```

### Please note that The project is currently configured for Linux natives (natives-linux)


## Running

Run the application with:

```
gradle run
```

The configured main class is:

```
com.buggy.GLz.Game
```

The engine will create a window, load the font and model assets, and display the loading screen before entering the main loop.

## Creating Objects

Objects are created from a mesh and a texture.

Example:

```
Engine.Mesh teapot = engine.new Mesh(
    Engine.loadMesh("/com/buggy/GLz/res/utahteapot.mesh")
);

int sampleTex = engine.loadTexture(
    "/com/buggy/GLz/res/sample.png",
    true
);

engine.GameObjects.add(
    engine.new GameObject(teapot, sampleTex, 7, 0, 0, 2f)
);
```

## Status

```
WORK IN PROGRESS
```

GLz is still early in development. APIs, controls, rendering behavior, asset formats, and project structure may (and probably will) change frequently.

## Notes

GLz currently uses immediate mode OpenGL. It will always will. If you have an issue then fork GLz.

Collision detection is currently basic and uses object-scale AABBs rather than accurate mesh collision. Please note that collision will probably change.

## License

GLz is licensed under the GNU General Public License v3.0 or later. See [LICENSE](LICENSE) for details.
