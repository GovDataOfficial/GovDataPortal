# -*- coding: utf-8 -*-
import re
import codecs
import logging
import sys, errno, os
logging.basicConfig(level=logging.INFO)

f = codecs.open(sys.argv[1], "r", "utf-8")
outdir = 'target/test'
if not os.path.exists(outdir):
    os.makedirs(outdir)
o = codecs.open(outdir + '/Language-utf8encoded.properties', "w", "UTF-8")

changed_lines = False
for line in f:
    # first the really screwed up bits: two unicode chars for what is definitely one:
    original_line = line
    line = line.replace('\\u00c3\\u00a4', u'ä')
    line = line.replace('\\u00c3\\u00bc', u'ü')
    line = line.replace('\\u00c3\\u00b6', u'ö')
    line = line.replace('\\u00c3\\u009f', u'ß')
    line = line.replace('\\u00c3\\u0084', u'Ä')
    line = line.replace('\\u00c3\\u009c', u'Ü')
    line = line.replace('\\u00c3\\u0096', u'Ö')
    line = re.sub(r'\\u([0-9a-fA-F]{4})', lambda m:unichr(int('0x' + m.group(1)[2:], 0)), line)
    if not line == original_line:
        logging.info('changed line: ' + original_line)
        changed_lines = True
    o.write(line)
if changed_lines:
    sys.exit(errno.EPROTO)