package com.buggy.GLz;
import static org.lwjgl.opengl.GL11.*;

import java.util.Locale;

import com.buggy.GLz.Engine.GameObject;

import static org.lwjgl.glfw.GLFW.*;
public class Game {
Engine engine;
int fontTex;
int charSize = 8;
int fontCols = 16;
int fontAtlasWidth = 128;
int fontAtlasHeight = 128;
boolean helpScreen = true;
boolean f1WasDown = false;
boolean FPSScreen = true;
boolean taWasDown = false;
public Game(Engine engine) {
    this.engine = engine;
}
void initFont() {
    fontTex = engine.loadTexture("/com/buggy/GLz/res/font.bmp", false);
}
void initModels() {
    // You initialize your models in here by calling Engine.Mesh MeshName = engine.new Mesh(Engine.loacMesh("/Path/To/Mesh.mesh"));
    Engine.Mesh teapot = engine.new Mesh(Engine.loadMesh("/com/buggy/GLz/res/utahteapot.mesh"));
    // You initialize your textures in here by calling int TextureName = engine.loadTexture("/Path/To/Texture.png", true);
    int sampleTex = engine.loadTexture("/com/buggy/GLz/res/sample.png", true);
    engine.GameObjects.add(engine.new GameObject(teapot, sampleTex, 7, 0, 0, 2f));
}
void objectlogic(GameObject obj, float deltaTime) {
    if (obj.id == 1) {
        obj.yaw += 10f * deltaTime;
    }
    if (obj.id == 2) {
        obj.roll += 60f * deltaTime;
    }
}
void uiBegin() {
    glDisable(GL_CULL_FACE);
    glDisable(GL_LIGHTING);
    glDisable(GL_DEPTH_TEST);
    glDisable(GL_FOG);
    glDepthMask(false);
    glEnable(GL_TEXTURE_2D); 
    glMatrixMode(GL_PROJECTION);
    glPushMatrix();
    glLoadIdentity();
    glOrtho(0, engine.width, engine.height, 0, -1, 1);
    glMatrixMode(GL_MODELVIEW);
    glPushMatrix();
    glLoadIdentity();
}

void uiEnd() {
    glMatrixMode(GL_MODELVIEW);
    glPopMatrix();
    glMatrixMode(GL_PROJECTION);
    glPopMatrix();
    glMatrixMode(GL_MODELVIEW);
    glEnable(GL_TEXTURE_2D);
    glDepthMask(true);
    glEnable(GL_DEPTH_TEST);
    glEnable(GL_LIGHTING);
    glEnable(GL_FOG);
    glEnable(GL_CULL_FACE);
}
void drawChar(char ch, float x, float y, float scaleX, float scaleY) {
    int id = (int)ch;
    int cx = id % fontCols;
    int cy = id / fontCols;
    float u0 = (cx * charSize) / (float)fontAtlasWidth;
    float v0 = (cy * charSize) / (float)fontAtlasHeight;
    float u1 = ((cx + 1) * charSize) / (float)fontAtlasWidth;
    float v1 = ((cy + 1) * charSize) / (float)fontAtlasHeight;
    float w = charSize * scaleX;
    float h = charSize * scaleY;
    glBindTexture(GL_TEXTURE_2D, fontTex);
    glBegin(GL_QUADS);
    glTexCoord2f(u0, v0);
    glVertex2f(x, y);
    glTexCoord2f(u1, v0);
    glVertex2f(x + w, y);
    glTexCoord2f(u1, v1);
    glVertex2f(x + w, y + h);
    glTexCoord2f(u0, v1);
    glVertex2f(x, y + h);
    glEnd();
}

void drawText(String text, float x, float y, float scaleX, float scaleY, float spacing) {
    for (int i = 0; i < text.length(); i++) {drawChar(text.charAt(i),x + i * (charSize * scaleX + spacing),y,scaleX,scaleY);}
}
int FPSIndex;
String fps;
void drawFPS(float deltaTime) {
    if (!FPSScreen) return;
	uiBegin();
	if (FPSIndex++ % 150 == 0)
		fps = String.format(Locale.US,"%.1f", 1.0f / deltaTime);
	drawText(fps + " FPS", 40, 40, 2.8f, 1.8f);
	uiEnd();
	FPSIndex++;
}
void drawHelpScreen() {
    if (!helpScreen) return;
    glDisable(GL_CULL_FACE);
    glDisable(GL_LIGHTING);
    glDisable(GL_DEPTH_TEST);
    glDisable(GL_FOG);
    glDepthMask(false);
    glMatrixMode(GL_PROJECTION);
    glPushMatrix();
    glLoadIdentity();
    glOrtho(0, engine.width, engine.height, 0, -1, 1);
    glMatrixMode(GL_MODELVIEW);
    glPushMatrix();
    glLoadIdentity();
    glDisable(GL_TEXTURE_2D);
    glColor4f(0f, 0f, 0f, 1f);
    glBegin(GL_QUADS);
    glVertex2f(0, 0);
    glVertex2f(engine.width, 0);
    glVertex2f(engine.width, engine.height);
    glVertex2f(0, engine.height);
    glEnd();
    glEnable(GL_TEXTURE_2D);
    drawText("GLz ENGINE", 40, 40, 2.8f, 1.8f);
    drawText("W-FORWARD", 40, 100, 2.4f, 1.6f);
    drawText("S-BACKWARD", 40, 130, 2.4f, 1.6f);
    drawText("A-LEFT", 40, 160, 2.4f, 1.6f);
    drawText("D-RIGHT", 40, 190, 2.4f, 1.6f);
    drawText("ESC-QUIT", 40, 270, 2.4f, 1.6f);
    drawText("F11-FULLSCREEN", 420, 100, 2.4f, 1.6f);
    drawText("F1-TOGGLE HELP", 420, 130, 2.4f, 1.6f);
    drawText("MENU", 420, 160, 2.4f, 1.6f);
    drawText("TAB-TOGGLE FPS", 420, 190, 2.4f, 1.6f);
    
    drawText("(F1 TO CLOSE)", 420, 570, 2.4f, 1.6f);
    glMatrixMode(GL_MODELVIEW);
    glPopMatrix();
    glMatrixMode(GL_PROJECTION);
    glPopMatrix();
    glMatrixMode(GL_MODELVIEW);
    glDepthMask(true);
    glEnable(GL_DEPTH_TEST);
    glEnable(GL_LIGHTING);
    glEnable(GL_FOG);
    glEnable(GL_CULL_FACE);
}
void drawLoadingScreen() {
    glDisable(GL_CULL_FACE);
    glDisable(GL_LIGHTING);
    glDisable(GL_DEPTH_TEST);
    glDisable(GL_FOG);
    glDepthMask(false);
    glMatrixMode(GL_PROJECTION);
    glPushMatrix();
    glLoadIdentity();
    glOrtho(0, engine.width, engine.height, 0, -1, 1);
    glMatrixMode(GL_MODELVIEW);
    glPushMatrix();
    glLoadIdentity();
    glDisable(GL_TEXTURE_2D);
    glColor4f(0f, 0f, 0f, 1f);
    glBegin(GL_QUADS);
    glVertex2f(0, 0);
    glVertex2f(engine.width, 0);
    glVertex2f(engine.width, engine.height);
    glVertex2f(0, engine.height);
    glEnd();
    glEnable(GL_TEXTURE_2D);
    drawText("GLz ENGINE", 40, 40, 2.8f, 1.8f);
    drawText("LOADING ASSETS...", 40, 100, 2.4f, 1.6f);
    glMatrixMode(GL_MODELVIEW);
    glPopMatrix();
    glMatrixMode(GL_PROJECTION);
    glPopMatrix();
    glMatrixMode(GL_MODELVIEW);
    glDepthMask(true);
    glEnable(GL_DEPTH_TEST);
    glEnable(GL_LIGHTING);
    glEnable(GL_FOG);
    glEnable(GL_CULL_FACE);
}

void updateUI(float deltaTime) {
    boolean f1Down = glfwGetKey(engine.getWindow(), GLFW_KEY_F1) == GLFW_PRESS;
    if (f1Down && !f1WasDown) helpScreen = !helpScreen;
    f1WasDown = f1Down;
    boolean taDown = glfwGetKey(engine.getWindow(), GLFW_KEY_TAB) == GLFW_PRESS;
    if (taDown && !taWasDown) FPSScreen = !FPSScreen;
    taWasDown = taDown;
}
void drawText(String text, float x, float y, float sx, float sy) {
    glColor4f(0f, 0.25f, 0f, 1f);
    drawText(text, x + 3, y + 3, sx, sy, 2f);
    glColor4f(0f, 1f, 0.25f, 1f);
    drawText(text, x, y, sx, sy, 2f);
}

public static void main(String[] args) {
    new Engine().run();
}
}
