//
// Created by imxqd on 2017/3/31.
//

#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_xyz_imxqd_twoer_im_Client_URL(JNIEnv *env, jobject ) {
    std::string str = "https://api.cn.ronghub.com/user/getToken.xml";
    return env->NewStringUTF(str.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_xyz_imxqd_twoer_im_Client_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string str = "C0Nx3fPkUn9C";
    return env->NewStringUTF(str.c_str());
}






