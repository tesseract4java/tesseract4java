# Tesseract GUI


A graphical user interface for the [Tesseract OCR engine][tesseract]. The
program has been introduced in the [Master’s thesis “Analyses and Heuristics for
the Improvement of Optical Character Recognition Results for Fraktur
Texts”][thesis] by Paul Vorbach.

[tesseract]: https://code.google.com/p/tesseract-ocr/
[thesis]: http://nbn-resolving.de/urn/resolver.pl?urn:nbn:de:bvb:20-opus-106527


## Screenshots

![Preprocessing](https://raw.githubusercontent.com/tesseract4java/tesseract-gui/master/screenshots/gui-preprocessing.png)
Preprocessing view

![Box Editor](https://raw.githubusercontent.com/tesseract4java/tesseract-gui/master/screenshots/gui-comparison.png)
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

## Download & Usage

You can download and build the source code using Gradle. I will soon publish
more detailed information on how to do this as well as a binary release.


## Credits

  - This software uses the [Tesseract OCR engine][tesseract].
  - This software uses [ocrevalUAtion] by Rafael C. Carrasco for providing
    accuracy measures of the OCR results.
  - This software uses the [Silk icon set][silk] by Mark James
    ([famfamfam.com]).

[ocrevalUAtion]: https://github.com/impactcentre/ocrevalUAtion
[silk]: http://www.famfamfam.com/lab/icons/silk/
[famfamfam.com]: http://www.famfamfam.com/
[CC-BY-3.0]: http://creativecommons.org/licenses/by/3.0/


## License

GPLv3

~~~
Tesseract GUI - a graphical user interface for the Tesseract OCR engine
Copyright (C) 2014  Paul Vorbach

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
