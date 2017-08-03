# Android Word Predictor Library
This is a library for predicting current word and next word in English and Persian languages. In this library we used one popular dictionary for each language. For obtaining good results, we tested the dictionaries on more than 1K users and then we extracted most popular words in dictionaries and increased the frequency of those words.<br />
In this project we used N-Gram (N from 1 to 4) to getting better result on next word prediction. If you have a suggestion, we are all ear:)<br />
<br />
![](http://ayhansalami.ir/wordprediction/1-small.jpg)
![](http://ayhansalami.ir/wordprediction/2-small.jpg)
![](http://ayhansalami.ir/wordprediction/3-small.jpg)
<br />
You can download sample application from here.
[Sample Application](http://ayhansalami.ir/wordprediction/app-release.apk)

## Installing
For using this library you should download the zip file and then import the "prediction" folder into your project. After that you should initialize Sugar ORM Context (You can read sugar orm documentation or look at the example application).
(This library will be available on jcenter soon.)

## Examples
For using this library you can look at the examples in sample projects. If you have a question, feel free to contact me at salami.d@removemeaut.ac.ir

## TODO
- Add another languages support
- Remove stop words before any other process
- Improve dictionaries quality by increasing more common words frequency
- Using other NLP algorithms in order to increase prediction precision

## License
    Copyright 2013 Ayhan Salami

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
