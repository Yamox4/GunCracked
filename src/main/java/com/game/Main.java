package com.game;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

public class Main {

    private long window;
    private Game game;

    public void run() {
        init();
        loop();
        cleanup();
    }

    private void init() {
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);

        window = GLFW.glfwCreateWindow(1600, 1028, "Tron Cube Game", MemoryUtil.NULL, MemoryUtil.NULL);
        if (window == MemoryUtil.NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Position window on second monitor if available
        positionWindowOnSecondMonitor();

        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwSwapInterval(1);
        GLFW.glfwShowWindow(window);

        GL.createCapabilities();

        game = new Game(window);
        game.init();
    }

    private void loop() {
        GL11.glClearColor(0.10f, 0.10f, 0.12f, 1.0f); // Purple sky background
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(1.0f); // Thicker lines for better visibility

        while (!GLFW.glfwWindowShouldClose(window)) {
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            game.update();
            game.render();

            GLFW.glfwSwapBuffers(window);
            GLFW.glfwPollEvents();
        }
    }

    private void positionWindowOnSecondMonitor() {
        // Get all available monitors
        org.lwjgl.PointerBuffer monitors = GLFW.glfwGetMonitors();

        if (monitors != null && monitors.remaining() > 1) {
            // Get the second monitor (index 1)
            long secondMonitor = monitors.get(1);

            // Get the video mode of the second monitor
            org.lwjgl.glfw.GLFWVidMode vidMode = GLFW.glfwGetVideoMode(secondMonitor);

            if (vidMode != null) {
                // Get monitor position
                int[] monitorX = new int[1];
                int[] monitorY = new int[1];
                GLFW.glfwGetMonitorPos(secondMonitor, monitorX, monitorY);

                // Center the window on the second monitor
                int windowWidth = 1600;
                int windowHeight = 1028;
                int posX = monitorX[0] + (vidMode.width() - windowWidth) / 2;
                int posY = monitorY[0] + (vidMode.height() - windowHeight) / 2;

                GLFW.glfwSetWindowPos(window, posX, posY);

                System.out.println("Window positioned on second monitor at: " + posX + ", " + posY);
                System.out.println("Second monitor resolution: " + vidMode.width() + "x" + vidMode.height());
            }
        } else {
            System.out.println("Second monitor not found. Using primary monitor.");
            // Center on primary monitor as fallback
            long primaryMonitor = GLFW.glfwGetPrimaryMonitor();
            org.lwjgl.glfw.GLFWVidMode vidMode = GLFW.glfwGetVideoMode(primaryMonitor);

            if (vidMode != null) {
                int windowWidth = 1600;
                int windowHeight = 1028;
                int posX = (vidMode.width() - windowWidth) / 2;
                int posY = (vidMode.height() - windowHeight) / 2;

                GLFW.glfwSetWindowPos(window, posX, posY);
            }
        }
    }

    private void cleanup() {
        game.cleanup();
        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
    }

    public static void main(String[] args) {
        new Main().run();
    }
}
