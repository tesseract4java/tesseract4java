ocrevalUAtion [![Build Status](https://secure.travis-ci.org/impactcentre/ocrevalUAtion.png?branch=master)](http://travis-ci.org/impactcentre/ocrevalUAtion)
=============

This set of classes provides basic support to perform the comparison of
two text files: a reference file (a ground-truth document) and a the output from an OCR engine (a text file).

Options for specific behavior include: ignore case, ignore diacritics, 
ignore punctuation, ignore stop-words, Unicode and user-defined equivalences between characters.

It can be used with the graphic user interface (GUI) provided, in addition to command line interface usage.

Supported input formats include: plain text, FineReader 10 XML, PAGE XML, ALTO XML and hOCR HTML.

The output generates a report with statistics (including CER and WER error rates) 
and a table with the parallell input texts where the differences are highlighted.

A gentle introduction to OCR evaluation and to this tool can be found at https://sites.google.com/site/textdigitisation/

You can download the latest release from [here](https://bintray.com/impactocr/maven/ocrevalUAtion).

Instructions on how to use ocrevalUAtion can be found in the [wiki](https://github.com/impactcentre/ocrevalUAtion/wiki).




