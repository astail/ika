const Discord = require('discord.js');
const client = new Discord.Client();

const token = '************';

client.on('ready', () => {
  console.log('ログインしました。');

  setInterval(function() {
      var exec = require('child_process').exec;
      var COMMAND = 'cat ./status.log';
      exec(COMMAND, function(error, stdout, stderr) {
      if (error !== null) {
        console.log('exec error: ' + error);
        return;
      }
      client.user.setActivity(stdout, {
      type: 'PLAYING'
      });
    });
  // 60秒
  }, 60000);
});

client.on('message', message => {
  if (message.content.startsWith('!ika')) {
    var option = message.content.replace('!ika' , '')

    var exec = require('child_process').exec;
    var COMMAND = 'java -jar ika-assembly-0.1.0-SNAPSHOT.jar' + option;
    exec(COMMAND, function(error, stdout, stderr) {
    if (error !== null) {
      console.log('exec error: ' + error);
      return;
    }
      message.channel.send(stdout);
    })
  }
});

client.login(token);
