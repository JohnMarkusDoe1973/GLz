package com.buggy.GLz;

import org.lwjgl.opengl.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;
import java.nio.ByteBuffer;
import org.lwjgl.glfw.GLFWVidMode;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import java.nio.IntBuffer;

public class Engine {
    Game game = new Game(this);
    float lx = -0.45f;
    float ly = 0.90f;
    float lz = 0.30f;
	class Mesh {
	    float[] vertices;

	    Mesh(float[] vertices) {
	        this.vertices = vertices;
	    }
	}
    static int nextID = 1;
	class GameObject {
	    int id;
	    float x, y, z, u, v;
	    float yaw, pitch, roll;
	    float scale;
	    Mesh mesh;
	    int texture;

	    GameObject(Mesh mesh, int texture, float x, float y, float z, float scale) {
	        this.id = nextID++;
	        System.out.println(this.id);
	        this.mesh = mesh;
	        this.texture = texture;
	        this.x = x;
	        this.y = y;
	        this.z = z;
	        this.scale = scale;
	    }

	    void update(float dt) {
	        
	    }
	    void render() {
	    	float[] vertices = mesh.vertices;
	        glPushMatrix();
	        glTranslatef(x, y, z);
	        glScalef(scale, scale, scale);
	        glRotatef(yaw, 0, 1, 0);
	        glRotatef(pitch, 1, 0, 0);
	        glRotatef(roll, 0, 0, 1);
	        glBindTexture(GL_TEXTURE_2D, texture);
	        glBegin(GL_TRIANGLES);
	        for (int i = 0; i < vertices.length; i += 15) {
        		glColor3f(1f, 1f, 1f);
        		float v1x = vertices[i];
        		float v1y = vertices[i+1];
        		float v1z = vertices[i+2];
        		float v1u = vertices[i+3];
        		float v1v = vertices[i+4];
        		float v2x = vertices[i+5];
        		float v2y = vertices[i+6];
        		float v2z = vertices[i+7];
        		float v2u = vertices[i+8];
        		float v2v = vertices[i+9];
        		float v3x = vertices[i+10];
        		float v3y = vertices[i+11];
        		float v3z = vertices[i+12];
        		float v3u = vertices[i+13];
        		float v3v = vertices[i+14];

        		float edge1x = v2x - v1x;
        		float edge2x = v3x - v1x;
        		float edge1y = v2y - v1y;
        		float edge2y = v3y - v1y;
        		float edge1z = v2z - v1z;
        		float edge2z = v3z - v1z;
        		float nx = edge1y*edge2z - edge1z*edge2y;
        		float ny = edge1z*edge2x - edge1x*edge2z;
        		float nz = edge1x*edge2y - edge1y*edge2x;
        		glNormal3f(nx, ny, nz);
			    glTexCoord2f(v1u, v1v);
        	    glVertex3f(v1x,v1y,v1z);
        	    glTexCoord2f(v2u, v2v);
        	    glVertex3f(v2x,v2y,v2z);
        	    glTexCoord2f(v3u, v3v);
        	    glVertex3f(v3x,v3y,v3z);
        	    
        	}
	        glEnd();

	        glPopMatrix();
	    }
	}
	ArrayList<GameObject> GameObjects = new ArrayList<>();
	public void closeGame() {
	    glfwSetWindowShouldClose(this.window, true);
	}
	float yaw;
	float pitch;
	float X;
	float Y;
	float Z = 5f;
	float[] vertices = {
		    -1, -1, 0, 0, 0,
		     1, -1, 0, 1, 0,
		     1,  1, 0, 1, 1,

		    -1, -1, 0, 0, 0,
		     1,  1, 0, 1, 1,
		    -1,  1, 0, 0, 1
		};
	
    long window;
    int width = 1920;
    int height = 1080;
    int outerwidth = 1920;
    int outerheight = 1080;
    double lastMouseX = width / 2.0;
    double lastMouseY = height / 2.0;
    boolean firstMouse = true;
    int texture; 
    int loadTexture(String path, boolean stbiStatus) {
        ByteBuffer imageFile = loadJarResource(path);
        IntBuffer w = BufferUtils.createIntBuffer(1);
        IntBuffer h = BufferUtils.createIntBuffer(1);
        IntBuffer c = BufferUtils.createIntBuffer(1);
        stbi_set_flip_vertically_on_load(stbiStatus);
        ByteBuffer image =stbi_load_from_memory(imageFile, w, h, c, 4);
        if (image == null)throw new RuntimeException(stbi_failure_reason());
        int tex = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, tex);
        glTexImage2D(GL_TEXTURE_2D,0,GL_RGBA,w.get(),h.get(),0,GL_RGBA,GL_UNSIGNED_BYTE,image);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        stbi_image_free(image);
        return tex;
    }
    
    private void mouseLook() {
        double[] mouseX = new double[1];
        double[] mouseY = new double[1];
        glfwGetCursorPos(window, mouseX, mouseY);
        if (firstMouse) {
            lastMouseX = mouseX[0];
            lastMouseY = mouseY[0];
            firstMouse = false;
            return;
        }
        double dx = mouseX[0] - lastMouseX;
        double dy = mouseY[0] - lastMouseY;
        lastMouseX = mouseX[0];
        lastMouseY = mouseY[0];
        float sensitivity = 0.1f;
        yaw -= dx * sensitivity;
        pitch -= dy * sensitivity;
        if (pitch > 89f) pitch = 89f;
        if (pitch < -89f) pitch = -89f;
    }
    public void run() {
        init();
        game.initFont();
        glClear(GL_COLOR_BUFFER_BIT);
        game.drawLoadingScreen();
        glfwSwapBuffers(window);
        game.initModels();
        loop();
        glfwDestroyWindow(window);
        glfwTerminate();
    }
    ByteBuffer loadJarResource(String path) {
        try (InputStream in = Engine.class.getResourceAsStream(path)) {
            if (in == null) {
                throw new RuntimeException("Resource missing from JAR: " + path);
            }

            byte[] bytes = in.readAllBytes();

            ByteBuffer buffer = BufferUtils.createByteBuffer(bytes.length);
            buffer.put(bytes);
            buffer.flip();

            return buffer;
        } catch (Exception e) {
            throw new RuntimeException("Could not read JAR resource: " + path, e);
        }
    }
    static float[] loadMesh(String path) {
    	ArrayList<Float> values = new ArrayList<>();

    	try (InputStream in = Engine.class.getResourceAsStream(path);
    			BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"))) {
                 String line;
                 while ((line = reader.readLine()) != null) {
                	 line = line.trim();

                	 if (line.isEmpty()) {
                	     continue;
                	 }
                     String[] parts = line.split("\\s+");
                     float x = Float.parseFloat(parts[0]);
                     float y = Float.parseFloat(parts[1]);
                     float z = Float.parseFloat(parts[2]);
                     float u = Float.parseFloat(parts[3]);
                     float v = Float.parseFloat(parts[4]);
                     values.add(x);
                     values.add(y);
                     values.add(z);
                     values.add(u);
                     values.add(v);
                 }
             } catch (Exception e) {
                 throw new RuntimeException("Could not load mesh: " + path, e);
             }

        float[] result = new float[values.size()];
        for (int i = 0; i < values.size(); i++) {
        	result[i] = values.get(i);
        }
        return result;
    }
    float velX = 0f;
    float velY = 0f;
    float velZ = 0f;
    float playerW = .9f;
    float playerH = .9f;

    boolean collides(float x, float y, float z) {
        float minX = x - playerW;
        float maxX = x + playerW;
        float minY = y - 0.2f;
        float maxY = y + playerH;
        float minZ = z - playerW;
        float maxZ = z + playerW;

        for (GameObject obj : GameObjects) {
            if (aabbOverlap(
                minX, minY, minZ, maxX, maxY, maxZ,
                obj.x - obj.scale, obj.y - obj.scale, obj.z - obj.scale,
                obj.x + obj.scale, obj.y + obj.scale, obj.z + obj.scale
            )) {
                return true;
            }
        }
        return false;
    }

    boolean fullscreen = false;
    int windowPosX, windowPosY;
    int windowWidth = width;
    int windowHeight = height;
    void toggleFullscreen() {
        fullscreen = !fullscreen;
        long monitor = glfwGetPrimaryMonitor();
        GLFWVidMode vid = glfwGetVideoMode(monitor);
        if (fullscreen) {
        	glfwSetWindowMonitor(window, monitor, 0, 0, vid.width(), vid.height(), vid.refreshRate());
            glViewport(0, 0, vid.width(), vid.height());
            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();
            float aspect = vid.width() / (float) vid.height();
            float near = 0.1f;
            float far = 200f;
            glFrustum(-aspect * near, aspect * near, -near, near, near, far);
            glMatrixMode(GL_MODELVIEW);
            glLoadIdentity();
        } else {
        	glfwSetWindowMonitor(window, 0, windowPosX, windowPosY + 50, windowWidth, windowHeight, 0);
            glViewport(0, 0, windowWidth, windowHeight);
            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();
            float aspect = windowWidth/ (float) windowHeight;
            float near = 0.1f;
            float far = 200f;
            glFrustum(-aspect * near, aspect * near, -near, near, near, far);
            glMatrixMode(GL_MODELVIEW);
            glLoadIdentity();
        }
    }

	public long getWindow() {
		return window;
	}
	
    boolean f11WasDown = false;
    boolean aabbOverlap( float ax1, float ay1, float az1, float ax2, float ay2, float az2, float bx1, float by1, float bz1, float bx2, float by2, float bz2) {return ax1 <= bx2 && ax2 >= bx1 &&ay1 <= by2 && ay2 >= by1 &&az1 <= bz2 && az2 >= bz1;}
    private void movement(float deltaTime) {
                float speed = 5f * deltaTime;
                float angleSpeed = 5 * speed;
                if (glfwGetKey(window, GLFW_KEY_UP) == GLFW_PRESS) pitch += angleSpeed;
                if (glfwGetKey(window, GLFW_KEY_DOWN) == GLFW_PRESS) pitch -= angleSpeed;
                if (glfwGetKey(window, GLFW_KEY_LEFT) == GLFW_PRESS) { yaw += angleSpeed; }
                if (glfwGetKey(window, GLFW_KEY_RIGHT) == GLFW_PRESS) { yaw -= angleSpeed;}
                if (glfwGetKey(window, GLFW_KEY_LEFT_CONTROL) == GLFW_PRESS) { velX *= 0.35f;velY *= 0.35f;velZ *= 0.35f;}
                boolean f11Down = glfwGetKey(window, GLFW_KEY_F11) == GLFW_PRESS;
                if (f11Down && !f11WasDown) {toggleFullscreen();}
                f11WasDown = f11Down;

                if ((glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) && (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS)) speed /= Math.sqrt(2);
                if ((glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) && (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS)) speed /= Math.sqrt(2);
                if ((glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) && (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS)) speed /= Math.sqrt(2);
                if ((glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) && (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS)) speed /= Math.sqrt(2);
                
                if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
                    float angle = -(float)Math.toRadians(yaw);
                    float pitchAngle = (float)Math.toRadians(pitch);
                    velX += speed * (float)Math.sin(angle) * Math.cos(pitchAngle);
                    velY += speed * (float)Math.sin(pitchAngle);
                    velZ -= speed * (float)Math.cos(angle) * Math.cos(pitchAngle); 
                }
                if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) { 
                    float angle = -(float)Math.toRadians(yaw);
                    float pitchAngle = (float)Math.toRadians(pitch);
                    velX -= speed * (float)Math.sin(angle) * Math.cos(pitchAngle);
                    velY -= speed * (float)Math.sin(pitchAngle);
                    velZ += speed * (float)Math.cos(angle) * Math.cos(pitchAngle);
                }
                if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
                    float angle = -(float)Math.toRadians(yaw + 90);
                    velX += speed * (float)Math.sin(angle);
                    velZ -= speed * (float)Math.cos(angle);
                }
                if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) { 
                    float angle = -(float)Math.toRadians(yaw + 90);
                    velX -= speed * (float)Math.sin(angle);
                    velZ += speed * (float)Math.cos(angle);
                }
                if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS) closeGame();

    }
    boolean visible(GameObject obj) {
        float dx = obj.x - X;
        float dy = obj.y - Y;
        float dz = obj.z - Z;

        float distSq = dx*dx + dy*dy + dz*dz;
        float maxDist = 200f + obj.scale;

        return distSq < maxDist * maxDist;
    }
    
    private void loop() {
    	double lastTime = glfwGetTime();

        while (!glfwWindowShouldClose(window)) {

    	    double currentTime = glfwGetTime();
    	    float deltaTime = (float)(currentTime - lastTime);
    	    lastTime = currentTime;
        	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        	
        	glMatrixMode(GL_MODELVIEW);
        	movement(deltaTime);
            float nextX = X + velX;
            float nextY = Y + velY;
            float nextZ = Z + velZ;

            if (!collides(nextX, Y, Z)) X = nextX;
            else velX = 0;

            if (!collides(X, nextY, Z)) Y = nextY;
            else velY = 0;

            if (!collides(X, Y, nextZ)) Z = nextZ;
            else velZ = 0;
        	mouseLook();
        	glLoadIdentity();
        	glRotatef(-pitch, 1,0,0);
        	glRotatef(-yaw,   0,1,0);
        	glTranslatef(-X,-Y,-Z);

        	FloatBuffer lightPos = BufferUtils.createFloatBuffer(4).put(new float[]{lx, ly, lz, 0.0f}); lightPos.flip();
            glLightfv(GL_LIGHT0, GL_POSITION, lightPos);
        		
        	for (GameObject obj : GameObjects) {
        		game.objectlogic(obj, deltaTime);
        	    obj.update(deltaTime);
        	    if (visible(obj)) {
        	    	obj.render();
        	    }
        	}
        	game.updateUI(deltaTime);
        	game.drawFPS(deltaTime);
        	game.drawHelpScreen();
        	
            glfwSwapBuffers(window);
            glfwPollEvents();
            velX *= 0.95f;
            velY *= 0.95f;
            velZ *= 0.95f;
        }
    }
    
    private void init() {
        glfwInit();
        window = glfwCreateWindow(width, height, "GLz", 0, 0);
        glfwWindowHint(GLFW_SAMPLES, 0);
        glfwMakeContextCurrent(window);
        GL.createCapabilities();
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        glfwSwapInterval(0);
        glClearColor(0x2e / 255f,0x35 / 255f,0x45 / 255f,1.0f);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        float aspect = width / (float) height;
        float near = 0.1f;
        float far = 200f;
        glFrustum(-aspect * near, aspect * near, -near, near, near, far);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);
        glEnable(GL_COLOR_MATERIAL);
        glColorMaterial(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE);
        glEnable(GL_NORMALIZE);
        FloatBuffer ambientLight = BufferUtils.createFloatBuffer(4).put(new float[]{0.2f, 0.2f, 0.2f, 1.0f}); ambientLight.flip();
        FloatBuffer diffuseLight = BufferUtils.createFloatBuffer(4).put(new float[]{0.8f, 0.75f, 0.7f, 1.0f}); diffuseLight.flip(); 
        glLightfv(GL_LIGHT0, GL_AMBIENT, ambientLight);
        glLightfv(GL_LIGHT0, GL_DIFFUSE, diffuseLight);
        glEnable(GL_FOG);
        glFogi(GL_FOG_MODE, GL_EXP2);
        glFogf(GL_FOG_DENSITY, 0.03f);
        FloatBuffer fogColor = BufferUtils.createFloatBuffer(4).put(new float[]{0x2e / 255f, 0x35 / 255f, 0x45 / 255f, 1.0f}); fogColor.flip();
        glFogfv(GL_FOG_COLOR, fogColor);
        glFogf(GL_FOG_START, 5.0f);
        glFogf(GL_FOG_END, 40.0f);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
    }

}
