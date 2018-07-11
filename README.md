# ika

`sbt assembly`

```
% java -jar target/scala-2.11/ika-assembly-0.1.0-SNAPSHOT.jar --help
Usage: ika [options]

  -r, --rule <rule>  デフォルトはregularです。 regular | gachi | league
  -t, --time <time>  デフォルト値はnowです。 now | next
  --help             このヘルプを表示します。
```


# バイト好きのためのサーバでの運用@discord

discordのbot用tokenを用意して `discord-bot.js` へ書いてください。

botのステータスを出すためにcronを設定します。

cronへ設定すると今バイトを募集しているかどうかのログをファイルに書きます。

`1 * * * * java -jar /home/astel/discord-splatoon-bot/ika-assembly-0.1.0-SNAPSHOT.jar -b coop | head -n 1 > /home/astel/discord-splatoon-bot/status.log`

tmuxなどで `node discord-bot.js` で起動してください。

discordで `今のバイト` や `次のバイト武器` など書き込みすると結果を出します。
