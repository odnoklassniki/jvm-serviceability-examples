#include <jvmti.h>
#include <stdarg.h>
#include <string.h>
#include <stdio.h>

static FILE* out;
static jrawMonitorID vmtrace_lock;
static jlong start_time;

static void trace(jvmtiEnv* jvmti, const char* fmt, ...) {
    jvmti->RawMonitorEnter(vmtrace_lock);

    jlong current_time;
    jvmti->GetTime(&current_time);

    char buf[1024];
    va_list args;
    va_start(args, fmt);
    vsprintf(buf, fmt, args);
    va_end(args);

    jlong time = current_time - start_time;
    fprintf(out, "[%d.%05d] %s\n", (int)(time / 1000000000),
                                   (int)(time % 1000000000 / 10000), buf);

    jvmti->RawMonitorExit(vmtrace_lock);
}

static char* fix_class_name(char* class_name) {
    // Strip 'L' and ';' from class signature
    class_name[strlen(class_name) - 1] = 0;
    return class_name + 1;
}

void JNICALL VMStart(jvmtiEnv* jvmti, JNIEnv* jni) {
    trace(jvmti, "VM started");
}

void JNICALL VMInit(jvmtiEnv* jvmti, JNIEnv* jni, jthread thread) {
    trace(jvmti, "VM initialized");
}

void JNICALL VMDeath(jvmtiEnv* jvmti, JNIEnv* jni) {
    trace(jvmti, "VM destroyed");
}

void JNICALL ClassFileLoadHook(jvmtiEnv* jvmti, JNIEnv* jni,
                               jclass class_being_redefined, jobject loader,
                               const char* name, jobject protection_domain,
                               jint data_len, const unsigned char* data,
                               jint* new_data_len, unsigned char** new_data) {
    trace(jvmti, "Loading class: %s", name);
}

void JNICALL ClassPrepare(jvmtiEnv* jvmti, JNIEnv* jni,
                          jthread thread, jclass klass) {
    char* name;
    jvmti->GetClassSignature(klass, &name, NULL);
    trace(jvmti, "Class prepared: %s", fix_class_name(name));
    jvmti->Deallocate((unsigned char*)name);
}

void JNICALL DynamicCodeGenerated(jvmtiEnv* jvmti, const char* name,
                                  const void* address, jint length) {
    trace(jvmti, "Dynamic code generated: %s", name);
}

void JNICALL CompiledMethodLoad(jvmtiEnv* jvmti, jmethodID method,
                                jint code_size, const void* code_addr,
                                jint map_length, const jvmtiAddrLocationMap* map,
                                const void* compile_info) {
    jclass holder;
    char* holder_name;
    char* method_name;
    jvmti->GetMethodName(method, &method_name, NULL, NULL);
    jvmti->GetMethodDeclaringClass(method, &holder);
    jvmti->GetClassSignature(holder, &holder_name, NULL);
    trace(jvmti, "Method compiled: %s.%s", fix_class_name(holder_name), method_name);
    jvmti->Deallocate((unsigned char*)method_name);
    jvmti->Deallocate((unsigned char*)holder_name);
}

void JNICALL GarbageCollectionStart(jvmtiEnv* jvmti) {
    trace(jvmti, "GC started");
}

void JNICALL GarbageCollectionFinish(jvmtiEnv* jvmti) {
    trace(jvmti, "GC finished");
}

JNIEXPORT jint JNICALL Agent_OnLoad(JavaVM* vm, char* options, void* reserved) {
    if (options == NULL || !options[0]) {
        out = stderr;
    } else if ((out = fopen(options, "w")) == NULL) {
        fprintf(stderr, "Cannot open output file: %s\n", options);
        return 1;
    }

    jvmtiEnv* jvmti;
    vm->GetEnv((void**)&jvmti, JVMTI_VERSION_1_0);

    jvmti->CreateRawMonitor("vmtrace_lock", &vmtrace_lock);
    jvmti->GetTime(&start_time);
    trace(jvmti, "VMTrace started");

    jvmtiCapabilities capabilities = {0};
    capabilities.can_generate_all_class_hook_events = 1;
    capabilities.can_generate_compiled_method_load_events = 1;
    capabilities.can_generate_garbage_collection_events = 1;
    jvmti->AddCapabilities(&capabilities);
    
    jvmtiEventCallbacks callbacks = {0};
    callbacks.VMStart = VMStart;
    callbacks.VMInit = VMInit;
    callbacks.VMDeath = VMDeath;
    callbacks.ClassFileLoadHook = ClassFileLoadHook;
    callbacks.ClassPrepare = ClassPrepare;
    callbacks.DynamicCodeGenerated = DynamicCodeGenerated;
    callbacks.CompiledMethodLoad = CompiledMethodLoad;
    callbacks.GarbageCollectionStart = GarbageCollectionStart;
    callbacks.GarbageCollectionFinish = GarbageCollectionFinish;
    jvmti->SetEventCallbacks(&callbacks, sizeof(callbacks));

    jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_VM_START, NULL);
    jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_VM_INIT, NULL);
    jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_VM_DEATH, NULL);
    jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_CLASS_FILE_LOAD_HOOK, NULL);
    jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_CLASS_PREPARE, NULL);
    jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_DYNAMIC_CODE_GENERATED, NULL);
    jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_COMPILED_METHOD_LOAD, NULL);
    jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_GARBAGE_COLLECTION_START, NULL);
    jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_GARBAGE_COLLECTION_FINISH, NULL);

    return 0;
}

JNIEXPORT void JNICALL Agent_OnUnload(JavaVM *vm) {
    if (out != NULL && out != stderr) {
        fclose(out);
    }
}
