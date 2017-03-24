# Build-FFmpeg

Windows 疏通编译流程, 简单输出了一下

Linux 记录在 Linux 中的编译流程, 夹杂了一些linux知识, 最后创建了一个安卓应用试运行一下

# FFmpeg

FFmpeg 包含了很多优秀的算法, 可以提取出来单独使用

## FFmpeg 一共包含8个库:

>不需要的编译时可以 disable 掉(avdevice, postproc 等可以去掉)   
>因为 avcodec 中包含了很多编解码方式, 所以包会比较大, 这时也可以 disable-encoder=NAME 和 disable-decoder=NAME   
>disable 参考 configure 中的介绍

1. avcodec : 编解码, 包含了几乎所有主流编解码算法(最重要的库)
2. avformat : 封装格式处理(flv, mp4 等)
3. avfilter : 滤镜特效处理
4. avdevice : 各种设备的输入输出
5. avutil : 工具库 (大部分分库都需要这个库的支持)
6. postproc : 后加工
7. swresample : 音频采样数据格式转换
8. swscale : 视频像素数据格式转换 

这里主要用到 avcodec, avformat, avutil, swscale

## FFmpeg 解码流程 -- 解码过程是一帧一帧的读取处理

>流程只是一般流程, 解码器也可以直接指定不进行查找

* --> av_register_all() 注册所有组件(这里是全部注册, 实际用到哪个注册哪个即可)
* --> avformat_open_input() 打开输入视频文件
* --> avformat_find_stream_ino() 获取视频文件信息
* --> avcodec_find_decoder() 查找解码器
* --> avcodec_open2() 打开解码器
* --> av_read_frame() 从输入文件读取一帧压缩数据, 这里进入循环, 读取不到结束循环
* --> 循环 --> AVPacket 数据封装包
* ----> avcodec_decode_video2() 解码一帧压缩数据
* ----> AVFrame 像素数据
* ----> Show On Screen..  
* --> 直到 false
* --> close
* ----> avcodec_close() 关闭解码器
* ----> avformat_close_input() 关闭输入视频文件 
	

# FFmpeg 示例

>在 ffmpeg 源码 -> doc -> examples 中查看示例 或者 Linux 中查看 c 代码

```
#include "include/libavformat/avformat.h"  // 封装格式
#include "include/libavcodec/avcodec.h"    // 解码
#include "include/libswscale/swscale.h"    // 缩放

// 假设在 jni 中
void deal(JNIEnv *env, jclass jcls, jstring input_jstr, jstring output_jstr){
	const char* input_cstr = (*env)->GetStringUTFChars(env, input_jstring, NULL);
	const char* output_cstr = (*env)->GetStringUTFChars(env, output_jstring, NULL);

	// 1. 注册组件
	av_register_all();
	
	// 封装格式上下文
	AVFormatContext *formatContext = avformat_alloc_context();

	// 2. 打开视频文件
	if(avformat_open_input(&formatContext, input_cstr, NULL, NULL) != 0){
		// 打开失败
	} else{
		// 3. 获取视频信息
		if(avformat_find_stream_info(formatContext, NULL) < 0){
			// 获取失败
		} else {
			// 找到解码器
			// 视频数据分为视频, 音频, 字幕等, 这里找视频数据
			int video_stream_index = -1;
			int i = 0;
			for(; i < formatContext->nb_streams; i++){
				// 根据类型判断是否是视频流
				if(formatContext->streams[i]->codec_type == AVMEDIA_TYPE_VIDEO){
					video_stream_index = i;
					break;
				}
			}
			if(voide_stream_index >= 0){
				// 获取编解码器上下文
				AVCodecContext *codecContext = formatContext->streams[video_stream_index]->codec;
				// 4. 拿到解码器
				AVCodec *codec avcodec_find_decoder(codecContext->codec_id);
				if(codec == NULL){
					// 解码器获取失败
				} else {
					// 5. 打开解码器
					if(avcodec_open2(codecContext, codec, NULL) < 0){
						// 解码器打开失败
					} else {
						// 6. 读取压缩的视频数据 AVPacket

						// 压缩数据
						AVPacket *packet = av_malloc(sizeof(AVPacket));
						av_init_packet(packet);

						// 像素数据
						AVFrame *frame = av_frame_alloc();
						AVFrame *yuvFrame = av_frame_alloc();

						//只有指定了AVFrame的像素格式、画面大小才能真正分配内存
						//缓冲区分配内存
						uint8_t *out_buffer = (uint8_t *)av_malloc(avpicture_get_size(AV_PIX_FMT_YUV420P, codecContext->width, codecContext->height));
						//初始化缓冲区
						avpicture_fill((AVPicture *)yuvFrame, out_buffer, AV_PIX_FMT_YUV420P, codecContext->width, codecContext->height);
						
						// 输出文件
						FILE *fp_yuv = fopen(output_cstr, "wb");
						
						// 使用这个进行缩放
						struct SwsContext *sws_ctx = sws_gettContext(
								codecContext->width, codecContext->height,  codecContext->pix_fmt,
								codecContext->width, codecContext->height,  AV_PIX_FMT_YUV420P,
								SWS_BILINEAR, NULL, NULL, NULL
							);

						int len = 0;
						int got_frame;

						while(av_read_frame(formatContext, packet) >= 0){
							// 7. 解码
							len = avcodec_decode_video2(codecContext, frame, &got_frame, packet);
							// 0 代表没有下一帧, 解码完成, 非 0 代表正在解码
							if(got_frame){
								// 解码中

								// 转为 指定的 420p像素帧
								sws_scale(
									sws_ctx, 
									frame->data, 
									frame->linesize, 
									0, 
									frame->height, 
									yuvFrame->data,
									yuvFrame->linesize
									);

								// 向YUV文件保存解码之后的帧数据
								// AVFrame->YUV
								// 一个像素包含一个Y
								int y_size = codecContext->width * codecContext->height;
								// y:u:v 比例 4:1:1
								fwrite(yuvFrame->data[0], 1, y_size, fp_yuv); // y
								fwrite(yuvFrame->data[1], 1, y_size/4, fp_yuv); // u
								fwrite(yuvFrame->data[2], 1, y_size/4, fp_yuv); // v

								// 释放
							} else {
								// 解码完成
							}
							av_free_packet(packet);
						}

						// 释放
						fclose(fp_yuv);
						av_frame_free(&frame);
						avcodec_close(codecContext);
						avformat_free_context(formatContext);
					}
				}
			} else {
				// 没有找到视频流
			}
		}
	}

	(*env)->ReleaseStringUTFChars(env, input_jstr, input_cstr);
	(*env)->ReleaseStringUTFChars(env, output_jstr, output_cstr);
}
```
