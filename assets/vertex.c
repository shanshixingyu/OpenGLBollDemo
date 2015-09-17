uniform mat4 mMVPMatrix;
uniform mat4 mMatrix;
uniform vec3 aLightPosition;
attribute vec3 aPosition;
attribute vec3 aNormal;
varying vec3 vPosition;
varying vec4 vAmbient;
varying vec4 vDiffuse;


void pointLight(in vec3 normal, inout vec4 diffuse, in vec3 lightPosition, in vec4 lightDiffuse){
    vec3 normalTarget = aPosition +normal;
    vec3 newNormal= (mMatrix * vec4(normalTarget,4)).xyz-(mMatrix * vec4(aPosition,4)).xyz;
    newNormal= normalize(newNormal);

    vec3 light=lightPosition - (mMatrix * vec4(aPosition,4)).xyz;
    light=normalize(light);

    diffuse = lightDiffuse * max(0.0,dot(newNormal,light));
}

void main(){
    gl_Position = mMVPMatrix * vec4(aPosition,1);
    vPosition = aPosition;
    vAmbient = vec4(0.2,0.2,0.2,1.0);
    vec4 diffuse= vec4(0.0,0.0,0.0,0.0);
    pointLight(aPosition,diffuse,aLightPosition,vec4(0.8,0.8,0.8,1.0));
    vDiffuse = diffuse;
}