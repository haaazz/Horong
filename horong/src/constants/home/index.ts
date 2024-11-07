interface HomeConstantType {
  [key: string]: {
    'home-header': string
    'home-chat-placeholder': string
    'home-txt1': string
    'home-txt2': string
    'home-horong-error-txt': string

    'guide-btn': string
    'mic-btn': string
    'community-btn': string
    'exchange-btn': string
  }
}

export const HOME_CONSTANT: HomeConstantType = {
  KOREAN: {
    'home-header': '호롱챗',
    'home-chat-placeholder': '메시지를 입력해주세요',
    'home-txt1': '안녕하세요, 호롱입니다 :D',
    'home-txt2': '궁금하신게 있으시면 편하게 말씀해주세요 ❤',
    'home-horong-error-txt':
      '답변 생성 중에 오류가 발생했어요. 잠시후 다시 시도해주세요.',

    'guide-btn': '한국 가이드',
    'mic-btn': '한국어 학습',
    'community-btn': '커뮤니티',
    'exchange-btn': '사설 환전소',
  },

  ENGLISH: {
    'home-header': 'HorongChat',
    'home-chat-placeholder': 'Please enter a message',
    'home-txt1': 'Hello, I am Horong :D',
    'home-txt2': 'If you have any questions, feel free to ask ❤',
    'home-horong-error-txt':
      'An error occurred while generating a response. Please try again in a moment.',

    'guide-btn': 'Korea Guide',
    'mic-btn': 'Korean Learning',
    'community-btn': 'Community',
    'exchange-btn': 'Private Exchange',
  },

  CHINESE: {
    'home-header': 'HorongChat',
    'home-chat-placeholder': '请输入消息',
    'home-txt1': '你好，我是Horong :D',
    'home-txt2': '如果您有任何问题，请随时问 ❤',
    'home-horong-error-txt': '生成回复时发生错误。请稍后再试',

    'guide-btn': '韩国指南',
    'mic-btn': '韩语学习',
    'community-btn': '社区',
    'exchange-btn': '私人交易所',
  },

  JAPANESE: {
    'home-header': 'HorongChat',
    'home-chat-placeholder': 'メッセージを入力してください',
    'home-txt1': 'こんにちは、私はHorongです :D',
    'home-txt2': '質問がある場合は、お気軽にお尋ねください ❤',
    'home-horong-error-txt':
      '回答の生成中にエラーが発生しました。少し待ってからもう一度お試しください。',

    'guide-btn': '韓国ガイド',
    'mic-btn': '韓国語学習',
    'community-btn': 'コミュニティ',
    'exchange-btn': 'プライベート取引所',
  },
}
