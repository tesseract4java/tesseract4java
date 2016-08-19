# tesseract4java: Tesseract GUI


A graphical user interface for the [Tesseract OCR engine][tesseract]. The program has been introduced in the [Master’s
thesis “Analyses and Heuristics for the Improvement of Optical Character Recognition Results for Fraktur Texts”][thesis]
by Paul Vorbach (German).

[tesseract]: https://github.com/tesseract-ocr/tesseract
[thesis]: http://nbn-resolving.de/urn/resolver.pl?urn:nbn:de:bvb:20-opus-106527


[![Donate with PayPal](https://www.paypalobjects.com/en_US/i/btn/btn_donate_SM.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=LF8T2JP2YUUUE)


## Download

Binary distributions and release notes are available in the [releases section].

[Releases section]: https://github.com/tesseract4java/tesseract4java/releases


## Screenshots

![Preprocessing](https://raw.githubusercontent.com/tesseract4java/tesseract-gui/master/screenshots/gui-preprocessing.png)

Preprocessing view

![Box Editor](https://raw.githubusercontent.com/tesseract4java/tesseract-gui/master/screenshots/gui-box-editor.png)

Box editor for training

![Glyph Overview](https://raw.githubusercontent.com/tesseract4java/tesseract-gui/master/screenshots/gui-glyph-overview.png)

Glyph overview for easier detection of errors

![Comparison View](https://raw.githubusercontent.com/tesseract4java/tesseract-gui/master/screenshots/gui-comparison.png)

Comparison view to compare the original document with the perceived result

![Transcription View](https://raw.githubusercontent.com/tesseract4java/tesseract-gui/master/screenshots/gui-evaluation.png)

Evaluation view with a transcription field

![ocrevalUAtion](https://raw.githubusercontent.com/tesseract4java/tesseract-gui/master/screenshots/ocrevaluation.png)

[ocrevalUAtion]

![Batch Export](https://raw.githubusercontent.com/tesseract4java/tesseract-gui/master/screenshots/gui-batch-export.png)

Batch export functionality to handle large projects


## Building and running the software

This software is written in Java and can be built using [Apache Maven]. In order to build the software you have to
follow these steps:

 1. Obtain a copy either by cloning the repository or downloading the current [zip file][zip].
 2. Also obtain a copy of a [patched version of ocrevalUAtion][ocrevalUAtion patched] ([zip file][ocrevalUAtion zip]).
 3. Open a command line in the ocrevalUAtion directory and run `mvn clean install`.
 4. `cd` to the tesseract4java directory and run `mvn clean package -Pstandalone`. This will include the
    Tesseract binaries for your platform. You can manually define the platform by providing the option
    `-Djavacpp.platform=[PLATFORM]` (available platforms are `windows-x86_64`, `windows-x86`, `linux-x86_64`, `linux-x86`,
    and `macosx-x86_64`).

After you've run through all steps, the directory "tesseract4java/gui/target" will contain the file
"tesseract4java-[VERSION]-[PLATFORM].jar", which you can run by double-clicking or executing
`java -jar tesseract4java-[VERSION]-[PLATFORM].jar`.

[Apache Maven]: https://maven.apache.org/
[zip]: https://github.com/tesseract4java/tesseract4java/archive/develop.zip
[ocrevalUAtion patched]: https://github.com/tesseract4java/ocrevalUAtion
[ocrevalUAtion zip]: https://github.com/tesseract4java/ocrevalUAtion/archive/master.zip


## Credits

  - This software uses the [Tesseract OCR engine][tesseract] ([APLv2.0]).
  - This software uses [ocrevalUAtion] by Rafael C. Carrasco for providing
    accuracy measures of the OCR results ([GPLv3]).
  - This software uses the [Silk icon set][silk] by Mark James
    ([famfamfam.com]) ([CC-BY-3.0]).

[APLv2.0]: http://www.apache.org/licenses/LICENSE-2.0
[GPLv3]: https://www.gnu.org/licenses/gpl-3.0.html
[ocrevalUAtion]: https://github.com/impactcentre/ocrevalUAtion
[silk]: http://www.famfamfam.com/lab/icons/silk/
[famfamfam.com]: http://www.famfamfam.com/
[CC-BY-3.0]: http://creativecommons.org/licenses/by/3.0/


## License

GPLv3

~~~
tesseract4java - a graphical user interface for the Tesseract OCR engine
Copyright (C) 2014-2016 Paul Vorbach

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
~~~
