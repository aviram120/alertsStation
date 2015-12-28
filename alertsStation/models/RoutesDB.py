from google.appengine.ext import ndb
import json

class Routes(ndb.Model):
    route_id = ndb.IntegerProperty()
    agency_id = ndb.IntegerProperty()
    route_short_name = ndb.StringProperty()
    route_long_name = ndb.StringProperty()
    route_desc = ndb.StringProperty()
    route_type = ndb.IntegerProperty()

    @staticmethod
    def readFromGtfsRoutes():
        fo = open('./resources/routes.txt', "r")
        for line in fo:
            words = line.split(",")

            route_id_loc = words[0]
            route_id_loc = route_id_loc.strip()
            route_id_loc = int(route_id_loc)

            agency_id_loc = words[1]
            agency_id_loc = agency_id_loc.strip()
            agency_id_loc = int(agency_id_loc)

            route_short_name_loc = words[2]

            route_long_name_loc = words[3]

            route_desc_loc = words[4]

            route_type_loc = words[5]
            route_type_loc = route_type_loc.strip()
            route_type_loc = int(route_type_loc)

            addRow=Routes(route_id=route_id_loc, agency_id=agency_id_loc, route_short_name = route_short_name_loc,
                         route_long_name = route_long_name_loc, route_desc = route_desc_loc, route_type = route_type_loc)
            addRow.put()

        fo.close()

    @staticmethod
    def getAllRoutes():
        routes=Routes.query()
        list=[]

        for res in routes:
            tempRoutes = {}
            tempRoutes['route_id']=res.route_id
            tempRoutes['agencyId']=res.agency_id
            tempRoutes['route_short_name']=res.route_short_name
            tempRoutes['route_long_name']=res.route_long_name
            tempRoutes['route_desc']=res.route_desc
            tempRoutes['route_type']=res.route_type
            list.append(tempRoutes)

        reply_json=json.dumps(list,ensure_ascii=False)

        return reply_json


