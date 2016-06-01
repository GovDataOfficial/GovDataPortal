import elasticsearch
import unicodecsv as csv
import argparse
from datetime import datetime, timedelta, tzinfo
import codecs
import pytz

# get all arguments we need
parser = argparse.ArgumentParser()
parser.add_argument("server", help="host:port of the elastic search 1.x server")
parser.add_argument("index", help="name of the elastic search index holding the searchhistory")
parser.add_argument("outfile", help="csv output will be written there")
parser.add_argument("--start", help="only include entries created after this date. Format: dd.mm.yyyy")
parser.add_argument("--end", help="only include entries created before this date. Format: dd.mm.yyyy")
args = parser.parse_args()

locale_berlin = pytz.timezone('Europe/Berlin')
locale_utc = pytz.utc

# server = 'mtest4.gd.swe.seitenbau.net:9200'
# outfile = 'searchhistory.csv'
# index = 'govdata-searchhistory-de'
chunksize = 10000

conn = elasticsearch.Elasticsearch(args.server)

outfile = codecs.open(args.outfile, 'wb')
csvout = csv.writer(outfile, delimiter=',', quotechar="\"", dialect='excel', encoding='utf-8')
csvout.writerow(["Suchbegriff", "Zeitpunkt"])

def main():
    try:
        rowcounter = 0
        body = {
           'fields': [
             'phrase',
             'timestamp'
           ]
         }
        
        # add date filter
        datefilter = {}
        if args.start:
            dt = datetime.strptime(args.start, "%d.%m.%Y")
            dt_berlin = locale_berlin.localize(dt)
            datefilter["gte"] = dt_berlin.astimezone(locale_utc)
        if args.end:
            dt = datetime.strptime(args.end, "%d.%m.%Y") + timedelta(days=1)
            dt_berlin = locale_berlin.localize(dt)
            datefilter["lt"] = dt_berlin.astimezone(locale_utc)
        if datefilter:
            body['query'] = {
                              "filtered" : {
                                "query" : {
                                  "match_all" : {}
                                },
                                "filter" : {
                                  "range" : {
                                    "timestamp": datefilter
                                  }
                                }
                              }
                            }
        
        results = conn.search(index=args.index, body=body, scroll="10m", size=chunksize)
        scroll_id = results['_scroll_id']
        
        while len(results['hits']['hits']) > 0:
            rowcounter += len(results['hits']['hits'])
            for r in results['hits']['hits']: #2016-03-21T14:01:28.932Z
                dt = datetime.strptime(r['fields']['timestamp'][0], "%Y-%m-%dT%H:%M:%S.%fZ")
                dt_utc = locale_utc.localize(dt)
                dt_localized = dt_utc.astimezone(locale_berlin)
                csvout.writerow([r['fields']['phrase'][0], dt_localized.strftime("%Y-%m-%d %H:%M:%S")])
            
            # load next chunk
            results = conn.scroll(scroll_id=scroll_id, scroll="10m")
        
    finally:
        print "{} Zeilen geschrieben".format(rowcounter)
        outfile.close()


if __name__ == '__main__':
    main() 
