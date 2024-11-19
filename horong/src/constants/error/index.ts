export interface ErrorConstantType {
  [key: string]: Record<string, string>
}

export const ERROR_CONSTANT: ErrorConstantType = {
  KOREAN: {
    SECURITY_400_1: '비밀번호 생성 규칙에 맞지 않습니다.',
    SECURITY_400_2: '이전에 사용한 비밀번호는 사용할 수 없습니다.',
    TOKEN_401_1: '토큰이 유효하지 않습니다.',
    TOKEN_401_2: '토큰의 Signature가 일치하지 않습니다.',
    TOKEN_401_3: '토큰 발급처가 일치하지 않습니다.',
    TOKEN_401_4: '토큰이 만료되었습니다.',
    TOKEN_401_5: '토큰의 타입이 일치하지 않아 디코딩할 수 없습니다.',
    SECURITY_401_6: '사용자가 인증되지 않았습니다.',
    SECURITY_409_1: '6개월 안에 사용한 비밀번호는 사용할 수 없습니다.',
    SECURITY_500_1: '인증 필터 처리 중 오류가 발생했습니다.',
    SECURITY_500_2: '카카오 토큰을 만료하는 과정에서 오류가 발생했습니다.',
    REFRESH_TOKEN_401_1: '해당 리프레쉬토큰은 만료되었습니다.',
    TOKEN_400_1: '토큰 타입이 일치하지 않습니다.',
    TOKEN_500_1: '토큰을 레디스에 저장하는데 실패했습니다.',

    USER_400_1: '이미 존재하는 ID입니다.',
    USER_400_2: '이미 존재하는 닉네임입니다.',
    USER_400_5: '비밀번호가 일치하지 않습니다.',
    USER_409_1: '이미 삭제된 회원입니다.',
    USER_409_2: '금지어가 포함되어 있습니다.',

    S3_400_1: '확장자는 jpg, jpeg, png, gif, mp3, wav만 가능합니다.',
    S3_400_2: '업로드할 용량이 2MB를 초과합니다.',
    S3_400_3: '업로드에 실패하였습니다.',
    S3_400_4: 'PreSignedURL 생성에 실패하였습니다.',
    S3_404_1: 'S3에 프로필 이미지를 찾을 수 없습니다.',
    USER_400_4: '유저 ID는 최대 16자까지 입력할 수 있습니다.',
    USER_400_6: '비밀번호는 8자 이상 20자 이하로 입력해야 합니다.',

    USER_400_7: '닉네임은 2자 이상 20자 이하로 입력해야 합니다.',
    USER_400_9: '닉네임은 한국어, 영어, 중국어, 일본어와 숫자만 가능합니다.',
    USER_400_10: '유저 ID는 영어와 숫자만 가능합니다.',
    USER_400_8: '지원하지 않는 언어입니다.',

    USER_404_1: '존재하지 않는 회원입니다.',
    USER_404_5: 'ID가 존재하지 않습니다.',
    unexpected_error: '예상치 못한 에러 발생',
  },

  ENGLISH: {
    SECURITY_400_1: 'Password does not meet the creation rules.',
    SECURITY_400_2: 'Previously used passwords cannot be reused.',
    TOKEN_401_1: 'Token is invalid.',
    TOKEN_401_2: 'Token signature does not match.',
    TOKEN_401_3: 'Token issuer does not match.',
    TOKEN_401_4: 'Token has expired.',
    TOKEN_401_5: 'Token type does not match and cannot be decoded.',
    SECURITY_401_6: 'User is not authenticated.',
    SECURITY_409_1: 'Passwords used within the last 6 months cannot be reused.',
    SECURITY_500_1:
      'An error occurred during authentication filter processing.',
    SECURITY_500_2: 'An error occurred while expiring the Kakao token.',
    REFRESH_TOKEN_401_1: 'This refresh token has expired.',
    TOKEN_400_1: 'Token type does not match.',
    TOKEN_500_1: 'Failed to store the token in Redis.',

    USER_400_1: 'ID already exists.',
    USER_400_2: 'Nickname already exists.',
    USER_400_5: 'Passwords do not match.',
    USER_409_1: 'This user has already been deleted.',
    USER_409_2: 'Forbidden word is included.',

    S3_400_1: 'Only jpg, jpeg, png, gif, mp3, wav extensions are allowed.',
    S3_400_2: 'Upload size exceeds 2MB.',
    S3_400_3: 'Upload failed.',
    S3_400_4: 'Failed to create PreSigned URL.',
    S3_404_1: 'Profile image not found in S3.',
    USER_400_4: 'User ID can be up to 16 characters.',
    USER_400_6: 'Password must be between 8 and 20 characters.',
    USER_400_7: 'Nickname must be between 2 and 20 characters.',
    USER_400_9:
      'Nickname can only contain Korean, English, Chinese, Japanese characters, and numbers.',
    USER_400_10: 'User ID can only contain English letters and numbers.',
    USER_400_8: 'Unsupported language.',
    USER_404_1: 'User not found.',
    USER_404_5: 'ID does not exist.',
    unexpected_error: 'An unexpected error occurred.',
  },

  CHINESE: {
    SECURITY_400_1: '密码不符合创建规则。',
    SECURITY_400_2: '无法使用以前使用过的密码。',
    TOKEN_401_1: '令牌无效。',
    TOKEN_401_2: '令牌签名不匹配。',
    TOKEN_401_3: '令牌发行者不匹配。',
    TOKEN_401_4: '令牌已过期。',
    TOKEN_401_5: '令牌类型不匹配，无法解码。',
    SECURITY_401_6: '用户未认证。',
    SECURITY_409_1: '过去6个月内使用过的密码无法重复使用。',
    SECURITY_500_1: '认证过滤器处理中发生错误。',
    SECURITY_500_2: '在过期卡卡令牌时发生错误。',
    REFRESH_TOKEN_401_1: '此刷新令牌已过期。',
    TOKEN_400_1: '令牌类型不匹配。',
    TOKEN_500_1: '未能将令牌存储到Redis中。',

    USER_400_1: 'ID已存在。',
    USER_400_2: '昵称已存在。',
    USER_400_5: '密码不匹配。',
    USER_409_1: '该用户已被删除。',
    USER_409_2: '包含禁用词。',

    S3_400_1: '仅允许jpg、jpeg、png、gif、mp3、wav扩展名。',
    S3_400_2: '上传的文件大小超过2MB。',
    S3_400_3: '上传失败。',
    S3_400_4: '生成PreSigned URL失败。',
    S3_404_1: '未找到S3中的个人资料图片。',
    USER_400_4: '用户ID最多可输入16个字符。',
    USER_400_6: '密码必须在8到20个字符之间。',
    USER_400_7: '昵称必须在2到20个字符之间。',
    USER_400_9: '昵称只能包含韩语、英语、中文、日语和数字。',
    USER_400_10: '用户ID只能包含英文字母和数字。',
    USER_400_8: '不支持的语言。',
    USER_404_1: '用户不存在。',
    USER_404_5: 'ID不存在。',
    unexpected_error: '发生了意外错误。',
  },

  JAPANESE: {
    SECURITY_400_1: 'パスワード生成ルールに合っていません。',
    SECURITY_400_2: '以前使用したパスワードは使用できません。',
    TOKEN_401_1: 'トークンが無効です。',
    TOKEN_401_2: 'トークンの署名が一致しません。',
    TOKEN_401_3: 'トークンの発行元が一致しません。',
    TOKEN_401_4: 'トークンの有効期限が切れています。',
    TOKEN_401_5: 'トークンのタイプが一致しないためデコードできません。',
    SECURITY_401_6: 'ユーザーが認証されていません。',
    SECURITY_409_1: '過去6ヶ月以内に使用したパスワードは使用できません。',
    SECURITY_500_1: '認証フィルタ処理中にエラーが発生しました。',
    SECURITY_500_2:
      'カカオトークンを期限切れにする過程でエラーが発生しました。',
    REFRESH_TOKEN_401_1: 'このリフレッシュトークンは期限切れです。',
    TOKEN_400_1: 'トークンタイプが一致しません。',
    TOKEN_500_1: 'トークンをRedisに保存できませんでした。',

    USER_400_1: 'すでに存在するIDです。',
    USER_400_2: 'すでに存在するニックネームです。',
    USER_400_5: 'パスワードが一致しません。',
    USER_409_1: 'すでに削除された会員です。',
    USER_409_2: '禁止ワードが含まれています。',

    S3_400_1: 'jpg, jpeg, png, gif, mp3, wav拡張子のみ許可されています。',
    S3_400_2: 'アップロードサイズが2MBを超えています。',
    S3_400_3: 'アップロードに失敗しました。',
    S3_400_4: 'PreSigned URLの生成に失敗しました。',
    S3_404_1: 'S3にプロフィール画像が見つかりません。',
    USER_400_4: 'ユーザーIDは最大16文字まで入力できます。',
    USER_400_6: 'パスワードは8文字以上20文字以下で入力してください。',
    USER_400_7: 'ニックネームは2文字以上20文字以下で入力してください。',
    USER_400_9:
      'ニックネームは韓国語、英語、中国語、日本語、数字のみが可能です。',
    USER_400_10: 'ユーザーIDは英語と数字のみ可能です。',
    USER_400_8: 'サポートされていない言語です。',
    USER_404_1: '存在しない会員です。',
    USER_404_5: 'IDが存在しません。',
    unexpected_error: '予期しないエラーが発生しました。',
  },
}
