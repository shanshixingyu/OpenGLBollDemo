 precision mediump float;
 uniform float uRadius;
 varying vec3 vPosition;
 varying vec4 vAmbient;
 varying vec4 vDiffuse;
 varying vec4 vSpecular;

 void main(){
     vec4 color;
     float n = 12.0;//一个坐标分量分的总份数
     float span = 2.0 * uRadius / n;//每一份的长度
     //每一维在立方体内的行列数
     int i = int((vPosition.x + uRadius)/span);
     int j = int((vPosition.y + uRadius)/span);
     int k = int((vPosition.z + uRadius)/span);
     //计算当点应位于白色块还是黑色块中
     int whichColor = int(mod(float(i+j+k),2.0));
     if(whichColor == 1) {//奇数时为红色
    		color = vec4(0.678,0.231,0.129,1.0);//红色
     }else {//偶数时为白色
    		color = vec4(1.0,1.0,1.0,1.0);//白色
     }

     //将计算出的颜色给此片元
     gl_FragColor = color*vAmbient + color*vDiffuse + color*vSpecular;
 }