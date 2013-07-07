/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_nbs_q3_spi_SPIFactory */

#ifndef _Included_com_nbs_q3_spi_SPIFactory
#define _Included_com_nbs_q3_spi_SPIFactory
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_nbs_q3_spi_SPIFactory
 * Method:    SPIConnect
 * Signature: (Ljava/lang/String;)Ljava/io/FileDescriptor;
 */
JNIEXPORT jobject JNICALL Java_com_nbs_q3_spi_SPIFactory_SPIConnect
  (JNIEnv *, jobject, jstring);

/*
 * Class:     com_nbs_q3_spi_SPIFactory
 * Method:    SPIConfigure
 * Signature: (Ljava/io/FileDescriptor;BBI)I
 */
JNIEXPORT jint JNICALL Java_com_nbs_q3_spi_SPIFactory_SPIConfigure
  (JNIEnv *, jobject, jobject, jbyte, jbyte, jint);

/*
 * Class:     com_nbs_q3_spi_SPIFactory
 * Method:    SPIDisconnect
 * Signature: (Ljava/io/FileDescriptor;)V
 */
JNIEXPORT void JNICALL Java_com_nbs_q3_spi_SPIFactory_SPIDisconnect
  (JNIEnv *, jobject, jobject);

/*
 * Class:     com_nbs_q3_spi_SPIFactory
 * Method:    SPIWriteRead
 * Signature: (Ljava/io/FileDescriptor;[BISBI)[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_nbs_q3_spi_SPIFactory_SPIWriteRead
(JNIEnv *, jobject, jobject, jbyteArray, jint, jshort, jbyte, jint);

#ifdef __cplusplus
}
#endif
#endif
