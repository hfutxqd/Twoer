//
// Created by imxqd on 17-4-8.
//
#include <jni.h>
#include <iostream>
#include <cstdlib>
#include <sstream>
#include "AI.h"
#include <android/log.h>

#define  LOG_TAG    "GameAi"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

using namespace std;

AI wine;

int gomocup() {
    string command;
    Pos input, best;
    char dot;
    int size = 15;
    while (1) {
        cin >> command;
        for (size_t i = 0; i < command.size(); i++) {
            char &c = command[i];
            if (c >= 'a' && c <= 'z') {
                c += 'A' - 'a';
            }
        }
        if (command == "START") {
            cin >> size;
            if (size > MaxSize || size <= 5) {
                LOGE("ERROR");
            } else {
                wine.SetSize(size);
                LOGI("OK");
            }
        } else if (command == "RESTART") {
            wine.ReStart();
            LOGI("OK");
        } else if (command == "TAKEBACK") {
            wine.DelMove();
            LOGI("OK");
        } else if (command == "BEGIN") {
            best = wine.TurnBest();
            wine.TurnMove(best);
            LOGI("%d,%d", best.x, best.y);
        } else if (command == "TURN") {
            cin >> input.x >> dot >> input.y;
            if (input.x < 0 || input.x >= size || input.y < 0 || input.y >= size
                || wine.cell[input.x + 4][input.y + 4].piece != Empty) {
                LOGE("ERROR");
            } else {
                wine.TurnMove(input);
                best = wine.TurnBest();
                wine.TurnMove(best);
                LOGI("%d,%d", best.x, best.y);
            }
        } else if (command == "BOARD") {
            int c;
            Pos m;
            stringstream ss;
            wine.ReStart();

            cin >> command;
            while (command != "DONE") {
                ss.clear();
                ss << command;
                ss >> m.x >> dot >> m.y >> dot >> c;
                if (m.x < 0 || m.x >= size || m.y < 0 || m.y >= size
                    || wine.cell[m.x + 4][m.y + 4].piece != Empty) {
                    LOGI("%d,%d", best.x, best.y);
                } else {
                    wine.TurnMove(m);
                }
                cin >> command;
            }
            best = wine.TurnBest();
            wine.TurnMove(best);
            cout << best.x << "," << best.y << endl;
        } else if (command == "INFO") {
            int value;
            string key;
            cin >> key;
            if (key == "timeout_turn") {
                cin >> value;
                if (value != 0)
                    wine.timeout_turn = value;

            } else if (key == "timeout_match") {
                cin >> value;
                if (value != 0)
                    wine.timeout_match = value;

            } else if (key == "time_left") {
                cin >> value;
                if (value != 0)
                    wine.time_left = value;

            }
        } else if (command == "END") {
            exit(0);

        }
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_xyz_imxqd_ta_game_RobotAI2_restart(JNIEnv *env, jobject instance) {
    wine.ReStart();
}

extern "C"
JNIEXPORT void JNICALL
Java_xyz_imxqd_ta_game_RobotAI2_initBoard(JNIEnv *env, jobject instance, jobjectArray map,
                                          jint size) {
    LOGI("init board...");
    wine.ReStart();
    Pos m;
    for (jint i = 0; i < size; i++) {
        jobjectArray arr = (jobjectArray) env->GetObjectArrayElement(map, i);
        int *p = env->GetIntArrayElements((jintArray) arr, false);
        m.x = p[0];
        m.y = p[1];
        LOGI("(%d, %d)", m.x, m.y);
        if (m.x < 0 || m.x >= wine.size || m.y < 0 || m.y >= wine.size
            || wine.cell[m.x + 4][m.y + 4].piece != Empty) {
            LOGE("init board error.");
        } else {
            wine.TurnMove(m);
        }
        env->DeleteLocalRef(arr);
    }

}

extern "C"
JNIEXPORT void JNICALL
Java_xyz_imxqd_ta_game_RobotAI2_start(JNIEnv *env, jobject instance, jint size) {
    wine.SetSize(size);
}

extern "C"
JNIEXPORT void JNICALL
Java_xyz_imxqd_ta_game_RobotAI2_turnMove(JNIEnv *env, jobject instance, jintArray p_) {
    jint *p = env->GetIntArrayElements(p_, NULL);
    Pos pos;
    pos.x = p[0];
    pos.y = p[1];
    wine.TurnMove(pos);
    env->ReleaseIntArrayElements(p_, p, 0);
}

extern "C"
JNIEXPORT jintArray JNICALL
Java_xyz_imxqd_ta_game_RobotAI2_turnBest(JNIEnv *env, jobject instance) {
    Pos pos = wine.TurnBest();
    int *p = new int[2];
    p[0] = pos.x;
    p[1] = pos.y;
    jintArray arr = env->NewIntArray(2);
    env->SetIntArrayRegion(arr, 0, 2, p);
    return arr;
}


extern "C"
JNIEXPORT void JNICALL
Java_xyz_imxqd_ta_game_RobotAI2_setTimeout(JNIEnv *env, jobject instance, jint timeout) {
    wine.timeout_turn = timeout;
}


