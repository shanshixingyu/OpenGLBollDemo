uniform mat4 uMVPMatrix;
uniform mat4 uMatrix;
uniform vec3 uLightPosition;
uniform vec3 uCameraPosition;
attribute vec3 aPosition;
attribute vec3 aNormal;
varying vec3 vPosition;
varying vec4 vAmbient;
varying vec4 vDiffuse;
varying vec4 vSpecular;


void pointLight(in vec3 normal, inout vec4 diffuse,inout vec4 specular,in vec4 lightDiffuse,in vec4 lightSpecular){
    vec3 normalTarget = aPosition +normal;
    vec3 newNormal= (uMatrix * vec4(normalTarget,4)).xyz-(uMatrix * vec4(aPosition,4)).xyz;
    newNormal= normalize(newNormal);

    vec3 light = uLightPosition - (uMatrix * vec4(aPosition,4)).xyz;
    light = normalize(light);
    // 散射光
    diffuse = lightDiffuse * max(0.0,dot(newNormal,light));

//    // 镜面光
    vec3 eye = uCameraPosition -(uMatrix * vec4(aPosition,4)).xyz;
    vec3 halfVector = normalize(eye + light);
    float temp = dot(newNormal,halfVector);
    float shininess=1.0;				//粗糙度，越小越光滑
    specular = lightSpecular * max(0.0,pow(temp,shininess));
}

void main(){
    gl_Position = uMVPMatrix * vec4(aPosition,1);
    vPosition = aPosition;
    vAmbient = vec4(0.2,0.2,0.2,1.0);
    vec4 diffuse= vec4(0.0,0.0,0.0,0.0);
    vec4 specular= vec4(0.0,0.0,0.0,0.0);
//    pointLight(aPosition,diffuse,specular,vec4(0.8,0.8,0.8,1.0),vec4(0.7,0.7,0.7,1.0));
    pointLight(aPosition,diffuse,specular,vec4(0.8,0.8,0.8,1.0),vec4(1.0,1.0,1.0,1.0));
    vDiffuse = diffuse;
}