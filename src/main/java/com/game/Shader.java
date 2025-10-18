package com.game;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

public class Shader {
    private int programId;
    private int vertexShaderId;
    private int fragmentShaderId;
    
    public Shader() {
        programId = GL20.glCreateProgram();
        if (programId == 0) {
            throw new RuntimeException("Could not create Shader");
        }
        
        createVertexShader();
        createFragmentShader();
        link();
    }
    
    private void createVertexShader() {
        String vertexShaderCode = 
            "#version 330 core\n" +
            "layout (location = 0) in vec3 position;\n" +
            "layout (location = 1) in vec3 normal;\n" +
            "uniform mat4 mvpMatrix;\n" +
            "uniform mat4 modelMatrix;\n" +
            "uniform mat4 viewMatrix;\n" +
            "uniform mat4 projectionMatrix;\n" +
            "out vec3 FragPos;\n" +
            "out vec3 Normal;\n" +
            "void main() {\n" +
            "    FragPos = vec3(modelMatrix * vec4(position, 1.0));\n" +
            "    Normal = mat3(transpose(inverse(modelMatrix))) * normal;\n" +
            "    gl_Position = mvpMatrix * vec4(position, 1.0);\n" +
            "}";
        
        vertexShaderId = createShader(vertexShaderCode, GL20.GL_VERTEX_SHADER);
    }
    
    private void createFragmentShader() {
        String fragmentShaderCode = 
            "#version 330 core\n" +
            "in vec3 FragPos;\n" +
            "in vec3 Normal;\n" +
            "uniform vec3 color;\n" +
            "uniform vec3 lightPos;\n" +
            "uniform vec3 viewPos;\n" +
            "uniform bool isWireframe;\n" +
            "out vec4 fragColor;\n" +
            "void main() {\n" +
            "    if (isWireframe) {\n" +
            "        fragColor = vec4(color, 1.0);\n" +
            "        return;\n" +
            "    }\n" +
            "    \n" +
            "    // Ambient\n" +
            "    float ambientStrength = 0.3;\n" +
            "    vec3 ambient = ambientStrength * color;\n" +
            "    \n" +
            "    // Diffuse\n" +
            "    vec3 norm = normalize(Normal);\n" +
            "    vec3 lightDir = normalize(lightPos - FragPos);\n" +
            "    float diff = max(dot(norm, lightDir), 0.0);\n" +
            "    vec3 diffuse = diff * color;\n" +
            "    \n" +
            "    // Specular\n" +
            "    float specularStrength = 0.8;\n" +
            "    vec3 viewDir = normalize(viewPos - FragPos);\n" +
            "    vec3 reflectDir = reflect(-lightDir, norm);\n" +
            "    float spec = pow(max(dot(viewDir, reflectDir), 0.0), 64);\n" +
            "    vec3 specular = specularStrength * spec * vec3(1.0);\n" +
            "    \n" +
            "    vec3 result = ambient + diffuse + specular;\n" +
            "    fragColor = vec4(result, 1.0);\n" +
            "}";
        
        fragmentShaderId = createShader(fragmentShaderCode, GL20.GL_FRAGMENT_SHADER);
    }
    
    private int createShader(String shaderCode, int shaderType) {
        int shaderId = GL20.glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new RuntimeException("Error creating shader. Type: " + shaderType);
        }
        
        GL20.glShaderSource(shaderId, shaderCode);
        GL20.glCompileShader(shaderId);
        
        if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == 0) {
            throw new RuntimeException("Error compiling Shader code: " + GL20.glGetShaderInfoLog(shaderId, 1024));
        }
        
        GL20.glAttachShader(programId, shaderId);
        return shaderId;
    }
    
    private void link() {
        GL20.glLinkProgram(programId);
        if (GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) == 0) {
            throw new RuntimeException("Error linking Shader code: " + GL20.glGetProgramInfoLog(programId, 1024));
        }
        
        if (vertexShaderId != 0) {
            GL20.glDetachShader(programId, vertexShaderId);
        }
        if (fragmentShaderId != 0) {
            GL20.glDetachShader(programId, fragmentShaderId);
        }
        
        GL20.glValidateProgram(programId);
        if (GL20.glGetProgrami(programId, GL20.GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating Shader code: " + GL20.glGetProgramInfoLog(programId, 1024));
        }
    }
    
    public void use() {
        GL20.glUseProgram(programId);
    }
    
    public void setUniform(String uniformName, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer fb = stack.mallocFloat(16);
            value.get(fb);
            GL20.glUniformMatrix4fv(GL20.glGetUniformLocation(programId, uniformName), false, fb);
        }
    }
    
    public void setUniform(String uniformName, Vector3f value) {
        GL20.glUniform3f(GL20.glGetUniformLocation(programId, uniformName), value.x, value.y, value.z);
    }
    
    public void setUniform(String uniformName, boolean value) {
        GL20.glUniform1i(GL20.glGetUniformLocation(programId, uniformName), value ? 1 : 0);
    }
    
    public void cleanup() {
        use();
        if (programId != 0) {
            GL20.glDeleteProgram(programId);
        }
    }
}