uniform mat4 mMVPMatrix;
attribute vec3 aPosition;
attribute vec4 aColor;
varying vec4 aaColor;

void main(){
    gl_Position=mMVPMatrix * vec4(aPosition,1);
    aaColor=aColor;
}