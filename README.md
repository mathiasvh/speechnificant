# speechnificant
Network and Internet Technologies (2017-2018) project @ Universitat Autònoma de Barcelona

## Scope
* Convert WAV files to pcm_s16be (PCM signed 16bit big endian)
* Compress pcm_s16be to own file format (sph) via mu-law, LPC and truncation of voiceless parts (>2s)
* Decompress sph to WAV file

## Useful links
* [ADPCMEncoder](https://github.com/sammarshallou/ouaudioapplets/blob/master/src/uk/ac/open/audio/adpcm/ADPCMEncoder.java#L68)
* [Theory and Java mu-law example](https://www.developer.com/java/other/article.php/3286861/Java-Sound-Compressing-Audio-with-mu-Law-Encoding.htm)
* [C example of mu-law and A-law](https://www.codeproject.com/Articles/14237/Using-the-G-standard)
* [ffmpeg examples and info](https://trac.ffmpeg.org/wiki/audio%20types)
* [Voice activity detection](https://dsp.stackexchange.com/questions/1522/simplest-way-of-detecting-where-audio-envelopes-start-and-stop)
* [Java ffmpeg wrapper](https://github.com/bramp/ffmpeg-cli-wrapper)

## Flow
![alt text](https://github.com/mathiasvh/speechnificant/blob/master/SpeechnificantFlow.png "Strategy")
