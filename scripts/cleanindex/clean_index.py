import elasticsearch
import argparse
import json
from ckanapi import RemoteCKAN
from ckanapi.errors import NotAuthorized, NotFound

# get all arguments we need
parser = argparse.ArgumentParser()
parser.add_argument("elasticsearchserver", help="host or host:port of the elastic search 1.x server")
parser.add_argument("index", help="name of the elastic search index")
parser.add_argument("ckanserver", help="host or host:port of the ckan server")
parser.add_argument("apikey", help="the apikey for accessing the ckan server")
args = parser.parse_args()

# elasticsearchserver = 'mtest4.gd.swe.seitenbau.net:9200'
# index = 'govdata-ckan-de'
# ckanserver = 'mtest4.gd.swe.seitenbau.net:5000'

chunksize = 100

es = elasticsearch.Elasticsearch(args.elasticsearchserver)


def main():
    try:
        rowcounter = 0
        missing_count = 0
        removed_count = 0
        error_count = 0
        package_ids_unique = set()
        body = {
           'fields': [
             'id'
           ]
         }

        results = es.search(index=args.index, body=body, scroll="10m", size=chunksize)
        scroll_id = results['_scroll_id']

        ckanserver_url = args.ckanserver if args.ckanserver.startswith('http') else 'http://' + args.ckanserver
        with RemoteCKAN(ckanserver_url, apikey=args.apikey) as ckan:
            while len(results['hits']['hits']) > 0:
                rowcounter += len(results['hits']['hits'])
                bulk_body = []
                for r in results['hits']['hits']:
                    package_id = r['fields']['id'][0]
                    es_type = r['_type']
                    es_id = r['_id']
                    package_ids_unique.add(package_id)
                    if package_id:
                        try:
                            pkg = ckan.action.package_show(id=package_id)
                            if pkg['state'] == 'deleted':
                                print ('Dataset with package_id %s is deleted in CKAN. Trying to delete document from elasticsearch.' % package_id)
                                action = {"delete":{"_id":es_id, "_type":es_type, '_index':args.index}}
                                bulk_body.append(json.dumps(action))
                        except NotAuthorized as err:
                            print ('Could not read dataset. Ignoring. Details: %s' % err)
                        except NotFound as err:
                            print ('Did not found dataset with package_id %s. Trying to delete document from elasticsearch.' % package_id)
                            missing_count += 1
                            action = {"delete":{"_id":es_id, "_type":es_type, '_index':args.index}}
                            bulk_body.append(json.dumps(action))
                if bulk_body:
                    bulk_body_r = '\n'.join(bulk_body) + '\n'
                    response = es.bulk(body=bulk_body_r)

                    # check if deleted
                    if response:
                        if not response['errors']:
                            removed_count += len(response['items'])
                        else:
                            for item in response['items']:
                                if item['found']:
                                    removed_count += 1
                                else:
                                    error_count += 1

                # load next chunk
                results = es.scroll(scroll_id=scroll_id, scroll="10m")
    finally:
        print ("{} rows processed. {} missing datasets in CKAN. {} documents successfully deleted from index."\
               "{} documents in error when deleting from index.".format(rowcounter, missing_count, removed_count, error_count))
        print ('unique packages: %s' % len(package_ids_unique))


if __name__ == '__main__':
    main() 
