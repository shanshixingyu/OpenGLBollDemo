uniform mat4 mMVPMatrix;
uniform vec3 aLightPosition;
attribute vec3 aPosition;
attribute vec3 aNormal;
varying vec3 vPosition;
varying vec4 vAmbient;


void pointLight(in vec3 normal, inout vec4 diffuse, in vec3 lightPosition, in vec4 lightDiffuse){
    vec3 normalTarget = aPosition +normal;

}

void main(){
    gl_Position = mMVPMatrix * vec4(aPosition,1);
    vPosition = aPosition;
    vAmbient=vec4(0.2,0.2,0.2,1.0);
}