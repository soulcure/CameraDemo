#include <jni.h>
#include <string>
#include "GraphicsHandle.h"

JNIEXPORT jint JNICALL
Java_com_example_camera_SuExec_imageArray(
        JNIEnv *env,
        jobject obj, jintArray array) {

    //获取参数int数组的元素个数;
    jsize size = env->GetArrayLength(array);
    if (size == 0) {
        return -1;
    }

    //获取int数组的所有元素
    jint *intArray = env->GetIntArrayElements(array, JNI_FALSE);

    //传递给c++处理
    GraphicsHandle handler;
    handler.onReceive(intArray);

    return 0;
}