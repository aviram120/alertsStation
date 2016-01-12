from google.appengine.ext import ndb
import json
import codecs

class Routes(ndb.Model):
    route_id = ndb.IntegerProperty()
    agency_id = ndb.IntegerProperty()
    route_short_name = ndb.StringProperty()
    route_long_name = ndb.StringProperty()
    route_desc = ndb.StringProperty()
    route_type = ndb.IntegerProperty()
    city_name = ndb.StringProperty()

    @staticmethod
    def readFromGtfsRoutes():
        fo = codecs.open('./resources/new_routes.txt', "r", "utf-8-sig")
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

            city_name_loc = words[6]

            addRow=Routes(route_id=route_id_loc, agency_id=agency_id_loc, route_short_name = route_short_name_loc,
                         route_long_name = route_long_name_loc, route_desc = route_desc_loc, route_type = route_type_loc, city_name = city_name_loc)
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

    @staticmethod
    def getAllCityByAgencyID(agency_id):
        list=[]
        routes=Routes.query(Routes.agency_id == agency_id)
        for resRout in routes:
            tempRoutes = {}
            tempRoutes['city_name']=resRout.route_id#TODO-- need to replace "route_id" to "city_name"
            if tempRoutes not in list:
                list.append(tempRoutes)

        reply_json=json.dumps(list,ensure_ascii=False)
        return reply_json

    @staticmethod
    def getAllRoutesByAgencyCity(agency_id,city_name):
        list=[]
        routes=Routes.query(Routes.agency_id == agency_id,Routes.city_name==city_name)
        for resRout in routes:
            tempRoutes = {}
            tempRoutes['route_id']=resRout.route_id
            tempRoutes['route_short_name']=resRout.route_short_name
            list.append(tempRoutes)

        reply_json=json.dumps(list,ensure_ascii=False)
        return reply_json
t









