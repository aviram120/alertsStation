from google.appengine.ext import ndb
import json
import logging
import codecs

class Stops(ndb.Model):
    stop_id = ndb.IntegerProperty()
    stop_name = ndb.StringProperty()
    stop_lat = ndb.StringProperty()
    stop_lon = ndb.StringProperty()

    @staticmethod
    def readFromGtfsStops():
        fo = codecs.open('./resources/JER_TLV/new_stops.txt', "r", "utf-8-sig")
        for line in fo:
            words = line.split(",")

            stop_id_loc = words[0]
            stop_id_loc = stop_id_loc.strip()
            stop_id_loc = int(stop_id_loc)

            stop_name_loc = words[1]
            stop_lat_loc = words[2]
            stop_lon_loc = words[3]



            addRow=Stops(stop_id=stop_id_loc, stop_name=stop_name_loc, stop_lat = stop_lat_loc, stop_lon = stop_lon_loc)
            addRow.put()
        fo.close()

    @staticmethod
    def getAllStops():
        stops=Stops.query()
        list=[]

        for res in stops:
            tempStops = {}
            tempStops['stop_id']=res.stop_id
            tempStops['stop_name']=res.stop_name
            tempStops['stop_lat']=res.stop_lat
            tempStops['stop_lon']=res.stop_lon
            
            list.append(tempStops)

        reply_json=json.dumps(list,ensure_ascii=False)
        return reply_json


