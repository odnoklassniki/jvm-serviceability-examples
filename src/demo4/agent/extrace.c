#include <jvmti.h>
#include <string.h>
#include <stdio.h>

void printStackTrace(JNIEnv* env, jobject exception) {
    jclass throwable_class = (*env)->FindClass(env, "java/lang/Throwable");
    jmethodID print_method = (*env)->GetMethodID(env, throwable_class, "printStackTrace", "()V");
    (*env)->CallVoidMethod(env, exception, print_method);
}

void JNICALL ExceptionCallback(jvmtiEnv* jvmti, JNIEnv* env, jthread thread,
                               jmethodID method, jlocation location, jobject exception,
                               jmethodID catch_method, jlocation catch_location) {
    char* class_name;
    jclass exception_class = (*env)->GetObjectClass(env, exception);
    (*jvmti)->GetClassSignature(jvmti, exception_class, &class_name, NULL);
    printf("Exception: %s\n", class_name);
    printStackTrace(env, exception);
}

JNIEXPORT jint JNICALL Agent_OnLoad(JavaVM* vm, char* options, void* reserved) {
    jvmtiEnv* jvmti;
    jvmtiEventCallbacks callbacks;
    jvmtiCapabilities capabilities;

    (*vm)->GetEnv(vm, (void**)&jvmti, JVMTI_VERSION_1_0);

    memset(&capabilities, 0, sizeof(capabilities));
    capabilities.can_generate_exception_events = 1;
    (*jvmti)->AddCapabilities(jvmti, &capabilities);

    memset(&callbacks, 0, sizeof(callbacks));
    callbacks.Exception = ExceptionCallback;
    (*jvmti)->SetEventCallbacks(jvmti, &callbacks, sizeof(callbacks));
    (*jvmti)->SetEventNotificationMode(jvmti, JVMTI_ENABLE, JVMTI_EVENT_EXCEPTION, NULL);

    return 0;
}
