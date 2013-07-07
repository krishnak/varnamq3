#include <jni.h>
#include <stdint.h>
#include <string.h>
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <getopt.h>
#include <fcntl.h>
#include <sys/ioctl.h>
#include <linux/types.h>
#include <linux/spi/spidev.h>
#include <com_nbs_q3_spi_SPIFactory.h>

JNIEXPORT jobject JNICALL Java_com_nbs_q3_spi_SPIFactory_SPIConnect
  (JNIEnv *env, jobject obj, jstring data)
{
	char *device;
		int fd;
		jobject ret;
		jfieldID field_fd;
		jmethodID const_fdesc;
		jclass class_fdesc, class_ioex;

		  class_ioex = (*env)->FindClass(env, "java/io/IOException");
		  if (class_ioex == NULL) return NULL;
		  class_fdesc = (*env)->FindClass(env, "java/io/FileDescriptor");
		  if (class_fdesc == NULL) return NULL;

		device = (*env)->GetStringUTFChars(env, data, NULL);
		if (device == NULL) {
		return NULL; /* OutOfMemoryError already thrown */
		}
        printf("JNI SPI Device used is  %s \n", device);
		fd = open(device, O_RDWR);
		(*env)->ReleaseStringUTFChars(env, data, device);

		  if (fd < 0)
		  {
			  printf("Open returned an error\n ");
		    // open returned an error. Throw an IOException with the error string
		    char buf[1024];
		    sprintf(buf, "open: %s", strerror(stdout));
		    (*env)->ThrowNew(env, class_ioex, buf);
		    return NULL;
		  }
		  printf("Open Success \n");


		  // construct a new FileDescriptor
		  const_fdesc = (*env)->GetMethodID(env, class_fdesc, "<init>", "()V");
		//  printf("After Get method ID  \n");

		  if (const_fdesc == NULL) return NULL;
		//  printf("Before New Object\n");

		  ret = (*env)->NewObject(env, class_fdesc, const_fdesc);
		//  printf("After NewObject  \n");
		  // poke the "fd" field with the file descriptor
		  field_fd = (*env)->GetFieldID(env, class_fdesc, "fd", "I");
		  if (field_fd == NULL) return NULL;
		  (*env)->SetIntField(env, ret, field_fd, fd);
		//  printf("Returning from JNI SPIConnect\n");

		  // and return it
		  return ret;
}

JNIEXPORT jint JNICALL Java_com_nbs_q3_spi_SPIFactory_SPIConfigure
  (JNIEnv *env, jobject obj, jobject device, jbyte mode, jbyte bits, jint speed)
{
	int ret;

	jclass fd_obj = (*env)->GetObjectClass(env,device);
	jfieldID field_id = (*env)->GetFieldID(env,fd_obj,"fd","I");
	int fd = (*env)->GetIntField(env,device,field_id);
	ret = ioctl(fd,SPI_IOC_WR_MODE,&mode);
	if(ret == -1)
		return ret;
    ret = ioctl(fd, SPI_IOC_WR_BITS_PER_WORD, &bits);
	if(ret == -1)
		return ret;
    ret = ioctl(fd, SPI_IOC_WR_MAX_SPEED_HZ, &speed);
    if(ret == -1)
    		return ret;
}


JNIEXPORT void JNICALL Java_com_nbs_q3_spi_SPIFactory_SPIDisconnect
  (JNIEnv *env, jobject obj,jobject device)
{
	jclass fd_obj = (*env)->GetObjectClass(env,device);
	jfieldID field_id = (*env)->GetFieldID(env,fd_obj,"fd","I");
	int fd = (*env)->GetIntField(env,device,field_id);
	close(fd);
}

JNIEXPORT jbyteArray JNICALL Java_com_nbs_q3_spi_SPIFactory_SPIWriteRead
(JNIEnv *env, jobject obj, jobject device, jbyteArray tx, jint length,jshort delay,jbyte bits,jint speed)
{
	int ret;
	int val;
	jbyteArray result = (*env)->NewByteArray(env, length);

	jbyte *tx1= (*env)->GetByteArrayElements(env,tx,NULL);
	/* printf("Sending SPI Command\n");
	 for (val = 0; val < length; val++) {
	                if (!(val % 11))
	                        puts("");
	                printf("%.2X ", tx1[val]);
	        }
*/
	uint8_t rx1[length] ;
    memset(rx1,0,length*sizeof(uint8_t));

			jclass fd_obj = (*env)->GetObjectClass(env,device);
					jfieldID field_id = (*env)->GetFieldID(env,fd_obj,"fd","I");
					int fd = (*env)->GetIntField(env,device,field_id);


					struct spi_ioc_transfer tr = {
									.tx_buf = (unsigned long)tx1,
									.rx_buf = (unsigned long)rx1,
									.len = length,
									.delay_usecs = delay,
									.speed_hz = speed,
									.bits_per_word = bits,
							};
			ret = ioctl(fd, SPI_IOC_MESSAGE(1), &tr);

		if(ret == -1)
					printf("SPI WRITE UNSUCCESSFUL\n");
	//	else
				//	printf("SPI data %.2X,%.2X\n",rx1[0],rx1[1]);

			(*env)->SetByteArrayRegion(env,result,0,length,rx1);
					(*env)->ReleaseByteArrayElements(env, tx, tx1, 0);
			   return result;
}
