
#include <stdio.h>
#include <stdlib.h>

extern "C" // C/C++ ��� ָʾ����������C���Խ��б���
{
#include "libavcodec\avcodec.h"
};

void main(){
	// ��������ļ�����Ƿ����óɹ�
	printf("%s\n", avcodec_configuration());
	getchar();
}