uniform mat4 mMVPMatrix;
attribute vec3 aPosition;
varying vec3 vPosition;

void main(){
    gl_Position = mMVPMatrix * vec4(aPosition,1);
    vPosition = aPosition;
}