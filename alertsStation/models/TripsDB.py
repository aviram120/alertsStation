from google.appengine.ext import ndb
import json
import codecs

from models.StopTimeDB import StopTime
from models.StopsDB import Stops

class Trips(ndb.Model):
    route_id = ndb.IntegerProperty()
    trip_id = ndb.StringProperty()
    direction_id = ndb.IntegerProperty()

    @staticmethod
    def readFromGtfsTrips():
        fo = codecs.open('./resources/JER_TLV/trips_JER_TLV.txt', "r", "utf-8-sig")
        for line in fo:
            words = line.split(",")

            route_id_loc = words[0]
            route_id_loc = route_id_loc.strip()
            route_id_loc = int(route_id_loc)

            trip_id_loc = words[1]
					   
            direction_id_loc = words[2]
            direction_id_loc = direction_id_loc.strip()
            direction_id_loc = int(direction_id_loc)

            addRow=Trips(route_id=route_id_loc, trip_id=trip_id_loc, direction_id = direction_id_loc)
            addRow.put()
        fo.close()

    @staticmethod
    def getAllTrips():
        trips=Trips.query()
        list=[]

        for res in trips:
            tempTrips = {}
            tempTrips['route_id']=res.route_id
            tempTrips['trip_id']=res.trip_id
            tempTrips['direction_id']=res.direction_id
            list.append(tempTrips)

        reply_json=json.dumps(list,ensure_ascii=False)
        return reply_json

    @staticmethod
    def getAllStpsByRoutID(rout_id):
        list=[]

        trip_idRow=Trips.query(Trips.route_id==rout_id)

        stopTime=StopTime.query(StopTime.trip_id==trip_idRow.trip_id)

        for stopTimesID in stopTime:
            stops=Stops.query(Stops.stop_id==stopTimesID.stop_id)
            temp = {}

            temp['stop_sequence']=stopTimesID.stop_sequence
            temp['stop_name']=stops.stop_name
            temp['stop_lat']=stops.stop_lat
            temp['stop_lon']=stops.stop_lon
            list.append(temp)

        reply_json=json.dumps(list,ensure_ascii=False)
        return reply_json






