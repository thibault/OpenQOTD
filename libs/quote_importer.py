#! /usr/bin/env python

# © Copyright 2011 Thibault Jouannic <thibault@jouannic.fr>. All Rights Reserved.
#  This file is part of OpenQOTD.
#
#  OpenQOTD is free software: you can redistribute it and/or modify
#  it under the terms of the GNU General Public License as published by
#  the Free Software Foundation, either version 3 of the License, or
#  (at your option) any later version.
#
#  OpenQOTD is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
#
#  You should have received a copy of the GNU General Public License
#  along with OpenQOTD. If not, see <http://www.gnu.org/licenses/>.



import sqlite3
import libxml2
import sys
import codecs
import os


def check_usage():
    if len(sys.argv) != 3:
        print "Usage : %s /path/to/assets /path/to/db.db" % sys.argv[0]
        sys.exit(1)

    return True

def create_db(db_path):
    conn = sqlite3.connect(db_path)
    c = conn.cursor()
    c.execute('DROP TABLE IF EXISTS android_metadata')
    c.execute('DROP TABLE IF EXISTS quote')
    c.execute('CREATE TABLE android_metadata (locale TEXT DEFAULT "en_US")')
    c.execute('''CREATE TABLE quote (
        _id INTEGER PRIMARY KEY,
        quote TEXT,
        lang VARCHAR(2))
    ''')

    return conn

def import_quotes_from(assets_path, conn):
    for lang in os.listdir(assets_path):
        lang_path = os.path.join(assets_path, lang)
        for file in os.listdir(lang_path):
            import_file(conn, assets_path, lang, file)

def import_file(conn, assets, lang, file):
    file_path = os.path.join(assets, lang, file)
    fh = codecs.open(file_path, 'r', 'utf-8')

    quote = u""
    for line in fh.readlines():
        if line == u"%\n" and quote != u"":
            save_quote(conn, quote, lang)
            quote = u""
        else:
            quote += line

def save_quote(conn, quote, lang):
    c = conn.cursor()
    c.execute('INSERT INTO quote(quote, lang) VALUES(?, ?)', (quote.strip(), lang))

if __name__ == '__main__':
    check_usage()
    assets_path = sys.argv[1]
    db_path = sys.argv[2]

    conn = create_db(db_path)
    import_quotes_from(assets_path, conn)
    conn.commit()
