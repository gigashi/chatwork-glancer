# Chatwork Glancer

AndroidのChatworkアプリは、通知にメッセージ内容が表示されず、表示する設定等も無い。  
別アプリから通知をフックし、Chatworkの[API](http://developer.chatwork.com/ja/)を使用しメッセージ内容を通知に表示する。

# Usage

1. チャットワークにログインし、[https://www.chatwork.com/service/packages/chatwork/subpackages/api/token.php](https://www.chatwork.com/service/packages/chatwork/subpackages/api/token.php)からAPIトークンを取得する
1. アプリにトークンを入力し、「トークンを設定」を押す
1. APIが正しければ自分のアカウント情報が表示される
1. Chatworkアプリにメッセージ通知が来るたびにこのアプリがメッセージ内容を通知に表示する

## Develop Environment

```bash
cat "/Applications/Android Studio.app/Contents/Info.plist" | grep "Android Studio" | grep "build"
    <string>Android Studio 3.5, build AI-191.8026.42.35.6010548. Copyright JetBrains s.r.o., (c) 2000-2019</string>
```

