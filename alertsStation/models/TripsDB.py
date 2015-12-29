from google.appengine.ext import ndb
import json
import codecs

class Trips(ndb.Model):
    route_id = ndb.IntegerProperty()
    trip_id = ndb.IntegerProperty()
    direction_id = ndb.IntegerProperty()

    @staticmethod
    def readFromGtfsTrips():
        fo = codecs.open('./resources/trips.txt', "r", "utf-8-sig")
        for line in fo:
            words = line.split(",")

            route_id_loc = words[0]
            route_id_loc = route_id_loc.strip()
            route_id_loc = int(route_id_loc)

            trip_id_loc = words[2]
            trip_id_loc = trip_id_loc.strip()
            trip_id_loc = int(trip_id_loc)
					   
            direction_id_loc = words[3]
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
