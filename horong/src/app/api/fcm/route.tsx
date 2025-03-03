import { NextRequest, NextResponse } from 'next/server'

import { FCM_CONSTANT } from '@/constants/fcm/index.ts'
import admin from '@/util/fcmAdmin.ts'

export async function POST(request: NextRequest) {
  const {
    userId,
    type,
    contentId,
    boardType,
    lang,
  }: {
    userId: number
    type: 'COMMENT' | 'MESSAGE'
    lang: string
    contentId?: number
    boardType?: string
  } = await request.json()
  try {
    // 특정 userId에 해당하는 사용자 토큰 가져오기
    const snapshot = await admin
      .firestore()
      .collection('users')
      .where('userId', '==', userId)
      .get()

    if (snapshot.empty) {
      return NextResponse.json(
        { message: `No user found with userId` },
        { status: 404 },
      )
    }

    const userData = snapshot.docs[0].data()
    const token = userData.fcmToken

    if (!token) {
      return NextResponse.json(
        { message: `No FCM token found` },
        { status: 404 },
      )
    }
    // FCM 메시지 구성
    const message = {
      data: {
        contentType: type,
        boardType: `${boardType}`,
        title:
          type === 'COMMENT'
            ? `${FCM_CONSTANT[lang]['fcm-push-title-comment-txt']}`
            : `${FCM_CONSTANT[lang]['fcm-push-title-message-txt']}`,
        body: `${FCM_CONSTANT[lang]['fcm-push-body-txt']}`,
        url:
          type === 'COMMENT'
            ? `https://horong.kr/community/${boardType}/${contentId}`
            : `https://horong.kr/inbox`,
      },
      token,
    }
    await admin.messaging().send(message)

    return NextResponse.json(
      { message: 'Notification sent successfully' },
      { status: 200 },
    )
  } catch (error) {
    return NextResponse.json(
      { error: (error as Error).message },
      { status: 500 },
    )
  }
}
