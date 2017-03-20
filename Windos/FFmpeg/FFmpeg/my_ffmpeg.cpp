
#include <stdio.h>
#include <stdlib.h>

extern "C" // C/C++ 混编 指示编译器按照C语言进行编译
{
#include "libavcodec\avcodec.h"
};

void main(){
	// 输出配置文件检查是否配置成功
	printf("%s\n", avcodec_configuration());
	getchar();
}